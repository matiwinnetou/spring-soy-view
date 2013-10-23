package pl.matisoft.soy.data.adjust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 23.10.13
 * Time: 11:12
 */
public class SpringModelAdjusterTest {

    private SpringModelAdjuster springModelAdjuster = new SpringModelAdjuster();

    @Test
    public void defaultModelKey() throws Exception {
        Assert.assertEquals("model key should be 'model'", "model", springModelAdjuster.getModelKey());
    }

    @Test
    public void setModelKey() throws Exception {
        springModelAdjuster.setModelKey("newModelKey");
        Assert.assertEquals("model key should be 'newModelKey'", "newModelKey", springModelAdjuster.getModelKey());
    }

    @Test
    public void adjustMap() throws Exception {
        final Object modelObject = new Object();
        final Map<String,Object> map = new HashMap();
        map.put("model", modelObject);
        Assert.assertEquals("should return modelObject", modelObject, springModelAdjuster.adjust(map));
    }

    @Test
    public void adjustMapFail() throws Exception {
        final Object modelObject = new Object();
        final Map<String,Object> map = new HashMap();
        map.put("model2", modelObject);
        Assert.assertNull("should return null", springModelAdjuster.adjust(map));
    }

    @Test
    public void adjustSpringModel() throws Exception {
        final Object modelObject = new Object();
        final Model model = new ExtendedModelMap();
        model.addAttribute("model", modelObject);
        Assert.assertEquals("should return modelObject", modelObject, springModelAdjuster.adjust(model));
    }

    @Test
    public void adjustPassThrough() throws Exception {
        final Object modelObject = new Object();
        Assert.assertEquals("should return modelObject", modelObject, springModelAdjuster.adjust(modelObject));
    }

}
