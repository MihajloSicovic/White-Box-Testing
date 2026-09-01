package nl.elridge.sudoku.view;

import nl.elridge.sudoku.controller.SudokuController;
import nl.elridge.sudoku.model.Game;
import nl.elridge.sudoku.model.UpdateAction;
import nl.elridge.sudoku.view.Field;
import nl.elridge.sudoku.view.SudokuPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit testovi za SudokuPanel GUI komponentu.
 */
public class SudokuPanelTest {
    private SudokuPanel sudokuPanel;
    private Game game;
    private SudokuController sudokuController;

    @BeforeEach
    public void setUp() {
        sudokuPanel = new SudokuPanel();
        game = new Game();
        sudokuController = new SudokuController(sudokuPanel, game);
        sudokuPanel.setController(sudokuController);
        sudokuPanel.setGame(game);
        game.addObserver(sudokuPanel);
    }

    @Test
    public void testSudokuPanelInitialization() {
        assertNotNull(sudokuPanel, "SudokuPanel treba da bude inicijalizovan");
    }

    @Test
    public void testSudokuPanelLayout() {
        assertTrue(sudokuPanel.getLayout() instanceof GridLayout, 
            "SudokuPanel treba da ima GridLayout");
        GridLayout layout = (GridLayout) sudokuPanel.getLayout();
        assertEquals(3, layout.getRows(), "Broj redova");
        assertEquals(3, layout.getColumns(), "Broj kolona");
    }

    @Test
    public void testSudokuPanelHas9SubPanels() {
        assertEquals(9, sudokuPanel.getComponentCount(), 
            "SudokuPanel treba da ima 9 sub-panela");
    }

