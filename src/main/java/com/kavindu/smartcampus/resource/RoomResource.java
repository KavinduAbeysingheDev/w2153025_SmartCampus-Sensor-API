package com.kavindu.smartcampus.resource;

import com.kavindu.smartcampus.model.ApiResponse;
import com.kavindu.smartcampus.model.Room;
import com.kavindu.smartcampus.repository.SmartCampusStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final SmartCampusStore store = SmartCampusStore.getInstance();

    @GET
    public Response getAllRooms() {
        return Response.ok(store.getRooms()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        Room created = store.addRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location)
                .entity(new ApiResponse<>("Room created successfully.", created))
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        return Response.ok(store.getRoom(roomId)).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        store.deleteRoom(roomId);
        return Response.ok(new ApiResponse<>("Room deleted successfully.", roomId)).build();
    }
}
