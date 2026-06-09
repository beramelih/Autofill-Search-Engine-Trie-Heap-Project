package autocomplete;

/**
 * A dictionary word paired with its frequency (used for ranking).
 */
public class WordEntry implements Comparable<WordEntry> {

    private final String word;
    private final int frequency;

    public WordEntry(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(WordEntry other) {
        int byFrequency = Integer.compare(other.frequency, this.frequency);
        if (byFrequency != 0) {
            return byFrequency;
        }
        return this.word.compareToIgnoreCase(other.word);
    }

    @Override
    public String toString() {
        return word + " (" + frequency + ")";
    }
}
