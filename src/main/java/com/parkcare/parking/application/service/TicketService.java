package com.parkcare.parking.application.service;

import com.parkcare.parking.application.entity.TicketHistory;
import com.parkcare.parking.application.entity.Ticket;
import com.parkcare.parking.application.model.InputRequest;
import com.parkcare.parking.application.model.ActiveTicketResponse;
import com.parkcare.parking.application.model.CheckOutResponse;
import com.parkcare.parking.application.repository.TicketHistoryRepository;
import com.parkcare.parking.application.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ValidationService validationService;

    @Value("${parking.rate.per.hour:3000}")
    private Integer ratePerHour;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    private final Clock clock;

    private int calculateParkingPrice(Instant checkInAt, Instant checkOutAt) {
        long seconds = Duration.between(checkInAt, checkOutAt).getSeconds();
        long hours = (long) Math.ceil(seconds / 3600.0);
        return (int) (ratePerHour * Math.max(hours, 1));
    }

    @Transactional
    public ActiveTicketResponse checkIn(
            InputRequest request
    ) {
        validationService.validate(request);

        if (ticketRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket Already Registered");
        }

        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setPlateNumber(request.getPlateNumber());
        ticket.setCheckInAt(Instant.now(clock));

        ticketRepository.save(ticket);

        return ActiveTicketResponse.builder()
                .id(ticket.getId())
                .plateNumber(ticket.getPlateNumber())
                .checkInAt(ticket.getCheckInAt())
                .build();
    }

    @Transactional
    public CheckOutResponse checkOut(
            InputRequest request
    ) {
        validationService.validate(request);

        Ticket ticket = ticketRepository.findFirstByPlateNumber(request.getPlateNumber()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket Not Found")
        );

        Instant now = Instant.now(clock);
        Integer totalPrice = calculateParkingPrice(ticket.getCheckInAt(), now);

        ticket.setCheckOutAt(now);
        ticket.setPrice(totalPrice);

        TicketHistory ticketHistory = new TicketHistory().builder()
                .id(UUID.randomUUID().toString())
                .plateNumber(ticket.getPlateNumber())
                .checkInAt(ticket.getCheckInAt())
                .checkOutAt(ticket.getCheckOutAt())
                .price(ticket.getPrice())
                .ratePerHour(ratePerHour)
                .idReference(ticket.getId())
                .build();

        ticketHistoryRepository.save(ticketHistory);
        ticketRepository.delete(ticket);

        return CheckOutResponse.builder()
                .id(ticket.getId())
                .plateNumber(ticket.getPlateNumber())
                .checkInAt(ticket.getCheckInAt())
                .checkOutAt(ticket.getCheckOutAt())
                .price(ticket.getPrice())
                .build();
    }

    public ActiveTicketResponse getTicketDetail(InputRequest request) {
        validationService.validate(request);

        Ticket ticket = ticketRepository.findFirstByPlateNumber(request.getPlateNumber()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket Not Found")
        );

        return ActiveTicketResponse.builder()
                .id(ticket.getId())
                .plateNumber(ticket.getPlateNumber())
                .checkInAt(ticket.getCheckInAt())
                .build();
    }

    public List<Ticket> getAllActiveTickets() {
        return ticketRepository.findAllByOrderByCheckInAtDesc();
    }

    public List<TicketHistory> getAllHistoryTickets() {
        return ticketHistoryRepository.findAllByOrderByCheckOutAtDesc();
    }
}
