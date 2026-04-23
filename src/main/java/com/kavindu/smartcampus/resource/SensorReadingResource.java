package com.kavindu.smartcampus.resource;

import com.kavindu.smartcampus.model.ApiResponse;
import com.kavindu.smartcampus.model.SensorReading;
import com.kavindu.smartcampus.repository.SmartCampusStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final SmartCampusStore store;

    public SensorReadingResource(String sensorId, SmartCampusStore store) {
        this.sensorId = sensorId;
        this.store = store;
    }

    @GET
    public Response getReadings() {
        return Response.ok(store.getReadings(sensorId)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReading(SensorReading sensorReading, @Context UriInfo uriInfo) {
        SensorReading created = store.addReading(sensorId, sensorReading);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location)
                .entity(new ApiResponse<>("Sensor reading saved successfully.", created))
                .build();
    }
}
