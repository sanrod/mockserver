package models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@Getter
@RequiredArgsConstructor
public enum Cities {
    TBILISI("TBILISI"),
    MOSCOW("MOSCOW"),
    SEOUL("SEOUL"),
    BELGRADE("BELGRADE"),
    WASHINGTON("WASHINGTON"),
    TOKYO("TOKYO");
    private final String name;

    private static final List<Cities> VALUES =
            List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Cities randomCity() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
