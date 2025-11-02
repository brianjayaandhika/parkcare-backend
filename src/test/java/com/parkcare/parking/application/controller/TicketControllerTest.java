package com.parkcare.parking.application.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkcare.parking.application.entity.TicketHistory;
import com.parkcare.parking.application.entity.Ticket;
import com.parkcare.parking.application.model.*;
import com.parkcare.parking.application.repository.TicketHistoryRepository;
import com.parkcare.parking.application.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Clock clock;
    private final ZoneId zone = ZoneId.of("UTC");

    private void createNewTicket(InputRequest request) throws Exception {
        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        );
    }

    @BeforeEach
    void setUp() throws Exception {
        ticketRepository.deleteAll();
        ticketHistoryRepository.deleteAll();

        when(clock.getZone()).thenReturn(zone);

        InputRequest req_1 = new InputRequest();
        req_1.setPlateNumber("B1100DAH");

        Instant t0 = Instant.now();
        when(clock.instant()).thenReturn(t0);

        InputRequest req_2 = new InputRequest();
        req_2.setPlateNumber("B1928CCC");

        createNewTicket(req_1);
        createNewTicket(req_2);
    }

    @Test
    void checkInBadRequestNoPayload() throws Exception {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setPlateNumber("B1234XYZ");

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        );
    }

    @Test
    void checkInBadRequestAlreadyExists() throws Exception {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setPlateNumber("B1100DAH");

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest))
        ).andExpectAll(
                status().isBadRequest()
        );
    }

    @Test
    void checkInBadRequestWrongPattern() throws Exception {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setPlateNumber("B1100DAH");

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest))
        ).andExpectAll(
                status().isBadRequest()
        );
    }

    @Test
    void checkInSuccess() throws Exception {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setPlateNumber("B1234XYZ");

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<Ticket> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertNull(response.getData().getPrice());
                    assertNull(response.getData().getCheckOutAt());

                    Boolean b = ticketRepository.existsByPlateNumber(response.getData().getPlateNumber());
                    assertTrue(b);
                }
        );
    }

    @Test
    void checkOutBadRequestNoPayload() throws Exception {
        InputRequest request = new InputRequest();
        request.setPlateNumber("B1234XYZ");

        mockMvc.perform(
                post("/api/check-out")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        );
    }

    @Test
    void checkOutNotFound() throws Exception {
        InputRequest request = new InputRequest();

        request.setPlateNumber("B8888XXX");
        mockMvc.perform(
                post("/api/check-out")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        );
    }

    @Test
    void checkOutSuccess() throws Exception {
        InputRequest request = new InputRequest();
        request.setPlateNumber("B1100DAH");

        Instant t0 = Instant.now();
        when(clock.instant()).thenReturn(t0.plus(7, ChronoUnit.HOURS));

        mockMvc.perform(
                post("/api/check-out")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<CheckOutResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());

                    Boolean b = ticketHistoryRepository.existsByPlateNumber("B1100DAH");
                    Boolean b1 = ticketRepository.existsByPlateNumber("B1100DAH");
                    assertTrue(b);
                    assertFalse(b1);

                    assertEquals(21000, response.getData().getPrice());
                }
        );
    }

    @Test
    void getTicketDetailNotFound() throws Exception {
        mockMvc.perform(
                get("/api/tickets/" + "B1111XXX")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        );
    }

    @Test
    void getTicketDetailSuccess() throws Exception {
        mockMvc.perform(
                get("/api/tickets/" + "B1100DAH")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ActiveTicketResponse> o = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(o.getErrors());
                }
        );
    }

    @Test
    void getAllActiveTicketsSuccess() throws Exception {
        mockMvc.perform(
                get("/api/tickets")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<Ticket>> o = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(o.getErrors());
                    assertEquals(2, o.getData().size());
                }
        );
    }

    @Test
    void getAllHistoryTicketsSuccess() throws Exception {
        InputRequest request = new InputRequest();
        request.setPlateNumber("B1928CCC");

        mockMvc.perform(
                post("/api/check-out")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        );

        mockMvc.perform(
                get("/api/tickets-history")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<TicketHistory>> o = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(o.getErrors());
                    assertEquals(1, o.getData().size());
                }
        );
    }
}
