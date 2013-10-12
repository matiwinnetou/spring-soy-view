package pl.matisoft.soy.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 14:54
 */
public class YahooOutputProcessorTest {

    @InjectMocks
    private YahooOutputProcessor yahooOutputProcessor = new YahooOutputProcessor();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSimpleJs() throws Exception {
        final String js = "function myFunction()\n" +
                "{\n" +
                "var x=\"\",i;\n" +
                "for (i=0;i<5;i++)\n" +
                "  {\n" +
                "  x=x + \"The number is \" + i + \"<br>\";\n" +
                "  }\n" +
                "document.getElementById(\"demo\").innerHTML=x;\n" +
                "}";
        final StringReader reader = new StringReader(js);

        final StringWriter writer = new StringWriter();
        yahooOutputProcessor.process(reader, writer);

        final String expected = "function myFunction(){var x=\"\",i;for(i=0;i<5;i++){x=x+\"The number is \"+i+\"<br>\";}document.getElementById(\"demo\").innerHTML=x;}";
        final String compiled = writer.getBuffer().toString();
        assertEquals(expected, compiled);
    }

}
