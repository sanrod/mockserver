package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.goods.AvailableGoods;
import models.shop.ErrorMessage;
import models.shop.Good;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import utils.postgres.Client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            AvailableGoods body = new AvailableGoods();

            ErrorMessage message = new ErrorMessage();
            String shopId = httpRequest.getFirstPathParameter("shopId");
            List<Good> goods = null;
            try {
                goods = new Methods().getGoods(shopId);
            } catch (Exception ex) {
                message = ErrorMessage.builder()
                        .message(ex.getMessage())
                        .build();
            }

            HashMap<String, List<HashMap<String, String>>> infos = getInfos(Objects.requireNonNull(goods));
            for (Map.Entry<String, List<HashMap<String, String>>> entry : infos.entrySet()) {
                String key = entry.getKey();
                List<HashMap<String, String>> value = entry.getValue();

                switch (key) {
                    case "Toys": {
                        body = AvailableGoods.builder()
                                .shopId(Integer.parseInt(shopId))
                                .goods(new Methods().convertSqlToToys(value))
                                .build();
                        break;
                    }
                    case "Foods": {
                        body = AvailableGoods.builder()
                                .shopId(Integer.parseInt(shopId))
                                .goods(new Methods().convertSqlToFoods(value))
                                .build();
                        break;
                    }
                    case "Cars": {
                        body = AvailableGoods.builder()
                                .shopId(Integer.parseInt(shopId))
                                .goods(new Methods().convertSqlToCars(value))
                                .build();
                        break;
                    }
                    case "Dogs": {
                        body = AvailableGoods.builder()
                                .shopId(Integer.parseInt(shopId))
                                .goods(new Methods().convertSqlToDogs(value))
                                .build();
                        break;
                    }
                    case "Cats": {
                        body = AvailableGoods.builder()
                                .shopId(Integer.parseInt(shopId))
                                .goods(new Methods().convertSqlToCats(value))
                                .build();
                        break;
                    }
                    case "Birds": {
                        body = AvailableGoods.builder()
                                .shopId(Integer.parseInt(shopId))
                                .goods(new Methods().convertSqlToBirds(value))
                                .build();
                        break;
                    }
                }
            }

            HttpResponse response = new Methods().returnErrorMessageResponse(message);
            if (response != null) {
                return response;
            }

            JsonBody jsonBody = json(
                    new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(body),
                    MediaType.APPLICATION_JSON);
            return response()
                    .withBody(jsonBody);
        }
    }

    private static HashMap<String, List<HashMap<String, String>>> getInfos(List<Good> goods) throws SQLException {
        HashMap<String, List<HashMap<String, String>>> result = new HashMap<>();

        String type = goods.stream().findFirst().get().getType();
        List<HashMap<String, String>> firstInfo = Client.select(type, "id <> 0");
        result.put(type, firstInfo);

        if (goods.stream().filter(x -> x.getType().equals(type)).toList().size() != goods.size()) {
            String secondType = goods.stream().filter(x -> !x.getType().equals(type)).toList().stream().findFirst()
                    .get().getType();
            String thirdType = goods.stream().filter(x -> !x.getType().equals(type) && !x.getType().equals(secondType))
                    .toList().stream().findFirst().get().getType();

            List<HashMap<String, String>> secondInfo = Client.select(secondType, "id <> 0");
            List<HashMap<String, String>> thirdInfo = Client.select(thirdType, "id <> 0");

            result.put(secondType, secondInfo);
            result.put(thirdType, thirdInfo);
        }

        return result;
    }
}
