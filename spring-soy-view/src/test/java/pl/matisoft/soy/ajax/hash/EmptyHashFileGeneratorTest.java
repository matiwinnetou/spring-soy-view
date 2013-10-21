package pl.matisoft.soy.ajax.hash;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/10/2013
 * Time: 20:15
 */
public class EmptyHashFileGeneratorTest {

    private EmptyHashFileGenerator emptyHashFileGenerator = new EmptyHashFileGenerator();


    @Test
    public void defaultNotNull() throws Exception {
        Assert.assertNotNull("should not be null", emptyHashFileGenerator.hash(null));
    }

    @Test
    public void defaultAbsent() throws Exception {
        Assert.assertFalse("should be absent", emptyHashFileGenerator.hash(null).isPresent());
    }

}
