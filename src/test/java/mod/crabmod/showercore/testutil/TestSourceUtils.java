package mod.crabmod.showercore.testutil;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class TestSourceUtils {

    private TestSourceUtils() {}

    public static String readSource(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            fail(path.getFileName() + " not found at " + path.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        return Files.readString(path);
    }

    public static String normalize(String s) {
        return s.replace("\r\n", "\n");
    }

    public static Path resolveAgainstCwdAncestors(Path relative, int maxAncestors) {
        if (Files.isDirectory(relative)) return relative;
        Path cwd = Paths.get("").toAbsolutePath();
        for (int i = 0; i < maxAncestors; i++) {
            Path candidate = cwd.resolve(relative);
            if (Files.isDirectory(candidate)) return candidate;
            if (cwd.getParent() == null) break;
            cwd = cwd.getParent();
        }
        return relative.toAbsolutePath();
    }

    public static List<Path> listByGlob(Path dir, String glob) throws IOException {
        List<Path> out = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, glob)) {
            for (Path p : stream) out.add(p);
        }
        if (out.isEmpty()) {
            fail("No files matching '" + glob + "' under " + dir.toAbsolutePath());
        }
        return out;
    }

    public static String extractMethodBody(String source, Pattern sig, String humanName) {
        Matcher m = sig.matcher(source);
        if (!m.find()) {
            fail("Could not locate signature for '" + humanName + "' (pattern: " + sig.pattern() + ")");
        }
        int parenOpen = source.indexOf('(', m.start());
        int pDepth = 1;
        int j = parenOpen + 1;
        while (j < source.length() && pDepth > 0) {
            char c = source.charAt(j);
            if (c == '(') pDepth++;
            else if (c == ')') pDepth--;
            j++;
        }
        int openBrace = source.indexOf('{', j);
        assertNotEquals(-1, openBrace, "Signature for '" + humanName + "' has no opening body brace");

        int depth = 1;
        int i = openBrace + 1;
        while (i < source.length() && depth > 0) {
            char c = source.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            i++;
        }
        return source.substring(openBrace + 1, i - 1);
    }
}
