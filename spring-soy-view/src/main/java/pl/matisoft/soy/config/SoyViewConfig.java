package pl.matisoft.soy.config;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 18:02
 */
public class SoyViewConfig {

    public final static String DEFAULT_TEMPLATE_FILES_PATH = "/WEB-INF/templates";
    public final static String DEFAULT_ENCODING = "utf-8";
    public final static boolean DEFAULT_DEBUG_ON = false;
    public final static String DEFAULT_I18N_MESSAGES_PATH = "messages";
    public final static boolean DEFAULT_I18N_FALLBACK_TO_ENGLISH = true;
    public final static boolean DEFAULT_RECURSIVE_TEMPLATES_SEARCH = true;
    public final static String DEFAULT_MODEL_ADJUSTER_KEY = "model";
    public final static int DEFAULT_FUTURE_TIMEOUT_IN_SECONDS = 2 * 60; //2 minutes
    public final static String DEFAULT_CACHE_CONTROL = "public, max-age=86400";
    public final static String DEFAULT_EXPIRE_HEADERS = expireYearHeader();
    public final static String DEFAULT_FILES_EXTENSION = "soy";

    private boolean debugOn = DEFAULT_DEBUG_ON;

    private String encoding = DEFAULT_ENCODING;

    private static String expireYearHeader() {
        final Calendar gregorianCalendar = GregorianCalendar.getInstance(); //TODO think over locale
        final int year = gregorianCalendar.get(GregorianCalendar.YEAR);

        return String.format("Mon, 16 May %s 20:00:00 GMT", year);
    }

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
