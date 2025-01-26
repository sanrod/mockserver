package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.goods.AvailableGoods;
import models.shop.Good;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import java.util.HashMap;
import java.util.List;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.mockserver.model.Parameter.param;

public class GetShopsGoods {
    public Expectation createExp() {
        String path = "/getShop/{shopId}";
        return Expectation.when(
                request().withPath(path)
                        .withPathParameters(
                                param("shopId", "[0-9]{1,}")
                        )
                        .withMethod("GET"),
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
            String shopId = httpRequest.getFirstPathParameter("shopId");

            boolean shopExists = new Methods().checkShopExist(shopId);
            if (!shopExists) {
                return response()
                        .withStatusCode(400)
                        .withBody("The shop is not found!");
            }

            List<Good> goods;
            try {
                goods = new Methods().getGoods(shopId);
            } catch (Exception ex) {
                return response()
                        .withStatusCode(500)
                        .withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }

            try {
                List<HashMap<String, String>> info = new Methods().getGoodsInfo(goods);
                AvailableGoods body = AvailableGoods.builder()
                        .shopId(Integer.parseInt(shopId))
                        .goods(info)
                        .build();

                JsonBody jsonBody = json(
                        new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(body),
                        MediaType.APPLICATION_JSON);
                return response()
                        .withBody(jsonBody);
            } catch (Exception ex) {
                return response()
                        .withStatusCode(500)
                        .withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }
        }
    }
}
