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

    /**
     *  Removes soy and js extensions
     */
    public static String[] stripExtensions(final String[] exts, final String dotWithExt) {
        if (exts == null) {
            return new String[0];
        }
        final String[] newStrippedExts = new String[exts.length];
        for (int i = 0; i < newStrippedExts.length; i++) {
            if (exts[i].contains(dotWithExt)) {
                newStrippedExts[i] = exts[i].replace(dotWithExt, "");
            } else if (exts[i].contains(".js")) {
                newStrippedExts[i] = exts[i].replace(".js", "");
            } else {
                newStrippedExts[i] = exts[i];
            }
        }

        return newStrippedExts;
    }

    public static Optional<String> stripExtension(final String ext, final String dotWithExt) {
        final String[] strings = stripExtensions(new String[]{ext}, dotWithExt);
        if (strings.length == 0) {
            return Optional.absent();
        }

        return Optional.fromNullable(strings[0]);
    }

}
