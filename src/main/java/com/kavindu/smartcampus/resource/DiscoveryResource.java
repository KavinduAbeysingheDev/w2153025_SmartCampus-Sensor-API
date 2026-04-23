package com.kavindu.smartcampus.resource;

import com.kavindu.smartcampus.model.DiscoveryResponse;
import com.kavindu.smartcampus.model.StatsResponse;
import com.kavindu.smartcampus.repository.SmartCampusStore;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    private final SmartCampusStore store = SmartCampusStore.getInstance();

    @GET
    public Response discover(@Context UriInfo uriInfo) {
        String baseUri = uriInfo.getBaseUri().toString();
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 1);
        }

        DiscoveryResponse response = new DiscoveryResponse();
        response.setApiName("Smart Campus Sensor Management API");
        response.setVersion("1.0");
        response.setDescription("RESTful service for managing campus IoT infrastructure — rooms, sensor devices, and live/historical measurement data.");
        response.setSpecVersion("JAX-RS 2.1 / Jersey 2.41");

        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("team", "Campus Infrastructure & IoT Engineering");
        contact.put("email", "campus-iot@university.ac.uk");
        contact.put("department", "Facilities Management & Digital Systems");
        response.setContact(contact);

        Map<String, Object> resources = new LinkedHashMap<>();
        resources.put("rooms", createResourceInfo("/api/v1/rooms", "GET, POST, PUT, DELETE", "Campus room registry — list, create, update, and remove rooms"));
        resources.put("sensors", createResourceInfo("/api/v1/sensors", "GET, POST, PUT, DELETE", "Sensor device registry — manage IoT devices and filter by type"));
        resources.put("readings", createResourceInfo("/api/v1/sensors/{sensorId}/readings", "GET, POST", "Sensor reading history — append measurements and retrieve logs"));
        response.setResources(resources);

        Map<String, String> server = new LinkedHashMap<>();
        server.put("baseUri", baseUri);
        server.put("protocol", "HTTP/1.1");
        server.put("status", "operational");
        response.setServer(server);

        return Response.ok(response).build();
    }

    private Map<String, String> createResourceInfo(String href, String methods, String description) {
        Map<String, String> resourceInfo = new LinkedHashMap<>();
        resourceInfo.put("href", href);
        resourceInfo.put("methods", methods);
        resourceInfo.put("description", description);
        return resourceInfo;
    }

    @GET
    @Path("health")
    public Response health() {
        return Response.ok(new com.kavindu.smartcampus.model.ApiResponse<>("Service is running.", "UP")).build();
    }

    @GET
    @Path("stats")
    public Response stats() {
        StatsResponse stats = store.getStats();
        return Response.ok(stats).build();
    }

    @POST
    @Path("admin/reset")
    public Response reset() {
        store.resetToSeedData();
        return Response.ok(new com.kavindu.smartcampus.model.ApiResponse<>("Seed data has been restored.", store.getStats())).build();
    }
}
