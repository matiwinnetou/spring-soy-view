package soy.model;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 22:59
 */
public class EmptyGlobalModelResolver implements GlobalModelResolver {

    public Map<String, ?> resolveData() {
        return Maps.newHashMap();
    }

}
