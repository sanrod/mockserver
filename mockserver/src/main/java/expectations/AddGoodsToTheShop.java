package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.enums.GoodTypes;
import models.requests.AddGoodsRequest;
import models.requests.CreateShopRequest;
import models.shop.Good;
import models.shop.Shop;
import models.storage.AddGoodsResult;
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
            AddGoodsRequest addGoodsRequest;
            try {
                addGoodsRequest = new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .readValue(httpRequest.getBodyAsString(), AddGoodsRequest.class);
            } catch (Exception ex) {
                return response()
                        .withStatusCode(400)
                        .withBody("Wrong body message!");
            }


            boolean shopExists = new Methods().checkShopExist(addGoodsRequest.getShopId());

            if (!shopExists) {
                return response()
                        .withStatusCode(400)
                        .withBody("The shop doesn't exist! You have to create it first.");
            }

            CreateShopRequest request = CreateShopRequest.builder()
                    .shop(Shop.builder()
                            .id(Integer.parseInt(addGoodsRequest.getShopId()))
                            .build())
                    .birds(addGoodsRequest.getBirds())
                    .cars(addGoodsRequest.getCars())
                    .cats(addGoodsRequest.getCats())
                    .dogs(addGoodsRequest.getDogs())
                    .foods(addGoodsRequest.getFoods())
                    .toys(addGoodsRequest.getToys())
                    .build();

            GoodTypes type = new Methods().checkGoodsInRequest(request);
            if (type == GoodTypes.WRONG_TYPE) {
                return response()
                        .withBody("Can't add the goods to the shop!There are more than one type of goods or" +
                                " an incorrect type!")
                        .withStatusCode(400);
            }

            try {
                AddGoodsResult goods = new Methods().transformGoodsToDbFormat(type, request);
                List<Good> existingGoods = new Methods().getGoods(addGoodsRequest.getShopId());
                existingGoods.addAll(goods.getAdded());
                HashMap<String, String> goodMap = new HashMap<>();
                goodMap.put("goods", new ObjectMapper().writeValueAsString(existingGoods));
                Client.updateRow("Shops", String.format("id = %s", addGoodsRequest.getShopId()), goodMap);
                return response()
                        .withStatusCode(201)
                        .withBody(String.format("The goods %s added to the shop! The goods %s are excluded",
                                new ObjectMapper().writeValueAsString(goods.getAdded()),
                                new ObjectMapper().writeValueAsString(goods.getExcluded())));
            } catch (Exception ex) {
                return response()
                        .withStatusCode(500)
                        .withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }
        }
    }
}
