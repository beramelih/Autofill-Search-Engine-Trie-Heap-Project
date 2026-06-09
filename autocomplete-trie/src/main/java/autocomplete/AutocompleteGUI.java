package autocomplete;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Swing desktop UI for the autocomplete engine.
 */
public class AutocompleteGUI extends JFrame {

    private final AutocompleteEngine engine = new AutocompleteEngine();
    private final JTextField searchField = new JTextField(28);
    private final DefaultListModel<String> suggestionModel = new DefaultListModel<>();
    private final JList<String> suggestionList = new JList<>(suggestionModel);
    private final JLabel statusLabel = new JLabel("Type a prefix (e.g. \"th\") to see matching words below.");
    private final JScrollPane suggestionScroll = new JScrollPane(suggestionList);

    /** True while the list model is being rebuilt — ignore stray selection events. */
    private boolean refreshingList;

    public AutocompleteGUI() {
        super("Autocomplete — Trie + Priority Queue");
        buildLayout();
        wireEvents();
        SampleDictionary.load(engine);
        refreshSuggestions();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(8, 8));

        JPanel north = new JPanel(new BorderLayout(6, 6));
        north.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(searchLabel.getFont().deriveFont(Font.BOLD));
        north.add(searchLabel, BorderLayout.NORTH);
        north.add(searchField, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        JLabel listLabel = new JLabel("Completions for your prefix (ranked, top "
                + engine.getSuggestionLimit() + "):");
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        suggestionScroll.setPreferredSize(new Dimension(360, 220));
        center.add(listLabel, BorderLayout.NORTH);
        center.add(suggestionScroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        south.add(buildButtonPanel(), BorderLayout.NORTH);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        south.add(statusLabel, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildButtonPanel() {
        JButton addWordButton = new JButton("Add word");
        JButton loadSamplesButton = new JButton("Load samples");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.add(addWordButton);
        buttons.add(loadSamplesButton);
        buttons.add(clearButton);

        addWordButton.addActionListener(e -> onAddWord());
        loadSamplesButton.addActionListener(e -> onLoadSamples());
        clearButton.addActionListener(e -> onClear());

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        wrapper.add(buttons);
        return wrapper;
    }

    private void wireEvents() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshSuggestions();
            }
        });

        // Apply completion only on explicit user action — not when the list refreshes.
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    applySelectedSuggestion();
                }
            }
        });

        suggestionList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    applySelectedSuggestion();
                }
            }
        });
    }

    private void applySelectedSuggestion() {
        if (refreshingList) {
            return;
        }
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            searchField.setText(extractWord(selected));
            searchField.setCaretPosition(searchField.getText().length());
            searchField.requestFocusInWindow();
        }
    }

    private void refreshSuggestions() {
        String prefix = searchField.getText();
        List<WordEntry> suggestions = engine.getSuggestions(prefix);

        refreshingList = true;
        try {
            suggestionList.clearSelection();
            suggestionModel.clear();
            for (WordEntry entry : suggestions) {
                suggestionModel.addElement(formatSuggestion(entry));
            }
        } finally {
            refreshingList = false;
        }

        updateStatus(prefix, suggestions);
    }

    private void updateStatus(String prefix, List<WordEntry> suggestions) {
        if (prefix.isBlank()) {
            statusLabel.setText(engine.isEmpty()
                    ? "Dictionary is empty. Load samples or add words."
                    : "Type a prefix (e.g. \"th\") to see matching words below.");
        } else if (suggestions.isEmpty()) {
            statusLabel.setText("No matches for prefix \"" + prefix.trim() + "\".");
        } else {
            statusLabel.setText("Matches for \"" + prefix.trim() + "\" — click a word to complete.");
        }
    }

    private static String formatSuggestion(WordEntry entry) {
        return String.format("%-18s freq: %d", entry.getWord(), entry.getFrequency());
    }

    private static String extractWord(String listLine) {
        return listLine.trim().split("\\s+")[0];
    }

    private void onAddWord() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 6, 6));
        JTextField wordField = new JTextField();
        JTextField freqField = new JTextField("10");
        panel.add(new JLabel("Word:"));
        panel.add(wordField);
        panel.add(new JLabel("Frequency:"));
        panel.add(freqField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add word to dictionary",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String word = wordField.getText().trim();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Word cannot be empty.", "Input error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int frequency = Integer.parseInt(freqField.getText().trim());
            if (frequency < 0) {
                throw new NumberFormatException();
            }
            engine.addWord(word, frequency);
            refreshSuggestions();
            statusLabel.setText("Added \"" + word.toLowerCase() + "\" with frequency " + frequency + ".");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Frequency must be a non-negative integer.", "Input error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onLoadSamples() {
        SampleDictionary.load(engine);
        refreshSuggestions();
        statusLabel.setText("Sample dictionary loaded. Type any prefix to see matches.");
    }

    private void onClear() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Remove all words from the dictionary?",
                "Clear dictionary",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            engine.clear();
            searchField.setText("");
            refreshSuggestions();
            statusLabel.setText("Dictionary cleared.");
        }
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> new AutocompleteGUI().setVisible(true));
    }
}
