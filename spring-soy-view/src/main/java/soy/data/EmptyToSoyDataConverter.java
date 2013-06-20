package soy.data;

import java.util.Map;

/**
* Created with IntelliJ IDEA.
* User: mati
* Date: 20/06/2013
* Time: 22:34
*/
public class EmptyToSoyDataConverter implements ToSoyDataConverter {

    @Override
    public Map<String, ?> convert(final Object model) throws Exception {
        return (Map<String, ?>) model;
    }

}
