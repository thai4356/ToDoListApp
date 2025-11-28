package todo.todo.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import todo.todo.security.interceptor.TrackLogRequestInterceptor;
import todo.todo.security.interceptor.UserInterCeptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserInterCeptor userInterceptor;
    private final TrackLogRequestInterceptor trackLogRequestInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final long MAX_AGE_SECS = 3600;
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trackLogRequestInterceptor).addPathPatterns("/**");
        registry.addInterceptor(userInterceptor).addPathPatterns("/api/v*/**")
                .excludePathPatterns(
            "/api/v*/auth/**",
                        "/api/v*/media/**",
                        "/api/v*/team/invite/accept",
                        "/api/v*/team/invite/accept/**");
    }

    @Override
    public void configureContentNegotiation(@NotNull ContentNegotiationConfigurer configurer) {
        WebMvcConfigurer.super.configureContentNegotiation(configurer);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

}