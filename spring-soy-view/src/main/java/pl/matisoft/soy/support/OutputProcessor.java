package pl.matisoft.soy.support;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 06/10/2013
 * Time: 15:55
 */
public interface OutputProcessor {

    void process(Reader reader, Writer writer) throws IOException;

}
