package autocomplete;

import java.util.HashMap;
import java.util.Map;



public class TrieNode {

    private final Map<Character, TrieNode> children = new HashMap<>();
    private boolean endOfWord;
    private int frequency;

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return endOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        this.endOfWord = endOfWord;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public TrieNode getChild(char ch) {
        return children.get(ch);
    }

    public TrieNode getOrCreateChild(char ch) {
        return children.computeIfAbsent(ch, ignored -> new TrieNode());
    }
}
