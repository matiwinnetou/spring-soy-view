package pl.matisoft.soy.ajax.utils;

import com.google.common.base.Joiner;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 26/10/2013
 * Time: 11:41
 */
public class PathUtils {

    private PathUtils() {
        // protects from instantiation
    }

    /**
     * Converts a String array to a String with coma separator
     * example: String["a.soy", "b.soy"] - output: a.soy,b.soy
     * @param array - array
     * @return comma separated list
     */
   public static String arrayToPath(final String[] array) {
        if (array == null) {
            return "";
        }

        return Joiner.on(",").skipNulls().join(array);
    }

}
