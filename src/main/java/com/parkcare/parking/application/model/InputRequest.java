package com.parkcare.parking.application.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputRequest {

    @NotNull (message = "Plate Number cannot be null")
    @Pattern(regexp = "^[A-Z]{1,2}\\d{1,4}[A-Z]{0,3}$", message = "Incorrect Plate Number Format")
    private String plateNumber;
}