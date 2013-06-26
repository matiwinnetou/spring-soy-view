package pl.matisoft.soy.data.adjust;

import org.springframework.ui.Model;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 27.06.13
 * Time: 00:08
 */
public class SpringModelAdjuster implements ModelAdjuster {

    private String modelKey = "model";

    @Override
    public Object adjust(Object obj) {
        if (obj instanceof Model) {
            final Model model = (Model) obj;

            return model.asMap().get("model");
        }
        if (obj instanceof Map) {
            final Map map = (Map) obj;

            return map.get("model");
        }

        return obj;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

}
