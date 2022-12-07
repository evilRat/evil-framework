package com.evil.framework.web.swagger;

import com.evil.framework.web.autoconfigure.WebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Optional;

/**
 * Swagger configuration class
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(value = "framework.web.swagger.enable",matchIfMissing = true,havingValue = "ture")
public class SwaggerConfig {

    private final WebProperties.SwaggerProperties properties;

    public SwaggerConfig(WebProperties webProperties) {
        this.properties = webProperties.getSwagger();
    }

    @Bean
    @ConditionalOnMissingBean
    public Docket createRestApi() {
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()));
        Optional.ofNullable(properties.getPath().getRegex()).ifPresent(pathRegex -> builder.paths(PathSelectors.regex(pathRegex)));
        Optional.ofNullable(properties.getPath().getAntPath()).ifPresent(antPath -> builder.paths(PathSelectors.regex(antPath)));

        return builder.build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .termsOfServiceUrl(properties.getTermsOfServiceUrl())
                .version(properties.getVersion())
                .contact(properties.getContact())
                .license(properties.getLicense())
                .licenseUrl(properties.getLicenseUrl())
                .build();
    }

}

