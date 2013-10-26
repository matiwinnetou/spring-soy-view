package pl.matisoft.soy.ajax.hash;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/10/2013
 * Time: 20:15
 */
public class EmptyHashFileGenerator implements HashFileGenerator {

    @Override
    public Optional<String> hash(final Optional<URL> url) throws IOException {
        return Optional.absent();
    }

    @Override
    public Optional<String> hashMulti(Collection<URL> urls) throws IOException {
        return Optional.absent();
    }

}

