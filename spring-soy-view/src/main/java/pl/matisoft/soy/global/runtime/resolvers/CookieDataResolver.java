package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 16:37
 */
@Deprecated
public class CookieDataResolver implements RuntimeDataResolver {

    private static final Logger logger = LoggerFactory.getLogger(CookieDataResolver.class);

    private String prefix = "_request.cookie.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, final SoyMapData root) {
        if (request.getCookies() == null) {
            logger.debug("no cookies!");
            return;
        }

        for (final Cookie cookie : request.getCookies()) {
            if (StringUtils.hasLength(cookie.getName())) {
                final String keyPrefix = prefix + cookie.getName();
                root.put(keyPrefix + ".name", cookie.getName());
                if (StringUtils.hasLength(cookie.getValue())) {
                    root.put(keyPrefix + ".value", cookie.getValue());
                }
                if (StringUtils.hasLength(cookie.getComment())) {
                    root.put(keyPrefix + ".comment", cookie.getComment());
                }
                if (StringUtils.hasLength(cookie.getDomain())) {
                    root.put(keyPrefix + ".domain", cookie.getDomain());
                }
                root.put(keyPrefix + ".maxAge", cookie.getMaxAge());

                if (StringUtils.hasLength(cookie.getPath())) {
                    root.put(keyPrefix + ".path", cookie.getPath());
                }
                root.put(keyPrefix + ".version", cookie.getVersion());
                root.put(keyPrefix + ".secure", cookie.getSecure());
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
