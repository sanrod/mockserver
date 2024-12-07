package utils.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.enums.Cities;
import models.enums.Countries;
import models.goods.animals.Bird;
import models.goods.animals.Cat;
import models.goods.animals.Dog;
import models.goods.cars.Car;
import models.goods.food.Food;
import models.goods.toys.Toy;
import models.shop.Address;
import models.shop.Good;
import models.shop.Shop;
import utils.postgres.Client;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    public void generateShops() throws SQLException, JsonProcessingException {
        List<Good> goods = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());

        Shop shop = createShop(1, "Toy");
        for (int i = 1; i < 11; i++) {
            goods.add(Good.builder()
                    .id(i)
                    .type("Toys")
                    .build());
        }
        Client.insert("Shops", List.of(
                String.valueOf(shop.getId()),
                shop.getName(),
                objectMapper.writeValueAsString(shop.getAddress()),
                objectMapper.writeValueAsString(goods)));
        goods.removeAll(goods);

        for (int i = 1; i < 11; i++) {
            goods.add(Good.builder()
                    .id(i)
                    .type("Cars")
                    .build());
        }
        shop = createShop(2, "Car");
        Client.insert("Shops", List.of(
                String.valueOf(shop.getId()),
                shop.getName(),
                objectMapper.writeValueAsString(shop.getAddress()),
                objectMapper.writeValueAsString(goods)));
        goods.removeAll(goods);

        for (int i = 1; i < 11; i++) {
            goods.add(Good.builder()
                    .id(i)
                    .type("Foods")
                    .build());
        }
        shop = createShop(3, "Food");
        Client.insert("Shops", List.of(
                String.valueOf(shop.getId()),
                shop.getName(),
                objectMapper.writeValueAsString(shop.getAddress()),
                objectMapper.writeValueAsString(goods)));
        goods.removeAll(goods);

        for (int i = 1; i < 11; i++) {
            goods.add(Good.builder()
                    .id(i)
                    .type("Birds")
                    .build());
        }
        for (int i = 1; i < 11; i++) {
            goods.add(Good.builder()
                    .id(i)
                    .type("Cats")
                    .build());
        }
        for (int i = 1; i < 11; i++) {
            goods.add(Good.builder()
                    .id(i)
                    .type("Dogs")
                    .build());
        }
        shop = createShop(4, "Pet");
        Client.insert("Shops", List.of(
                String.valueOf(shop.getId()),
                shop.getName(),
                objectMapper.writeValueAsString(shop.getAddress()),
                objectMapper.writeValueAsString(goods)));

        createBirds();
        createCars();
        createCats();
        createDogs();
        createFoods();
        createToys();
    }

    private Shop createShop(int id, String namePrefix) {
        return Shop.builder()
                .address(createAddress())
                .goods(null)
                .id(id)
                .name(String.format("%s %s", namePrefix, RandomGenerator.generateString()))
                .build();
    }

    private Address createAddress() {
        return Address.builder()
                .city(Cities.randomCity().getName())
                .closeDate(LocalDate.now().plusMonths(6))
                .country(Countries.randomCountry().getName())
                .openDate(LocalDate.now().minusMonths(6))
                .street(RandomGenerator.generateString())
                .build();
    }

    private void createToys() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Toy toy = Toy.builder()
                    .id(i + 1)
                    .name(RandomGenerator.generateString())
                    .forAdults(RandomGenerator.getRandomBool())
                    .price(RandomGenerator.getRandomNumber())
                    .build();
            Client.insert("Toys", List.of(
                    String.valueOf(toy.getId()),
                    toy.getName(),
                    String.valueOf(toy.isForAdults()),
                    String.valueOf(toy.getPrice())
            ));
        }
    }

    private void createFoods() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Food food = Food.builder()
                    .calories(RandomGenerator.getRandomNumber())
                    .expirationDate(null)
                    .fat(RandomGenerator.getRandomNumber())
                    .id(i + 1)
                    .name(RandomGenerator.generateString())
                    .sugarAmount(RandomGenerator.getRandomNumber())
                    .price(RandomGenerator.getRandomNumber())
                    .build();
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
    }

    private void createCars() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Car car = Car.builder()
                    .availableSince(LocalDate.now().minusMonths(1))
                    .discount(0)
                    .horsePowers(RandomGenerator.getRandomNumber())
                    .id(i + 1)
                    .name(RandomGenerator.generateString())
                    .price(RandomGenerator.getRandomNumber())
                    .sideOfSteeringWheel("Left")
                    .build();
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
    }

    private void createBirds() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Bird bird = Bird.builder()
                    .canSign(RandomGenerator.getRandomBool())
                    .canSpeak(RandomGenerator.getRandomBool())
                    .id(i + 1)
                    .name(RandomGenerator.generateString())
                    .price(RandomGenerator.getRandomNumber())
                    .size("Small")
                    .build();
            Client.insert("Birds", List.of(
                    String.valueOf(bird.getId()),
                    bird.getName(),
                    String.valueOf(bird.isCanSign()),
                    String.valueOf(bird.isCanSpeak()),
                    String.valueOf(bird.getSize()),
                    String.valueOf(bird.getPrice())
            ));
        }
    }

    private void createCats() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Cat cat = Cat.builder()
                    .carnivore(RandomGenerator.getRandomBool())
                    .crazy(RandomGenerator.getRandomBool())
                    .id(i + 1)
                    .name(RandomGenerator.generateString())
                    .paws(4)
                    .price(RandomGenerator.getRandomNumber())
                    .weight(RandomGenerator.getRandomNumber())
                    .build();
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
    }

    private void createDogs() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Dog dog = Dog.builder()
                    .aggressive(RandomGenerator.getRandomBool())
                    .barkingALot(RandomGenerator.getRandomBool())
                    .carnivore(RandomGenerator.getRandomBool())
                    .id(i + 1)
                    .name(RandomGenerator.generateString())
                    .paws(4)
                    .price(RandomGenerator.getRandomNumber())
                    .weight(RandomGenerator.getRandomNumber())
                    .build();
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
    }
}
