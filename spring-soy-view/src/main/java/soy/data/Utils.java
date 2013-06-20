package soy.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgBundleImpl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Various utility methods.
 * 
 * @author chris
 */
public class Utils {

    /**
     * Convert all data stored in a POJO or Map<String, Object> into a format compatible with Soy's DataMap.
     * This method will convert nested POJOs to a corresponding nested Maps.
     * 
     * @param obj The Map or POJO who's data should be converted.
     * @return A Map of data compatible with Soy.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ?> toSoyCompatibleMap(Object obj) {
        Object ret = toSoyCompatibleObjects(obj);
        if (!(ret instanceof Map)) {
            throw new IllegalArgumentException("Input should be a Map or POJO.");
        }

        return (Map<String, ?>) ret;
    }
    
    /**
     * Convert an object (or graph of objects) to types compatible with Soy (data able to be stored in SoyDataMap).
     * This will convert:
     *    - POJOs to Maps
     *    - Iterables to Lists
     *    - all strings and primitives remain as is.
     *    
     * @param obj The object to convert.
     * @return The object converted (in applicable).
     */
    private static Object toSoyCompatibleObjects(Object obj) {
        
        if (obj == null) {
            return obj;
        }

        if (Primitives.isWrapperType(obj.getClass())
                || obj.getClass().isPrimitive()
                || obj instanceof String) {
            return obj;
        }

        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, Object> newMap = new HashMap<String, Object>(map.size());
            for (String key : map.keySet()) {
                newMap.put(key, toSoyCompatibleObjects(map.get(key)));
            }
            return newMap;
        }

        if (obj instanceof Iterable<?>) {
            List<Object> list = Lists.newArrayList();
            for (Object subValue : ((Iterable<?>) obj)) {
                list.add(toSoyCompatibleObjects(subValue));
            }            
            return list;
        }
        
        if (obj.getClass().isArray()) {
            return obj;
        }

        // At this point we must assume it's a POJO so map-it.
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> pojoMap = (Map<String, Object>) pojoToMap(obj);
            Map<String, Object> newMap = new HashMap<String, Object>(pojoMap.size());
            for (String key : pojoMap.keySet()) {
                newMap.put(key, toSoyCompatibleObjects(pojoMap.get(key)));
            }
            return newMap;
        }
    }
     
    /**
     * Convert a Java POJO (aka Bean) to a Map<String, Object>.
     * @param pojo The Java pojo object with standard getters and setters.
     * @return Pojo data as a Map.
     */
    public static Map<String, ?> pojoToMap(Object pojo) {

        Map<String, Object> map = new HashMap<String, Object>();
        
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(pojo.getClass());

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    map.put(pd.getName(), pd.getReadMethod().invoke(pojo));
                }
            }
        
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    
    /**
     * Convert (at least attempt) any object to a SoyMapData instance. 
     * @param obj The object to convert.
     * @return The created SoyMapData.
     */
    public static SoyMapData objectToSoyDataMap(Object obj) {
    	if (obj == null) {
    		return new SoyMapData();
    	}
    	if (obj instanceof SoyMapData) {
    		return (SoyMapData) obj;
    	}
    	return new SoyMapData(toSoyCompatibleMap(obj));
    }

}
