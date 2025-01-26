package models.goods.animals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class Dog {
    private int paws;
    private boolean carnivore;
    private int id;
    private String name;
    private int weight;
    private boolean barkingALot;
    private boolean aggressive;
    private int price;
}
