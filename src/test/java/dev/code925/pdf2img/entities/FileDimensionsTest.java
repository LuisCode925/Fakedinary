package dev.code925.pdf2img.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileDimensionsTest {

    @Test
    public void testFileIsVertical(){
        FileDimensions dimensions = new FileDimensions(100.0f, 200.0f);
        assertEquals(100.0f, dimensions.width());
        assertEquals(200.0f, dimensions.height());
        assertEquals(Orientation.VERTICAL, dimensions.getOrientation());
    }

    @Test
    public void testFileIsHorizontal(){
        FileDimensions dimensions = new FileDimensions(200.0f, 100.0f);
        assertEquals(200.0f, dimensions.width());
        assertEquals(100.0f, dimensions.height());
        assertEquals(Orientation.HORIZONTAL, dimensions.getOrientation());
    }

    @Test
    void testOrientationWithDecimals() {
        FileDimensions dimensions = new FileDimensions(5.5f, 10.5f);
        assertEquals(5.5f, dimensions.width());
        assertEquals(10.5f, dimensions.height());
        assertEquals(Orientation.VERTICAL, dimensions.getOrientation(),"La lógica debe funcionar correctamente con valores flotantes.");
    }

}