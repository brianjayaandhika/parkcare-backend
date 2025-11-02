package com.parkcare.parking.application.controller;

import com.parkcare.parking.application.entity.TicketHistory;
import com.parkcare.parking.application.entity.Ticket;
import com.parkcare.parking.application.model.*;
import com.parkcare.parking.application.repository.TicketHistoryRepository;
import com.parkcare.parking.application.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    @PostMapping(
            path = "/api/check-in",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ActiveTicketResponse> checkIn(@RequestBody(required = true) InputRequest request) {
        ActiveTicketResponse activeTicketResponse = ticketService.checkIn(request);

        return WebResponse.<ActiveTicketResponse>builder().data(activeTicketResponse).build();
    }

    @PostMapping(
            path = "/api/check-out",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckOutResponse> checkOut(@RequestBody(required = true) InputRequest request) {
        CheckOutResponse checkOutResponse = ticketService.checkOut(request);
        return WebResponse.<CheckOutResponse>builder().data(checkOutResponse).build();
    }

    @GetMapping(
            path = "/api/tickets/{plateNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ActiveTicketResponse> getTicketDetail(@PathVariable(name = "plateNumber") InputRequest request) {
        ActiveTicketResponse ticketDetail = ticketService.getTicketDetail(request);

        return WebResponse.<ActiveTicketResponse>builder().data(ticketDetail).build();
    }

    @GetMapping(
            path = "/api/tickets",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<Ticket>> getActiveTickets() {
        List<Ticket> allTickets = ticketService.getAllActiveTickets();
        return WebResponse.<List<Ticket>>builder().data(allTickets).build();
    }

    @GetMapping(
            path = "/api/tickets-history",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TicketHistory>> getHistoryTickets() {
        List<TicketHistory> allHistoryTickets = ticketService.getAllHistoryTickets();
        return WebResponse.<List<TicketHistory>>builder().data(allHistoryTickets).build();
    }
}
