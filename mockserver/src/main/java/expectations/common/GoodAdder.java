package expectations.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.goods.animals.Bird;
import models.goods.animals.Cat;
import models.goods.animals.Dog;
import models.goods.cars.Car;
import models.goods.food.Food;
import models.goods.toys.Toy;
import models.shop.Good;
import models.storage.AddGoodsResult;
import utils.postgres.Client;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoodAdder {
    public AddGoodsResult addBirds(List<Bird> birds) throws JsonProcessingException, SQLException {
        List<Good> result = new ArrayList<>();
        List<String> excludedGoods = new ArrayList<>();
        for (Bird bird : birds) {
            if (checkBird(bird)) {
                addBird(bird);
                result.add(Good.builder()
                        .id(bird.getId())
                        .type("Birds")
                        .build());
            } else {
                excludedGoods.add(new ObjectMapper().writeValueAsString(bird));
            }
        }
        return AddGoodsResult.builder()
                .added(result)
                .excluded(excludedGoods)
                .build();
    }

    public void addBird(Bird bird) throws SQLException {
        Client.insert("Birds", List.of(
                String.valueOf(bird.getId()),
                bird.getName(),
                String.valueOf(bird.isCanSign()),
                String.valueOf(bird.isCanSpeak()),
                String.valueOf(bird.getSize()),
                String.valueOf(bird.getPrice())
        ));
    }

    public AddGoodsResult addCats(List<Cat> cats) throws JsonProcessingException, SQLException {
        List<Good> result = new ArrayList<>();
        List<String> excludedGoods = new ArrayList<>();
        for (Cat cat : cats) {
            if (checkCat(cat)) {
                addCat(cat);
                result.add(Good.builder()
                        .id(cat.getId())
                        .type("Cats")
                        .build());
            } else {
                excludedGoods.add(new ObjectMapper().writeValueAsString(cat));
            }
        }
        return AddGoodsResult.builder()
                .added(result)
                .excluded(excludedGoods)
                .build();
    }

    public void addCat(Cat cat) throws SQLException {
        Client.insert("Cats", List.of(
                String.valueOf(cat.getId()),
                cat.getName(),
                String.valueOf(cat.getPaws()),
                String.valueOf(cat.isCarnivore()),
                String.valueOf(cat.isCrazy()),
                String.valueOf(cat.getPrice()),
                String.valueOf(cat.getWeight())
        ));
    }

    public AddGoodsResult addDogs(List<Dog> dogs) throws JsonProcessingException, SQLException {
        List<Good> result = new ArrayList<>();
        List<String> excludedGoods = new ArrayList<>();
        for (Dog dog : dogs) {
            if (checkDog(dog)) {
                addDog(dog);
                result.add(Good.builder()
                        .id(dog.getId())
                        .type("Dogs")
                        .build());
            } else {
                excludedGoods.add(new ObjectMapper().writeValueAsString(dog));
            }
        }
        return AddGoodsResult.builder()
                .added(result)
                .excluded(excludedGoods)
                .build();
    }

    public void addDog(Dog dog) throws SQLException {
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
    }

    public AddGoodsResult addCars(List<Car> cars) throws JsonProcessingException, SQLException {
        List<Good> result = new ArrayList<>();
        List<String> excludedGoods = new ArrayList<>();
        for (Car car : cars) {
            if (checkCar(car)) {
                addCar(car);
                result.add(Good.builder()
                        .id(car.getId())
                        .type("Cars")
                        .build());
            } else {
                excludedGoods.add(new ObjectMapper().writeValueAsString(car));
            }
        }
        return AddGoodsResult.builder()
                .added(result)
                .excluded(excludedGoods)
                .build();
    }

    public void addCar(Car car) throws SQLException {
        Client.insert("Cars", List.of(
                String.valueOf(car.getId()),
                car.getName(),
                car.getSideOfSteeringWheel(),
                String.valueOf(car.getHorsePowers()),
                String.valueOf(car.getAvailableSince()),
                String.valueOf(car.getDiscount()),
                String.valueOf(car.getPrice())
        ));
    }

    public AddGoodsResult addToys(List<Toy> toys) throws JsonProcessingException, SQLException {
        List<Good> result = new ArrayList<>();
        List<String> excludedGoods = new ArrayList<>();
        for (Toy toy : toys) {
            if (checkToy(toy)) {
                addToy(toy);
                result.add(Good.builder()
                        .id(toy.getId())
                        .type("Toys")
                        .build());
            } else {
                excludedGoods.add(new ObjectMapper().writeValueAsString(toy));
            }
        }
        return AddGoodsResult.builder()
                .added(result)
                .excluded(excludedGoods)
                .build();
    }

    public void addToy(Toy toy) throws SQLException {
        Client.insert("Toys", List.of(
                String.valueOf(toy.getId()),
                toy.getName(),
                String.valueOf(toy.isForAdults()),
                String.valueOf(toy.getPrice())
        ));
    }

    public AddGoodsResult addAllFood(List<Food> foods) throws JsonProcessingException, SQLException {
        List<Good> result = new ArrayList<>();
        List<String> excludedGoods = new ArrayList<>();
        for (Food food : foods) {
            if (checkFood(food)) {
                addFood(food);
                result.add(Good.builder()
                        .id(food.getId())
                        .type("Foods")
                        .build());
            } else {
                excludedGoods.add(new ObjectMapper().writeValueAsString(food));
            }
        }
        return AddGoodsResult.builder()
                .added(result)
                .excluded(excludedGoods)
                .build();
    }

    public void addFood(Food food) throws SQLException {
        Client.insert("Foods", List.of(
                String.valueOf(food.getId()),
                food.getName(),
                String.valueOf(food.getExpirationDate()),
                String.valueOf(food.getCalories()),
                String.valueOf(food.getSugarAmount()),
                String.valueOf(food.getFat()),
                String.valueOf(food.getPrice())
        ));
    }

    public boolean checkCat(Cat cat) throws SQLException {
        return Client.select("Cats", String.format("id = %s", cat.getId())).isEmpty();
    }

    public boolean checkDog(Dog dog) throws SQLException {
        return Client.select("Dogs", String.format("id = %s", dog.getId())).isEmpty();
    }

    public boolean checkBird(Bird bird) throws SQLException {
        return Client.select("Birds", String.format("id = %s", bird.getId())).isEmpty();
    }

    public boolean checkToy(Toy toy) throws SQLException {
        return Client.select("Toys", String.format("id = %s", toy.getId())).isEmpty();
    }

    public boolean checkFood(Food food) throws SQLException {
        return Client.select("Foods", String.format("id = %s", food.getId())).isEmpty();
    }

    public boolean checkCar(Car car) throws SQLException {
        return Client.select("Cars", String.format("id = %s", car.getId())).isEmpty();
    }
}
