package pl.matisoft.soy.global;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 20:57
 */
public class EmptyGlobalModelResolverTest {

    private EmptyGlobalModelResolver emptyGlobalModelResolver = new EmptyGlobalModelResolver();

    @Test
    public void returnsNotNullByDefault() throws Exception {
        final Optional<SoyMapData> soyMapDataOptional = emptyGlobalModelResolver.resolveData(null);
        Assert.assertNotNull(soyMapDataOptional);
    }

    @Test
    public void returnsAbsentByDefault() throws Exception {
        final Optional<SoyMapData> soyMapDataOptional = emptyGlobalModelResolver.resolveData(null);
        Assert.assertFalse(soyMapDataOptional.isPresent());
    }

}
