package soy.data;

import com.google.template.soy.data.SoyMapData;

/**
* Created with IntelliJ IDEA.
* User: mati
* Date: 20/06/2013
* Time: 22:34
*/
public class EmptyToSoyDataConverter implements ToSoyDataConverter {

    @Override
    public SoyMapData toSoyMap(final Object model) throws Exception {
        return new SoyMapData();
    }

}
