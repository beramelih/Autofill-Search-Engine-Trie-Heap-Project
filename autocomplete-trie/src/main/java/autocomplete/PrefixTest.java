package autocomplete;

import java.util.List;
import java.util.stream.Collectors;

// Headless checks that prefix autocomplete works for arbitrary prefixes.

public final class PrefixTest {

    private PrefixTest() {
    }

    public static void main(String[] args) {
        AutocompleteEngine engine = new AutocompleteEngine();
        SampleDictionary.load(engine);

        check(engine, "", List.of());
        check(engine, "th", List.of("the", "there", "think"));
        check(engine, "da", List.of("data", "database", "date", "day"));
        check(engine, "jav", List.of("java", "javascript"));
        check(engine, "str", List.of("structure", "string", "stream"));
        check(engine, "xyz", List.of());

        System.out.println("All prefix tests passed.");
    }

    private static void check(AutocompleteEngine engine, String prefix, List<String> mustInclude) {
        List<String> words = engine.getSuggestions(prefix).stream()
                .map(WordEntry::getWord)
                .collect(Collectors.toList());

        if (prefix.isEmpty()) {
            if (!words.isEmpty()) {
                fail(prefix, "expected empty list, got " + words);
            }
            System.out.println("[OK] \"\" -> (empty)");
            return;
        }

        for (String required : mustInclude) {
            if (!words.contains(required)) {
                fail(prefix, "missing \"" + required + "\" in " + words);
            }
        }

        for (String word : words) {
            if (!word.startsWith(prefix)) {
                fail(prefix, "word \"" + word + "\" does not start with prefix");
            }
        }

        System.out.println("[OK] \"" + prefix + "\" -> " + words);
    }

    private static void fail(String prefix, String message) {
        System.err.println("FAIL prefix=\"" + prefix + "\": " + message);
        System.exit(1);
    }
}
