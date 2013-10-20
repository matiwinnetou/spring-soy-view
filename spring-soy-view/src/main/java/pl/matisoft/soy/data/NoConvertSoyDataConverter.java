package pl.matisoft.soy.data;

import javax.annotation.Nullable;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 12:04
 *
 * An implementation that will not convert model to SoyMapData but rather
 * it will return a SoyMapData, in case a passed in model happens to be a SoyMapData
 * object itself.
 */
public class NoConvertSoyDataConverter implements ToSoyDataConverter {

    /**
     * Pass a model object and return a SoyMapData if a model object happens
     * to be a SoyMapData.
     *
     * An implementation will also check if a passed in object is a Map and return
     * a SoyMapData wrapping that map
     *
     * @param model
     * @return SoyMapData if model is of this type and an empty SoyMapData in case model is *not* a SoyMapData
     * @throws Exception
     */
    @Override
    public Optional<SoyMapData> toSoyMap(@Nullable final Object model) throws Exception {
        if (model instanceof SoyMapData) {
            return Optional.of((SoyMapData) model);
        }
        if (model instanceof Map) {
            return Optional.of(new SoyMapData(model));
        }

        return Optional.of(new SoyMapData());
    }

}
