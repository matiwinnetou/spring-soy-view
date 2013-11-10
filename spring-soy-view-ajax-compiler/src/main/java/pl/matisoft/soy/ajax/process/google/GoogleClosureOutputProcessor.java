package pl.matisoft.soy.ajax.process.google;

import javax.annotation.concurrent.ThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pl.matisoft.soy.ajax.process.OutputProcessor;
import pl.matisoft.soy.config.SoyViewConfigDefaults;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 06/10/2013
 * Time: 15:57
 */
@ThreadSafe
public class GoogleClosureOutputProcessor implements OutputProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GoogleClosureOutputProcessor.class);

    private String encoding = SoyViewConfigDefaults.DEFAULT_ENCODING;

    private CompilationLevel compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;

    private boolean logCompilerErrors = true;

    private boolean logCompilerWarnings = false;

    @Override
    public void process(final Reader reader, final Writer writer) throws IOException {
        final String originalJsSourceCode = IOUtils.toString(reader);
        try {
            Compiler.setLoggingLevel(Level.SEVERE);
            final Compiler compiler = new Compiler();
            final CompilerOptions compilerOptions = newCompilerOptions();
            compilationLevel.setOptionsForCompilationLevel(compilerOptions);
            //make it play nice with GAE
            compiler.disableThreads();
            compiler.initOptions(compilerOptions);

            final SourceFile input = SourceFile.fromInputStream("dummy.js", new ByteArrayInputStream(originalJsSourceCode.getBytes(getEncoding())));
            final Result result = compiler.compile(Lists.<SourceFile>newArrayList(), Lists.newArrayList(input), compilerOptions);

            logWarningsAndErrors(result);

            boolean origFallback = false;
            if (result.success) {
                final String minifiedJsSourceCode = compiler.toSource();
                if (StringUtils.isEmpty(minifiedJsSourceCode)) {
                    origFallback = true;
                } else {
                    writer.write(minifiedJsSourceCode);
                }
            } else {
                origFallback = true;
            }
            if (origFallback) {
                writer.write(originalJsSourceCode);
            }
        } finally {
            reader.close();
            writer.close();
        }
    }

    private void logWarningsAndErrors(final Result result) {
        if (logCompilerErrors && result.errors.length > 0) {
            for (JSError jsError : result.errors)
            logger.warn("js error:" + jsError.toString());
        }
        if (logCompilerWarnings && result.warnings.length > 0) {
            for (JSError jsError : result.warnings)
                logger.warn("js warn:" + jsError.toString());
        }
    }

    /**
     * @return default {@link com.google.javascript.jscomp.CompilerOptions} object to be used by compressor.
     */
    protected CompilerOptions newCompilerOptions() {
        final CompilerOptions options = new CompilerOptions();

        /**
         * According to John Lenz from the Closure Compiler project, if you are using the Compiler API directly, you
         * should specify a CodingConvention. {@link http://code.google.com/p/wro4j/issues/detail?id=155}
         */
        options.setCodingConvention(new ClosureCodingConvention());
        options.setOutputCharset(getEncoding());
        //set it to warning, otherwise compiler will fail
        options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.WARNING);

        return options;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setCompilationLevel(String compilationLevel) {
        this.compilationLevel = CompilationLevel.valueOf(compilationLevel);
    }

    public CompilationLevel getCompilationLevel() {
        return compilationLevel;
    }

    public void setLogCompilerErrors(boolean logCompilerErrors) {
        this.logCompilerErrors = logCompilerErrors;
    }

    public void setLogCompilerWarnings(boolean logCompilerWarnings) {
        this.logCompilerWarnings = logCompilerWarnings;
    }

}
