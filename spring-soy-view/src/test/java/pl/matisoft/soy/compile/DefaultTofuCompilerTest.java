package pl.matisoft.soy.compile;

import java.net.URL;
import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgBundleImpl;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import com.google.template.soy.tofu.SoyTofu;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.10.13
 * Time: 17:23
 */
public class DefaultTofuCompilerTest {

    @InjectMocks
    private DefaultTofuCompiler defaultTofuCompiler = new DefaultTofuCompiler();

    @Mock
    private CompileTimeGlobalModelResolver compileTimeGlobalModelResolver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void debugDefault() throws Exception {
        Assert.assertFalse("debug should be off", defaultTofuCompiler.isHotReloadMode());
    }

    @Test
    public void debugWorks() throws Exception {
        defaultTofuCompiler.setHotReloadMode(true);
        Assert.assertTrue("debug should be on", defaultTofuCompiler.isHotReloadMode());
    }

    @Test
    public void testSoyJsSrcOptionsNotNull() throws Exception {
        Assert.assertNotNull(defaultTofuCompiler.getSoyJsSrcOptions());
    }

    @Test
    public void testName() throws Exception {
        final SoyJsSrcOptions soyJsSrcOptions = new SoyJsSrcOptions();
        defaultTofuCompiler.setSoyJsSrcOptions(soyJsSrcOptions);
        Assert.assertEquals(soyJsSrcOptions, defaultTofuCompiler.getSoyJsSrcOptions());
    }

    @Test
    public void testCompileSoyToBinaryWithEmptyCompileTimeModel() throws Exception {
        when(compileTimeGlobalModelResolver.resolveData()).thenReturn(Optional.<SoyMapData>absent());
        final URL template1 = getClass().getClassLoader().getResource("templates/template1.soy");
        final SoyTofu tofu = defaultTofuCompiler.compile(Lists.newArrayList(template1));
        Assert.assertNotNull(tofu);
    }

    @Test
    public void testCompileSoyToJsWithCompileTimeModel() throws Exception {
        final SoyMapData soyMapData = new SoyMapData();
        soyMapData.put("test1", "test2");
        when(compileTimeGlobalModelResolver.resolveData()).thenReturn(Optional.of(soyMapData));
        final URL template1 = getClass().getClassLoader().getResource("templates/template1.soy");
        final SoyTofu tofu = defaultTofuCompiler.compile(Lists.newArrayList(template1));
        Assert.assertNotNull("tofu object should not be null", tofu);
    }

    @Test
    public void testCompileSoyToJsWithEmptyCompileTimeModel() throws Exception {
        when(compileTimeGlobalModelResolver.resolveData()).thenReturn(Optional.<SoyMapData>absent());
        final URL template1 = getClass().getClassLoader().getResource("templates/template1.soy");
        final Collection<String> tofu = defaultTofuCompiler.compileToJsSrc(Lists.newArrayList(template1), null);
        Assert.assertFalse("tofu compiled collection should not be empty", tofu.isEmpty());
        Assert.assertEquals("tofu compiled collection.size should be 1", 1, tofu.size());
    }

    @Test
    public void testCompileSoyToBinaryWithCompileTimeModel() throws Exception {
        final SoyMapData soyMapData = new SoyMapData();
        soyMapData.put("test1", "test2");
        when(compileTimeGlobalModelResolver.resolveData()).thenReturn(Optional.of(soyMapData));
        when(compileTimeGlobalModelResolver.resolveData()).thenReturn(Optional.<SoyMapData>absent());
        final URL template1 = getClass().getClassLoader().getResource("templates/template1.soy");
        final URL template2 = getClass().getClassLoader().getResource("templates/template2.soy");
        final Collection<String> tofu = defaultTofuCompiler.compileToJsSrc(Lists.newArrayList(template1, template2), null);
        Assert.assertFalse("tofu compiled collection should not be empty", tofu.isEmpty());
        Assert.assertEquals("tofu compiled collection.size should be 2", 2, tofu.size());
    }

    @Test
    //FIX IT
    public void testCompileSoyToBinaryWithCompileTimeModelAndSoyMsgBundle() throws Exception {
        final String compiledJs =
                "// This file was automatically generated from template3.soy.\n" +
                "// Please don't edit this file by hand.\n" +
                "\n" +
                "if (typeof soy == 'undefined') { var soy = {}; }\n" +
                "if (typeof soy.example == 'undefined') { soy.example = {}; }\n" +
                "\n" +
                "\n" +
                "soy.example.test = function(opt_data, opt_ignored) {\n" +
                "  return 'notTranslated';\n" +
                "};\n";
        final SoyMsgPart soyMsgPart = new SoyMsgRawTextPart("translated");
        final SoyMsg soyMsg = new SoyMsg(1, 1, "pl_PL", "test1", "desc", false, null, null, true, Lists.newArrayList(soyMsgPart));
        final SoyMsgBundle soyMsgBundle = new SoyMsgBundleImpl("pl_PL", Lists.<SoyMsg>newArrayList(soyMsg));
        when(compileTimeGlobalModelResolver.resolveData()).thenReturn(Optional.<SoyMapData>absent());
        final URL template1 = getClass().getClassLoader().getResource("templates/template3.soy");
        final Collection<String> tofu = defaultTofuCompiler.compileToJsSrc(Lists.newArrayList(template1), soyMsgBundle);
        Assert.assertFalse("tofu compiled collection should not be empty", tofu.isEmpty());
        Assert.assertEquals("tofu compiled collection.size should be 1", 1, tofu.size());
        Assert.assertEquals("compiledJs should be equal", compiledJs, tofu.iterator().next());
    }

}
