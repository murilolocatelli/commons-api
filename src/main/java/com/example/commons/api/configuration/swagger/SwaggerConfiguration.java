package com.example.commons.api.configuration.swagger;

import org.springframework.beans.factory.annotation.Value;

public abstract class SwaggerConfiguration {

    @Value("${application.name}")
    private String applicationName;

    @Value("${application.description}")
    private String applicationDescription;

    @Value("${application.version}")
    private String applicationVersion;

    /*@Bean
    public Docket docket(ApiInfo apiInfo) {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(this.baseControllerPackage()))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo);
    }

    @Bean
    public ApiInfo apiInfo() {
        this.applicationVersion = Optional.ofNullable(this.applicationVersion)
            .map(t -> t.replace("-SNAPSHOT", ""))
            .orElse(this.applicationVersion);

        return new ApiInfo(this.applicationName, this.applicationDescription, this.applicationVersion,
            null, null, null, null, Collections.emptyList());
    }

    protected abstract String baseControllerPackage();*/

}
