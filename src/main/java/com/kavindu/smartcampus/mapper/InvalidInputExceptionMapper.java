package com.kavindu.smartcampus.mapper;

import com.kavindu.smartcampus.exception.InvalidInputException;
import com.kavindu.smartcampus.model.ApiError;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(InvalidInputException exception) {
        ApiError error = new ApiError(400, "Bad Request", exception.getMessage(),
                uriInfo == null ? "" : uriInfo.getPath(), System.currentTimeMillis());

        return Response.status(400).entity(error).build();
    }
}
