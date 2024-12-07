package expectations.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.goods.animals.Bird;
import models.goods.animals.Cat;
import models.goods.animals.Dog;
import models.goods.cars.Car;
import models.goods.food.Food;
import models.goods.toys.Toy;
import models.shop.ErrorMessage;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

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
}
