package models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Animals {
    CATS("CATS"),
    DOGS("DOGS"),
    BIRDS("BIRDS");

    private final String name;
}
