package pl.matisoft.soy.ajax.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 26/10/2013
 * Time: 11:41
 */
public class PathUtils {

    /**
     * Converts a String array to a String with coma separator
     * example: String["a.soy", "b.soy"] -> output: a.soy,b.soy
     */
   public static String arrayToPath(final String[] array) {
        if (array == null) {
            return "";
        }
        final Joiner joiner = Joiner.on(",").skipNulls();

        return joiner.join(array);
    }

}
