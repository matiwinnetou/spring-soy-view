package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 09:28
 */
public class RenderRequest {

    private final Optional<SoyTofu> compiledTemplates;
    private final String templateName;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final Object model;
    private final Optional<SoyMapData> soyModel;

    private final Optional<SoyMapData> globalRuntimeModel;
    private final Optional<SoyMsgBundle> soyMsgBundle;

    private RenderRequest(final Builder builder) {
        this.compiledTemplates = builder.compiledTemplates;
        this.templateName = builder.templateName;
        this.request = builder.request;
        this.response = builder.response;
        this.model = builder.model;
        this.soyModel = builder.soyModel;
        this.globalRuntimeModel = builder.globalRuntimeModel;
        this.soyMsgBundle = builder.soyMsgBundle;
    }

    public Optional<SoyMapData> getSoyModel() {
        return soyModel;
    }

    public Optional<SoyMapData> getGlobalRuntimeModel() {
        return globalRuntimeModel;
    }

    public Optional<SoyMsgBundle> getSoyMsgBundle() {
        return soyMsgBundle;
    }

    public Optional<SoyTofu> getCompiledTemplates() {
        return compiledTemplates;
    }

    public String getTemplateName() {
        return templateName;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Object getModel() {
        return model;
    }

    public static class Builder {

        private Optional<SoyTofu> compiledTemplates;
        private String templateName;
        private HttpServletRequest request;
        private HttpServletResponse response;
        private Object model;
        private Optional<SoyMapData> soyModel = Optional.absent();
        private Optional<SoyMapData> globalRuntimeModel = Optional.absent();
        private Optional<SoyMsgBundle> soyMsgBundle = Optional.absent();

        public Builder compiledTemplates(Optional<SoyTofu> compiledTemplates) {
            this.compiledTemplates = compiledTemplates;
            return this;
        }

        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder response(HttpServletResponse response) {
            this.response = response;
            return this;
        }

        public Builder request(HttpServletRequest request) {
            this.request = request;
            return this;
        }

        public Builder model(Object model) {
            this.model = model;
            return this;
        }

        public Builder soyMapData(Optional<SoyMapData> soyMapData) {
            this.soyModel = soyMapData;
            return this;
        }

        public Builder globalRuntimeModel(Optional<SoyMapData> globalRuntimeModel) {
            this.globalRuntimeModel = globalRuntimeModel;
            return this;
        }

        public Builder soyMsgBundle(Optional<SoyMsgBundle> soyMsgBundle) {
            this.soyMsgBundle = soyMsgBundle;
            return this;
        }

        public RenderRequest build() {
            return new RenderRequest(this);
        }

    }

}
