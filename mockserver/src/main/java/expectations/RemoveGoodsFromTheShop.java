package expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.requests.RemoveGoodsRequest;
import models.shop.Good;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import utils.postgres.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class RemoveGoodsFromTheShop {
    public Expectation createExp() {
        String path = "/removeGoods";
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
            RemoveGoodsRequest removeGoodsRequest = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(httpRequest.getBodyAsString(), RemoveGoodsRequest.class);

            boolean shopExists = new Methods().checkShopExist(removeGoodsRequest.getShopId());

            if (!shopExists) {
                return response().withStatusCode(400).withBody("The shop doesn't exist! You have to create it first.");
            }
            if (!checkGoods(removeGoodsRequest)) {
                return response().withStatusCode(404).withBody("The goods are not found in the shop!");
            }

            try {
                deleteGoods(removeGoodsRequest);
                return response().withStatusCode(201).withBody("The goods were removed!");
            } catch (Exception ex) {
                return response().withStatusCode(500).withBody(String.format("Something went wrong!\n %s", ex.getMessage()));
            }
        }

        private static boolean checkGoods(RemoveGoodsRequest removeGoodsRequest) throws Exception {
            List<Good> goods = new Methods().getGoods(removeGoodsRequest.getShopId());
            for (String id : removeGoodsRequest.getGoodsIds()) {
                if (goods.stream().filter(x -> id.equals(String.valueOf(x.getId()))).toList().isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static void deleteGoods(RemoveGoodsRequest removeGoodsRequest) throws Exception {
            final List<Good> goods = new Methods().getGoods(removeGoodsRequest.getShopId());
            List<Good> itemsForRemove = new ArrayList<>();
            List<Good> updatedGoods = new ArrayList<>(goods);

            for (String id : removeGoodsRequest.getGoodsIds()) {
                Good itemForRemove = goods.stream().filter(x -> x.getId() == Integer.parseInt(id))
                        .toList()
                        .stream().findFirst()
                        .get();
                itemsForRemove.add(itemForRemove);
                updatedGoods.remove(itemForRemove);
            }

            for (Good item : itemsForRemove) {
                Client.deleteFrom(item.getType(), String.format(" id = %s", item.getId()));
            }

            if (itemsForRemove.size() == goods.size()) {
                Client.deleteFrom("Shops", String.format("id = %s", removeGoodsRequest.getShopId()));
            } else {
                HashMap<String, String> goodMap = new HashMap<>();
                goodMap.put("goods", new ObjectMapper().writeValueAsString(updatedGoods));
                Client.updateRow("Shops", String.format("id = %s", removeGoodsRequest.getShopId()), goodMap);
            }
        }
    }
}
