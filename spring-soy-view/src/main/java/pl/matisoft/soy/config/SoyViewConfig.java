package pl.matisoft.soy.config;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 18:02
 */
public class SoyViewConfig {

    public final static String DEFAULT_ENCODING = "utf-8";

    private boolean debugOn;

    private String encoding = DEFAULT_ENCODING;

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(final boolean debugOn) {
        this.debugOn = debugOn;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
