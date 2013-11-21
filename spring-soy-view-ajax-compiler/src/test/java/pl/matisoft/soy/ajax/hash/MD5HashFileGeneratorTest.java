package pl.matisoft.soy.ajax.hash;

import java.net.URL;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 17.10.13
 * Time: 22:33
 */
public class MD5HashFileGeneratorTest {

    private MD5HashFileGenerator hashFileGenerator = new MD5HashFileGenerator();

    @Before
    public void setUp() throws Exception {
        hashFileGenerator = new MD5HashFileGenerator();
    }

    @Test
    public void testDebugByDefault() throws Exception {
        Assert.assertFalse("debug should be off by default", hashFileGenerator.isHotReloadMode());
    }

    @Test
    public void testDefaultWriteExpireUnit() throws Exception {
        Assert.assertEquals("default write expire unit should be", "DAYS", hashFileGenerator.getExpireAfterWriteUnit());
    }

    @Test
    public void testDefaultExpireTime() throws Exception {
        Assert.assertEquals("default expire in days is 1 day", 1, hashFileGenerator.getExpireAfterWrite());
    }

    @Test
    public void testDefaultMaxEntries() throws Exception {
        Assert.assertEquals("default cache size is 10000", 10000, hashFileGenerator.getCacheMaxSize());
    }

    @Test
    public void testAbsentUrlReturnsAbsentHash() throws Exception {
        Assert.assertFalse("absent url results in absent hash", hashFileGenerator.hash(Optional.<URL>absent()).isPresent());
    }

    @Test
    public void testReturnedHashMatchesDebugOff() throws Exception {
        final URL url = getClass().getClassLoader().getResource("templates/template1.soy");
        final Optional<String> hash =  hashFileGenerator.hash(Optional.of(url));
        Assert.assertTrue(hash.isPresent());
        Assert.assertEquals("md5 hash should match", "db9e0c4790bafb99c8c8cb9462279a75", hash.get());
    }

    @Test
    public void testReturnedHashMatchesDebugOffCacheSound() throws Exception {
        final URL url = getClass().getClassLoader().getResource("templates/template1.soy");
        hashFileGenerator.hash(Optional.of(url));
        Assert.assertEquals("a cache size should be 1", 1L, hashFileGenerator.cache.size());
        Assert.assertEquals("cache should contain an entry", "db9e0c4790bafb99c8c8cb9462279a75", hashFileGenerator.cache.getIfPresent(url));
    }

    @Test
    public void testReturnedHashMatchesDebugOn() throws Exception {
        hashFileGenerator.setHotReloadMode(true);
        hashFileGenerator.afterPropertiesSet();
        final URL url = getClass().getClassLoader().getResource("templates/template1.soy");
        final Optional<String> hash =  hashFileGenerator.hash(Optional.of(url));
        Assert.assertTrue(hash.isPresent());
        Assert.assertEquals("md5 hash should match", "db9e0c4790bafb99c8c8cb9462279a75", hash.get());
    }

    @Test
    public void testReturnedHashMatchesDebugOnCacheSound() throws Exception {
        hashFileGenerator.setHotReloadMode(true);
        final URL url = getClass().getClassLoader().getResource("templates/template1.soy");
        hashFileGenerator.hash(Optional.of(url));
        Assert.assertEquals("debug true, caching should be empty", 0L, hashFileGenerator.cache.size());
    }

    @Test
    public void testReturnedHashMatchesDebugOffCacheWillNotExplode() throws Exception {
        hashFileGenerator.setCacheMaxSize(1);
        hashFileGenerator.afterPropertiesSet();
        final URL url1 = getClass().getClassLoader().getResource("templates/template1.soy");
        hashFileGenerator.hash(Optional.of(url1));

        final URL url2 = getClass().getClassLoader().getResource("templates/template2.soy");
        hashFileGenerator.hash(Optional.of(url2));

        Assert.assertEquals("cache size should not extend 1", 1L, hashFileGenerator.cache.size());
    }

    @Test
    public void testMultiHash() throws Exception {
        hashFileGenerator.setCacheMaxSize(1);
        hashFileGenerator.afterPropertiesSet();
        final URL url1 = getClass().getClassLoader().getResource("templates/template1.soy");
        final URL url2 = getClass().getClassLoader().getResource("templates/template2.soy");

        final Optional<String> hash = hashFileGenerator.hashMulti(Lists.newArrayList(url1, url2));
        Assert.assertTrue("should be present", hash.isPresent());
        Assert.assertEquals("should be equal to hash val", "d41d8cd98f00b204e9800998ecf8427e", hash.get());
    }

}
