package com.parkcare.parking.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "ticket_histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketHistory {

    @Id
    private String id;

    @Column(name = "plate_number")
    private String plateNumber;

    @Column(name = "check_in_at")
    private Instant checkInAt;

    @Column(name = "check_out_at")
    private Instant checkOutAt;

    private Integer price;

    @Column(name = "rate_per_hour")
    private Integer ratePerHour;

    @Column(name = "id_reference")
    private String idReference;
}
