package pl.matisoft.soy.global;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import org.junit.Assert;
import org.junit.Test;
import pl.matisoft.soy.global.runtime.EmptyGlobalRuntimeModelResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 20:57
 */
public class EmptyGlobalModelResolverTest {

    private EmptyGlobalRuntimeModelResolver emptyGlobalModelResolver = new EmptyGlobalRuntimeModelResolver();

    @Test
    public void returnsNotNullByDefault() throws Exception {
        final Optional<SoyMapData> soyMapDataOptional = emptyGlobalModelResolver.resolveData(null, null, null);
        Assert.assertNotNull(soyMapDataOptional);
    }

    @Test
    public void returnsAbsentByDefault() throws Exception {
        final Optional<SoyMapData> soyMapDataOptional = emptyGlobalModelResolver.resolveData(null, null, null);
        Assert.assertFalse(soyMapDataOptional.isPresent());
    }

}
