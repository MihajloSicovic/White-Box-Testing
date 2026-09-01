package nl.elridge.sudoku.view;

import nl.elridge.sudoku.view.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit testovi za Field GUI komponentu.
 */
public class FieldTest {
    private Field field;

    @BeforeEach
    public void setUp() {
        field = new Field(3, 5);
    }

    @Test
    public void testFieldInitialization() {
        assertNotNull(field, "Field treba da bude inicijalizovan");
        assertEquals(3, field.getFieldX(), "X pozicija");
        assertEquals(5, field.getFieldY(), "Y pozicija");
    }

    @Test
    public void testFieldDimensions() {
        Dimension preferredSize = field.getPreferredSize();
        assertEquals(40, preferredSize.width, "Širina field-a");
        assertEquals(40, preferredSize.height, "Visina field-a");
    }

    @Test
    public void testFieldAlignment() {
        assertEquals(SwingConstants.CENTER, field.getHorizontalAlignment(), 
            "Tekst treba da bude centriran");
    }

    @Test
    public void testFieldOpaque() {
        assertTrue(field.isOpaque(), "Field treba da bude opaque");
    }

    @Test
    public void testFieldBorder() {
        assertNotNull(field.getBorder(), "Field treba da ima border");
    }

    @Test
    public void testFieldFont() {
        Font font = field.getFont();
        assertNotNull(font, "Font treba da postoji");
        assertEquals(Font.PLAIN, font.getStyle(), "Font stil");
        assertEquals(20, font.getSize(), "Font veličina");
    }

    @Test
    public void testFieldPositionGetters() {
        Field testField = new Field(7, 2);
        assertEquals(7, testField.getFieldX(), "X pozicija");
        assertEquals(2, testField.getFieldY(), "Y pozicija");
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0",
        "4, 4",
        "8, 8",
        "2, 7",
        "6, 1"
    })
    public void testFieldPositions(int x, int y) {
        Field testField = new Field(x, y);
        assertEquals(x, testField.getFieldX(), "X pozicija za (" + x + ", " + y + ")");
        assertEquals(y, testField.getFieldY(), "Y pozicija za (" + x + ", " + y + ")");
    }

    @Test
    public void testSetNumberWithUserInput() {
        field.setNumber(5, true);
        assertEquals("5", field.getText(), "Tekst field-a");
        assertEquals(Color.BLUE, field.getForeground(), "Boja teksta za user input");
    }

    @Test
    public void testSetNumberWithoutUserInput() {
        field.setNumber(7, false);
        assertEquals("7", field.getText(), "Tekst field-a");
        assertEquals(Color.BLACK, field.getForeground(), "Boja teksta za non-user input");
    }

    @Test
    public void testSetNumberZeroWithUserInput() {
        field.setNumber(5, true);
        field.setNumber(0, true);
        assertEquals("", field.getText(), "Prazan field za broj 0");
        assertEquals(Color.BLUE, field.getForeground(), "Boja ostaje plava");
    }

    @Test
    public void testSetNumberZeroWithoutUserInput() {
        field.setNumber(3, false);
        field.setNumber(0, false);
        assertEquals("", field.getText(), "Prazan field za broj 0");
        assertEquals(Color.BLACK, field.getForeground(), "Boja ostaje crna");
    }

    @ParameterizedTest
    @CsvSource({
        "1, true",
        "2, false",
        "3, true",
        "4, false",
        "5, true",
        "6, false",
        "7, true",
        "8, false",
        "9, true"
    })
    public void testSetNumberVariousCombinations(int number, boolean userInput) {
        field.setNumber(number, userInput);
        assertEquals(String.valueOf(number), field.getText(), 
            "Tekst treba da bude " + number);
        assertEquals(userInput ? Color.BLUE : Color.BLACK, field.getForeground(), 
            "Boja za userInput=" + userInput);
    }

    @Test
    public void testSetNumberMultipleTimes() {
        field.setNumber(3, true);
        assertEquals("3", field.getText(), "Prvi broj");
        assertEquals(Color.BLUE, field.getForeground(), "Plava boja");
        
        field.setNumber(7, false);
        assertEquals("7", field.getText(), "Drugi broj");
        assertEquals(Color.BLACK, field.getForeground(), "Crna boja");
        
        field.setNumber(0, true);
        assertEquals("", field.getText(), "Prazan field");
        assertEquals(Color.BLUE, field.getForeground(), "Plava boja opet");
    }

    @Test
    public void testFieldBoundaries() {
        // Test graničnih vrednosti
        Field cornerField1 = new Field(0, 0);
        assertEquals(0, cornerField1.getFieldX(), "Gornji levi ugao X");
        assertEquals(0, cornerField1.getFieldY(), "Gornji levi ugao Y");
        
        Field cornerField2 = new Field(8, 8);
        assertEquals(8, cornerField2.getFieldX(), "Donji desni ugao X");
        assertEquals(8, cornerField2.getFieldY(), "Donji desni ugao Y");
    }

    @Test
    public void testSetNumberNegative() {
        // Test sa negativnim brojem (edge case)
        field.setNumber(-1, false);
        assertEquals("", field.getText(), "Negativan broj treba da rezultuje praznim field-om");
    }

    @Test
    public void testSetNumberLarge() {
        // Test sa velikim brojem (van normalnog opsega 1-9)
        field.setNumber(15, true);
        assertEquals("15", field.getText(), "Veliki broj treba da se prikaže");
    }
}
