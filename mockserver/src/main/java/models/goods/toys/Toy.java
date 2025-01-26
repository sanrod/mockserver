package models.goods.toys;

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
public class Toy {
    private int id;
    private String name;
    private boolean forAdults;
    private int price;
}
