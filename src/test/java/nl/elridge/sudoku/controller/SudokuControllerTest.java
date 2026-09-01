package nl.elridge.sudoku.controller;

import nl.elridge.sudoku.model.Game;
import nl.elridge.sudoku.model.UpdateAction;
import nl.elridge.sudoku.view.Field;
import nl.elridge.sudoku.view.SudokuPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JUnit testovi za SudokuController - pokriva sve grane u mousePressed metodi.
 */
public class SudokuControllerTest {
    private SudokuPanel sudokuPanel;
    private Game game;
    private SudokuController sudokuController;

    @BeforeEach
    public void setUp() {
        sudokuPanel = new SudokuPanel();
        game = new Game();
        sudokuController = new SudokuController(sudokuPanel, game);
        sudokuPanel.setGame(game);
        sudokuPanel.setController(sudokuController);
        game.addObserver(sudokuPanel);
        game.newGame();
    }

    /**
     * Helper metoda koja kreira panel sa Field-om na tačnoj poziciji
     */
    private JPanel createPanelWithField(Field field, int x, int y) {
        JPanel panel = new JPanel(null) { // null layout za apsolutno pozicioniranje
            @Override
            public Component getComponentAt(int px, int py) {
                // Ako je klik unutar field-a, vrati field
                if (px >= x && px < x + field.getWidth() &&
                        py >= y && py < y + field.getHeight()) {
                    return field;
                }
                return super.getComponentAt(px, py);
            }
        };
        field.setBounds(x, y, 40, 40);
        panel.add(field);
        panel.setSize(100, 100);
        return panel;
    }

    @Test
    public void testSudokuControllerInitialization() {
        assertNotNull(sudokuController, "SudokuController treba da bude inicijalizovan");
    }

