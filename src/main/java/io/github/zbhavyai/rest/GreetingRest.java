package io.github.zbhavyai.rest;

import io.github.zbhavyai.service.GreetingService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/")
public class GreetingRest {

    private final GreetingService service;

    @Inject
    public GreetingRest(final GreetingService service) {
        this.service = service;
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> greet() {
        return this.service.greet();
    }

    @GET
    @Path("/error")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> error() {
        return this.service.error();
    }
}
