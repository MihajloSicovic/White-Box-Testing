package nl.elridge.sudoku.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.provider.CsvFileSource;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Observer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameTest {
    private static Game game;

    @BeforeAll
    public static void beforeAll() {
        game = new Game();
    }

    @BeforeEach
    public void beforeEach() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                game.setNumber(i, j, 0);
            }
        }
    }

    @Test
    @Order(1)
    public void checkTest() {
        game.newGame();
        game.checkGame();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.getNumber(i, j) == 0)
                    assertFalse(game.isCheckValid(i, j), "Invalid check state");
                else assertTrue(game.isCheckValid(i, j), "Invalid check state");
            }
        }
    }

    @Test
    @Order(2)
    public void helpTest() {
        game.setHelp(true);
        assertTrue(game.isHelp(), "Help state");

        game.setHelp(false);
        assertFalse(game.isHelp(), "Help state");
    }

    @Test
    @Order(3)
    public void selectedNumberTest() {
        game.setSelectedNumber(3);
        assertEquals(3, game.getSelectedNumber(), "Selected number");

        game.setSelectedNumber(0);
        assertEquals(0, game.getSelectedNumber(), "Selected number");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/number.csv", numLinesToSkip = 1)
    @Order(4)
    public void numberTest(int x, int y, int num) {
        game.setNumber(x, y, num);
        assertEquals(num, game.getNumber(x, y), "Number");
    }

    @Test
    @Order(5)
    public void numberCandidateFalseTest() {
        game.setNumber(0, 0, 3);
        assertFalse(game.isSelectedNumberCandidate(0, 0), "Candidate");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/candidate.csv", numLinesToSkip = 1)
    @Order(6)
    public void numberCandidateTest(int setX, int setY, int num, int checkX, int checkY) {
        game.setNumber(setX, setY, num);
        game.setSelectedNumber(num);
        assertFalse(game.isSelectedNumberCandidate(checkX, checkY), "Candidate");
    }

    @Test
    @Order(7)
    public void numberCandidateTrueTest() {
        game.setNumber(3, 4, 5);
        game.setSelectedNumber(5);
        assertTrue(game.isSelectedNumberCandidate(8, 8), "Candidate");
    }

    @Test
    @Order(8)
    // PIT Test
    public void validSolutionTest() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Field field = Game.class.getDeclaredField("solution");
        field.setAccessible(true);

        Method method = Game.class.getDeclaredMethod("isValid", int[][].class);
        method.setAccessible(true);

        int[][] solution = (int[][])field.get(game);
        assertTrue((boolean)method.invoke(game, (Object)solution), "Solution invalid");
    }

    @Test
    @Order(9)
    // PIT Test
    public void validGameNotEmptyTest() {
        game.newGame();

        boolean empty = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.getNumber(i, j) != 0) {
                    empty = false;
                    break;
                }
            }
        }

        assertFalse(empty, "Empty game");
    }

    @Test
    @Order(10)
    // PIT Test
    public void randomSolutionTest() throws NoSuchFieldException, IllegalAccessException {
        Field field = Game.class.getDeclaredField("solution");
        field.setAccessible(true);

        int[][] prevSolution = (int[][])field.get(game);

        game.newGame();

        int[][] solution = (int[][])field.get(game);

        assertFalse(Arrays.deepEquals(prevSolution, solution), "Solution invalid");
    }

    @Test
    @Order(11)
    // PIT Test
    public void gameNotSolutionTest() throws NoSuchFieldException, IllegalAccessException {
        game.newGame();

        Field fieldSolution = Game.class.getDeclaredField("solution");
        fieldSolution.setAccessible(true);

        int[][] solution = (int[][])fieldSolution.get(game);

        Field fieldGame = Game.class.getDeclaredField("game");
        fieldGame.setAccessible(true);

        int[][] gameGame = (int[][])fieldGame.get(game);

        assertFalse(Arrays.deepEquals(gameGame, solution), "Solution invalid");
    }

    @Test
    @Order(12)
    // PIT Test
    void invalidBoardTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        int[][] board = {
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,0}
        };

        Method isValid = Game.class.getDeclaredMethod("isValid", int[][].class);
        isValid.setAccessible(true);

        boolean result = (boolean)isValid.invoke(game, (Object)board);

        assertFalse(result, "Board has multiple solutions and must be invalid");
    }

    @Test
    @Order(13)
    // PIT Test
    public void changedNotifyTest() {
        Observer obs = mock(Observer.class);

        game.addObserver(obs);
        game.newGame();
        verify(obs, times(1)).update(any(), any());

        game.checkGame();
        verify(obs, times(2)).update(any(), any());

        game.setHelp(false);
        verify(obs, times(3)).update(any(), any());

        game.setSelectedNumber(2);
        verify(obs, times(4)).update(any(), any());

        game.deleteObservers();
    }
}
