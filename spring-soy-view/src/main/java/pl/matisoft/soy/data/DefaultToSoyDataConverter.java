package pl.matisoft.soy.data;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.google.template.soy.data.SoyMapData;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:54
 */
public class DefaultToSoyDataConverter implements ToSoyDataConverter {

    @Override
    public Optional<SoyMapData> toSoyMap(final Object model) throws Exception {
        if (model == null) {
            return Optional.absent();
        }

        return Optional.fromNullable(objectToSoyDataMap(model));
    }

    private static Map<String, ?> toSoyCompatibleMap(Object obj) {
        Object ret = toSoyCompatibleObjects(obj);
        if (!(ret instanceof Map)) {
            throw new IllegalArgumentException("Input should be a Map or POJO.");
        }

        return (Map<String, ?>) ret;
    }

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

    private static Map<String, ?> pojoToMap(Object pojo) {
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

    private static SoyMapData objectToSoyDataMap(Object obj) {
        if (obj == null) {
            return new SoyMapData();
        }
        if (obj instanceof SoyMapData) {
            return (SoyMapData) obj;
        }
        return new SoyMapData(toSoyCompatibleMap(obj));
    }

}
