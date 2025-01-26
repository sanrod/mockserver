package expectations.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.shop.Good;
import models.shop.Shop;
import utils.postgres.Client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ShopAdder {
    public void createShop(Shop shop, List<Good> goods) throws JsonProcessingException, SQLException {
        Client.insert("Shops", List.of(
                String.valueOf(shop.getId()),
                shop.getName(),
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsString(shop.getAddress()),
                new ObjectMapper().writeValueAsString(goods)));
    }

    public boolean canCreateShop(Shop shop) throws SQLException {
        List<HashMap<String, String>> result =
                Client.select("Shops",
                        String.format(" id = %s OR name = '%s'", shop.getId(), shop.getName()));

        return result.isEmpty();
    }
}
