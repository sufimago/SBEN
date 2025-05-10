package com.sufi.module.util;

import java.time.LocalDateTime;

public class KeyOptionUtil {
    public static ParsedKeyOption parse(String keyOption) {
        String[] parts = keyOption.split("\\|");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Formato de keyOption inválido: " + keyOption);
        }

        int listingId = Integer.parseInt(parts[0]);
        LocalDateTime fechaEntrada = LocalDateTime.parse(parts[1] + "T00:00:00");
        LocalDateTime fechaSalida = LocalDateTime.parse(parts[2] + "T00:00:00");
        int occupancy = Integer.parseInt(parts[3]);

        return new ParsedKeyOption(listingId, fechaEntrada, fechaSalida, occupancy);
    }

    public record ParsedKeyOption(
            int listingId,
            LocalDateTime fechaEntrada,
            LocalDateTime fechaSalida,
            int occupancy
    ) {}
}
