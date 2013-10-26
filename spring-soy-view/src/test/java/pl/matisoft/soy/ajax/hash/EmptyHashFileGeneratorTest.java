package pl.matisoft.soy.ajax.hash;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/10/2013
 * Time: 20:15
 */
public class EmptyHashFileGeneratorTest {

    private EmptyHashFileGenerator emptyHashFileGenerator = new EmptyHashFileGenerator();

    @Test
    public void defaultHashOneNotNull() throws Exception {
        Assert.assertNotNull("should not be null", emptyHashFileGenerator.hash(null));
    }

    @Test
    public void defaultHashOneAbsent() throws Exception {
        Assert.assertFalse("should be absent", emptyHashFileGenerator.hash(null).isPresent());
    }

    @Test
    public void defaultHashOneAbsentWithValue() throws Exception {
        Assert.assertFalse("should be absent", emptyHashFileGenerator.hash(Optional.of(new URL("file://"))).isPresent());
    }

    @Test
    public void defaultHashMultiNotNull() throws Exception {
        Assert.assertNotNull("should not be null", emptyHashFileGenerator.hashMulti(null));
    }

    @Test
    public void defaultHashMultiAbsent() throws Exception {
        Assert.assertFalse("should be absent", emptyHashFileGenerator.hashMulti(null).isPresent());
    }

    @Test
    public void defaultHashMultiEmptyNotNull() throws Exception {
        Assert.assertNotNull("should not be null", emptyHashFileGenerator.hashMulti(Collections.EMPTY_LIST));
    }

    @Test
    public void defaultHashMultiEmptyNotNullAbsentWithValue() throws Exception {
        Assert.assertNotNull("should not be null", emptyHashFileGenerator.hashMulti(Lists.newArrayList(new URL("file://"))));
    }

    @Test
    public void defaultHashMultiEmptyAbsent() throws Exception {
        Assert.assertFalse("should be absent", emptyHashFileGenerator.hashMulti(Collections.EMPTY_LIST).isPresent());
    }

}
