package models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@Getter
@RequiredArgsConstructor
public enum Countries {
    USA("USA"),
    RUSSIA("RUSSIA"),
    JAPAN("JAPAN"),
    KOREA("KOREA"),
    SERBIA("SERBIA"),
    GEORGIA("GEORGIA");
    private final String name;

    private static final List<Countries> VALUES =
            List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Countries randomCountry() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
