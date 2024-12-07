package org.mockserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import expectations.Expectations;
import org.mockserver.integration.ClientAndServer;
import utils.generator.TestDataGenerator;
import utils.postgres.Client;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException, JsonProcessingException {
        Client.createBirds();
        Client.createCars();
        Client.createCats();
        Client.createDogs();
        Client.createFoods();
        Client.createToys();
        Client.createShops();

        new TestDataGenerator().generateShops();
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(8431);
        mockServer.upsert(Expectations.getExpectations());
    }
}