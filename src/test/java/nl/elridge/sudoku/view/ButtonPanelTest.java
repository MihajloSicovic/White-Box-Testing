package nl.elridge.sudoku.view;

import nl.elridge.sudoku.controller.ButtonController;
import nl.elridge.sudoku.model.Game;
import nl.elridge.sudoku.model.UpdateAction;
import nl.elridge.sudoku.view.ButtonPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit testovi za ButtonPanel GUI komponentu.
 */
public class ButtonPanelTest {
    private ButtonPanel buttonPanel;
    private Game game;
    private ButtonController buttonController;

    @BeforeEach
    public void setUp() {
        game = new Game();
        buttonPanel = new ButtonPanel();
        buttonController = new ButtonController(game);
        buttonPanel.setController(buttonController);
        game.addObserver(buttonPanel);
    }

    @Test
    public void testButtonPanelInitialization() {
        assertNotNull(buttonPanel, "ButtonPanel treba da bude inicijalizovan");
        assertNotNull(buttonPanel.btnNew, "New dugme treba da postoji");
        assertNotNull(buttonPanel.btnCheck, "Check dugme treba da postoji");
        assertNotNull(buttonPanel.btnExit, "Exit dugme treba da postoji");
        assertNotNull(buttonPanel.cbHelp, "Help checkbox treba da postoji");
        assertNotNull(buttonPanel.btnNumbers, "Number dugmad treba da postoje");
        assertEquals(9, buttonPanel.btnNumbers.length, "Treba da postoji 9 number dugmadi");
    }

    @Test
    public void testNewButtonProperties() {
        assertEquals("New", buttonPanel.btnNew.getText(), "New dugme tekst");
        assertFalse(buttonPanel.btnNew.isFocusable(), "New dugme ne treba da bude focusable");
    }

    @Test
    public void testCheckButtonProperties() {
        assertEquals("Check", buttonPanel.btnCheck.getText(), "Check dugme tekst");
        assertFalse(buttonPanel.btnCheck.isFocusable(), "Check dugme ne treba da bude focusable");
    }

    @Test
    public void testExitButtonProperties() {
        assertEquals("Exit", buttonPanel.btnExit.getText(), "Exit dugme tekst");
        assertFalse(buttonPanel.btnExit.isFocusable(), "Exit dugme ne treba da bude focusable");
    }

    @Test
    public void testHelpCheckBoxProperties() {
        assertEquals("Help on", buttonPanel.cbHelp.getText(), "Help checkbox tekst");
        assertTrue(buttonPanel.cbHelp.isSelected(), "Help checkbox treba da bude selektovan po default-u");
        assertFalse(buttonPanel.cbHelp.isFocusable(), "Help checkbox ne treba da bude focusable");
    }

    @Test
    public void testNumberButtonsProperties() {
        for (int i = 0; i < 9; i++) {
            JToggleButton btn = buttonPanel.btnNumbers[i];
            assertEquals(String.valueOf(i + 1), btn.getText(), "Number dugme " + (i + 1) + " tekst");
            assertFalse(btn.isFocusable(), "Number dugme ne treba da bude focusable");
            assertEquals(40, btn.getPreferredSize().width, "Number dugme širina");
            assertEquals(40, btn.getPreferredSize().height, "Number dugme visina");
        }
    }

    @Test
    public void testNumberButtonsInButtonGroup() {
        assertNotNull(buttonPanel.bgNumbers, "ButtonGroup treba da postoji");
        
        // Testiranje da se samo jedno dugme može selektovati odjednom
        buttonPanel.btnNumbers[0].setSelected(true);
        assertTrue(buttonPanel.btnNumbers[0].isSelected(), "Prvo dugme treba da bude selektovano");
        
        buttonPanel.btnNumbers[1].setSelected(true);
        assertFalse(buttonPanel.btnNumbers[0].isSelected(), "Prvo dugme ne treba više da bude selektovano");
        assertTrue(buttonPanel.btnNumbers[1].isSelected(), "Drugo dugme treba da bude selektovano");
    }

    @Test
    public void testUpdateNewGame() {
        // Selektovanje broja
        buttonPanel.btnNumbers[3].setSelected(true);
        assertTrue(buttonPanel.btnNumbers[3].isSelected(), "Dugme treba da bude selektovano");
        
        // Slanje NEW_GAME update-a
        buttonPanel.update(game, UpdateAction.NEW_GAME);
        
        // Provera da je selekcija očišćena
        for (int i = 0; i < 9; i++) {
            assertFalse(buttonPanel.btnNumbers[i].isSelected(), 
                "Number dugme " + (i + 1) + " ne treba da bude selektovano nakon NEW_GAME");
        }
    }

    @Test
    public void testUpdateCheck() {
        // Selektovanje broja
        buttonPanel.btnNumbers[5].setSelected(true);
        assertTrue(buttonPanel.btnNumbers[5].isSelected(), "Dugme treba da bude selektovano");
        
        // Slanje CHECK update-a
        buttonPanel.update(game, UpdateAction.CHECK);
        
        // Provera da je selekcija očišćena
        for (int i = 0; i < 9; i++) {
            assertFalse(buttonPanel.btnNumbers[i].isSelected(), 
                "Number dugme " + (i + 1) + " ne treba da bude selektovano nakon CHECK");
        }
    }

    @Test
    public void testNewButtonAction() {
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
    }

    @Test
    public void testHelpCheckBoxAction() {
        assertTrue(game.isHelp(), "Help treba da bude uključen inicijalno");
        
        buttonPanel.cbHelp.doClick();
        assertFalse(game.isHelp(), "Help treba da bude isključen nakon klika");
        
        buttonPanel.cbHelp.doClick();
        assertTrue(game.isHelp(), "Help treba da bude uključen nakon drugog klika");
    }

    @Test
    public void testNumberButtonAction() {
        assertEquals(0, game.getSelectedNumber(), "Inicijalno selektovani broj treba da bude 0");
        
        buttonPanel.btnNumbers[4].doClick();
        assertEquals(5, game.getSelectedNumber(), "Selektovani broj treba da bude 5");
        
        buttonPanel.btnNumbers[8].doClick();
        assertEquals(9, game.getSelectedNumber(), "Selektovani broj treba da bude 9");
    }

    @Test
    public void testMultipleNumberButtonActions() {
        for (int i = 0; i < 9; i++) {
            buttonPanel.btnNumbers[i].doClick();
            assertEquals(i + 1, game.getSelectedNumber(), 
                "Selektovani broj treba da bude " + (i + 1));
        }
    }
}
