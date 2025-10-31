package io.github.mahjoubech.smartlogiv2.model.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ColisStatus {
    CREE,
    COLLECTE,
    EN_STOCK,
    EN_TRANSIT,
    LIVRE,
    ANNULE;

    public static String getAllowedValues() {
        return Arrays.stream(ColisStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
