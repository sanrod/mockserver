package expectations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.requests.CreateShopRequest;
import models.shop.Good;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import utils.postgres.Client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class AddShop {
    public Expectation createExp() {
        String path = "/addShop";
        return Expectation.when(
                request().withPath(path).withMethod("POST"),
                Times.unlimited(),
                TimeToLive.unlimited()
        ).thenRespond(
                callback()
                        .withCallbackClass(TestExpectationResponseCallback.class)
        );
    }

    public static class TestExpectationResponseCallback implements ExpectationResponseCallback {
        private static HashMap<String, Boolean> canCreateGoods;

        @Override
        public HttpResponse handle(HttpRequest httpRequest) throws Exception {
            CreateShopRequest request = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(httpRequest.getBodyAsString(), CreateShopRequest.class);

            if (!(new Methods().canCreateShop(request.getShop()))) {
                return response()
                        .withBody("Can't create the shop! Already exists with the same id or name!")
                        .withStatusCode(400);
            }

            canCreateGoods = new Methods().canCreateGoods(request);

            if (!canCreateGoods.get("canCreate")) {
                return response()
                        .withBody("Can't create the shop! The goods already exist or there more than one type!")
                        .withStatusCode(400);
            }


            try {
                createShopAndGoods(request);
                return response().withStatusCode(201).withBody("The shop created successfully");
            } catch (Exception ex) {
                return response().withStatusCode(500).withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }
        }

        private static void createShopAndGoods(CreateShopRequest request) throws SQLException, JsonProcessingException {
            List<Good> goods = new Methods().createGoods(canCreateGoods, request);

            Client.insert("Shops", List.of(
                    String.valueOf(request.getShop().getId()),
                    request.getShop().getName(),
                    new ObjectMapper()
                            .registerModule(new JavaTimeModule())
                            .writeValueAsString(request.getShop().getAddress()),
                    new ObjectMapper().writeValueAsString(goods)));
        }
    }
}
