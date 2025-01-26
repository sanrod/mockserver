package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import expectations.common.ShopAdder;
import models.enums.GoodTypes;
import models.requests.CreateShopRequest;
import models.storage.AddGoodsResult;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

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

        @Override
        public HttpResponse handle(HttpRequest httpRequest) throws Exception {
            CreateShopRequest request;
            try {
                request = new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .readValue(httpRequest.getBodyAsString(), CreateShopRequest.class);
            } catch (Exception e) {
                return response()
                        .withBody("Can't create the shop! The request body is wrong!")
                        .withStatusCode(500);
            }

            ShopAdder adder = new ShopAdder();
            if (!adder.canCreateShop(request.getShop())) {
                return response()
                        .withBody("Can't create the shop! Already exists with the same id or name!")
                        .withStatusCode(400);
            }

            GoodTypes type = new Methods().checkGoodsInRequest(request);
            if (type == GoodTypes.WRONG_TYPE) {
                return response()
                        .withBody("Can't create the shop!There are more than one type of goods or an incorrect type!")
                        .withStatusCode(400);
            }

            try {
                AddGoodsResult goods = new Methods().transformGoodsToDbFormat(type, request);
                adder.createShop(request.getShop(), goods.getAdded());
                return response().withStatusCode(201).withBody(String.format("The shop created successfully! The following " +
                        "goods were excluded %s", goods.getExcluded()));
            } catch (Exception ex) {
                return response().withStatusCode(500).withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }
        }
    }
}
