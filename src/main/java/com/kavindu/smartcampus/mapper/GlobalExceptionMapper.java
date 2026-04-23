package com.kavindu.smartcampus.mapper;

import com.kavindu.smartcampus.model.ApiError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) exception;
            int status = webApplicationException.getResponse().getStatus();
            ApiError error = new ApiError(status, Response.Status.fromStatusCode(status).getReasonPhrase(),
                    "The request could not be processed.", uriInfo == null ? "" : uriInfo.getPath(),
                    System.currentTimeMillis());
            return Response.status(status).entity(error).build();
        }

        LOGGER.log(Level.SEVERE, "Unhandled server error", exception);

        ApiError error = new ApiError(500, "Internal Server Error",
                "An unexpected error occurred. Please contact the administrator if the problem persists.",
                uriInfo == null ? "" : uriInfo.getPath(), System.currentTimeMillis());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
