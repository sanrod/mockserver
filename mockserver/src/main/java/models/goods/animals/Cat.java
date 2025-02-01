package models.goods.animals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Cat {
    private int paws;
    private boolean carnivore;
    private int id;
    private String name;
    private int weight;
    private boolean crazy;
    private int price;

    @JsonProperty("crazy")
    public void setCrazy(String crazy) {
        this.crazy = Boolean.getBoolean(crazy);
    }
    @JsonProperty("carnivore")
    public void setCarnivore(String carnivore) {
        this.carnivore = Boolean.getBoolean(carnivore);
    }
}
