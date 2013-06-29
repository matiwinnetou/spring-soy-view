package pl.matisoft.soy.hash;

import com.google.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 29.06.13
 * Time: 23:58
 */
public class MD5HashFileGenerator implements HashFileGenerator {

    @Override
    public Optional<String> hash(final Optional<URL> url) throws IOException {
        if (!url.isPresent()) {
            return Optional.absent();
        }

        final InputStream is = url.get().openStream();

        return Optional.fromNullable(getMD5Checksum(is));
    }

    public static byte[] createChecksum(final InputStream is) {
        byte[] buffer = new byte[1024];
        try {
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return complete.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getMD5Checksum(final InputStream is) {
        byte[] b = createChecksum(is);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }

        return result;
    }

}
