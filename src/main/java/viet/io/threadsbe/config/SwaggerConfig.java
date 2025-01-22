package viet.io.threadsbe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "swagger")
@RequiredArgsConstructor
public class SwaggerConfig {

    @Value("${swagger.title}")
    private String appName;
    @Value("${swagger.description}")
    private String appDescription;
    @Value("${swagger.version}")
    private String appVersion;
    private String appLicense;
    private String appLicenseUrl;
    @Value("${swagger.contact.name}")
    private String contactName;
    private String contactUrl;
    @Value("${swagger.contact.email}")
    private String contactMail;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description(appDescription)
                        .license(new License().name(appLicense).url(appLicenseUrl))
                        .contact(new Contact().name(contactName).url(contactUrl).email(contactMail)))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    GroupedOpenApi managementApi() {
        return GroupedOpenApi.builder().pathsToMatch("/actuator/**").group("Management Api").build();
    }

    @Bean
    GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder().pathsToExclude("/actuator/**").group("Default Api").build();
    }
}