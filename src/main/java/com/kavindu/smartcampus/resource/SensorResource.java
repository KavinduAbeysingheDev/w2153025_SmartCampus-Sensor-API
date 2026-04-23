package com.kavindu.smartcampus.resource;

import com.kavindu.smartcampus.model.ApiResponse;
import com.kavindu.smartcampus.model.Sensor;
import com.kavindu.smartcampus.repository.SmartCampusStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final SmartCampusStore store = SmartCampusStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type,
                               @QueryParam("roomId") String roomId,
                               @QueryParam("status") String status) {
        return Response.ok(store.getSensors(type, roomId, status)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        Sensor created = store.addSensor(sensor);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location)
                .entity(new ApiResponse<>("Sensor registered successfully.", created))
                .build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        return Response.ok(store.getSensor(sensorId)).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource sensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId, store);
    }
}
