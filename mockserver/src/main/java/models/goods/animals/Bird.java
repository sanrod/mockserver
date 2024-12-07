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
@JsonIgnoreProperties(ignoreUnknown = true)
@Jacksonized
public class Bird {
    private String name;
    private int id;
    private boolean canSign;
    private boolean canSpeak;
    private String size;
    private int price;
}
