package soy.data;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyMapData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:28
 */
public class PojoReflectionToSoyDataConverter implements ToSoyDataConverter {

    private Map<String, ?> convert(Object model) throws Exception {
        Method[] methods = model.getClass().getMethods();
        Map<String, Object> soyParameters = new HashMap<String, Object>();

        for (final Method method : methods) {
            String name = method.getName();
            if (!name.equals("getClass") && name.startsWith("get")) {
                Object parameter = method.invoke(model);
                String parameterName = name.substring(3, 4).toLowerCase() + name.substring(4);

                if (parameter instanceof List) {
                    List<Object> newParameter = new ArrayList<Object>();
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    List<Object> asList = (List) parameter;
                    for (Object p : asList) {
                        try {
                            SoyData soyData = SoyData.createFromExistingData(p);
                            newParameter.add(soyData);
                        } catch (SoyDataException e) {
                            Map<String, ?> asMap = convert(p);
                            newParameter.add(asMap);
                        }
                    }
                    soyParameters.put(parameterName, newParameter);
                } else {
                    try {
                        SoyData soyData = SoyData.createFromExistingData(parameter);
                        soyParameters.put(parameterName, soyData);
                    } catch (SoyDataException e) {
                        Map<String, ?> asMap = convert(parameter);
                        soyParameters.put(parameterName, asMap);
                    }
                }
            }
        }

        return soyParameters;
    }

    @Override
    public SoyMapData toSoyMap(final Object model) throws Exception {
        final Map<String, ?> map = convert(model);

        return new SoyMapData(map);
    }

}
