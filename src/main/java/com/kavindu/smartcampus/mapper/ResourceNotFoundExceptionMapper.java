package com.kavindu.smartcampus.mapper;

import com.kavindu.smartcampus.exception.ResourceNotFoundException;
import com.kavindu.smartcampus.model.ApiError;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        ApiError error = new ApiError(404, "Not Found", exception.getMessage(),
                uriInfo == null ? "" : uriInfo.getPath(), System.currentTimeMillis());

        return Response.status(404).entity(error).build();
    }
}
