package net.ictcampus.paketdienst;

import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Class to make Junit TestCases
 */
public class TestClass{
    @Test
    public void markerSizeTest(){
        MapActivity map= new MapActivity();
        map.createMailBoxTest();
        assertEquals(3, MapActivity.getMarkerOptionsMailBox().size());
    }

}
