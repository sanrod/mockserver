package expectations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import expectations.common.Methods;
import models.shop.*;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import utils.postgres.Client;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
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
            List<HashMap<String, String>> result = new ArrayList<>();
            ErrorMessage message = new ErrorMessage();

            try {
                result = Client.select("Shops", "id <> 0");
            } catch (Exception ex) {
                message = ErrorMessage.builder()
                        .message(String.format("Some error occurred during request. The error %s", ex.getMessage()))
                        .build();
            }


            List<Shop> body = new ArrayList<>();
            for (HashMap<String, String> map : result) {
                int id = Integer.parseInt(map.get("id"));
                String name = map.get("name");
                String addressStr = map.get("address");
                String goodsStr = map.get("goods");

                if (!goodsStr.isEmpty()) {
                    Address address1 = new ObjectMapper().registerModule(new JavaTimeModule())
                            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                            .readValue(addressStr, Address.class);
                    if (address1.getOpenDate().toEpochSecond(LocalTime.now(), ZoneOffset.UTC) < ZonedDateTime.now().toEpochSecond()
                            &&
                            address1.getCloseDate().toEpochSecond(LocalTime.now(), ZoneOffset.UTC) > ZonedDateTime.now().toEpochSecond()) {

                        Goods goods;
                        try {
                            goods = getGoods(goodsStr);
                        } catch (Exception ex) {
                            message = ErrorMessage.builder()
                                    .message(String.format("Requesting the products for the shopId = %s caused the error %s",
                                            id, ex.getMessage()))
                                    .build();
                            break;
                        }

                        body.add(Shop.builder()
                                .id(id)
                                .name(name)
                                .address(address1)
                                .goods(goods)
                                .build());
                    }
                }
            }

            HttpResponse response =  new Methods().returnErrorMessageResponse(message);
            if(response != null) {
                return response;
            }
            JsonBody jsonBody = json(
                    new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(body),
                    MediaType.APPLICATION_JSON);
            return response()
                    .withBody(jsonBody);
        }

        private final static List<String> pets = List.of("Birds", "Cats", "Dogs");

        private static Goods getGoods(String goodsStr) throws Exception {
            List<Good> goods = new ObjectMapper().readValue(goodsStr, new TypeReference<>() {
            });

            int amount = goods.size();
            int price = 0;
            String type = "";
            ZonedDateTime earliestExpirationDate = ZonedDateTime.now();

            for (Good good : goods) {
                if (!type.isEmpty()) {
                    if (!pets.contains(type)) {
                        if (!type.equals(good.getType())) {
                            throw new Exception(String.format("The shop contains two different (%s and %s) " +
                                    "types of products!", type, good.getType()));
                        }
                    }
                }
                type = good.getType();
                int id = good.getId();

                List<HashMap<String, String>> result = Client.select(type, String.format(" id = %s", id));
                if (result.isEmpty()) {
                    throw new Exception(String.format("A good with id = %s and type = %s is not found!", id, type));
                }

                price += Integer.parseInt(result.stream().findFirst().get().get("price"));

                if (type.equals("Foods")) {
                    String expDate = result.stream().findFirst().get().get("expirationDate");

                    if (expDate != null) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(expDate);

                        if (zonedDateTime.toEpochSecond() < earliestExpirationDate.toEpochSecond()) {
                            earliestExpirationDate = zonedDateTime;
                        }
                    }
                }
            }

            return Goods.builder()
                    .goodsType(pets.contains(type)? "pets": type )
                    .totalPrice(price)
                    .amount(amount)
                    .expirationDate(type.equals("Foods")? earliestExpirationDate : null)
                    .build();
        }
    }
}
