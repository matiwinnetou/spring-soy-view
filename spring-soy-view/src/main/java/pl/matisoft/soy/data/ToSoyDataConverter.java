package pl.matisoft.soy.data;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:27
 *
 * An interface, which converts a view model (e.g) POJO object to a Soy's
 * compatible data structure
 */
public interface ToSoyDataConverter {

    /**
     * Convert a view model object to SoyMapData structure
     *
     * @param model
     * @return
     * @throws Exception
     */
    Optional<SoyMapData> toSoyMap(@Nullable final Object model) throws Exception;

}
