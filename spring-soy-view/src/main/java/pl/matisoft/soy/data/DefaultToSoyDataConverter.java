//(c) Copyright 2011-2013 PaperCut Software Int. Pty. Ltd. http://www.papercut.com/
//
//        Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//        You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//        Unless required by applicable law or agreed to in writing, software
//        distributed under the License is distributed on an "AS IS" BASIS,
//        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//        See the License for the specific language governing permissions and
//        limitations under the License.

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: Copyright 2011-2013 PaperCut Software Int. Pty. Ltd. http://www.papercut.com/
 * Licensed under the Apache License, Version 2.0 (the "License");
 * https://github.com/codedance/silken
 */
public class DefaultToSoyDataConverter implements ToSoyDataConverter {

    private long futureTimeOutInSeconds = 2 * 60; // 2 minutes

    @Override
    public Optional<SoyMapData> toSoyMap(final Object model) throws Exception {
        if (model == null) {
            return Optional.absent();
        }

        return Optional.fromNullable(objectToSoyDataMap(model));
    }

    private Map<String, ?> toSoyCompatibleMap(final Object obj) throws InterruptedException, ExecutionException, TimeoutException {
        Object ret = toSoyCompatibleObjects(obj);
        if (!(ret instanceof Map)) {
            throw new IllegalArgumentException("Input should be a Map or POJO.");
        }

        return (Map<String, ?>) ret;
    }

    private Object toSoyCompatibleObjects(Object obj) throws InterruptedException, ExecutionException, TimeoutException {
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

        if (obj instanceof Future<?>) {
            final Future future = (Future) obj;

            return toSoyCompatibleObjects(future.get(futureTimeOutInSeconds, TimeUnit.SECONDS));
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

    private static Map<String, ?> pojoToMap(final Object pojo) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(pojo.getClass());

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

    private SoyMapData objectToSoyDataMap(Object obj) throws InterruptedException, ExecutionException, TimeoutException {
        if (obj == null) {
            return new SoyMapData();
        }
        if (obj instanceof SoyMapData) {
            return (SoyMapData) obj;
        }
        return new SoyMapData(toSoyCompatibleMap(obj));
    }

    public void setFutureTimeOutInSeconds(long futureTimeOutInSeconds) {
        this.futureTimeOutInSeconds = futureTimeOutInSeconds;
    }

}
