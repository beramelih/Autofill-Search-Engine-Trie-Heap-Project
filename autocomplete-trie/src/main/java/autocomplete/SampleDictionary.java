package autocomplete;

/**
 * Demo dictionary with diverse words and sample frequencies for prefix autocomplete.
 */
public final class SampleDictionary {

    private SampleDictionary() {
    }

    /** Clears the engine and loads the demo dictionary (~50 words). */
    public static void load(AutocompleteEngine engine) {
        engine.clear();
        String[][] samples = {
                // Common short words
                {"the", "500"},
                {"and", "420"},
                {"for", "380"},
                {"are", "200"},
                {"all", "190"},
                {"any", "85"},
                {"app", "80"},
                {"ask", "75"},
                // "th*" family
                {"there", "220"},
                {"think", "180"},
                {"this", "160"},
                {"that", "155"},
                {"then", "140"},
                {"than", "95"},
                {"three", "88"},
                {"thread", "72"},
                {"thing", "70"},
                {"through", "65"},
                // "da*" family
                {"data", "120"},
                {"database", "95"},
                {"date", "88"},
                {"day", "75"},
                {"dark", "50"},
                {"dash", "45"},
                // "do*" family
                {"dog", "70"},
                {"door", "65"},
                {"down", "60"},
                {"done", "55"},
                // "jav*" family
                {"java", "110"},
                {"javascript", "90"},
                // "str*" family
                {"structure", "76"},
                {"string", "74"},
                {"stream", "70"},
                {"strategy", "68"},
                // CS / DSA terms
                {"tree", "100"},
                {"trie", "85"},
                {"try", "72"},
                {"true", "68"},
                {"type", "64"},
                {"search", "105"},
                {"sort", "80"},
                {"stack", "78"},
                {"algorithm", "98"},
                {"array", "92"},
                {"graph", "86"},
                {"queue", "84"},
                {"heap", "82"},
                {"priority", "79"},
                {"hash", "77"},
                {"list", "73"},
                {"node", "71"},
                {"path", "69"},
                {"join", "55"},
                {"jump", "50"},
                {"just", "48"},
                {"code", "115"},
                {"class", "90"},
                {"compile", "60"},
                {"computer", "58"},
                {"cshgsjghj", "67"}
        };

        for (String[] row : samples) {
            engine.addWord(row[0], Integer.parseInt(row[1]));
        }
    }
}
