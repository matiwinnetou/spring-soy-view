package soy.model;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 23:06
 */
public class DefaultGlobalModelResolver implements GlobalModelResolver {

    private Map data;

    @Override
    public Map<String, ?> resolveData() {
        return data;
    }

    public void setData(final Map data) {
        this.data = data;
    }

}
