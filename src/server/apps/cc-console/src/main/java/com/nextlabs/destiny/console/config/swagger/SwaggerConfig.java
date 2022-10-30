package com.nextlabs.destiny.console.config.swagger;

import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Swagger API docs configuration.
 *
 * @author Sachindra Dasun
 */
@PropertySource("classpath:swagger.properties")
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Bean
    @ConditionalOnMissingBean
    public Docket apiDoc(Environment env) {
        ApiInfo apiInfo = new ApiInfo("NextLabs API Reference",
                env.getProperty("swagger.ui.description"),
                "1.0",
                "",
                new Contact("", "", ""),
                "",
                "",
                new ArrayList<>());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .useDefaultResponseMessages(false)
                .genericModelSubstitutes(ConsoleResponseEntity.class)
                .alternateTypeRules()
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex(".*/api/v1/policy/mgmt.*" +
                        "|.*/api/v1/policy/search.*" +
                        "|.*/api/v1/component/mgmt.*" +
                        "|.*/api/v1/component/search.*" +
                        "|.*/api/v1/policyModel/mgmt.*" +
                        "|.*/api/v1/policyModel/search.*"))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public UiConfiguration tryItOutConfig() {
        final String[] methodsWithTryItOutButton = {};
        return UiConfigurationBuilder.builder().supportedSubmitMethods(methodsWithTryItOutButton).build();
    }
}
