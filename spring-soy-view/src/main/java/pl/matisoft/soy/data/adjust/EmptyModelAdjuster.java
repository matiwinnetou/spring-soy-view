package pl.matisoft.soy.data.adjust;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 27.06.13
 * Time: 00:07
 */
public class EmptyModelAdjuster implements ModelAdjuster {

    @Override
    public Object adjust(Object obj) {
        return obj;
    }

}
