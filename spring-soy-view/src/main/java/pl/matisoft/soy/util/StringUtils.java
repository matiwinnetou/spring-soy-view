package pl.matisoft.soy.util;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 17:58
 */
public class StringUtils {

    public static String nullSafe(final String str) {
        if (str == null) {
            return "";
        }

        return str;
    }

}
