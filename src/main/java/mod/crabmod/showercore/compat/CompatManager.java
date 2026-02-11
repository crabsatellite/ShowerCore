package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Central manager for all mod compatibility integrations in ShowerCore.
 * Provides safe initialization with automatic fallback on errors.
 *
 * Features:
 * - Safe initialization with try-catch wrapper
 * - Automatic disabling of failed compat modules
 * - Version tracking for debugging
 * - Thread-safe concurrent access
 * - Comprehensive error logging with diagnostics
 */
public class CompatManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String GITHUB_ISSUES_URL = "https://github.com/crabsatellite/ShowerCore/issues";

    // Track which compats have been disabled due to errors
    private static final Map<String, CompatError> DISABLED_COMPATS = new ConcurrentHashMap<>();

    // Track all registered compats and their status
    private static final Map<String, CompatInfo> REGISTERED_COMPATS = new ConcurrentHashMap<>();

    // Flag to track if we've logged the startup summary
    private static volatile boolean startupSummaryLogged = false;

    /**
     * Information about a registered compat module
     */
    public static class CompatInfo {
        public final String modId;
        public final String displayName;
        public final BooleanSupplier isLoaded;
        public final Runnable initializer;
        public final String[] requiredApiClasses;
        public volatile boolean initialized = false;
        public volatile boolean enabled = true;
        public volatile String modVersion = "unknown";

        public CompatInfo(String modId, String displayName, BooleanSupplier isLoaded,
                          Runnable initializer, String... requiredApiClasses) {
            this.modId = modId;
            this.displayName = displayName;
            this.isLoaded = isLoaded;
            this.initializer = initializer;
            this.requiredApiClasses = requiredApiClasses;
        }
    }

    /**
     * Information about a compat error
     */
    public static class CompatError {
        public final String compatName;
        public final String modId;
        public final String modVersion;
        public final String errorMessage;
        public final String stackTrace;
        public final long timestamp;

        public CompatError(String compatName, String modId, String modVersion, Throwable error) {
            this.compatName = compatName;
            this.modId = modId;
            this.modVersion = modVersion;
            this.errorMessage = error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
            this.stackTrace = getStackTraceString(error);
            this.timestamp = System.currentTimeMillis();
        }

        private static String getStackTraceString(Throwable error) {
            StringBuilder sb = new StringBuilder();
            sb.append(error.getClass().getName()).append(": ").append(error.getMessage()).append("\n");
            for (StackTraceElement element : error.getStackTrace()) {
                if (element.getClassName().startsWith("mod.crabmod.showercore")) {
                    sb.append("  at ").append(element).append("\n");
                }
            }
            if (error.getCause() != null) {
                sb.append("Caused by: ").append(error.getCause().getClass().getName())
                        .append(": ").append(error.getCause().getMessage());
            }
            return sb.toString();
        }
    }

    /**
     * Register a compat module for safe initialization.
     *
     * @param modId       The mod ID
     * @param displayName Display name for logging
     * @param isLoaded    Check if the mod is loaded
     * @param initializer The initialization logic
     */
    public static void registerCompat(String modId, String displayName,
                                      BooleanSupplier isLoaded, Runnable initializer) {
        REGISTERED_COMPATS.put(modId, new CompatInfo(modId, displayName, isLoaded, initializer));
    }

    /**
     * Register a compat module with required API class verification.
     * The requiredApiClasses will be checked via Class.forName() BEFORE the initializer runs.
     * If any class is missing, the compat will be disabled with a detailed diagnostic.
     *
     * @param modId              The mod ID
     * @param displayName        Display name for logging
     * @param isLoaded           Check if the mod is loaded
     * @param initializer        The initialization logic
     * @param requiredApiClasses Fully qualified class names to verify before init
     */
    public static void registerCompat(String modId, String displayName,
                                      BooleanSupplier isLoaded, Runnable initializer,
                                      String... requiredApiClasses) {
        REGISTERED_COMPATS.put(modId, new CompatInfo(modId, displayName, isLoaded, initializer, requiredApiClasses));
    }

    /**
     * Initialize all registered compat modules safely.
     */
    public static void initializeAll() {
        LOGGER.info("=== ShowerCore Mod Compatibility System ===");
        LOGGER.info("Initializing mod integrations...");

        for (CompatInfo compat : REGISTERED_COMPATS.values()) {
            initializeCompat(compat);
        }

        logStartupSummary();
    }

    /**
     * Safely initialize a single compat module.
     */
    private static void initializeCompat(CompatInfo compat) {
        if (!safeCheckLoaded(compat)) {
            LOGGER.debug("{} not detected, skipping integration.", compat.displayName);
            return;
        }

        // Get mod version
        compat.modVersion = getModVersion(compat.modId);

        LOGGER.info("{} detected (version: {}). Initializing integration...",
                compat.displayName, compat.modVersion);

        try {
            // Verify required API classes before running the initializer
            if (compat.requiredApiClasses != null && compat.requiredApiClasses.length > 0) {
                LOGGER.debug("Verifying {} API classes for {}...",
                        compat.requiredApiClasses.length, compat.displayName);
                verifyApiClasses(compat.modId, compat.requiredApiClasses);
            }

            compat.initializer.run();
            compat.initialized = true;
            LOGGER.info("{} integration initialized successfully.", compat.displayName);
        } catch (Throwable e) {
            handleCompatError(compat, e);
        }
    }

    /**
     * Verify that required API classes from an external mod are available.
     *
     * @param modId      The mod ID whose API is being verified
     * @param classNames Fully qualified class names to check
     * @throws RuntimeException if any class is not found
     */
    public static void verifyApiClasses(String modId, String... classNames) {
        List<String> missing = new ArrayList<>();
        for (String className : classNames) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                missing.add(className);
            }
        }
        if (!missing.isEmpty()) {
            String modVersion = getModVersion(modId);
            StringBuilder sb = new StringBuilder();
            sb.append("API verification failed for mod '").append(modId)
                    .append("' (version: ").append(modVersion).append("). Missing classes:\n");
            for (String cls : missing) {
                sb.append("  - ").append(cls).append("\n");
            }
            sb.append("The installed version of this mod may have changed its API.");
            LOGGER.error(sb.toString());
            throw new RuntimeException(sb.toString());
        }
        LOGGER.debug("API verification passed for '{}': all {} classes found.", modId, classNames.length);
    }

    /**
     * Safely register event handler classes on the Forge EVENT_BUS for a compat module.
     * Verifies each handler class can be loaded and provides diagnostics on failure.
     *
     * @param modId          The mod ID this registration belongs to
     * @param handlerClasses The event handler classes to register
     */
    public static void registerEventHandlers(String modId, Class<?>... handlerClasses) {
        for (Class<?> handlerClass : handlerClasses) {
            try {
                Class.forName(handlerClass.getName(), true, handlerClass.getClassLoader());
                MinecraftForge.EVENT_BUS.register(handlerClass);
                LOGGER.debug("  Registered event handler: {}", handlerClass.getSimpleName());
            } catch (Throwable e) {
                LOGGER.error("Failed to register event handler {} for compat module '{}'",
                        handlerClass.getSimpleName(), modId);
                LOGGER.error("  Error: {} - {}", e.getClass().getName(), e.getMessage());
                throw new RuntimeException("Event handler registration failed: " + handlerClass.getSimpleName(), e);
            }
        }
    }

    /**
     * Execute an event handler callback safely within a compat context.
     * Use this in @SubscribeEvent methods to wrap the entire method body.
     *
     * @param modId     The mod ID for the compat module
     * @param eventName Name of the event/method for logging
     * @param action    The actual event handling logic
     */
    public static void safeEventCall(String modId, String eventName, Runnable action) {
        if (!isCompatEnabled(modId)) {
            return;
        }
        try {
            action.run();
        } catch (Throwable e) {
            handleRuntimeError(modId, eventName, e);
        }
    }

    /**
     * Execute an event handler callback safely with a return value.
     *
     * @param modId        The mod ID for the compat module
     * @param eventName    Name of the event/method for logging
     * @param action       The actual event handling logic
     * @param defaultValue Value to return if compat is disabled or error occurs
     * @return The result of the action, or defaultValue on failure
     */
    public static <T> T safeEventCall(String modId, String eventName, Supplier<T> action, T defaultValue) {
        if (!isCompatEnabled(modId)) {
            return defaultValue;
        }
        try {
            return action.get();
        } catch (Throwable e) {
            handleRuntimeError(modId, eventName, e);
            return defaultValue;
        }
    }

    /**
     * Check if a compat module is enabled (not disabled due to error).
     *
     * @param modId The mod ID to check
     * @return true if the compat module is enabled and initialized
     */
    public static boolean isCompatEnabled(String modId) {
        if (DISABLED_COMPATS.containsKey(modId)) {
            return false;
        }
        CompatInfo info = REGISTERED_COMPATS.get(modId);
        return info != null && info.enabled && info.initialized;
    }

    /**
     * Handle a compat initialization error.
     */
    private static void handleCompatError(CompatInfo compat, Throwable error) {
        compat.enabled = false;

        CompatError compatError = new CompatError(
                compat.displayName,
                compat.modId,
                compat.modVersion,
                error
        );
        DISABLED_COMPATS.put(compat.modId, compatError);

        LOGGER.error("========================================");
        LOGGER.error("SHOWERCORE COMPAT ERROR: {} integration DISABLED", compat.displayName);
        LOGGER.error("Mod: {} (version: {})", compat.modId, compat.modVersion);
        LOGGER.error("ShowerCore version: {}", getShowerCoreVersion());
        LOGGER.error("Minecraft version: {}", getMinecraftVersion());
        LOGGER.error("Error type: {}", error.getClass().getName());
        LOGGER.error("Error message: {}", error.getMessage());

        // Classify the error for easier debugging
        if (error instanceof NoClassDefFoundError || error instanceof ClassNotFoundException) {
            LOGGER.error("DIAGNOSIS: A required class from {} was not found.", compat.displayName);
            LOGGER.error("  This usually means the mod's API has changed in this version.");
            LOGGER.error("  Missing class: {}", error.getMessage());
        } else if (error instanceof NoSuchMethodError || error instanceof NoSuchFieldError) {
            LOGGER.error("DIAGNOSIS: A method/field in {}'s API was removed or renamed.", compat.displayName);
            LOGGER.error("  The mod's API is incompatible with this version of ShowerCore.");
            LOGGER.error("  Changed API element: {}", error.getMessage());
        } else if (error instanceof AbstractMethodError) {
            LOGGER.error("DIAGNOSIS: {} added new abstract methods that ShowerCore hasn't implemented.", compat.displayName);
            LOGGER.error("  Method: {}", error.getMessage());
        } else {
            LOGGER.error("DIAGNOSIS: Unexpected error during initialization.");
        }

        LOGGER.error("Please report this issue at: {}", GITHUB_ISSUES_URL);
        LOGGER.error("Full stack trace:", error);
        if (error.getCause() != null) {
            LOGGER.error("Root cause: {} - {}", error.getCause().getClass().getName(), error.getCause().getMessage());
        }
        LOGGER.error("========================================");
    }

    /**
     * Handle a runtime error in a compat module.
     */
    private static void handleRuntimeError(String modId, String operationName, Throwable error) {
        CompatInfo compat = REGISTERED_COMPATS.get(modId);
        if (compat == null) {
            LOGGER.error("SHOWERCORE RUNTIME ERROR: Unknown compat module '{}' reported error in operation '{}'",
                    modId, operationName);
            LOGGER.error("Error: {} - {}", error.getClass().getName(), error.getMessage());
            LOGGER.error("Stack trace:", error);
            return;
        }

        // Disable the compat
        compat.enabled = false;

        CompatError compatError = new CompatError(
                compat.displayName,
                modId,
                compat.modVersion,
                error
        );
        DISABLED_COMPATS.put(modId, compatError);

        LOGGER.error("========================================");
        LOGGER.error("SHOWERCORE RUNTIME ERROR: {} integration DISABLED", compat.displayName);
        LOGGER.error("Operation: {}", operationName);
        LOGGER.error("Mod: {} (version: {})", modId, compat.modVersion);
        LOGGER.error("ShowerCore version: {}", getShowerCoreVersion());
        LOGGER.error("Minecraft version: {}", getMinecraftVersion());
        LOGGER.error("Error type: {}", error.getClass().getName());
        LOGGER.error("Error message: {}", error.getMessage());

        // Classify the error for easier debugging
        if (error instanceof NoClassDefFoundError || error instanceof ClassNotFoundException) {
            LOGGER.error("DIAGNOSIS: A required class from {} was not found at runtime.", compat.displayName);
            LOGGER.error("  Missing class: {}", error.getMessage());
            LOGGER.error("  This may indicate the mod version has changed its internal class structure.");
        } else if (error instanceof NoSuchMethodError || error instanceof NoSuchFieldError) {
            LOGGER.error("DIAGNOSIS: An API method/field in {} was removed or its signature changed.", compat.displayName);
            LOGGER.error("  Changed API element: {}", error.getMessage());
        } else if (error instanceof AbstractMethodError) {
            LOGGER.error("DIAGNOSIS: {} requires new methods that ShowerCore hasn't implemented yet.", compat.displayName);
            LOGGER.error("  Method: {}", error.getMessage());
        } else if (error instanceof NullPointerException) {
            LOGGER.error("DIAGNOSIS: NullPointerException during {} operation.", operationName);
            LOGGER.error("  This may indicate the mod's data/state was not available when expected.");
        } else {
            LOGGER.error("DIAGNOSIS: Unexpected runtime error during {} operation.", operationName);
        }

        LOGGER.error("The integration has been disabled to prevent further crashes.");
        LOGGER.error("Please report this issue at: {}", GITHUB_ISSUES_URL);
        LOGGER.error("Full stack trace:", error);
        if (error.getCause() != null) {
            LOGGER.error("Root cause: {} - {}", error.getCause().getClass().getName(), error.getCause().getMessage());
        }
        LOGGER.error("========================================");
    }

    /**
     * Report a runtime error from an integration module.
     * Called by integration classes that handle their own try-catch for performance.
     *
     * @param modId         The mod ID
     * @param operationName Name of the operation that failed
     * @param error         The error that occurred
     */
    public static void reportRuntimeError(String modId, String operationName, Throwable error) {
        handleRuntimeError(modId, operationName, error);
    }

    /**
     * Get the version of a mod.
     */
    public static String getModVersion(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
    }

    /**
     * Get ShowerCore mod version.
     */
    public static String getShowerCoreVersion() {
        return getModVersion("showercore");
    }

    /**
     * Get Minecraft version.
     */
    public static String getMinecraftVersion() {
        return FMLLoader.versionInfo().mcVersion();
    }

    /**
     * Log a startup summary of all compat modules.
     */
    private static void logStartupSummary() {
        if (startupSummaryLogged) return;
        startupSummaryLogged = true;

        LOGGER.info("=== ShowerCore Compatibility Summary ===");
        LOGGER.info("ShowerCore version: {}", getShowerCoreVersion());
        LOGGER.info("Minecraft version: {}", getMinecraftVersion());
        LOGGER.info("");

        int enabledCount = 0;
        int disabledCount = 0;
        int notLoadedCount = 0;

        for (CompatInfo compat : REGISTERED_COMPATS.values()) {
            if (!safeCheckLoaded(compat)) {
                LOGGER.info("  [NOT LOADED] {} - mod not present", compat.displayName);
                notLoadedCount++;
            } else if (!compat.enabled) {
                LOGGER.info("  [DISABLED]   {} (v{}) - initialization failed",
                        compat.displayName, compat.modVersion);
                disabledCount++;
            } else if (compat.initialized) {
                LOGGER.info("  [ENABLED]    {} (v{})", compat.displayName, compat.modVersion);
                enabledCount++;
            }
        }

        LOGGER.info("");
        LOGGER.info("Summary: {} enabled, {} disabled, {} not loaded",
                enabledCount, disabledCount, notLoadedCount);

        if (disabledCount > 0) {
            LOGGER.warn("Some integrations failed! Check logs above for details.");
            LOGGER.warn("Report issues at: {}", GITHUB_ISSUES_URL);
        }

        LOGGER.info("======================================");
    }

    /**
     * Check if there are any disabled compats.
     */
    public static boolean hasDisabledCompats() {
        return !DISABLED_COMPATS.isEmpty();
    }

    /**
     * Get all disabled compat names.
     */
    public static Set<String> getDisabledCompatNames() {
        Set<String> names = new HashSet<>();
        for (CompatError error : DISABLED_COMPATS.values()) {
            names.add(error.compatName);
        }
        return names;
    }

    /**
     * Safely check if a compat module's target mod is loaded.
     * Catches any exceptions thrown by the isLoaded supplier.
     *
     * @param compat The compat info to check
     * @return true if the mod is loaded, false otherwise or on error
     */
    private static boolean safeCheckLoaded(CompatInfo compat) {
        try {
            return compat.isLoaded.getAsBoolean();
        } catch (Throwable e) {
            LOGGER.error("Error checking if {} is loaded: {}", compat.displayName, e.getMessage());
            return false;
        }
    }
}
