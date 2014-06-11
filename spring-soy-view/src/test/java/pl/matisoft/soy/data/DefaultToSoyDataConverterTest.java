package pl.matisoft.soy.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyMapData;

public class DefaultToSoyDataConverterTest {
	private final DefaultToSoyDataConverter objectToSoyDataConverter = new DefaultToSoyDataConverter();
	private final String key = "key";
	private final String keyValue = "keyValue";

	@Test
	public void testToSoyMapWhenNoModel() throws Exception {
		Optional<SoyMapData> soyData = objectToSoyDataConverter.toSoyMap(null);

		assertFalse(soyData.isPresent());
	}

	@Test
	public void testToSoyMapWhenModelIsPresent() throws Exception {
		Map<String, String> model = new HashMap<String, String>();
		model.put(key, keyValue);
		Optional<SoyMapData> soyData = objectToSoyDataConverter.toSoyMap(model);

		assertTrue(soyData.isPresent());

		SoyMapData data = soyData.get();

		assertEquals(keyValue, data.get(key).stringValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToSoyCompatibleMapWhenNotMap() throws Exception {
		String test = "won't work";
		objectToSoyDataConverter.toSoyCompatibleMap(test);
	}

	@Test
	public void testToSoyCompatibleObjectsWhenObjectIsNull() throws Exception {
		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(null);

		assertNull(testObject);
	}

	@Test
	public void testToSoyCompatibleObjectsWhenObjectIsString() throws Exception {
		String testInput = "test";
		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(testInput);

		assertEquals("test", testObject.toString());
	}

	@Test
	public void testToSoyCompatibleObjectsWhenObjectIsWrapperType()
			throws Exception {
		Integer testInput = 42;
		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(testInput);

		assertTrue(testObject instanceof Integer);
		assertEquals(testInput, (Integer) testObject);
	}

	@Test
	public void testToSoyCompatibleObjectsWhenObjectIsIterable()
			throws Exception {
		List<String> testInput = new ArrayList<String>();
		testInput.add("test");
		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(testInput);

		assertTrue(testObject instanceof Iterable<?>);
		@SuppressWarnings("unchecked")
		List<String> actual = (List<String>) testObject;
		assertEquals(1, actual.size());

		assertEquals("test", actual.get(0));
	}

	@Test
	public void testToSoyCompatibleObjectsWhenObjectIsArray() throws Exception {
		String[] testInput = new String[] { "test" };
		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(testInput);

		assertTrue(testObject.getClass().isArray());

		String[] actual = (String[]) testObject;
		assertEquals(1, actual.length);

		assertEquals("test", actual[0]);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testToSoyCompatibleObjectsWhenObjectIsPojo() throws Exception {
		PojoTest testInput = new PojoTest();
		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(testInput);

		assertTrue(testObject instanceof Map);
		assertEquals("test", ((Map<String, String>) testObject).get("name"));
	}

	@Test
	public void testObjectToSoyDataMapWhenObjectIsNull() throws Exception {
		SoyMapData testObject = objectToSoyDataConverter
				.objectToSoyDataMap(null);

		assertTrue(testObject instanceof SoyMapData);
	}

	@Test
	public void testObjectToSoyDataMapWhenObjectIsSoyMapData() throws Exception {
		SoyMapData testInput = new SoyMapData();
		testInput.putSingle(key, SoyData.createFromExistingData(keyValue));
		SoyMapData testObject = objectToSoyDataConverter
				.objectToSoyDataMap(testInput);

		assertTrue(testObject instanceof SoyMapData);
		assertEquals(keyValue, ((SoyMapData) testObject).get(key).stringValue());
	}

	@Test
	public void testObjectToSoyDataMapWhenObjectIsMap() throws Exception {
		Map<String, String> testInput = new HashMap<String, String>();
		testInput.put(key, keyValue);
		SoyMapData testObject = objectToSoyDataConverter
				.objectToSoyDataMap(testInput);

		assertTrue(testObject instanceof SoyMapData);
		assertEquals(keyValue, ((SoyMapData) testObject).get(key).stringValue());
	}

	@Test
	public void testObjectToSoyDataMapWhenObjectIsCallable() throws Exception {
		Callable<String> testInput = new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "test";
			}
		};

		Object testObject = objectToSoyDataConverter
				.toSoyCompatibleObjects(testInput);

		assertEquals("test", testObject.toString());
	}

	private static class PojoTest {
		String name = "test";

		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
		public void setName(String name) {
			this.name = name;
		}
	}
}