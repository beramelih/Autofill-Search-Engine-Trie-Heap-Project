package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

//Ranks autocomplete candidates with a max-heap so the highest-frequency words are retrieved first.

public class SuggestionRanker {

    /**
     * Returns up to words ordered by descending frequency.
     * Ties are broken alphabetically (case-insensitive).
     */
    
    public List<WordEntry> topK(Collection<WordEntry> candidates, int limit) {
        if (candidates == null || candidates.isEmpty() || limit <= 0) {
            return List.of();
        }

        // Max-heap: WordEntry.compareTo orders higher frequency first.
        PriorityQueue<WordEntry> maxHeap = new PriorityQueue<>(candidates);

        List<WordEntry> ranked = new ArrayList<>(Math.min(limit, maxHeap.size()));
        while (!maxHeap.isEmpty() && ranked.size() < limit) {
            ranked.add(maxHeap.poll());
        }
        return ranked;
    }
}
