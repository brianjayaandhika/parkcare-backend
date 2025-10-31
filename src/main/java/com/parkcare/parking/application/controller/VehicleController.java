package com.parkcare.parking.application.controller;

import com.parkcare.parking.application.model.*;
import com.parkcare.parking.application.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping(
            path = "/api/check-in",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckInResponse> checkIn(@RequestBody(required = true) CheckInRequest request) {
        CheckInResponse checkInResponse = vehicleService.checkIn(request);

        return WebResponse.<CheckInResponse>builder().data(checkInResponse).build();
    }

    @PostMapping(
            path = "/api/check-out",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckOutResponse> checkOut(@RequestBody(required = true) CheckOutRequest request) {
        CheckOutResponse checkOutResponse = vehicleService.checkOut(request);
        return WebResponse.<CheckOutResponse>builder().data(checkOutResponse).build();
    }
}
