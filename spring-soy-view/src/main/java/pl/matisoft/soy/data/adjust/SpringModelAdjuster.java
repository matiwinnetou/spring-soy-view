package pl.matisoft.soy.data.adjust;

import org.springframework.ui.Model;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 27.06.13
 * Time: 00:08
 *
 * An implementation of ModelAdjuster that will look up a model key and return it
 * based on a configuration parameter.
 */
public class SpringModelAdjuster implements ModelAdjuster {

    private String modelKey = "model";

    @Override
    public @Nullable Object adjust(@Nullable final Object obj) {
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

    public void setModelKey(final String modelKey) {
        this.modelKey = modelKey;
    }

    public String getModelKey() {
        return modelKey;
    }

}
