package pl.matisoft.soy.data;

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
    public SoyMapData toSoyMap(final Object model) throws Exception {
        if (model instanceof SoyMapData) {
            return (SoyMapData) model;
        }
        if (model instanceof Map) {
            return new SoyMapData(model);
        }

        return new SoyMapData();
    }

}
