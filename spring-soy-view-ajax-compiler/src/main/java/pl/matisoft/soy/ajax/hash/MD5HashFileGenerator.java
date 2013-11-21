package pl.matisoft.soy.ajax.hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import pl.matisoft.soy.config.SoyViewConfigDefaults;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 29.06.13
 * Time: 23:58
 *
 * An MD5 algorithm implementation of a hash file generator
 *
 * It is important to notice that this implementation supports a dev and prod modes
 * in dev (hotReloadMode) mode the implementation will not cache md5 hash checksums, conversely
 * in prod mode it will cache it. The cache can be fine tuned via setters.
 */
public class MD5HashFileGenerator implements HashFileGenerator, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MD5HashFileGenerator.class);

    private boolean hotReloadMode = SoyViewConfigDefaults.DEFAULT_HOT_RELOAD_MODE;

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
        if (isHotReloadModeOff()) {
            final String md5 = cache.getIfPresent(url.get());

            logger.debug("md5 hash:{}", md5);

            if (md5 != null) {
                return Optional.of(md5);
            }
        }

        final InputStream is = url.get().openStream();
        final String md5 = getMD5Checksum(is);

        if (isHotReloadModeOff()) {
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

    public static String getMD5Checksum(final InputStream is) throws IOException {
        final HashFunction hf = Hashing.md5();

        final HashCode hashCode = hf.hashBytes(getBytesFromInputStream(is));

        return hashCode.toString();
    }

    private static byte[] getBytesFromInputStream(final InputStream inStream) throws IOException {
        // Get the size of the file
        long streamLength = inStream.available();

        if (streamLength > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) streamLength];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = inStream.read(bytes,
                offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file ");
        }

        // Close the input stream and return bytes
        inStream.close();

        return bytes;
    }

    public void setHotReloadMode(boolean hotReloadMode) {
        this.hotReloadMode = hotReloadMode;
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

    private boolean isHotReloadModeOff() {
        return !hotReloadMode;
    }

    public boolean isHotReloadMode() {
        return hotReloadMode;
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
