package mod.crabmod.showercore.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
