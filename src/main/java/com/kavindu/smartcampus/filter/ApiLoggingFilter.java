package com.kavindu.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(ApiLoggingFilter.class.getName());
    private static final String START_TIME = "startTime";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME, System.currentTimeMillis());
        LOGGER.info(() -> String.format("Incoming request -> %s %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        long startedAt = requestContext.getProperty(START_TIME) instanceof Long
                ? (Long) requestContext.getProperty(START_TIME)
                : System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startedAt;

        LOGGER.info(() -> String.format("Outgoing response -> %s %s | status=%d | durationMs=%d",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus(),
                duration));
    }
}
