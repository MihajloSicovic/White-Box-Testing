package nl.elridge.sudoku.view;

import nl.elridge.sudoku.controller.ButtonController;
import nl.elridge.sudoku.controller.SudokuController;
import nl.elridge.sudoku.model.Game;
import nl.elridge.sudoku.view.ButtonPanel;
import nl.elridge.sudoku.view.SudokuPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integracioni testovi za GUI Sudoku aplikacije.
 * Testiraju interakciju između različitih komponenti.
 */
public class SudokuGUIIntegrationTest {
    private Game game;
    private ButtonPanel buttonPanel;
    private SudokuPanel sudokuPanel;
    private ButtonController buttonController;
    private SudokuController sudokuController;

    @BeforeEach
    public void setUp() {
        game = new Game();
        
        buttonPanel = new ButtonPanel();
        buttonController = new ButtonController(game);
        buttonPanel.setController(buttonController);
        
        sudokuPanel = new SudokuPanel();
        sudokuController = new SudokuController(sudokuPanel, game);
        sudokuPanel.setController(sudokuController);
        sudokuPanel.setGame(game);
        
        game.addObserver(buttonPanel);
        game.addObserver(sudokuPanel);
    }

    @Test
    public void testCompleteApplicationSetup() {
        assertNotNull(game, "Game treba da postoji");
        assertNotNull(buttonPanel, "ButtonPanel treba da postoji");
        assertNotNull(sudokuPanel, "SudokuPanel treba da postoji");
        assertNotNull(buttonController, "ButtonController treba da postoji");
        assertNotNull(sudokuController, "SudokuController treba da postoji");
    }

    @Test
    public void testNewGameUpdatesAllComponents() {
        // Čuvanje početnog stanja
        int[][] initialGame = new int[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                initialGame[y][x] = game.getNumber(x, y);
            }
        }
        
        // Klik na New dugme
        buttonPanel.btnNew.doClick();
        
