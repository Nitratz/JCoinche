package JCoinche.Client;

import org.junit.Test;

import static org.junit.Assert.*;

public class JCoincheTest {

    @Test
    public void testProgram() {
        int a = JCoinche.Program(2, 2);
        assertEquals(4, a);
    }

}