package io.github.zbhavyai.service;

import io.github.zbhavyai.models.SimpleResponse;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class GreetingServiceImpl implements GreetingService {

    @Override
    public Uni<Response> greet() {
        Response res = Response
                .status(Status.OK)
                .entity(SimpleResponse.create("Hello World!"))
                .build();

        return Uni.createFrom().item(res);
    }

    @Override
    public Uni<Response> error() {
        return Uni.createFrom()
                .failure(() -> new WebApplicationException(
                        Response.status(Status.BAD_REQUEST)
                                .entity(SimpleResponse.create(Status.BAD_REQUEST.getReasonPhrase()))
                                .build()));
    }
}