        // Provera da se igra promenila
        boolean gameChanged = false;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (initialGame[y][x] != game.getNumber(x, y)) {
                    gameChanged = true;
                    break;
                }
            }
        }
        assertTrue(gameChanged, "Nova igra treba da se generiše");
        
        // Provera da je button selection očišćena
        for (int i = 0; i < 9; i++) {
            assertFalse(buttonPanel.btnNumbers[i].isSelected(), 
                "Number dugme ne treba da bude selektovano");
        }
    }

    @Test
    public void testNumberSelectionWorkflow() {
        game.newGame();
        
        // Selektovanje broja
        buttonPanel.btnNumbers[4].doClick(); // Broj 5
        assertEquals(5, game.getSelectedNumber(), "Selected number treba da bude 5");
        
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
            // Postavljanje broja u igru
            game.setNumber(emptyX, emptyY, 5);
            assertEquals(5, game.getNumber(emptyX, emptyY), 
                "Broj treba da bude postavljen u igru");
        }
    }

    @Test
    public void testHelpFunctionality() {
        game.newGame();
        
        // Uključivanje help-a
        assertTrue(game.isHelp(), "Help treba da bude uključen po default-u");
        
        // Isključivanje help-a
        buttonPanel.cbHelp.doClick();
        assertFalse(game.isHelp(), "Help treba da bude isključen");
        
        // Uključivanje help-a ponovo
        buttonPanel.cbHelp.doClick();
        assertTrue(game.isHelp(), "Help treba da bude uključen ponovo");
    }

    @Test
    public void testCheckGameFunctionality() {
        game.newGame();
        
        // Popunjavanje svih praznih polja sa (verovatno) pogrešnim brojevima
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    game.setNumber(x, y, 1);
                }
            }
        }
        
        // Klik na Check dugme
        buttonPanel.btnCheck.doClick();
        
        // Provera da je selected number resetovan
        assertEquals(0, game.getSelectedNumber(), 
            "Selected number treba da bude 0 nakon check-a");
    }

    @Test
    public void testObserverPattern() {
        // Provera da su observer-i registrovani
        assertEquals(2, game.countObservers(), "Treba da postoje 2 observer-a");
        
        // Test da promene u modelu obaveštavaju view-ove
        int oldSelectedNumber = game.getSelectedNumber();
        game.setSelectedNumber(7);
        assertNotEquals(oldSelectedNumber, game.getSelectedNumber(), 
            "Model treba da se promeni");
    }

    @Test
    public void testCompleteGamePlayScenario() {
        // 1. Nova igra
        buttonPanel.btnNew.doClick();
        
        // 2. Selektovanje broja
        buttonPanel.btnNumbers[2].doClick(); // Broj 3
        assertEquals(3, game.getSelectedNumber(), "Broj 3 treba da bude selektovan");
        
        // 3. Pronalaženje praznog field-a i postavljanje broja
        boolean numberSet = false;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    game.setNumber(x, y, 3);
                    assertEquals(3, game.getNumber(x, y), "Broj treba da bude postavljen");
                    numberSet = true;
                    break;
                }
            }
            if (numberSet) break;
        }
        
        // 4. Provera igre
        buttonPanel.btnCheck.doClick();
        assertEquals(0, game.getSelectedNumber(), "Selected number treba da bude resetovan");
        
        // 5. Toggle help
        boolean initialHelp = game.isHelp();
        buttonPanel.cbHelp.doClick();
        assertEquals(!initialHelp, game.isHelp(), "Help state treba da se promeni");
    }

    @Test
    public void testMultipleNewGames() {
        for (int i = 0; i < 3; i++) {
            int[][] beforeGame = new int[9][9];
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    beforeGame[y][x] = game.getNumber(x, y);
                }
            }
            
            buttonPanel.btnNew.doClick();
            
            boolean changed = false;
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (beforeGame[y][x] != game.getNumber(x, y)) {
                        changed = true;
                        break;
                    }
                }
            }
            assertTrue(changed, "Igra treba da se promeni pri iteraciji " + i);
        }
    }

    @Test
    public void testAllNumberButtons() {
        for (int i = 0; i < 9; i++) {
            buttonPanel.btnNumbers[i].doClick();
            assertEquals(i + 1, game.getSelectedNumber(), 
                "Selected number treba da bude " + (i + 1));
            
            // Provera da je samo jedno dugme selektovano
            int selectedCount = 0;
            for (int j = 0; j < 9; j++) {
                if (buttonPanel.btnNumbers[j].isSelected()) {
                    selectedCount++;
                }
            }
            assertEquals(1, selectedCount, "Samo jedno dugme treba da bude selektovano");
        }
    }

    @Test
    public void testSudokuPanelStructure() {
        // Provera strukture SudokuPanel-a (3x3 panel-a, svaki sa 3x3 field-a)
        assertEquals(9, sudokuPanel.getComponentCount(), "9 sub-panela");
        
        int totalFields = 0;
        for (Component comp : sudokuPanel.getComponents()) {
            if (comp instanceof javax.swing.JPanel) {
                javax.swing.JPanel subPanel = (javax.swing.JPanel) comp;
                totalFields += subPanel.getComponentCount();
            }
        }
        assertEquals(81, totalFields, "81 field ukupno");
    }

    @Test
    public void testButtonPanelStructure() {
        // Provera postojanja svih komponenti
        assertNotNull(buttonPanel.btnNew, "New dugme");
        assertNotNull(buttonPanel.btnCheck, "Check dugme");
        assertNotNull(buttonPanel.btnExit, "Exit dugme");
        assertNotNull(buttonPanel.cbHelp, "Help checkbox");
        assertNotNull(buttonPanel.btnNumbers, "Number buttons");
        assertEquals(9, buttonPanel.btnNumbers.length, "9 number buttons");
    }

    @Test
    public void testGameStateConsistency() {
        game.newGame();
        
        // Provera da je inicijalno stanje konzistentno
        int emptyCount = 0;
        int filledCount = 0;
        
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (game.getNumber(x, y) == 0) {
                    emptyCount++;
                } else {
                    filledCount++;
                }
            }
        }
        
        assertEquals(81, emptyCount + filledCount, "Ukupan broj field-ova treba da bude 81");
        assertTrue(emptyCount > 0, "Treba da postoje prazni field-ovi");
        assertTrue(filledCount > 0, "Treba da postoje popunjeni field-ovi");
    }

    @Test
    public void testHelpWithCandidates() {
        game.newGame();
        game.setHelp(true);

        // Pronalaženje praznog polja
        int emptyX = -1;
        int emptyY = -1;

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

        assertTrue(emptyX != -1, "Treba da postoji bar jedno prazno polje");

        // Testiranje kandidata za različite brojeve
        final int finalX = emptyX;
        final int finalY = emptyY;

        for (int num = 1; num <= 9; num++) {
            game.setSelectedNumber(num);
            // isSelectedNumberCandidate ne treba da baci exception
            assertDoesNotThrow(() -> game.isSelectedNumberCandidate(finalX, finalY),
                    "Provera kandidata ne treba da baci exception");
        }
    }
}
