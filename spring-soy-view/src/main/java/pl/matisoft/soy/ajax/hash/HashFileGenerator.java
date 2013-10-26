package pl.matisoft.soy.ajax.hash;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 29.06.13
 * Time: 23:57
 *
 * An interface which for a given url calculates a hash checksum
 */
public interface HashFileGenerator {

   Optional<String> hash(Optional<URL> url) throws IOException;

   Optional<String> hashMulti(Collection<URL> urls) throws IOException;

}
