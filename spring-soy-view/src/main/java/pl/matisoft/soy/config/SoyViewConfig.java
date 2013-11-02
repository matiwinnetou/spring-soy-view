package pl.matisoft.soy.config;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 18:02
 */
public class SoyViewConfig {

    /**
     * Default directory for templates
     */
    public final static String DEFAULT_TEMPLATE_FILES_PATH = "/WEB-INF/templates";

    /**
     * Default encoding
     */
    public final static String DEFAULT_ENCODING = "utf-8";

    public final static boolean DEFAULT_DEBUG_ON = false;

    /**
     * Default soy files extension
     */
    public final static String DEFAULT_FILES_EXTENSION = "soy";

    /**
     * Whether templates should be precompiled on the startup of application
     */
    public final static boolean DEFAULT_PRECOMPILE_TEMPLATES = false;

    /**
     * Special soy prefix to indicate that this template if meant to be rendered by soy template renderer
     */
    public final static String DEFAULT_SOY_PREFIX = "soy:";

    private boolean debugOn = DEFAULT_DEBUG_ON;

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
