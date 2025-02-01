package models.goods.toys;

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
public class Toy {
    private int id;
    private String name;
    private boolean forAdults;
    private int price;

    @JsonProperty("forAdults")
    public void setForAdults(String forAdults) {
        this.forAdults = Boolean.getBoolean(forAdults);
    }
}
