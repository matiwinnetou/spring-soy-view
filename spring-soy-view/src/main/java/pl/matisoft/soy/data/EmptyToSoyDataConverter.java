package pl.matisoft.soy.data;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:34
 *
 * An implementation that always returns an Optional.absent(), implements
 * Null Object Pattern
*/
public class EmptyToSoyDataConverter implements ToSoyDataConverter {

    @Override
    public Optional<SoyMapData> toSoyMap(final Object model) throws Exception {
        return Optional.of(new SoyMapData());
    }

}
