package com.parkcare.parking.application.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkcare.parking.application.entity.Vehicle;
import com.parkcare.parking.application.model.CheckInRequest;
import com.parkcare.parking.application.model.CheckOutRequest;
import com.parkcare.parking.application.model.CheckOutResponse;
import com.parkcare.parking.application.model.WebResponse;
import com.parkcare.parking.application.repository.TicketHistoryRepository;
import com.parkcare.parking.application.repository.VehicleRepository;
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

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Clock clock;
    private final ZoneId zone = ZoneId.of("UTC");

    @BeforeEach
    void setUp() throws Exception {
        vehicleRepository.deleteAll();
        ticketHistoryRepository.deleteAll();

        when(clock.getZone()).thenReturn(zone);

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setPlateNumber("B 1100 DAH");

        Instant t0 = Instant.now();
        when(clock.instant()).thenReturn(t0);

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest))
        ).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void checkInBadRequestNoPayload() throws Exception {
        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setPlateNumber("B 1234 XYZ");

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
        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setPlateNumber("B 1100 DAH");

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest))
        ).andExpectAll(
                status().isBadRequest()
        );
    }

    @Test
    void checkInSuccess() throws Exception {
        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setPlateNumber("B 1234 XYZ");

        mockMvc.perform(
                post("/api/check-in")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<Vehicle> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertNull(response.getData().getPrice());
                    assertNull(response.getData().getCheckOutAt());

                    Boolean b = vehicleRepository.existsByPlateNumber(response.getData().getPlateNumber());
                    assertTrue(b);
                }
        );
    }

    @Test
    void checkOutBadRequestNoPayload() throws Exception {
        CheckOutRequest checkOutRequest = new CheckOutRequest();
        checkOutRequest.setPlateNumber("B 1234 XYZ");

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
        CheckOutRequest checkOutRequest = new CheckOutRequest();
        checkOutRequest.setPlateNumber("random-string");

        mockMvc.perform(
                post("/api/check-out")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkOutRequest))
        ).andExpectAll(
                status().isNotFound()
        );
    }

    @Test
    void checkOutSuccess() throws Exception {
        CheckOutRequest checkOutRequest = new CheckOutRequest();
        checkOutRequest.setPlateNumber("B 1100 DAH");

        Instant t0 = Instant.now();
        when(clock.instant()).thenReturn(t0.plus(7, ChronoUnit.HOURS));

        mockMvc.perform(
                post("/api/check-out")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkOutRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->  {
                    WebResponse<CheckOutResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());

                    Boolean b = ticketHistoryRepository.existsByPlateNumber("B 1100 DAH");
                    Boolean b1 = vehicleRepository.existsByPlateNumber("B 1100 DAH");
                    assertTrue(b);
                    assertFalse(b1);

                    assertEquals(21000, response.getData().getPrice());
                }
        );
    }
}
