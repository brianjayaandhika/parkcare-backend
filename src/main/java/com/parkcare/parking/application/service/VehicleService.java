package com.parkcare.parking.application.service;

import com.parkcare.parking.application.entity.TicketHistory;
import com.parkcare.parking.application.entity.Vehicle;
import com.parkcare.parking.application.model.CheckInRequest;
import com.parkcare.parking.application.model.CheckInResponse;
import com.parkcare.parking.application.model.CheckOutRequest;
import com.parkcare.parking.application.model.CheckOutResponse;
import com.parkcare.parking.application.repository.TicketHistoryRepository;
import com.parkcare.parking.application.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ValidationService validationService;

    @Value("${parking.rate.per.hour}")
    private Integer ratePerHour;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    private Integer calculateParkingPrice(Instant checkInAt, Instant checkOutAt) {
        Integer diff = (int) checkOutAt.until(checkInAt, ChronoUnit.HOURS);

        return ratePerHour * diff;
    }

    @Transactional
    public CheckInResponse checkIn(
            CheckInRequest request
    ) {
        validationService.validate(request);

        if (vehicleRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle already registered");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID().toString());
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setCheckInAt(Instant.now());

        vehicleRepository.save(vehicle);

        return CheckInResponse.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .checkInAt(vehicle.getCheckInAt())
                .build();
    }

    @Transactional
    public CheckOutResponse checkOut(
            CheckOutRequest request
    ) {
        validationService.validate(request);

        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(request.getPlateNumber()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle Not Found")
        );

        Instant now = Instant.now();
        Integer totalPrice = calculateParkingPrice(vehicle.getCheckInAt(), now);

        vehicle.setCheckOutAt(now);
        vehicle.setPrice(totalPrice);

        TicketHistory ticketHistory = new TicketHistory().builder()
                .id(UUID.randomUUID().toString())
                .plateNumber(vehicle.getPlateNumber())
                .checkInAt(vehicle.getCheckInAt())
                .checkOutAt(vehicle.getCheckOutAt())
                .price(vehicle.getPrice())
                .ratePerHour(ratePerHour)
                .build();

        ticketHistoryRepository.save(ticketHistory);
        vehicleRepository.delete(vehicle);

        return CheckOutResponse.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .checkInAt(vehicle.getCheckInAt())
                .checkOutAt(vehicle.getCheckOutAt())
                .price(vehicle.getPrice())
                .build();
    }
}
