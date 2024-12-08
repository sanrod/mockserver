package expectations.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.goods.animals.Bird;
import models.goods.animals.Cat;
import models.goods.animals.Dog;
import models.goods.cars.Car;
import models.goods.food.Food;
import models.goods.toys.Toy;
import models.requests.CreateShopRequest;
import models.shop.ErrorMessage;
import models.shop.Good;
import models.shop.Shop;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import utils.postgres.Client;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class Methods {
    public HttpResponse returnErrorMessageResponse(ErrorMessage message) throws JsonProcessingException {
        if (message.getMessage() != null) {
            JsonBody jsonBody = json(
                    new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(message),
                    MediaType.APPLICATION_JSON);
            return response()
                    .withBody(jsonBody)
                    .withStatusCode(500);
        }
        return null;
    }

    public List<Food> convertSqlToFoods(List<HashMap<String, String>> sqlMessage) {
        List<Food> foods = new ArrayList<>();

        for (HashMap<String, String> hash : sqlMessage) {
            Food food = Food.builder()
                    .calories(Integer.parseInt(hash.get("calories")))
                    .expirationDate(LocalDate.parse(hash.get("expirationDate")))
                    .fat(Integer.parseInt(hash.get("fat")))
                    .id(Integer.parseInt(hash.get("id")))
                    .name(hash.get("name"))
                    .price(Integer.parseInt(hash.get("price")))
                    .sugarAmount(Integer.parseInt(hash.get("sugarAmount")))
                    .build();
            foods.add(food);
        }

        return foods;
    }

    public List<Toy> convertSqlToToys(List<HashMap<String, String>> sqlMessage) {
        List<Toy> toys = new ArrayList<>();

        for (HashMap<String, String> hash : sqlMessage) {
            Toy toy = Toy.builder()
                    .forAdults(Boolean.parseBoolean(hash.get("forAdults")))
                    .id(Integer.parseInt(hash.get("id")))
                    .name(hash.get("name"))
                    .price(Integer.parseInt(hash.get("price")))
                    .build();
            toys.add(toy);
        }

        return toys;
    }

    public List<Car> convertSqlToCars(List<HashMap<String, String>> sqlMessage) {
        List<Car> cars = new ArrayList<>();

        for (HashMap<String, String> hash : sqlMessage) {
            Car car = Car.builder()
                    .availableSince(LocalDate.parse(hash.get("availableSince")))
                    .discount(Integer.parseInt(hash.get("discount")))
                    .horsePowers(Integer.parseInt(hash.get("horsePower")))
                    .id(Integer.parseInt(hash.get("id")))
                    .name(hash.get("name"))
                    .price(Integer.parseInt(hash.get("price")))
                    .sideOfSteeringWheel(hash.get("sideOfSteeringWheel"))
                    .build();
            cars.add(car);
        }

        return cars;
    }

    public List<Dog> convertSqlToDogs(List<HashMap<String, String>> sqlMessage) {
        List<Dog> dogs = new ArrayList<>();

        for (HashMap<String, String> hash : sqlMessage) {
            Dog dog = Dog.builder()
                    .aggressive(Boolean.parseBoolean(hash.get("aggressive")))
                    .barkingALot(Boolean.parseBoolean(hash.get("barkingALot")))
                    .carnivore(Boolean.parseBoolean(hash.get("carnivore")))
                    .id(Integer.parseInt(hash.get("id")))
                    .name(hash.get("name"))
                    .paws(Integer.parseInt(hash.get("paws")))
                    .price(Integer.parseInt(hash.get("price")))
                    .weight(Integer.parseInt(hash.get("weight")))
                    .build();

            dogs.add(dog);
        }

        return dogs;
    }

    public List<Cat> convertSqlToCats(List<HashMap<String, String>> sqlMessage) {
        List<Cat> cats = new ArrayList<>();

        for (HashMap<String, String> hash : sqlMessage) {
            Cat cat = Cat.builder()
                    .carnivore(Boolean.parseBoolean(hash.get("carnivore")))
                    .crazy(Boolean.parseBoolean(hash.get("crazy")))
                    .id(Integer.parseInt(hash.get("id")))
                    .name(hash.get("name"))
                    .paws(Integer.parseInt(hash.get("paws")))
                    .price(Integer.parseInt(hash.get("price")))
                    .weight(Integer.parseInt(hash.get("weight")))
                    .build();

            cats.add(cat);
        }

        return cats;
    }

    public List<Bird> convertSqlToBirds(List<HashMap<String, String>> sqlMessage) {
        List<Bird> birds = new ArrayList<>();

        for (HashMap<String, String> hash : sqlMessage) {
            Bird bird = Bird.builder()
                    .canSign(Boolean.parseBoolean(hash.get("canSign")))
                    .canSpeak(Boolean.parseBoolean(hash.get("canSpeak")))
                    .id(Integer.parseInt(hash.get("id")))
                    .name(hash.get("name"))
                    .price(Integer.parseInt(hash.get("price")))
                    .size(hash.get("size"))
                    .build();

            birds.add(bird);
        }

        return birds;
    }

    public boolean canCreateShop(Shop shop) throws SQLException {
        List<HashMap<String, String>> result =
                Client.select("Shops",
                        String.format(" id = %s OR name = '%s'", shop.getId(), shop.getName()));

        return result.isEmpty();
    }

    public boolean checkShopExist(String shopId) throws SQLException {
        List<HashMap<String, String>> result =
                Client.select("Shops",
                        String.format(" id = %s", shopId));

        return !result.isEmpty();
    }

    public HashMap<String, Boolean> canCreateGoods(CreateShopRequest request) throws SQLException {
        List<Cat> cats = request.getCats();
        List<Dog> dogs = request.getDogs();
        List<Bird> birds = request.getBirds();
        List<Car> cars = request.getCars();
        List<Food> foods = request.getFoods();
        List<Toy> toys = request.getToys();

        boolean isAnimals = false;

        HashMap<String, Boolean> result = new HashMap<>();
        result.put("isCats", false);
        result.put("isBirds", false);
        result.put("isDogs", false);
        result.put("isCars", false);
        result.put("isToys", false);
        result.put("isFoods", false);

        if (cats != null) {
            isAnimals = true;
            result.replace("isCats", true);
            if (!checkCats(cats)) {
                result.put("canCreate", false);
                return result;
            }
        }
        if (dogs != null) {
            result.replace("isDogs", true);
            isAnimals = true;
            if (!checkDogs(dogs)) {
                result.put("canCreate", false);
                return result;
            }
        }
        if (birds != null) {
            result.replace("isBirds", true);
            isAnimals = true;
            if (!checkBirds(birds)) {
                result.put("canCreate", false);
                return result;
            }
        }
        if (cars != null) {
            result.replace("isCars", true);
            if (!checkCars(cars)) {
                result.put("canCreate", false);
                return result;
            }
        }
        if (toys != null) {
            result.replace("isToys", true);
            if (!checkToys(toys)) {
                result.put("canCreate", false);
                return result;
            }
        }
        if (foods != null) {
            result.replace("isFoods", true);
            if (!checkFoods(foods)) {
                result.put("canCreate", false);
                return result;
            }
        }

        List<Boolean> amount = new ArrayList<>();
        amount.add(isAnimals);
        amount.add(result.get("isCars"));
        amount.add(result.get("isFoods"));
        amount.add(result.get("isToys"));

        result.put("canCreate", amount.stream().filter(x -> x.equals(true)).toList().size() <= 1);
        return result;
    }

    private boolean checkCats(List<Cat> cats) throws SQLException {
        for (Cat cat : cats) {
            if (!Client.select("Cats", String.format("id = %s", cat.getId())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDogs(List<Dog> dogs) throws SQLException {
        for (Dog dog : dogs) {
            if (!Client.select("Dogs", String.format("id = %s", dog.getId())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkBirds(List<Bird> birds) throws SQLException {
        for (Bird bird : birds) {
            if (!Client.select("Birds", String.format("id = %s", bird.getId())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkToys(List<Toy> toys) throws SQLException {
        for (Toy toy : toys) {
            if (!Client.select("Toys", String.format("id = %s", toy.getId())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkFoods(List<Food> foods) throws SQLException {
        for (Food food : foods) {
            if (!Client.select("Foods", String.format("id = %s", food.getId())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCars(List<Car> cars) throws SQLException {
        for (Car car : cars) {
            if (!Client.select("Cars", String.format("id = %s", car.getId())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<Good> createGoods(HashMap<String, Boolean> canCreateGoods, CreateShopRequest request) throws SQLException {
        List<Good> goods = new ArrayList<>();
        if (canCreateGoods.get("isBirds")) {
            for (Bird bird : request.getBirds()) {
                Client.insert("Birds", List.of(
                        String.valueOf(bird.getId()),
                        bird.getName(),
                        String.valueOf(bird.isCanSign()),
                        String.valueOf(bird.isCanSpeak()),
                        String.valueOf(bird.getSize()),
                        String.valueOf(bird.getPrice())
                ));
                goods.add(Good.builder()
                        .type("Birds")
                        .id(bird.getId())
                        .build());
            }
        }
        if (canCreateGoods.get("isCars")) {
            for (Car car : request.getCars()) {
                Client.insert("Cars", List.of(
                        String.valueOf(car.getId()),
                        car.getName(),
                        car.getSideOfSteeringWheel(),
                        String.valueOf(car.getHorsePowers()),
                        String.valueOf(car.getAvailableSince()),
                        String.valueOf(car.getDiscount()),
                        String.valueOf(car.getPrice())
                ));
                goods.add(Good.builder()
                        .type("Cars")
                        .id(car.getId())
                        .build());
            }
        }
        if (canCreateGoods.get("isCats")) {
            for (Cat cat : request.getCats()) {
                Client.insert("Cats", List.of(
                        String.valueOf(cat.getId()),
                        cat.getName(),
                        String.valueOf(cat.getPaws()),
                        String.valueOf(cat.isCarnivore()),
                        String.valueOf(cat.isCrazy()),
                        String.valueOf(cat.getPrice()),
                        String.valueOf(cat.getWeight())
                ));
                goods.add(Good.builder()
                        .type("Cats")
                        .id(cat.getId())
                        .build());
            }
        }
        if (canCreateGoods.get("isDogs")) {
            for (Dog dog : request.getDogs()) {
                Client.insert("Dogs", List.of(
                        String.valueOf(dog.getId()),
                        dog.getName(),
                        String.valueOf(dog.getPaws()),
                        String.valueOf(dog.isCarnivore()),
                        String.valueOf(dog.isAggressive()),
                        String.valueOf(dog.isBarkingALot()),
                        String.valueOf(dog.getPrice()),
                        String.valueOf(dog.getWeight())
                ));
                goods.add(Good.builder()
                        .type("Dogs")
                        .id(dog.getId())
                        .build());
            }
        }
        if (canCreateGoods.get("isFoods")) {
            for (Food food : request.getFoods()) {
                Client.insert("Foods", List.of(
                        String.valueOf(food.getId()),
                        food.getName(),
                        String.valueOf(food.getExpirationDate()),
                        String.valueOf(food.getCalories()),
                        String.valueOf(food.getSugarAmount()),
                        String.valueOf(food.getFat()),
                        String.valueOf(food.getPrice())
                ));
                goods.add(Good.builder()
                        .type("Foods")
                        .id(food.getId())
                        .build());
            }
        }
        if (canCreateGoods.get("isToys")) {
            for (Toy toy : request.getToys()) {
                Client.insert("Toys", List.of(
                        String.valueOf(toy.getId()),
                        toy.getName(),
                        String.valueOf(toy.isForAdults()),
                        String.valueOf(toy.getPrice())
                ));
                goods.add(Good.builder()
                        .type("Toys")
                        .id(toy.getId())
                        .build());
            }
        }

        return goods;
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
}
