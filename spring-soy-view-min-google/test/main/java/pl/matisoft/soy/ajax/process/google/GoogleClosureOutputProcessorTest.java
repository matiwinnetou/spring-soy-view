package pl.matisoft.soy.ajax.process.google;

import java.io.StringReader;
import java.io.StringWriter;

import com.google.javascript.jscomp.CompilationLevel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 06/10/2013
 * Time: 17:00
 */
public class GoogleClosureOutputProcessorTest {

    @InjectMocks
    private GoogleClosureOutputProcessor googleClosureOutputProcessor = new GoogleClosureOutputProcessor();

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
        googleClosureOutputProcessor.process(reader, writer);

        final String expected = "function myFunction(){var b=\"\",a;for(a=0;5>a;a++)b=b+\"The number is \"+a+\"\\x3cbr\\x3e\";document.getElementById(\"demo\").innerHTML=b};";
        final String compiled = writer.getBuffer().toString();
        assertEquals(expected, compiled);
    }

    @Test
    public void testAdvancedJsFailbackToOriginal() throws Exception {
        googleClosureOutputProcessor.setCompilationLevel(CompilationLevel.ADVANCED_OPTIMIZATIONS.name());
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
        googleClosureOutputProcessor.process(reader, writer);

        final String compiled = writer.getBuffer().toString();
        assertEquals(js, compiled);
    }

}
