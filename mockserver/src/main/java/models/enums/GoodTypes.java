package models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GoodTypes {
    ANIMALS("ANIMALS"),
    TOYS("TOYS"),
    CARS("CARS"),
    FOOD("FOOD"),
    WRONG_TYPE("WRONG_TYPE");

    private final String name;
}
