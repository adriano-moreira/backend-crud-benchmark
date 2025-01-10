package study;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/ping")
public class PingResource {

    @RunOnVirtualThread
    @GET
    public Response getGreeting() {
        return Response.ok().build();
    }

}
