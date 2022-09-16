package com.msquare.flabook.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Profile({"local", "local_kuh", "develop"})
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

    private ApiInfo apiInfo() {

    	String description =
				"## 2020/05/29\n" +
						"- [POST] /v2/auth/key 인증번호 확인 : 파라미터 타입 변경(int -> String authKey)\n" +
						"- [POST] /v2/auth/login/phone 휴대폰번호 로그인\n" +
						"- [POST] /v2/auth/phone 휴대폰번호 인증 확인 문자 발송\n" +
						"\n" +
						"## 2020/06/03\n" +
						"- [POST] /v2/users/me/auth/phone 프로필관리 - 개인정보 - 휴대폰 번호 등록 - 인증번호 발송 추가\n" +
						"- [POST] /v2/users/me/phone 프로필관리 - 개인정보 - 휴대폰 번호 등록 추가\n" +
						"- [POST] /v2/users/me/auth/mail 프로필관리 - 개인정보 - 이메일 등록 - 인증번호 발송 추가\n" +
						"- [POST] /v2/users/me/mail 프로필관리 - 개인정보 - 이메일 등록 추가\n" +
						"\n" +
						"## 2020/06/11 - 3.계정연동 컨트롤러 추가\n" +
						"- [POST] /v2/modify/providers/mail - 계정연동 - 기존 휴대폰 로그인 사용자 - email 인증 번호 확인 및 전환 \n" +
						"- [POST] /v2/modify/providers/mail/auth - 계정연동 - 기존 휴대폰 로그인 사용자 - email 인증 번호 발송 \n" +
						"- [POST] /v2/modify/providers/{provider} - 계정연동 - 기존 휴대폰 로그인 사용자 -> sns 전환 \n" +
						"\n" +
						"## 2020/06/15 - 1.User인증 - token재발급 추가 \n" +
						"- [POST] /v2/auth/token - RefreshToken으로 AccessToken 재발급 \n" +
						"--- resultCode[9203], 만료된 토큰 확인 시 해당 API 호출하여 RefreshToken으로 AccessToken 재발급 \n";

        return new ApiInfoBuilder()
                .title("Rest API Server")
                .description(description)
                .build();
    }

    private ApiKey apiKey() {
    	return new ApiKey("apiKey", "Authorization", "header");
    }

    @Bean
    public SecurityConfiguration security() {
    return SecurityConfigurationBuilder.builder().scopeSeparator(",")
        .additionalQueryStringParams(null)
        .useBasicAuthenticationWithAccessCodeGrant(false).build();
    }

    private SecurityContext securityContext() {
    	return SecurityContext.builder().securityReferences(defaultAuth())
        .forPaths(PathSelectors.any()).build();
    }

    private List<SecurityReference> defaultAuth() {
    	AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    	AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    	authorizationScopes[0] = authorizationScope;
    	return Collections.singletonList(new SecurityReference("apiKey", authorizationScopes));
    }

    @Bean
    public Docket commonApi() {
    	ParameterBuilder aParameterBuilder = new ParameterBuilder();
        aParameterBuilder.name("Authorization") //헤더 이름
                .description("Access Tocken") //설명
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("v2")
                .apiInfo(this.apiInfo())
                //.tags(new Tag("API", "v2"))
                //.globalOperationParameters(aParameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.msquare.flabook.controllers"))
                //.paths(PathSelectors.ant("/v2/auth/**"))

                .build()
                .securitySchemes(Collections.singletonList(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()))
                .useDefaultResponseMessages(false);
    }

}
