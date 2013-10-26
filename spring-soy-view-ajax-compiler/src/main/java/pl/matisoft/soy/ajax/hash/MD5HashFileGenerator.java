package pl.matisoft.soy.ajax.hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;
import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import pl.matisoft.soy.config.SoyViewConfig;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 29.06.13
 * Time: 23:58
 *
 * An MD5 algorithm implementation of a hash file generator
 *
 * It is important to notice that this implementation supports a dev and prod modes
 * in dev (debugOn) mode the implementation will not cache md5 hash checksums, conversely
 * in prod mode it will cache it. The cache can be fine tuned via setters.
 */
public class MD5HashFileGenerator implements HashFileGenerator, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MD5HashFileGenerator.class);

    private boolean debugOn = SoyViewConfig.DEFAULT_DEBUG_ON;

    private final static int DEF_CACHE_MAX_SIZE = 10000;

    private final static String DEF_TIME_UNIT = "DAYS";

    private final static int DEF_EXPIRE_AFTER_WRITE = 1;

    /** maximum number of entries this cache will hold */
    private int cacheMaxSize = DEF_CACHE_MAX_SIZE;

    /** number of time units after which once written entries will expire */
    private int expireAfterWrite = DEF_EXPIRE_AFTER_WRITE;

    /** String used to denote a TimeUnit */
    private String expireAfterWriteUnit = DEF_TIME_UNIT;

    /**friendly*/ Cache<URL, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(expireAfterWrite, TimeUnit.valueOf(expireAfterWriteUnit))
            .maximumSize(cacheMaxSize)
            .concurrencyLevel(1) //look up a constant class, 1 is not very clear
            .build();

    public void afterPropertiesSet() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expireAfterWrite, TimeUnit.valueOf(expireAfterWriteUnit))
                .maximumSize(cacheMaxSize)
                .concurrencyLevel(1) //look up a constant class, 1 is not very clear
                .build();
    }

    /**
     * Calculates a md5 hash for an url
     *
     * If a passed in url is absent then this method will return absent as well
     *
     * @param url - an url to a soy template file
     * @return - md5 checksum of a template file
     * @throws IOException - in a case there is an IO error calculating md5 checksum
     */
    @Override
    public Optional<String> hash(final Optional<URL> url) throws IOException {
        if (!url.isPresent()) {
            return Optional.absent();
        }
        logger.debug("Calculating md5 hash, url:{}", url);
        if (isDebugOff()) {
            final String md5 = cache.getIfPresent(url.get());

            logger.debug("md5 hash:{}", md5);

            if (md5 != null) {
                return Optional.of(md5);
            }
        }

        final InputStream is = url.get().openStream();
        final String md5 = getMD5Checksum(is);

        if (isDebugOff()) {
            logger.debug("caching url:{} with hash:{}", url, md5);

            cache.put(url.get(), md5);
        }

        return Optional.fromNullable(md5);
    }

    @Override
    public Optional<String> hashMulti(final Collection<URL> urls) throws IOException {
        if (urls.isEmpty()) {
            return Optional.absent();
        }
        if (urls.size() == 1) {
            return hash(Optional.of(urls.iterator().next()));
        }

        final List<String> hashes = new ArrayList<String>();
        for (final URL url : urls) {
            final Optional<String> hash = hash(Optional.of(url));
            if (hash.isPresent()) {
                hashes.add(hash.get());
            }
        }

        final StringBuilder builder = new StringBuilder();
        for (final String hash : hashes) {
            builder.append(hash);
        }

        return Optional.fromNullable(getMD5Checksum(new ReaderInputStream(new StringReader(builder.toString()))));
    }

    public static byte[] createChecksum(final InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        try {
            final MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            try {
                is.close();
            } catch (final IOException e) {
                logger.error("unable to close the stream", e); //unlike to cause issues but log a stacktrace at least
            }
            return complete.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    public static String getMD5Checksum(final InputStream is) throws IOException {
        final byte[] b = createChecksum(is);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }

        return result;
    }

    private boolean isDebugOff() {
        return !debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    public void setExpireAfterWrite(int expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public void setExpireAfterWriteUnit(String expireAfterWriteUnit) {
        this.expireAfterWriteUnit = expireAfterWriteUnit;
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public int getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public String getExpireAfterWriteUnit() {
        return expireAfterWriteUnit;
    }

}
