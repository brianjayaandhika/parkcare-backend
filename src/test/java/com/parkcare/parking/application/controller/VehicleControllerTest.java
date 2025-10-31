package com.parkcare.parking.application.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkcare.parking.application.entity.Vehicle;
import com.parkcare.parking.application.model.CheckInRequest;
import com.parkcare.parking.application.model.WebResponse;
import com.parkcare.parking.application.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

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
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        vehicleRepository.deleteAll();

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setPlateNumber("B 1100 DAH");

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
}
