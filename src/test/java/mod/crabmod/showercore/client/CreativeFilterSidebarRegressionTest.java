package mod.crabmod.showercore.client;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreativeFilterSidebarRegressionTest {
    private static final Path SHOWER_CORE_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "ShowerCore.java");
    private static final Path FILTER_EVENT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "client", "event",
            "CreativeFilterScreenEvents.java");
    private static final Path TAG_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "common",
            "ShowerCoreItemTags.java");
    private static final Path TAG_BUTTON_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "client", "gui",
            "widget", "CreativeFilterTagButton.java");
    private static final Path ICON_BUTTON_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "client", "gui",
            "widget", "CreativeFilterIconButton.java");
    private static final Path TAG_ROOT = Paths.get(
            "src", "main", "resources", "data", "showercore", "tags", "items", "creative");

    @Test
    @DisplayName("Client setup registers the in-tab creative filter sidebar")
    void clientSetupRegistersCreativeFilterSidebar() throws IOException {
        String source = TestSourceUtils.readSource(SHOWER_CORE_SOURCE);

        assertTrue(source.contains("MinecraftForge.EVENT_BUS.register(new CreativeFilterScreenEvents())"),
                "The client setup must register CreativeFilterScreenEvents so ShowerCore items are "
                        + "filtered inside the existing Hot Bath creative tab.");
    }

    @Test
    @DisplayName("Creative filter stays scoped to the Hot Bath tab and preserves host-tab items")
    void filterTargetsHotBathTabAndPreservesHostItems() throws IOException {
        String source = TestSourceUtils.readSource(FILTER_EVENT_SOURCE);

        assertTrue(source.contains("ItemGroup.HOT_BATH_TAB.get()"),
                "The sidebar must attach to Hot Bath's existing creative tab, not a new ShowerCore tab.");
        assertTrue(source.contains("captureBaseTabItems"),
                "The filter must snapshot the original Hot Bath tab contents before replacing the menu.");
        assertTrue(source.contains("!showerCoreItems.contains(stack.getItem())"),
                "Hot Bath or third-party items in the same tab must remain visible while ShowerCore "
                        + "categories are filtered.");
        assertTrue(source.contains("menu.items.clear()") && source.contains("menu.items.addAll(newItems)"),
                "Changing category buttons must rebuild the current creative menu items.");
    }

    @Test
    @DisplayName("Creative filter sidebar uses MrCrayfish-style side tabs and icon controls")
    void sidebarUsesSideTabsAndIconControls() throws IOException {
        String eventSource = TestSourceUtils.readSource(FILTER_EVENT_SOURCE);
        String tagButtonSource = TestSourceUtils.readSource(TAG_BUTTON_SOURCE);
        String iconButtonSource = TestSourceUtils.readSource(ICON_BUTTON_SOURCE);

        assertTrue(eventSource.contains("new CreativeFilterIconButton("),
                "Filter controls should be icon buttons, not text buttons.");
        for (String icon : List.of("SCROLL_UP", "SCROLL_DOWN", "ENABLE_ALL", "DISABLE_ALL")) {
            assertTrue(eventSource.contains("CreativeFilterIconButton.Icon." + icon),
                    "Missing filter control icon " + icon);
        }
        assertFalse(eventSource.contains("Button.builder"));
        assertFalse(eventSource.contains("Component.literal(\"^\")"));
        assertFalse(eventSource.contains("Component.literal(\"v\")"));
        assertFalse(eventSource.contains("Component.literal(\"+\")"));
        assertFalse(eventSource.contains("Component.literal(\"-\")"));
        assertTrue(eventSource.contains("this.guiLeft - 28"));
        assertTrue(eventSource.contains("this.guiTop + 29 * (i - startIndex) + 10"));
        assertTrue(eventSource.contains("this.guiLeft - 22"));
        assertTrue(eventSource.contains("this.guiTop - 12"));
        assertTrue(eventSource.contains("this.guiTop + 127"));
        assertTrue(eventSource.contains("this.guiLeft - 50"));
        assertTrue(eventSource.contains("this.guiTop + 32"));

        assertTrue(tagButtonSource.contains("SELECTED_WIDTH = 32"));
        assertTrue(tagButtonSource.contains("UNSELECTED_WIDTH = 28"));
        assertTrue(tagButtonSource.contains("TAB_HEIGHT = 28"));
        assertTrue(tagButtonSource.contains("super(x, y, SELECTED_WIDTH, TAB_HEIGHT"));
        assertTrue(tagButtonSource.contains("this.toggled ? SELECTED_WIDTH : UNSELECTED_WIDTH"));
        assertTrue(tagButtonSource.contains("graphics.renderItem(this.icon, x + 8, y + 6)"));
        assertFalse(tagButtonSource.contains("tab_top_selected"));
        assertFalse(tagButtonSource.contains("blitSprite"));

        assertTrue(iconButtonSource.contains("super(x, y, 20, 20, CommonComponents.EMPTY"));
        assertTrue(iconButtonSource.contains("drawChevronUp"));
        assertTrue(iconButtonSource.contains("drawChevronDown"));
        assertTrue(iconButtonSource.contains("drawPlus"));
        assertTrue(iconButtonSource.contains("drawMinus"));
    }

    @Test
    @DisplayName("All five creative filter item tags are declared")
    void creativeFilterTagsDeclared() throws IOException {
        String source = TestSourceUtils.readSource(TAG_SOURCE);

        for (String tag : List.of(
                "creative/bath_cores",
                "creative/shower_heads",
                "creative/bathtubs",
                "creative/clawfoot_bathtubs",
                "creative/accessories")) {
            assertTrue(source.contains("\"" + tag + "\""),
                    "Missing ShowerCore item tag declaration for " + tag);
        }
    }

    @Test
    @DisplayName("Creative filter tag resources cover every current ShowerCore group")
    void tagResourcesCoverCurrentGroups() throws IOException {
        assertCategory("bath_cores", 6, id -> id.endsWith("_core"));
        assertCategory("shower_heads", 66, id -> id.contains("shower_head"));
        assertCategory("bathtubs", 33,
                id -> id.startsWith("bathtub_") && !id.startsWith("bathtub_clawfoot_"));
        assertCategory("clawfoot_bathtubs", 33, id -> id.startsWith("bathtub_clawfoot_"));
        assertCategory("accessories", 1, id -> id.equals("rubber_duck"));

        Set<String> legacy = readTagValues(TAG_ROOT.resolve("bathtubs.json"));
        Set<String> clawfoot = readTagValues(TAG_ROOT.resolve("clawfoot_bathtubs.json"));
        Set<String> overlap = new LinkedHashSet<>(legacy);
        overlap.retainAll(clawfoot);
        assertTrue(overlap.isEmpty(), "Legacy and clawfoot bathtub filters must not overlap: " + overlap);
    }

    private static void assertCategory(String name, int expectedCount, java.util.function.Predicate<String> rule)
            throws IOException {
        Set<String> values = readTagValues(TAG_ROOT.resolve(name + ".json"));
        assertEquals(expectedCount, values.size(), "Unexpected item count in " + name + " creative filter tag.");
        assertFalse(values.isEmpty(), name + " tag must not be empty.");
        for (String value : values) {
            assertTrue(rule.test(value), "Unexpected item '" + value + "' in " + name + " creative filter tag.");
        }
    }

    private static Set<String> readTagValues(Path path) throws IOException {
        String source = TestSourceUtils.readSource(path);
        Matcher matcher = Pattern.compile("\"showercore:([^\"]+)\"").matcher(source);
        Set<String> values = new LinkedHashSet<>();
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }
}
