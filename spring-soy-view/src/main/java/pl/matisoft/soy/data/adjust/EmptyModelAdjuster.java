package pl.matisoft.soy.data.adjust;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 27.06.13
 * Time: 00:07
 *
 * An implementation that returns the same object as passed in
 */
public class EmptyModelAdjuster implements ModelAdjuster {

    @Override
    public @Nullable Object adjust(@Nullable Object obj) {
        return obj;
    }

}
