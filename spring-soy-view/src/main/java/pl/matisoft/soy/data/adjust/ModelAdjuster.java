package pl.matisoft.soy.data.adjust;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 27.06.13
 * Time: 00:07
 *
 * An interface that defines a possibility to perform a model transformation before
 * that model is passed to ToSoyDataConverter.
 */
public interface ModelAdjuster {

    @Nullable Object adjust(@Nullable Object obj);

}
