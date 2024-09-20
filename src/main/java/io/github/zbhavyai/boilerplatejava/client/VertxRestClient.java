package io.github.zbhavyai.boilerplatejava.client;

import java.time.Duration;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.github.zbhavyai.boilerplatejava.models.SimpleResponse;
import io.github.zbhavyai.boilerplatejava.utils.JSONPrinter;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.codec.BodyCodec;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class VertxRestClient {

    private static final Logger LOGGER = Logger.getLogger(VertxRestClient.class.getSimpleName());

    public final WebClient client;
    private final Duration timeout;

    @Inject
    public VertxRestClient(
            Vertx vertx,
            @ConfigProperty(name = "timeout.secs", defaultValue = "5") long timeout) {

        this.client = WebClient.create(vertx);
        this.timeout = Duration.ofSeconds(timeout);
    }

    public <T> Uni<Response> getRequest(
            String uri,
            Map<String, String> headers,
            Class<T> responseType) {
        LOGGER.infof("getRequest: uri=\"%s\"", uri);

        HttpRequest<T> req = this.client
                .getAbs(uri)
                .as(BodyCodec.json(responseType))
                .putHeaders(this.convertMapToMultiMap(headers));

        return req
                .send()
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    public <T> Uni<Response> postRequest(
            String uri,
            Map<String, String> headers,
            Object payload,
            Class<T> responseType) {
        LOGGER.infof("postRequest: uri=\"%s\"", uri);
        LOGGER.debugf("postRequest: payload=\"%s\"", JSONPrinter.prettyPrint(payload));

        HttpRequest<T> req = this.client
                .postAbs(uri)
                .as(BodyCodec.json(responseType))
                .putHeaders(this.convertMapToMultiMap(headers));

        return req
                .sendJson(payload)
                .onItem().transform(r -> this.handleResponse(r))
                .onFailure().transform(t -> this.handleFailure(t))
                .ifNoItem()
                .after(this.timeout)
                .failWith(this.handleTimeout());
    }

    private <T> Response handleResponse(HttpResponse<T> res) {
        LOGGER.infof("handleResponse: status=\"%s\"", res.statusCode());
        LOGGER.debugf("handleResponse: headers=\"%s\", body=\"%s\"", res.headers(),
                JSONPrinter.prettyPrint(res.body()));

        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            return Response.status(res.statusCode()).entity(res.body()).build();
        } else {
            throw new WebApplicationException(
                    res.body() == null ? "null" : res.body().toString(),
                    res.statusCode());
        }
    }

    private Throwable handleFailure(Throwable t) {
        if (t instanceof WebApplicationException) {
            WebApplicationException tw = (WebApplicationException) t;

            LOGGER.errorf("handleFailure: statusCode=\"%s\", statusMessage=\"%s\" error=\"%s\"",
                    tw.getResponse().getStatus(),
                    tw.getResponse().getStatusInfo().getReasonPhrase(),
                    tw.getLocalizedMessage());

            return new WebApplicationException(
                    Response
                            .status(tw.getResponse().getStatus())
                            .entity(SimpleResponse.create(t.getLocalizedMessage()))
                            .build());
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

    private MultiMap convertMapToMultiMap(Map<String, String> obj) {
        return MultiMap.caseInsensitiveMultiMap().addAll(obj);
    }
}
