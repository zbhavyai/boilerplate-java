package io.github.zbhavyai.boilerplatejava.client;

import org.jboss.logging.Logger;

import io.github.zbhavyai.boilerplatejava.models.SimpleResponse;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/test/v1/")
public class TestResource {

    private static final Logger LOGGER = Logger.getLogger(TestResource.class.getSimpleName());

    @POST
    public Uni<Response> testPost(String payload) {
        LOGGER.infof("testPost");

        return Uni.createFrom().item(
                Response.ok(
                        SimpleResponse.create("Test response with payload: " + payload)).build());
    }
}
