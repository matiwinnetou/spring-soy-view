package pl.matisoft.soy.data;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 12:04
 */
public class NoConvertSoyDataConverter implements ToSoyDataConverter {

    @Override
    public Optional<SoyMapData> toSoyMap(final Object model) throws Exception {
        if (model instanceof SoyMapData) {
            return Optional.of((SoyMapData) model);
        }
        if (model instanceof Map) {
            return Optional.of(new SoyMapData(model));
        }

        return Optional.of(new SoyMapData());
    }

}
