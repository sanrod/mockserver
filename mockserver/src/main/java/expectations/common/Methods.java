package expectations.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.enums.GoodTypes;
import models.goods.animals.Bird;
import models.goods.animals.Cat;
import models.goods.animals.Dog;
import models.goods.cars.Car;
import models.goods.food.Food;
import models.goods.toys.Toy;
import models.requests.CreateShopRequest;
import models.shop.Address;
import models.shop.Good;
import models.shop.Goods;
import models.shop.Shop;
import models.storage.AddGoodsResult;
import utils.postgres.Client;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Methods {
    private final List<String> animals = new ArrayList<>() {
        {
            add("Cats");
        }

        {
            add("Dogs");
        }

        {
            add("Birds");
        }
    };

    public boolean checkShopExist(String shopId) throws SQLException {
        List<HashMap<String, String>> result =
                Client.select("Shops",
                        String.format(" id = %s", shopId));

        return !result.isEmpty();
    }

    public List<Shop> getAllShops() throws Exception {
        List<HashMap<String, String>> result = Client.select("Shops", "id <> 0");
        List<Shop> shops = new ArrayList<>();
        for (HashMap<String, String> shopLine : result) {
            Shop shop = Shop.builder()
                    .id(Integer.parseInt(shopLine.get("id")))
                    .address(new ObjectMapper()
                            .registerModule(new JavaTimeModule())
                            .readValue(shopLine.get("address"), Address.class))
                    .name(shopLine.get("name"))
                    .build();

            try {
                shop.setGoods(parseGoodsInfo(shopLine.get("goods")));
                shops.add(shop);
            } catch (Exception ignored) {
            }
        }
        return shops;
    }

    private Goods parseGoodsInfo(String goodsMap) throws Exception {
        List<Good> goods = new ObjectMapper().readValue(goodsMap, new TypeReference<>() {
        });

        String type = goods.stream().findFirst().get().getType();
        if (!goods.stream().filter(x -> !checkTypes(type, x.getType())).toList().isEmpty()) {
            throw new Exception("The shop contains multiple types!");
        }

        String finalType;
        if (animals.contains(type)) {
            finalType = "Animals";
        } else {
            finalType = type;
        }

        List<HashMap<String, String>> info = getGoodsInfo(goods);
        int amount = info.size();
        int price = 0;

        for (HashMap<String, String> oneGoodInfo : info) {
            price += Integer.parseInt(oneGoodInfo.get("price"));
        }

        return Goods.builder()
                .goodsType(finalType)
                .amount(amount)
                .totalPrice(price)
                .build();
    }

    private boolean checkTypes(String type, String typeToCheck) {
        if (animals.contains(type) && animals.contains(typeToCheck)) {
            return true;
        }
        return typeToCheck.equals(type);
    }

    public List<HashMap<String, String>> getGoodsInfo(List<Good> goods) throws SQLException {
        List<HashMap<String, String>> result = new ArrayList<>();
        for (Good good : goods) {
            HashMap<String, String> info =
                    Client.select(good.getType(), String.format("id = %s", good.getId()))
                            .stream().findFirst().get();
            if (info.isEmpty()) {
                info = new HashMap<>() {
                    {
                        put("NOT_FOUND",
                                String.format("A good with id = %s and type = %s is not found!",
                                        good.getId(),
                                        good.getType()));
                    }
                };
            }
            result.add(info);
        }
        return result;
    }

    public List<Good> getGoods(String id) throws Exception {
        List<HashMap<String, String>> sqlMessage = Client.select("Shops", String.format("id = %s", id));
        if (sqlMessage.isEmpty()) {
            throw new Exception("Shop is not found!");
        }

        HashMap<String, String> shopInSql = sqlMessage.stream().findFirst().get();
        String goodsStr = shopInSql.get("goods");

        return new ObjectMapper().readValue(goodsStr, new TypeReference<>() {
        });
    }

    public AddGoodsResult transformGoodsToDbFormat(GoodTypes goodType, CreateShopRequest request)
            throws SQLException, JsonProcessingException {
        AddGoodsResult result = null;
        switch (goodType) {
            case CARS -> result = new GoodAdder().addCars(request.getCars());
            case TOYS -> result = new GoodAdder().addToys(request.getToys());
            case FOOD -> result = new GoodAdder().addAllFood(request.getFoods());
            case ANIMALS -> {
                AddGoodsResult birds = new GoodAdder().addBirds(request.getBirds());
                AddGoodsResult cats = new GoodAdder().addCats(request.getCats());
                AddGoodsResult dogs = new GoodAdder().addDogs(request.getDogs());

                List<Good> added = birds.getAdded();
                added.addAll(cats.getAdded());
                added.addAll(dogs.getAdded());

                List<String> excluded = birds.getExcluded();
                excluded.addAll(cats.getExcluded());
                excluded.addAll(dogs.getExcluded());

                result = AddGoodsResult.builder()
                        .added(added)
                        .excluded(excluded)
                        .build();
            }
        }
        return result;
    }

    public GoodTypes checkGoodsInRequest(CreateShopRequest request) {
        List<Cat> cats = request.getCats();
        List<Dog> dogs = request.getDogs();
        List<Bird> birds = request.getBirds();
        List<Car> cars = request.getCars();
        List<Food> foods = request.getFoods();
        List<Toy> toys = request.getToys();

        GoodTypes result = GoodTypes.WRONG_TYPE;

        if (cats != null) {
            result = GoodTypes.ANIMALS;
        }
        if (dogs != null) {
            result = GoodTypes.ANIMALS;
        }
        if (birds != null) {
            result = GoodTypes.ANIMALS;
        }

        if (cars != null) {
            if (result != GoodTypes.WRONG_TYPE) {
                return result;
            }
            result = GoodTypes.CARS;
        }
        if (foods != null) {
            if (result != GoodTypes.WRONG_TYPE) {
                return result;
            }
            result = GoodTypes.FOOD;
        }
        if (toys != null) {
            if (result != GoodTypes.WRONG_TYPE) {
                return result;
            }
            result = GoodTypes.TOYS;
        }
        return result;
    }
}
