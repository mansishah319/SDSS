/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.custom;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Profile({"dev", "local"})
@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(
                        url = "${openapi.server.url}",
                        description = "${openapi.server.description}"
                )
        }
)
@SecurityScheme(
        name = "bearerAuthAbdu",
        description = "JWT AUTH",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String title;
    private static final String DESCRIPTION = ".برنامج تابع لإستشراف المستقبل لربط القضايا بالمختصين للإطلاع عليها وعمل الجلسات";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(this.getInfo());
    }

    private Contact myContactInfo() {
        Contact contact = new Contact();
        contact.setName("DubaiPolice");
        contact.setEmail("eng.mo.abdu@gmail.com");
        contact.setUrl("https://www.dubaipolice.gov.ae/wps/portal/home");
        return contact;
    }

    private Info getInfo() {
        return new Info()
                .title(title)
                .contact(myContactInfo())
                .version("0.1")
                .description(DESCRIPTION);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigure() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
                registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }
}
