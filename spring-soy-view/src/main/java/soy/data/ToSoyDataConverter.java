package soy.data;

import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:27
 */
public interface ToSoyDataConverter {

    public static final ToSoyDataConverter EMPTY = new EmptyToSoyDataConverter();

    SoyMapData toSoyMap(Object model) throws Exception;

}
