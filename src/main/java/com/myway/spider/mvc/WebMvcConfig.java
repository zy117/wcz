package com.myway.spider.mvc;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.myway.usf.mvc.constant.MvcConstant;
import com.myway.usf.mvc.converter.JsonHttpMessageConverter;
import com.myway.usf.mvc.interceptor.ActionContextInterceptor;
import com.myway.usf.mvc.resolver.ContextHandlerMethodArgumentResolver;

/**
 * @author shiningvon
 * @date 2017/12/15
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*").allowedOrigins("*").allowCredentials(true)
                .allowedMethods("GET", "POST", "HEAD", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders(MvcConstant.TOKEN_KEY, "Accept", "Origin", "X-Requested-With",
                        "Content-Type", "Last-Modified")
                .exposedHeaders("Set-Cookie", MvcConstant.TOKEN_KEY);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
                new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader(MvcConstant.TOKEN_KEY);
        corsConfiguration.addExposedHeader("Set-Cookie");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getTokenInterceptor()).excludePathPatterns("/status.do");
        registry.addInterceptor(getAccountInterceptor())
                .excludePathPatterns("/status.do");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(getContextArgumentResolver());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(getStringConverter());
        converters.add(getByteArrayConverter());
        converters.add(getJsonConverter());
    }

    @Bean
    public HandlerInterceptor getTokenInterceptor() {
        return new ActionContextInterceptor();
    }

    @Bean
    public HandlerInterceptor getAccountInterceptor() {
        ActionContextInterceptor interceptor = new ActionContextInterceptor();
        interceptor.setRegisterDefaultHandler(false);
        interceptor.addHandler(getAccountInfoContextHandler());
        return interceptor;
    }

    @Bean
    public AccountInfoContextHandler getAccountInfoContextHandler() {
        return new AccountInfoContextHandler();
    }

    @Bean
    public HandlerMethodArgumentResolver getContextArgumentResolver() {
        return new ContextHandlerMethodArgumentResolver();
    }

    @Bean
    public StringHttpMessageConverter getStringConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter();
        converter.setWriteAcceptCharset(false);
        converter.setSupportedMediaTypes(
                Arrays.asList(MediaType.parseMediaType("text/plain;charset=UTF-8"),
                        MediaType.parseMediaType("text/html;charset=UTF-8")));
        return converter;
    }

    @Bean
    public ByteArrayHttpMessageConverter getByteArrayConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    @Bean(name = "jsonConverter")
    public JsonHttpMessageConverter getJsonConverter() {
        JsonHttpMessageConverter converter = new JsonHttpMessageConverter();
        converter.setSupportedMediaTypes(
                Arrays.asList(MediaType.parseMediaType("text/html;charset=UTF-8"),
                        MediaType.parseMediaType("application/json;charset=UTF-8")));
        return converter;
    }

}
