package soy.global;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 23:06
 */
public class DefaultGlobalModelResolver implements GlobalModelResolver {

    private Map data;

    @Override
    public Optional<SoyMapData> resolveData() {
        if (data == null || data.isEmpty()) {
            return Optional.absent();
        }

        return Optional.of(new SoyMapData(data));
    }

    public void setData(final Map data) {
        this.data = data;
    }

}