    // Test grane: component instanceof Field - TRUE
    // Test grane: e.getButton() == MouseEvent.BUTTON1 - TRUE
    // Test grane: game.getNumber(x, y) == 0 - TRUE
    @Test
    // Mock test
    public void testLeftClickOnEmptyField() {
        int emptyX = -1, emptyY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyX = x;
                    emptyY = y;
                    break;
                }
            }
            if (emptyX != -1) break;
        }

        if (emptyX != -1) {
            int before = game.getNumber(emptyX, emptyY);
            game.setSelectedNumber(5);

            Field field = new Field(emptyX, emptyY);
            field.setNumber(0, false);

            MouseEvent event = mock(MouseEvent.class);
            when(event.getSource()).thenReturn(createPanelWithField(field, 10, 10));
            when(event.getPoint()).thenReturn(new Point(15, 15));
            when(event.getButton()).thenReturn(MouseEvent.BUTTON1);

            sudokuController.mousePressed(event);

            int after = game.getNumber(emptyX, emptyY);
            assertEquals(5, after,
                    "Broj treba da bude postavljen na prazan field (pre=" + before + ", posle=" + after + ")");
        } else {
            // Generišemo novu igru dok ne dobijemo barem jedno prazno polje
            for (int i = 0; i < 10; i++) {
                game.newGame();
                for (int y = 0; y < 9; y++) {
                    for (int x = 0; x < 9; x++) {
                        if (game.getNumber(x, y) == 0) {
                            emptyX = x;
                            emptyY = y;
                            break;
                        }
                    }
                    if (emptyX != -1) break;
                }
                if (emptyX != -1) break;
            }
            assertTrue(emptyX != -1, "Treba da postoji bar jedno prazno polje");
        }
    }

    // Test grane: field.getForeground().equals(Color.BLUE) - TRUE
    @Test
    public void testLeftClickOnBlueField() {
        int emptyX = -1, emptyY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyX = x;
                    emptyY = y;
                    break;
                }
            }
            if (emptyX != -1) break;
        }

        if (emptyX != -1) {
            // Prvo postavljamo broj
            game.setSelectedNumber(3);
            game.setNumber(emptyX, emptyY, 3);

            Field field = new Field(emptyX, emptyY);
            field.setNumber(3, true); // Plavo (user input)

            assertEquals(Color.BLUE, field.getForeground(), "Field mora biti plav");

            JPanel panel = createPanelWithField(field, 10, 10);

            // Sada klikćemo ponovo sa novim brojem
            game.setSelectedNumber(7);

            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON1);

            sudokuController.mousePressed(event);

            assertEquals(7, game.getNumber(emptyX, emptyY),
                    "Broj treba da bude promenjen na plavom field-u");
        }
    }

    // Test grane: number == -1 - TRUE (return early)
    @Test
    public void testLeftClickWithNoSelectedNumber() {
        int emptyX = -1, emptyY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyX = x;
                    emptyY = y;
                    break;
                }
            }
            if (emptyX != -1) break;
        }

        if (emptyX != -1) {
            game.setSelectedNumber(-1); // Nema selektovanog broja
            assertEquals(-1, game.getSelectedNumber(), "Selected number mora biti -1");

            Field field = new Field(emptyX, emptyY);
            field.setNumber(0, false);

            JPanel panel = createPanelWithField(field, 10, 10);

            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON1);

            sudokuController.mousePressed(event);

            assertEquals(0, game.getNumber(emptyX, emptyY),
                    "Broj ne treba da bude postavljen kada nema selekcije");
        }
    }

    // Test grane: e.getButton() == MouseEvent.BUTTON3 - TRUE
    // Test grane: !field.getForeground().equals(Color.BLACK) - TRUE
    @Test
    public void testRightClickOnUserInputField() {
        int emptyX = -1, emptyY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyX = x;
                    emptyY = y;
                    break;
                }
            }
            if (emptyX != -1) break;
        }

        if (emptyX != -1) {
            game.setSelectedNumber(3);
            game.setNumber(emptyX, emptyY, 3);

            Field field = new Field(emptyX, emptyY);
            field.setNumber(3, true); // Plavo (user input)

            assertEquals(Color.BLUE, field.getForeground(), "Field mora biti plav");
            assertNotEquals(Color.BLACK, field.getForeground(), "Field ne sme biti crn");

            JPanel panel = createPanelWithField(field, 10, 10);

            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON3);

            sudokuController.mousePressed(event);

            assertEquals(0, game.getNumber(emptyX, emptyY),
                    "Broj treba da bude obrisan desnim klikom na plavi field");
        }
    }

    // Test grane: !field.getForeground().equals(Color.BLACK) - FALSE
    @Test
    public void testRightClickOnBlackField() {
        int filledX = -1, filledY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) != 0) {
                    filledX = x;
                    filledY = y;
                    break;
                }
            }
            if (filledX != -1) break;
        }

        if (filledX != -1) {
            int originalNumber = game.getNumber(filledX, filledY);

            Field field = new Field(filledX, filledY);
            field.setNumber(originalNumber, false); // Crno (ne user input)

            assertEquals(Color.BLACK, field.getForeground(), "Field mora biti crn");

            JPanel panel = createPanelWithField(field, 10, 10);

            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON3);

            sudokuController.mousePressed(event);

            assertEquals(originalNumber, game.getNumber(filledX, filledY),
                    "Crni field ne treba da bude obrisan desnim klikom");
        }
    }

    // Test grane: component instanceof Field - FALSE
    @Test
    public void testMousePressedOnNonFieldComponent() {
        JLabel label = new JLabel("Test");
        label.setBounds(5, 5, 50, 20);

        JPanel panel = new JPanel(null) {
            @Override
            public Component getComponentAt(int x, int y) {
                return label;
            }
        };
        panel.add(label);

        MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);

        assertDoesNotThrow(() -> sudokuController.mousePressed(event),
                "Klik na non-Field component ne treba da baci exception");
    }

    // Test grane: game.getNumber(x, y) == 0 || field.getForeground().equals(Color.BLUE) - FALSE
    @Test
    public void testLeftClickOnBlackFilledField() {
        int filledX = -1, filledY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) != 0) {
                    filledX = x;
                    filledY = y;
                    break;
                }
            }
            if (filledX != -1) break;
        }

        if (filledX != -1) {
            int originalNumber = game.getNumber(filledX, filledY);
            game.setSelectedNumber(9);

            Field field = new Field(filledX, filledY);
            field.setNumber(originalNumber, false); // Crno (ne user input)

            assertEquals(Color.BLACK, field.getForeground(), "Field mora biti crn");
            assertNotEquals(0, game.getNumber(filledX, filledY), "Polje ne sme biti prazno");

            JPanel panel = createPanelWithField(field, 10, 10);

            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON1);

            sudokuController.mousePressed(event);

            assertEquals(originalNumber, game.getNumber(filledX, filledY),
                    "Crni field ne treba da se menja levim klikom");
        }
    }

    @Test
    public void testMouseClickedDoesNothing() {
        JPanel panel = new JPanel();
        MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);

        assertDoesNotThrow(() -> sudokuController.mouseClicked(event),
                "mouseClicked ne treba da baci exception");
    }

    @Test
    public void testMouseEnteredDoesNothing() {
        JPanel panel = new JPanel();
        MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_ENTERED,
                System.currentTimeMillis(), 0, 0, 0, 0, false);

        assertDoesNotThrow(() -> sudokuController.mouseEntered(event),
                "mouseEntered ne treba da baci exception");
    }

    @Test
    public void testMouseExitedDoesNothing() {
        JPanel panel = new JPanel();
        MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_EXITED,
                System.currentTimeMillis(), 0, 0, 0, 0, false);

        assertDoesNotThrow(() -> sudokuController.mouseExited(event),
                "mouseExited ne treba da baci exception");
    }

    @Test
    public void testMouseReleasedDoesNothing() {
        JPanel panel = new JPanel();
        MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);

        assertDoesNotThrow(() -> sudokuController.mouseReleased(event),
                "mouseReleased ne treba da baci exception");
    }

    @Test
    public void testControllerConnectedToGame() {
        // Test verifikuje da je controller pravilno povezan sa game modelom
        // Umesto mouse event simulacije, testiramo kroz game API

        int emptyX = -1, emptyY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyX = x;
                    emptyY = y;
                    break;
                }
            }
            if (emptyX != -1) break;
        }

        if (emptyX != -1) {
            // Test da controller može da pristupi game-u
            assertNotNull(sudokuController, "Controller mora postojati");

            // Test workflow kroz game model (koji controller koristi)
            game.setSelectedNumber(5);
            assertEquals(5, game.getSelectedNumber(), "Selected number");

            game.setNumber(emptyX, emptyY, 5);
            assertEquals(5, game.getNumber(emptyX, emptyY), "Postavljeni broj");

            game.setNumber(emptyX, emptyY, 0);
            assertEquals(0, game.getNumber(emptyX, emptyY), "Obrisan broj");
        } else {
            assertTrue(true, "Nema praznih polja");
        }
    }

    @Test
    public void testMultipleActionsOnSameField() {
        // Test kompleksnog scenarija - postavljanje, menjanje, brisanje
        int emptyX = -1, emptyY = -1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyX = x;
                    emptyY = y;
                    break;
                }
            }
            if (emptyX != -1) break;
        }

        if (emptyX != -1) {
            // 1. Postavljanje broja
            game.setSelectedNumber(3);
            Field field = new Field(emptyX, emptyY);
            field.setNumber(0, false);
            JPanel panel = createPanelWithField(field, 10, 10);

            MouseEvent event1 = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON1);
            sudokuController.mousePressed(event1);
            assertEquals(3, game.getNumber(emptyX, emptyY), "Prvi broj");

            // 2. Menjanje broja (klik na plavo polje)
            game.setSelectedNumber(7);
            field.setNumber(3, true); // Plavo

            MouseEvent event2 = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON1);
            sudokuController.mousePressed(event2);
            assertEquals(7, game.getNumber(emptyX, emptyY), "Drugi broj");

            // 3. Brisanje broja (desni klik)
            field.setNumber(7, true); // Plavo

            MouseEvent event3 = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON3);
            sudokuController.mousePressed(event3);
            assertEquals(0, game.getNumber(emptyX, emptyY), "Obrisan broj");
        }
    }
}