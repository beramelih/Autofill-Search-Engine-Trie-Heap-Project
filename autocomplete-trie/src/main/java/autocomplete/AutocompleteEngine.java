package autocomplete;

import java.util.List;

/**
 * Coordinates trie prefix lookup with priority-queue ranking of results.
 */
public class AutocompleteEngine {

    public static final int DEFAULT_SUGGESTION_LIMIT = 10;

    private final Trie trie = new Trie();
    private final SuggestionRanker ranker = new SuggestionRanker();
    private int suggestionLimit = DEFAULT_SUGGESTION_LIMIT;

    public void addWord(String word, int frequency) {
        trie.insert(word.trim().toLowerCase(), frequency);
    }

    public List<WordEntry> getSuggestions(String prefix) {
        String normalized = prefix == null ? "" : prefix.trim().toLowerCase();
        // Only suggest words that extend what the user has typed (prefix autocomplete).
        if (normalized.isEmpty()) {
            return List.of();
        }

        TrieNode node = trie.findNode(normalized);
        if (node == null) {
            return List.of();
        }

        List<WordEntry> matches = trie.collectWords(node, normalized);
        return ranker.topK(matches, suggestionLimit);
    }

    public void clear() {
        trie.clear();
    }

    public boolean isEmpty() {
        return trie.isEmpty();
    }

    public int getSuggestionLimit() {
        return suggestionLimit;
    }

    public void setSuggestionLimit(int suggestionLimit) {
        this.suggestionLimit = Math.max(1, suggestionLimit);
    }
}
