package expectations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.goods.animals.Bird;
import models.goods.animals.Cat;
import models.goods.animals.Dog;
import models.goods.cars.Car;
import models.goods.food.Food;
import models.goods.toys.Toy;
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

import java.sql.SQLException;
import java.util.ArrayList;
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
        @Override
        public HttpResponse handle(HttpRequest httpRequest) throws Exception {
            CreateShopRequest request = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(httpRequest.getBodyAsString(), CreateShopRequest.class);

            if (!canCreateShop(request.getShop())) {
                return response()
                        .withBody("Can't create the shop! Already exists with the same id or name!")
                        .withStatusCode(400);
            }

            if (!canCreateGoods(request)) {
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

        private static boolean canCreateShop(Shop shop) throws SQLException {
            List<HashMap<String, String>> result =
                    Client.select("Shops",
                            String.format(" id = %s OR name = '%s'", shop.getId(), shop.getName()));

            return result.isEmpty();
        }

        private static boolean isCats = false;
        private static boolean isDogs = false;
        private static boolean isBirds = false;
        private static boolean isFoods = false;
        private static boolean isCars = false;
        private static boolean isToys = false;

        private static boolean canCreateGoods(CreateShopRequest request) throws SQLException {
            List<Cat> cats = request.getCats();
            List<Dog> dogs = request.getDogs();
            List<Bird> birds = request.getBirds();
            List<Car> cars = request.getCars();
            List<Food> foods = request.getFoods();
            List<Toy> toys = request.getToys();

            boolean isAnimals = false;

            if (cats != null) {
                isCats = true;
                isAnimals = true;
                if (!checkCats(cats)) {
                    return false;
                }
            }
            if (dogs != null) {
                isDogs = true;
                isAnimals = true;
                if (!checkDogs(dogs)) {
                    return false;
                }
            }
            if (birds != null) {
                isBirds = true;
                isAnimals = true;
                if (!checkBirds(birds)) {
                    return false;
                }
            }
            if (cars != null) {
                isCars = true;
                if (!checkCars(cars)) {
                    return false;
                }
            }
            if (toys != null) {
                isToys = true;
                if (!checkToys(toys)) {
                    return false;
                }
            }
            if (foods != null) {
                isFoods = true;
                if (!checkFoods(foods)) {
                    return false;
                }
            }

            List<Boolean> amount = new ArrayList<>();
            amount.add(isAnimals);
            amount.add(isCars);
            amount.add(isFoods);
            amount.add(isToys);

            return amount.stream().filter(x -> x.equals(true)).toList().size() <= 1;
        }

        private static boolean checkCats(List<Cat> cats) throws SQLException {
            for (Cat cat : cats) {
                if (!Client.select("Cats", String.format("id = %s", cat.getId())).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static boolean checkDogs(List<Dog> dogs) throws SQLException {
            for (Dog dog : dogs) {
                if (!Client.select("Dogs", String.format("id = %s", dog.getId())).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static boolean checkBirds(List<Bird> birds) throws SQLException {
            for (Bird bird : birds) {
                if (!Client.select("Birds", String.format("id = %s", bird.getId())).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static boolean checkToys(List<Toy> toys) throws SQLException {
            for (Toy toy : toys) {
                if (!Client.select("Toys", String.format("id = %s", toy.getId())).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static boolean checkFoods(List<Food> foods) throws SQLException {
            for (Food food : foods) {
                if (!Client.select("Foods", String.format("id = %s", food.getId())).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static boolean checkCars(List<Car> cars) throws SQLException {
            for (Car car : cars) {
                if (!Client.select("Cars", String.format("id = %s", car.getId())).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private static void createShopAndGoods(CreateShopRequest request) throws SQLException, JsonProcessingException {
            List<Good> goods = new ArrayList<>();
            if (isBirds) {
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
            if (isCars) {
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
            if (isCats) {
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
            if (isDogs) {
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
            if (isFoods) {
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
            if (isToys) {
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
