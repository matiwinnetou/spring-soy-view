package soy.data;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:27
 */
public interface ToSoyDataConverter {

    public static final ToSoyDataConverter EMPTY = new EmptyToSoyDataConverter();

    Map<String, ?> convert(Object model) throws Exception;

}
