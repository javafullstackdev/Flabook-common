package com.msquare.flabook.configuration;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DataSource datasource;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final TokenAuthorizationFilter tokenAuthorizationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String[] AUTH_WHITELIST = {

                // -- Swagger UI v3 (OpenAPI)
                "/v3/api-docs/**",
                "/swagger-ui/**"
                // other public endpoints of your API may be appended to this array
        };


        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .addFilterBefore(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/v2/auth/**").permitAll()						//인증로직 권한 체크 안함
            .antMatchers("/v2/notices/**").permitAll()		//APP 릴리즈 버전, 공지 조회
            .antMatchers("/v2/policies/**").permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/v1/gambles", "/v1/gambles/**", "/v2/rankings").permitAll()
            .antMatchers(HttpMethod.GET, "/v2/rankings").permitAll()

            .antMatchers("/postings").permitAll() 						//사용여부확인 - 구글 독스 요청 주소 token 을 통해 validation check
            .antMatchers("/static/**").permitAll()
            .antMatchers("/docs/**").permitAll() 						// 구글 독스 요청 주소 token 을 통해 validation check
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/api/**").permitAll()
            .antMatchers("/v2/shops").permitAll()
            .antMatchers("/v2/shops").permitAll()
            .antMatchers(AUTH_WHITELIST).permitAll()
            .antMatchers("/v2/api-docs", "/v2/logs", "/swagger-ui.html","/swagger-resources/**", "/`swagger-ui.html`", "/webjars/**", "/swagger/**").permitAll()	//swagger
            .antMatchers("/**").authenticated()
            .anyRequest().authenticated()

            //ExceptionHandling
        	.and()
        	.exceptionHandling()
        	.authenticationEntryPoint(restAuthenticationEntryPoint);

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	//User Password 생성 시, bCryptPasswordEncoder 사용
    	auth.jdbcAuthentication().dataSource(datasource).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
