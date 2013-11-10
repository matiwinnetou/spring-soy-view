package pl.matisoft.soy.ajax.process.yahoo;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matisoft.soy.ajax.process.OutputProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 14:36
 */
public class YahooOutputProcessor implements OutputProcessor {

    private static final Logger logger = LoggerFactory.getLogger(YahooOutputProcessor.class);

    // options of YUI compressor
    private int linebreakpos = -1;

    /**
     * Renames variables.
     */
    boolean munge = false;

    boolean verbose = false;

    boolean preserveAllSemiColons = true;

    boolean disableOptimizations = false;

    private boolean logWarn = false;

    private boolean logError = true;

    @Override
    public void process(Reader reader, Writer writer) throws IOException {
        final JavaScriptCompressor javaScriptCompressor = new JavaScriptCompressor(reader, new ErrorReporter() {
            public void warning(final String message, final String sourceName, final int line, final String lineSource,
                                final int lineOffset) {
                if (!logWarn) {
                    return;
                }
                if (line < 0) {
                    logger.warn("\n[WARNING] " + message);
                } else {
                    logger.warn("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
                }
            }


            public void error(final String message, final String sourceName, final int line, final String lineSource,
                              final int lineOffset) {
                if (!logError) {
                    return;
                }
                if (line < 0) {
                    logger.error("\n[ERROR] " + message);
                } else {
                    logger.error("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
                }
            }

            public EvaluatorException runtimeError(final String message, final String sourceName, final int line,
                                                   final String lineSource, final int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
        });

        javaScriptCompressor.compress(writer, linebreakpos, munge, verbose, preserveAllSemiColons, disableOptimizations);
    }

    public void setMunge(final boolean munge) {
        this.munge = munge;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    public void setPreserveAllSemiColons(final boolean preserveAllSemiColons) {
        this.preserveAllSemiColons = preserveAllSemiColons;
    }

    public void setDisableOptimizations(final boolean disableOptimizations) {
        this.disableOptimizations = disableOptimizations;
    }

    public void setLogWarn(final boolean logWarn) {
        this.logWarn = logWarn;
    }

    public void setLogError(final boolean logError) {
        this.logError = logError;
    }

    public void setLinebreakpos(int linebreakpos) {
        this.linebreakpos = linebreakpos;
    }

}
