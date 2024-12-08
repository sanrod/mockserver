package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.requests.AddGoodsRequest;
import models.requests.CreateShopRequest;
import models.shop.Good;
import models.shop.Shop;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import utils.postgres.Client;

import java.util.HashMap;
import java.util.List;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class AddGoodsToTheShop {
    public Expectation createExp() {
        String path = "/addGoods";
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
            AddGoodsRequest addGoodsRequest = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(httpRequest.getBodyAsString(), AddGoodsRequest.class);

            boolean shopExists = new Methods().checkShopExist(addGoodsRequest.getShopId());

            if (!shopExists) {
                return response().withStatusCode(400).withBody("The shop doesn't exist! You have to create it first.");
            }

            HashMap<String, Boolean> canCreateGoods = new Methods().canCreateGoods(CreateShopRequest.builder()
                    .shop(Shop.builder()
                            .id(Integer.parseInt(addGoodsRequest.getShopId()))
                            .build())
                    .birds(addGoodsRequest.getBirds())
                    .cars(addGoodsRequest.getCars())
                    .cats(addGoodsRequest.getCats())
                    .dogs(addGoodsRequest.getDogs())
                    .foods(addGoodsRequest.getFoods())
                    .toys(addGoodsRequest.getToys())
                    .build());

            if (!canCreateGoods.get("canCreate")) {
                return response().withStatusCode(400).withBody("The goods already exist! You can add only new items.");
            }

            List<Good> goods = new Methods().createGoods(canCreateGoods, CreateShopRequest.builder()
                    .shop(Shop.builder()
                            .id(Integer.parseInt(addGoodsRequest.getShopId()))
                            .build())
                    .birds(addGoodsRequest.getBirds())
                    .cars(addGoodsRequest.getCars())
                    .cats(addGoodsRequest.getCats())
                    .dogs(addGoodsRequest.getDogs())
                    .foods(addGoodsRequest.getFoods())
                    .toys(addGoodsRequest.getToys())
                    .build());

            List<Good> existingGoods = new Methods().getGoods(addGoodsRequest.getShopId());
            goods.addAll(existingGoods);

            HashMap<String, String> goodMap = new HashMap<>();
            goodMap.put("goods", new ObjectMapper().writeValueAsString(goods));

            try {
                Client.updateRow("Shops", String.format("id = %s", addGoodsRequest.getShopId()), goodMap);
                return response().withStatusCode(201).withBody("The goods added to the shop!");
            } catch (Exception ex) {
                return response().withStatusCode(500).withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }
        }
    }
}
