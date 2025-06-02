package com.sufi.commons.service;

import com.sufi.module.service.availability.AvailabilityRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class AvailabilityService {
    public AvailabilityRequest construirDisponibilidadRequest(String fechaEntrada, String fechaSalida, Integer occupancy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(fechaEntrada, formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(fechaSalida, formatter).atStartOfDay();

        return new AvailabilityRequest(startDate, endDate, new ArrayList<>(), occupancy);
    }
}
