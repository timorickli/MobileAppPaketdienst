package net.ictcampus.paketdienst;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Class to make Junit TestCases
 */
public class TestClass {
    @Test
    public void markerSizeTest() {
        MapActivity map = new MapActivity();
        map.createMailBoxTest();
        assertNotEquals(2, MapActivity.getMarkerOptionsMailBox().size());
        assertEquals(3, MapActivity.getMarkerOptionsMailBox().size());
    }

}
