package com.kavindu.smartcampus.mapper;

import com.kavindu.smartcampus.exception.LinkedResourceNotFoundException;
import com.kavindu.smartcampus.model.ApiError;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ApiError error = new ApiError(422, "Unprocessable Entity", exception.getMessage(),
                uriInfo == null ? "" : uriInfo.getPath(), System.currentTimeMillis());

        return Response.status(422).entity(error).build();
    }
}