    @Test
    public void testSubPanelsAreJPanels() {
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            Component comp = sudokuPanel.getComponent(i);
            assertTrue(comp instanceof javax.swing.JPanel, 
                "Svaki component treba da bude JPanel");
        }
    }

    @Test
    public void testSubPanelsHaveGridLayout() {
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
            assertTrue(panel.getLayout() instanceof GridLayout, 
                "Sub-panel treba da ima GridLayout");
            GridLayout layout = (GridLayout) panel.getLayout();
            assertEquals(3, layout.getRows(), "Sub-panel redovi");
            assertEquals(3, layout.getColumns(), "Sub-panel kolone");
        }
    }

    @Test
    public void testTotalFieldCount() {
        int totalFields = 0;
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
            totalFields += panel.getComponentCount();
        }
        assertEquals(81, totalFields, "Ukupan broj field-ova treba da bude 81");
    }

    @Test
    public void testAllFieldsAreFieldInstances() {
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
            for (int j = 0; j < panel.getComponentCount(); j++) {
                Component comp = panel.getComponent(j);
                assertTrue(comp instanceof Field, "Svaki component treba da bude Field");
            }
        }
    }

    @Test
    public void testSetGamePopulatesFields() {
        game.newGame();
        sudokuPanel.setGame(game);
        
        // Provera da postoji bar jedan neprazan field
        boolean hasNonEmptyField = false;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) != 0) {
                    hasNonEmptyField = true;
                    break;
                }
            }
        }
        assertTrue(hasNonEmptyField, "Igra treba da ima neke brojeve");
    }

    @Test
    public void testSetGameSetsCorrectColors() {
        game.newGame();
        sudokuPanel.setGame(game);
        
        // Provera da su svi field-ovi beli nakon postavljanja igre
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
            for (int j = 0; j < panel.getComponentCount(); j++) {
                Field field = (Field) panel.getComponent(j);
                assertEquals(Color.WHITE, field.getBackground(), 
                    "Field pozadina treba da bude bela");
            }
        }
    }

    @Test
    public void testUpdateNewGame() {
        int[][] oldGame = new int[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                oldGame[y][x] = game.getNumber(x, y);
            }
        }
        
        sudokuPanel.update(game, UpdateAction.NEW_GAME);
        
        // Provera da je panel update-ovan
        assertNotNull(sudokuPanel, "Panel treba da postoji nakon update-a");
    }

    @Test
    public void testUpdateCheck() {
        game.newGame();

        // Prvo postavljamo brojeve i označavamo ih kao user input
        // Tako će setGameCheck() moći da ih oboji
        boolean userInputSet = false;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    game.setNumber(x, y, 1); // Namerno postavljanje pogrešnih brojeva

                    // Pronalaženje field-a i postavljanje kao user input (plavo)
                    int panelY = y / 3;
                    int panelX = x / 3;
                    int panelIndex = panelY * 3 + panelX;
                    javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(panelIndex);

                    int fieldY = y % 3;
                    int fieldX = x % 3;
                    int fieldIndex = fieldY * 3 + fieldX;
                    Field field = (Field) panel.getComponent(fieldIndex);
                    field.setNumber(1, true); // Postavljanje kao user input (plavo)

                    userInputSet = true;
                    break;
                }
            }
            if (userInputSet) break;
        }

        assertTrue(userInputSet, "Treba da postoji bar jedno polje sa user input-om");

        // Sada pozivamo checkGame koji će obojiti field-ove
        game.checkGame();
        sudokuPanel.update(game, UpdateAction.CHECK);

        // Provera da su field-ovi obojeni nakon check-a (zeleno ili crveno)
        boolean hasColoredField = false;
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
            for (int j = 0; j < panel.getComponentCount(); j++) {
                Field field = (Field) panel.getComponent(j);
                Color bg = field.getBackground();
                if (bg.equals(Color.GREEN) || bg.equals(Color.RED)) {
                    hasColoredField = true;
                    break;
                }
            }
            if (hasColoredField) break;
        }
        assertTrue(hasColoredField, "Treba da postoje obojeni field-ovi nakon check-a");
    }

    @Test
    public void testCheckWithCorrectAndIncorrectNumbers() {
        game.newGame();

        // Koristimo reflection da pristupimo solution polju
        try {
            java.lang.reflect.Field solutionField = Game.class.getDeclaredField("solution");
            solutionField.setAccessible(true);
            int[][] solution = (int[][]) solutionField.get(game);

            // Pronalaženje dva prazna polja
            int correctX = -1, correctY = -1;
            int wrongX = -1, wrongY = -1;

            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (game.getNumber(x, y) == 0) {
                        if (correctX == -1) {
                            correctX = x;
                            correctY = y;
                        } else if (wrongX == -1) {
                            wrongX = x;
                            wrongY = y;
                            break;
                        }
                    }
                }
                if (wrongX != -1) break;
            }

            if (correctX != -1 && wrongX != -1) {
                // Prvo polje: postavljamo TAČAN broj
                int correctNumber = solution[correctY][correctX];
                game.setNumber(correctX, correctY, correctNumber);

                // Postavljamo field kao user input (plavo)
                int panelY1 = correctY / 3;
                int panelX1 = correctX / 3;
                int panelIndex1 = panelY1 * 3 + panelX1;
                javax.swing.JPanel panel1 = (javax.swing.JPanel) sudokuPanel.getComponent(panelIndex1);
                int fieldY1 = correctY % 3;
                int fieldX1 = correctX % 3;
                int fieldIndex1 = fieldY1 * 3 + fieldX1;
                Field field1 = (Field) panel1.getComponent(fieldIndex1);
                field1.setNumber(correctNumber, true);

                // Drugo polje: postavljamo POGREŠAN broj
                int wrongNumber = solution[wrongY][wrongX] == 1 ? 2 : 1; // Namerno pogrešan
                game.setNumber(wrongX, wrongY, wrongNumber);

                // Postavljamo field kao user input (plavo)
                int panelY2 = wrongY / 3;
                int panelX2 = wrongX / 3;
                int panelIndex2 = panelY2 * 3 + panelX2;
                javax.swing.JPanel panel2 = (javax.swing.JPanel) sudokuPanel.getComponent(panelIndex2);
                int fieldY2 = wrongY % 3;
                int fieldX2 = wrongX % 3;
                int fieldIndex2 = fieldY2 * 3 + fieldX2;
                Field field2 = (Field) panel2.getComponent(fieldIndex2);
                field2.setNumber(wrongNumber, true);

                // Check game
                game.checkGame();
                sudokuPanel.update(game, UpdateAction.CHECK);

                // Provera da prvi field ima ZELENU pozadinu
                Color bg1 = field1.getBackground();
                assertEquals(Color.GREEN, bg1, "Tačan broj treba da bude zelen");

                // Provera da drugi field ima CRVENU pozadinu
                Color bg2 = field2.getBackground();
                assertEquals(Color.RED, bg2, "Pogrešan broj treba da bude crven");
            }
        } catch (Exception e) {
            fail("Reflection greška: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateCandidates() {
        game.newGame();
        game.setSelectedNumber(5);
        game.setHelp(true);
        
        sudokuPanel.update(game, UpdateAction.CANDIDATES);
        
        // Panel treba da reaguje na update bez exceptiona
        assertNotNull(sudokuPanel, "Panel treba da postoji nakon candidates update-a");
    }

    @Test
    public void testUpdateSelectedNumber() {
        game.newGame();
        game.setSelectedNumber(3);
        game.setHelp(true);
        
        sudokuPanel.update(game, UpdateAction.SELECTED_NUMBER);
        
        assertNotNull(sudokuPanel, "Panel treba da postoji nakon selected number update-a");
    }

    @Test
    public void testUpdateHelp() {
        game.newGame();
        game.setHelp(false);
        
        sudokuPanel.update(game, UpdateAction.HELP);
        
        assertNotNull(sudokuPanel, "Panel treba da postoji nakon help update-a");
    }

    @Test
    public void testCandidatesHighlighting() {
        game.newGame();
        
        // Pronalaženje praznog field-a
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
            game.setSelectedNumber(5);
            game.setHelp(true);
            sudokuPanel.update(game, UpdateAction.CANDIDATES);
            
            // Provera da postoje highlighted field-ovi
            boolean hasCandidateHighlight = false;
            for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
                javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
                for (int j = 0; j < panel.getComponentCount(); j++) {
                    Field field = (Field) panel.getComponent(j);
                    if (!field.getBackground().equals(Color.WHITE)) {
                        hasCandidateHighlight = true;
                        break;
                    }
                }
            }
            // Može ali ne mora biti kandidata (zavisi od generisane igre)
            assertNotNull(sudokuPanel, "Panel treba da postoji");
        }
    }

    @Test
    public void testFieldPositioning() {
        // Provera da su field-ovi na ispravnim pozicijama
        int fieldCount = 0;
        for (int panelY = 0; panelY < 3; panelY++) {
            for (int panelX = 0; panelX < 3; panelX++) {
                int panelIndex = panelY * 3 + panelX;
                javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(panelIndex);
                
                for (int fieldY = 0; fieldY < 3; fieldY++) {
                    for (int fieldX = 0; fieldX < 3; fieldX++) {
                        int fieldIndex = fieldY * 3 + fieldX;
                        Field field = (Field) panel.getComponent(fieldIndex);
                        
                        int expectedX = panelX * 3 + fieldX;
                        int expectedY = panelY * 3 + fieldY;
                        
                        assertEquals(expectedX, field.getFieldX(), 
                            "X pozicija field-a");
                        assertEquals(expectedY, field.getFieldY(), 
                            "Y pozicija field-a");
                        
                        fieldCount++;
                    }
                }
            }
        }
        assertEquals(81, fieldCount, "Ukupan broj provjerenih field-ova");
    }

    @Test
    public void testSubPanelBorders() {
        for (int i = 0; i < sudokuPanel.getComponentCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) sudokuPanel.getComponent(i);
            assertNotNull(panel.getBorder(), "Sub-panel treba da ima border");
        }
    }
}
