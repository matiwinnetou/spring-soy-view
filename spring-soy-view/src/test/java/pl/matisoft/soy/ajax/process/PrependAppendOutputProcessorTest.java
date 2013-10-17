package pl.matisoft.soy.ajax.process;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 16.10.13
 * Time: 18:21
 */
public class PrependAppendOutputProcessorTest {

    private PrependAppendOutputProcessor prependAppendOutputProcessor = new PrependAppendOutputProcessor();

    @Before
    public void setUp() throws Exception {
        prependAppendOutputProcessor = new PrependAppendOutputProcessor();
    }

    @Test
    public void testPrependAndAppendDefault() throws Exception {
        prependAppendOutputProcessor.setPrependText("define module abc {");
        prependAppendOutputProcessor.setAppendText("}");
        final String content = "text";

        final StringReader dataReader = new StringReader(content);
        final StringWriter dataWriter = new StringWriter();

        prependAppendOutputProcessor.process(dataReader, dataWriter);

        assertEquals("define module abc {\n" +
                "text\n" +
                "}", dataWriter.toString());
    }

    @Test
    public void testPrependAndAppendPrependAppendNewLineOff() throws Exception {
        prependAppendOutputProcessor.setPrependText("define module abc {");
        prependAppendOutputProcessor.setPrependNewLine(false);
        prependAppendOutputProcessor.setAppendText("}");
        prependAppendOutputProcessor.setAppendNewLine(false);

        final String content = "text";

        final StringReader dataReader = new StringReader(content);
        final StringWriter dataWriter = new StringWriter();

        prependAppendOutputProcessor.process(dataReader, dataWriter);

        assertEquals("define module abc {text}", dataWriter.toString());
    }

}
