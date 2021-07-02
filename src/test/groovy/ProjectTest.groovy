package test.groovy

import org.apache.tools.ant.util.ReaderInputStream
import org.junit.Test
import org.testng.Assert
import org.yaml.snakeyaml.Yaml

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


    @Test
    void loadYamlAndParse() {
        Yaml parser = new Yaml()
        File theYaml = new File("\\\\wsl\$\\Ubuntu-20.04\\home\\ozlevka\\projects\\augury\\deploy\\deployment.yaml")

        def deploy = parser.load(theYaml.text)

        String a = "b"
    }
}
