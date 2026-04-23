package com.kavindu.smartcampus.mapper;

import com.kavindu.smartcampus.exception.DuplicateResourceException;
import com.kavindu.smartcampus.model.ApiError;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DuplicateResourceExceptionMapper implements ExceptionMapper<DuplicateResourceException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(DuplicateResourceException exception) {
        ApiError error = new ApiError(409, "Conflict", exception.getMessage(),
                uriInfo == null ? "" : uriInfo.getPath(), System.currentTimeMillis());

        return Response.status(409).entity(error).build();
    }
}
