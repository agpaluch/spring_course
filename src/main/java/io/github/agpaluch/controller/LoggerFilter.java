package io.github.agpaluch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order
@Component
class LoggerFilter implements Filter{

    private static final Logger logger = LoggerFactory.getLogger(LoggerFilter.class);

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest){
            var httpRequest = (HttpServletRequest) request;
            logger.info("[doFilter] " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
         }
        chain.doFilter(request, response);
    }

}
