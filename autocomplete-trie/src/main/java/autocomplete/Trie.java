package autocomplete;

import java.util.ArrayList;
import java.util.List;


public class Trie {

    private final TrieNode root = new TrieNode();


    public void insert(String word, int frequency) {
        if (word == null || word.isEmpty() || frequency < 0) {
            return;
        }

        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.getOrCreateChild(ch);
        }

        if (current.isEndOfWord()) {
            current.setFrequency(current.getFrequency() + frequency);
        } else {
            current.setEndOfWord(true);
            current.setFrequency(frequency);
        }
    }

   
    public TrieNode findNode(String prefix) {
        if (prefix == null) {
            return root;
        }

        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            TrieNode next = current.getChild(ch);
            if (next == null) {
                return null;
            }
            current = next;
        }
        return current;
    }

 
    public List<WordEntry> collectWords(TrieNode start, String prefixSoFar) {
        List<WordEntry> results = new ArrayList<>();
        collectWordsRecursive(start, prefixSoFar, results);
        return results;
    }

    private void collectWordsRecursive(TrieNode node, String prefix, List<WordEntry> results) {
        if (node.isEndOfWord()) {
            results.add(new WordEntry(prefix, node.getFrequency()));
        }

        for (var entry : node.getChildren().entrySet()) {
            char ch = entry.getKey();
            collectWordsRecursive(entry.getValue(), prefix + ch, results);
        }
    }

    public void clear() {
        root.getChildren().clear();
        root.setEndOfWord(false);
        root.setFrequency(0);
    }

    public boolean isEmpty() {
        return root.getChildren().isEmpty() && !root.isEndOfWord();
    }
}
