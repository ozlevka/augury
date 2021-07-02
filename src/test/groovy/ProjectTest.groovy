package test.groovy

import org.junit.Test
import org.testng.Assert

class ProjectTest {

    @Test
    void testOfTest() {
        Assert.assertEquals(true, true)
    }

    @Test
    void testParseKubectlOutput() {
        String output = "augury-deployment   3/3     3            3           3m10s"
        def arr = output.split()

        println(arr)
    }
}
