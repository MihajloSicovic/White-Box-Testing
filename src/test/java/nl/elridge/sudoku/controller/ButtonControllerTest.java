package nl.elridge.sudoku.controller;

import nl.elridge.sudoku.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JUnit testovi za ButtonController.
 */
public class ButtonControllerTest {
    private Game game;
    private ButtonController buttonController;

    @BeforeEach
    public void setUp() {
        game = new Game();
        buttonController = new ButtonController(game);
    }

    @Test
    public void testButtonControllerInitialization() {
        assertNotNull(buttonController, "ButtonController treba da bude inicijalizovan");
    }

    @Test
    // Mock test
    public void testNewGameAction() {
        int[][] initialGame = new int[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                initialGame[y][x] = game.getNumber(x, y);
            }
        }
        
        ActionEvent event = mock(ActionEvent.class);
        when(event.getActionCommand()).thenReturn("New");
        buttonController.actionPerformed(event);
        
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
    // Mock test
    public void testCheckGameAction() {
        game = mock(Game.class);
        buttonController = new ButtonController(game);
        
        ActionEvent event = new ActionEvent(new JButton(), 
            ActionEvent.ACTION_PERFORMED, "Check");
        buttonController.actionPerformed(event);
        
        verify(game, times(1)).checkGame();
    }

    @Test
    public void testHelpOnAction() {
        game.setHelp(false);
        
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);
        
        ActionEvent event = new ActionEvent(checkBox, 
            ActionEvent.ACTION_PERFORMED, "Help on");
        buttonController.actionPerformed(event);
        
        assertTrue(game.isHelp(), "Help treba da bude uključen");
    }

    @Test
    public void testHelpOffAction() {
        game.setHelp(true);
        
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(false);
        
        ActionEvent event = new ActionEvent(checkBox, 
            ActionEvent.ACTION_PERFORMED, "Help on");
        buttonController.actionPerformed(event);
        
        assertFalse(game.isHelp(), "Help treba da bude isključen");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public void testNumberSelection(String number) {
        ActionEvent event = new ActionEvent(new JToggleButton(), 
            ActionEvent.ACTION_PERFORMED, number);
        buttonController.actionPerformed(event);
        
        assertEquals(Integer.parseInt(number), game.getSelectedNumber(), 
            "Selected number treba da bude " + number);
    }

    @Test
    public void testMultipleNumberSelections() {
        for (int i = 1; i <= 9; i++) {
            ActionEvent event = new ActionEvent(new JToggleButton(), 
                ActionEvent.ACTION_PERFORMED, String.valueOf(i));
            buttonController.actionPerformed(event);
            
            assertEquals(i, game.getSelectedNumber(), 
                "Selected number treba da bude " + i);
        }
    }

    @Test
    public void testSelectedNumberAfterCheck() {
        game.setSelectedNumber(5);
        assertEquals(5, game.getSelectedNumber(), "Inicijalni selected number");
        
        ActionEvent event = new ActionEvent(new JButton(), 
            ActionEvent.ACTION_PERFORMED, "Check");
        buttonController.actionPerformed(event);
        
        assertEquals(0, game.getSelectedNumber(), 
            "Selected number treba da bude resetovan nakon check-a");
    }

    @Test
    public void testActionCommandRecognition() {
        // Test da kontroler prepoznaje različite action commands
        String[] commands = {"New", "Check", "Help on", "1", "5", "9"};
        
        for (String command : commands) {
            try {
                Object source = command.equals("Help on") ? new JCheckBox() : new JButton();
                ActionEvent event = new ActionEvent(source, 
                    ActionEvent.ACTION_PERFORMED, command);
                buttonController.actionPerformed(event);
                // Ako nema exceptiona, command je prepoznat
                assertTrue(true, "Command '" + command + "' treba da bude prepoznat");
            } catch (Exception e) {
                fail("Command '" + command + "' nije prepoznat: " + e.getMessage());
            }
        }
    }

    @Test
    public void testHelpToggleMultipleTimes() {
        JCheckBox checkBox = new JCheckBox();
        
        for (int i = 0; i < 5; i++) {
            boolean expectedState = i % 2 == 0;
            checkBox.setSelected(expectedState);
            
            ActionEvent event = new ActionEvent(checkBox, 
                ActionEvent.ACTION_PERFORMED, "Help on");
            buttonController.actionPerformed(event);
            
            assertEquals(expectedState, game.isHelp(), 
                "Help state nakon " + i + " toggle-a");
        }
    }

    @Test
    public void testNumberSelectionChanges() {
        ActionEvent event1 = new ActionEvent(new JToggleButton(), 
            ActionEvent.ACTION_PERFORMED, "3");
        buttonController.actionPerformed(event1);
        assertEquals(3, game.getSelectedNumber(), "Prvi selected number");
        
        ActionEvent event2 = new ActionEvent(new JToggleButton(), 
            ActionEvent.ACTION_PERFORMED, "7");
        buttonController.actionPerformed(event2);
        assertEquals(7, game.getSelectedNumber(), "Drugi selected number");
        
        ActionEvent event3 = new ActionEvent(new JToggleButton(), 
            ActionEvent.ACTION_PERFORMED, "1");
        buttonController.actionPerformed(event3);
        assertEquals(1, game.getSelectedNumber(), "Treći selected number");
    }
}
