package pl.matisoft.soy.global.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/10/2013
 * Time: 23:26
 */
public class DefaultCompileTimeGlobalModelResolverTest {

    @InjectMocks
    private DefaultCompileTimeGlobalModelResolver defaultCompileTimeGlobalModelResolver = new DefaultCompileTimeGlobalModelResolver();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void defaultNotNull() throws Exception {
        Assert.assertNotNull("by default not null", defaultCompileTimeGlobalModelResolver.resolveData());
    }

    @Test
    public void emptyCheck() throws Exception {
        defaultCompileTimeGlobalModelResolver.setData(new HashMap());
        Assert.assertNotNull("by default not null", defaultCompileTimeGlobalModelResolver.resolveData());
    }

    @Test
    public void defaultAbsent() throws Exception {
        Assert.assertFalse("by default there is no data - returns absent", defaultCompileTimeGlobalModelResolver.resolveData().isPresent());
    }

    @Test
    public void absentCheckEmptyMap() throws Exception {
        defaultCompileTimeGlobalModelResolver.setData(new HashMap());
        defaultCompileTimeGlobalModelResolver.setData(new HashMap());
        Assert.assertNotNull("by default not null", defaultCompileTimeGlobalModelResolver.resolveData());
    }

    @Test
    public void setDataMapPresent() throws Exception {
        final Map<String,String> map = new HashMap<String, String>();
        map.put("name1", "value1");
        defaultCompileTimeGlobalModelResolver.setData(map);
        Assert.assertTrue("after setting data - returns not absent", defaultCompileTimeGlobalModelResolver.resolveData().isPresent());
        Assert.assertNotNull("after setting data - returns not absent", defaultCompileTimeGlobalModelResolver.resolveData());
    }

    @Test
    public void setDataMapString() throws Exception {
        final Map<String,String> map = new HashMap<String, String>();
        map.put("name1", "value1");
        defaultCompileTimeGlobalModelResolver.setData(map);
        Assert.assertEquals("after setting data - returns value1", "value1", defaultCompileTimeGlobalModelResolver.resolveData().get().get("name1").stringValue());
    }

    @Test
    public void setDataMapBoolean() throws Exception {
        final Map<String,Object> map = new HashMap<String, Object>();
        map.put("name1", Boolean.FALSE);
        defaultCompileTimeGlobalModelResolver.setData(map);
        Assert.assertFalse("after setting data - returns false", defaultCompileTimeGlobalModelResolver.resolveData().get().get("name1").booleanValue());
    }

    @Test
    public void setDataMapInt() throws Exception {
        final Map<String,Object> map = new HashMap<String, Object>();
        map.put("name1", Integer.valueOf(123));
        defaultCompileTimeGlobalModelResolver.setData(map);
        Assert.assertEquals("after setting data - returns 123", 123, defaultCompileTimeGlobalModelResolver.resolveData().get().get("name1").integerValue());
    }

    @Test
    public void setProperties() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");
        properties.setProperty("key3", "value3");
        defaultCompileTimeGlobalModelResolver.setProperties(properties);
        Assert.assertEquals("after setting data - returns value1", "value1", defaultCompileTimeGlobalModelResolver.resolveData().get().get("key1").stringValue());
        Assert.assertEquals("after setting data - returns value2", "value2", defaultCompileTimeGlobalModelResolver.resolveData().get().get("key2").stringValue());
        Assert.assertEquals("after setting data - returns value3", "value3", defaultCompileTimeGlobalModelResolver.resolveData().get().get("key3").stringValue());
    }

}
