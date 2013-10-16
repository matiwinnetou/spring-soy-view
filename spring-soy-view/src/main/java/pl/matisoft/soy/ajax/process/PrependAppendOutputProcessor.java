package pl.matisoft.soy.ajax.process;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 16.10.13
 * Time: 18:11
 *
 * An implementation of an output processor that will prependText and appendText something
 * once a compilation of JavaScript completed.
 *
 * A good example for the reason this class may be needed is requirejs, one may prependText
 * a requirejs module definition and appendText closing, such that a JavaScript response can be wrapped.
 *
 * It is up to a frontend developer to work with a backend developer to define what exactly could be in "prependText"
 * and "appendText" configuration.
 */
public class PrependAppendOutputProcessor implements OutputProcessor {

    /** text to prepend before a compiled JavaScript template */
    private String prependText = "";

    /** whether to add a new line in prepend case */
    private boolean prependNewLine = true;

    /** text to append after a compiled JavaScript template */
    private String appendText = "";

    /** whether to add a new line in append case */
    private boolean appendNewLine = true;

    @Override
    public void process(final Reader reader, final Writer writer) throws IOException {
        final StringBuilder builder = new StringBuilder();

        final String content = IOUtils.toString(reader);

        if (!StringUtils.isEmpty(prependText)) {
            builder.append(prependText);
            if (prependNewLine) {
                builder.append("\n");
            }
        }
        builder.append(content);
        if (!StringUtils.isEmpty(appendText)) {
            if (appendNewLine) {
                builder.append("\n");
            }
            builder.append(appendText);
        }

        writer.write(builder.toString());
    }

    public void setPrependText(String prependText) {
        this.prependText = prependText;
    }

    public void setAppendText(String appendText) {
        this.appendText = appendText;
    }

    public void setPrependNewLine(boolean prependNewLine) {
        this.prependNewLine = prependNewLine;
    }

    public void setAppendNewLine(boolean appendNewLine) {
        this.appendNewLine = appendNewLine;
    }

}
