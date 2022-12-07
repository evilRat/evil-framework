package com.evil.framework.web.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.Contact;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web配置类
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@Validated
@Data
@ConfigurationProperties(prefix = "framework.web")
public class WebProperties {

    /**
     * 请求Url前缀
     */
    private String requestUrlPrefix;

    private LoggerFilterProperties filter = new LoggerFilterProperties();

    @Valid
    private SwaggerProperties swagger = new SwaggerProperties();

    @Data
    public static class LoggerFilterProperties {

        private List<String> excludePatterns = new ArrayList<>();

        private Set<String> extraHeaders = new HashSet<>();

    }

    @Data
    public static class SwaggerProperties {

        /**
         * Swagger api private final Contact contact;
         */
        private String basePackage;

        /**
         * Api version
         */
        private String version;

        /**
         * Api title
         */
        private String title;

        /**
         * Api description
         */
        private String description;

        /**
         * Api the terms of service url
         */
        private String termsOfServiceUrl;

        /**
         * Api license information for this API
         */
        private String license;

        /**
         * Api license Url for this API
         */
        private String licenseUrl;

        private Contact contact;

        private Path path = new Path();

        @Data
        public static class Path {

            /**
             * @see PathSelectors#regex(String)
             */
            private String regex;

            /**
             * @see PathSelectors#ant(String)
             */
            private String antPath;

        }
    }

}
