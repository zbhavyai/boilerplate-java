package io.github.zbhavyai.boilerplatejava.client;

import java.time.Duration;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.github.zbhavyai.boilerplatejava.models.SimpleResponse;
import io.github.zbhavyai.boilerplatejava.utils.JSONMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class JaxRsClient implements RestClient {

    private static final Logger LOGGER = Logger.getLogger(JaxRsClient.class.getSimpleName());

    private final Client client;
    private final Duration timeout;

    @Inject
    public JaxRsClient(
            @ConfigProperty(name = "timeout.secs", defaultValue = "5") long timeout) {

        this.client = ClientBuilder.newClient();
        this.timeout = Duration.ofSeconds(timeout);
    }

    @Override
    public Uni<Response> getRequest(
            String uri,
            Map<String, String> headers) {
        LOGGER.infof("getRequest: uri=\"%s\"", uri);

        CompletionStageRxInvoker invoker = this.client
                .target(uri)
                .request()
                .rx();

        return Uni.createFrom().completionStage(invoker.get())
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    @Override
    public Uni<Response> postRequest(
            String uri,
            Map<String, String> headers,
            Object payload) {
        LOGGER.infof("postRequest: uri=\"%s\"", uri);
        LOGGER.debugf("postRequest: payload=\"%s\"", JSONMapper.serialize(payload));

        CompletionStageRxInvoker invoker = this.client
                .target(uri)
                .request()
                .rx();

        return Uni.createFrom().completionStage(invoker.post(Entity.json(payload)))
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    @Override
    public Uni<Response> putRequest(
            String uri,
            Map<String, String> headers,
            Object payload) {
        LOGGER.infof("putRequest: uri=\"%s\"", uri);
        LOGGER.debugf("putRequest: payload=\"%s\"", JSONMapper.serialize(payload));

        CompletionStageRxInvoker invoker = this.client
                .target(uri)
                .request()
                .rx();

        return Uni.createFrom().completionStage(invoker.put(Entity.json(payload)))
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    @Override
    public Uni<Response> patchRequest(
            String uri,
            Map<String, String> headers,
            Object payload) {
        LOGGER.infof("patchRequest: uri=\"%s\"", uri);
        LOGGER.debugf("patchRequest: payload=\"%s\"", JSONMapper.serialize(payload));

        CompletionStageRxInvoker invoker = this.client
                .target(uri)
                .request()
                .rx();

        return Uni.createFrom().completionStage(invoker.method("PATCH", Entity.json(payload)))
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    @Override
    public Uni<Response> deleteRequest(
            String uri,
            Map<String, String> headers) {
        LOGGER.infof("deleteRequest: uri=\"%s\"", uri);

        CompletionStageRxInvoker invoker = this.client
                .target(uri)
                .request()
                .rx();

        return Uni.createFrom().completionStage(invoker.delete())
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    private <T> Response handleResponse(Response res) {
        LOGGER.infof("handleResponse: status=\"%s\"", res.getStatus());
        LOGGER.debugf("handleResponse: headers=\"%s\", startBuffer=\"%b\", body=\"%s\"", res.getHeaders(),
                res.bufferEntity(), JSONMapper.serialize(res.readEntity(JsonObject.class)));

        if (res.getStatus() >= 200 && res.getStatus() < 300) {
            return res;
        } else {
            throw new WebApplicationException(res);
        }
    }

    private Throwable handleFailure(Throwable t) {
        if (t instanceof WebApplicationException) {
            WebApplicationException tw = (WebApplicationException) t;

            LOGGER.errorf("handleFailure: statusCode=\"%s\", statusMessage=\"%s\" error=\"%s\"",
                    tw.getResponse().getStatus(),
                    tw.getResponse().getStatusInfo().getReasonPhrase(),
                    tw.getLocalizedMessage());

            return new WebApplicationException(tw.getResponse());
        } else {
            LOGGER.errorf("handleFailure: error=\"%s\"", t.getLocalizedMessage());

            return new WebApplicationException(
                    Response
                            .status(Status.INTERNAL_SERVER_ERROR)
                            .entity(SimpleResponse.create(t.getLocalizedMessage()))
                            .build());
        }
    }

    private Throwable handleTimeout() {
        return new WebApplicationException(
                Response
                        .status(Status.GATEWAY_TIMEOUT)
                        .entity(SimpleResponse.create("Request timeout"))
                        .build());
    }
}
