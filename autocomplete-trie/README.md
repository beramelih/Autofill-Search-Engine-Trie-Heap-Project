# Autocomplete Trie (DSA2 Assignment)

Desktop autocomplete search engine built with a **Trie** for prefix lookup and a **Priority Queue (max-heap)** for ranking suggestions by frequency.

## Requirements

- Java 17 or newer
- [Apache Maven](https://maven.apache.org/) (recommended) or `javac` / `java` on the classpath

## Compile and run

### Maven (recommended)

```bash
cd /Users/bera/Projects/autocomplete-trie
mvn -q compile exec:java
```

Or build a runnable JAR:

```bash
mvn -q package
java -jar target/autocomplete-trie-1.0.0.jar
```

### Without Maven

```bash
cd /Users/bera/Projects/autocomplete-trie
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out autocomplete.Main
```

## Prefix autocomplete (any prefix)

Autocomplete uses **standard trie prefix lookup** — there is no special-casing for particular strings. For **any** non-empty prefix you type (e.g. `a`, `da`, `jav`, `str`, `th`), the engine:

1. Walks the trie to the node for that prefix (or returns no matches if the prefix is absent).
2. Collects every dictionary word **starting with** that prefix.
3. Ranks matches by frequency with a max-heap and shows the top **10**.

An **empty** search field shows no suggestions (only a status hint) so the list always reflects what you have typed.

### Headless prefix checks

```bash
cd /Users/bera/Projects/autocomplete-trie
javac -d out $(find src/main/java -name '*.java')
java -cp out autocomplete.PrefixTest
```

## GUI usage

1. **Search field** — type any prefix; the list updates on every keystroke.
2. **Ranked suggestions** — words that **start with** your prefix, highest frequency first (up to 10).
3. **Click a suggestion** (or press Enter in the list) — fills the search field; typing is never overwritten by list refresh.
4. **Add word** — insert a custom word and frequency into the trie.
5. **Load samples** — clear and reload ~50 demo words.
6. **Clear** — empty the dictionary.

Sample data is loaded automatically on startup.

## Project structure

| File | Role |
|------|------|
| `TrieNode.java` | Trie node: child map, end-of-word flag, frequency |
| `Trie.java` | Insert words, find prefix node, collect subtree words |
| `WordEntry.java` | Word + frequency; defines max-heap ordering |
| `SuggestionRanker.java` | `PriorityQueue` max-heap for top-K ranking |
| `AutocompleteEngine.java` | Connects trie lookup to heap ranking |
| `AutocompleteGUI.java` | Swing UI |
| `SampleDictionary.java` | Demo dictionary |
| `Main.java` | Starts GUI on EDT |

---

## How the Trie works

A trie stores characters along paths from a root. Each node has:

- A **map of children** (`char → TrieNode`)
- An **end-of-word** flag
- A **frequency** at terminal nodes (how often the word is “used” / ranked)

**Insert:** walk from the root following each character, creating nodes as needed; mark the last node as end-of-word and set/add frequency.

**Prefix search:** follow the prefix; if any character is missing, there are no matches. Otherwise, **DFS** from that node collects every complete word in the subtree (building full strings as we go).

This gives efficient prefix filtering: cost is proportional to prefix length plus the number of matching words, not the entire dictionary size for each character typed.

---

## How the Priority Queue ranks suggestions

After the trie returns all words under the current prefix, `SuggestionRanker` places them in a **`PriorityQueue<WordEntry>`** configured as a **max-heap**:

- `WordEntry.compareTo` orders by **higher frequency first**
- Ties break **alphabetically** (case-insensitive)

Java’s `PriorityQueue` is a min-heap by default; here `compareTo` inverts frequency so the head of the queue is the **best** suggestion. Polling repeatedly yields the top **N** suggestions (default 10).

Why a heap? For large candidate sets, a heap supports **top-K** extraction without sorting the full list. For this assignment’s dictionary size, the main goal is to demonstrate the **data structure** and a clear separation: trie = **what matches**, heap = **what order to show**.

---

## GUI integration flow

```
User types in JTextField
    → DocumentListener fires
    → AutocompleteEngine.getSuggestions(prefix)
        → Trie.findNode(prefix) + collectWords(...)
        → SuggestionRanker.topK(matches, 10)
    → JList model updated with "word    freq: N" lines
User clicks list item (or Enter on list)
    → search field set to selected word
(List refresh clears selection so typing is not overwritten.)
```

`Main` uses `SwingUtilities.invokeLater` so all UI work runs on the **Event Dispatch Thread**, as required for Swing.

---

## Assignment checklist

- [x] Trie with insert and prefix search
- [x] Frequency-based ranking
- [x] `PriorityQueue` (max-heap) central to ranking
- [x] Swing desktop GUI
- [x] Sample dictionary (~50 words)
- [x] Prefix autocomplete for any typed prefix
- [x] README with compile/run and concept explanations
