package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.shop.Shop;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class GetAvailableShops {
    public GetAvailableShops() {
    }

    public Expectation createExp() {
        String path = "/getAvailableShops";
        return Expectation.when(
                request().withPath(path).withMethod("GET"),
                Times.unlimited(),
                TimeToLive.unlimited()
        ).thenRespond(
                callback()
                        .withCallbackClass(TestExpectationResponseCallback.class)
        );
    }

    public static class TestExpectationResponseCallback implements ExpectationResponseCallback {
        @Override
        public HttpResponse handle(HttpRequest httpRequest) throws Exception {
            List<Shop> body = new ArrayList<>();
            try {
                body = new Methods().getAllShops();
            } catch (Exception ex) {
                response()
                        .withStatusCode(500)
                        .withBody(String.format("Some error occurred during request. The error %s", ex.getMessage()));
            }

            JsonBody jsonBody = json(
                    new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(body),
                    MediaType.APPLICATION_JSON);
            return response()
                    .withBody(jsonBody);
        }
    }
}
