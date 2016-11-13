package JCoinche.Server;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by LIEGEM on 11/11/2016.
 */
public class JCoincheTest {

    @Test
    public void testProgram() {
        int t = JCoinche.add(2, 2);
        assertEquals(4, t);
    }
}