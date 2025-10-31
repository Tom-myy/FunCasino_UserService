package com.evofun.userservice.shared.security.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class ExceptionConfig {
///it's for NoHandlerFoundException (without - doesn't work)
    @Bean
    public WebMvcRegistrations webMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new RequestMappingHandlerMapping() {
                    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
                        throw new NoHandlerFoundException(
                                request.getMethod(),
                                request.getRequestURI(),
                                new ServletServerHttpRequest(request).getHeaders()
                        );
                    }
                };
            }
        };
    }
}