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
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.template.soy.data.SoyMapData;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

/**
 * An implementation of ToSoyDataConverter that will recursively inspect a
 * passed in model and build a nested structure of SoyMapData objects, which
 * consist only of primitives supported by Soy and thus can be rendered.
 * 
 * An implementation supports passing a model object, which is wrapped in
 * Callable or a Future.
 * 
 * In case a model wrapped in Callable is passed, the implementation will get a
 * wrapped model object during invocation of this method.
 * 
 * In case a model wrapped in Future is passed, the implementation will
 * *synchronously* get a wrapped model object during invocation of this method,
 * assuming a 2 minutes timeout by default. If such a behaviour should be
 * altered, developers are requested to provider their own implementation.
 */
public class DefaultToSoyDataConverter implements ToSoyDataConverter {
	private Matcher<PropertyDescriptor> ignorablePropertiesMatcher;

	public DefaultToSoyDataConverter() {
		setIgnorablePropertiesMatcher(new DefaultIgnorablePropertiesMatcher());
	}

	public void setIgnorablePropertiesMatcher(
			Matcher<PropertyDescriptor> ignorablePropertiesMatcher) {
		this.ignorablePropertiesMatcher = ignorablePropertiesMatcher;
	}

	@Override
	public Optional<SoyMapData> toSoyMap(final Object model) throws Exception {
		if (model == null) {
			return Optional.absent();
		}

		return Optional.fromNullable(objectToSoyDataMap(model));
	}

	@SuppressWarnings("unchecked")
	protected Map<String, ?> toSoyCompatibleMap(final Object obj)
			throws Exception {
		Object ret = toSoyCompatibleObjects(obj);
		if (!(ret instanceof Map)) {
			throw new IllegalArgumentException("Input should be a Map or POJO.");
		}

		return (Map<String, ?>) ret;
	}

	protected Object toSoyCompatibleObjects(Object obj) throws Exception {
		if (obj == null) {
			return obj;
		}

		if (Primitives.isWrapperType(obj.getClass()) || obj instanceof String) {
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

		if (obj instanceof Callable) {
			final Callable<?> callable = (Callable<?>) obj;

			return toSoyCompatibleObjects(callable.call());
		}

		if (obj.getClass().isArray()) {
			return obj;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> pojoMap = (Map<String, Object>) pojoToMap(obj);
		Map<String, Object> newMap = new HashMap<String, Object>(pojoMap.size());
		for (String key : pojoMap.keySet()) {
			newMap.put(key, toSoyCompatibleObjects(pojoMap.get(key)));
		}

		return newMap;
	}

	protected Map<String, ?> pojoToMap(final Object pojo) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(pojo.getClass());

			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (!isIgnorable(pd)) {
					map.put(pd.getName(), pd.getReadMethod().invoke(pojo));
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return map;
	}

	protected SoyMapData objectToSoyDataMap(Object obj) throws Exception {
		if (obj == null) {
			return new SoyMapData();
		}
		if (obj instanceof SoyMapData) {
			return (SoyMapData) obj;
		}
		return new SoyMapData(toSoyCompatibleMap(obj));
	}

	protected boolean isIgnorable(PropertyDescriptor pd) {
		return ignorablePropertiesMatcher.matches(pd);
	}

	private static class DefaultIgnorablePropertiesMatcher extends
			AbstractMatcher<PropertyDescriptor> implements Serializable {
		private static final long serialVersionUID = 0;
		private static final List<String> ignorableProperties = unmodifiableList(newArrayList(
				"class", "metaClass"));

		@Override
		public boolean matches(PropertyDescriptor pd) {
			return pd.getReadMethod() == null
					|| ignorableProperties.contains(pd.getName());
		}
	}
}
