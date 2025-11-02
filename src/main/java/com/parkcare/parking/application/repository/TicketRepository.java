package com.parkcare.parking.application.repository;

import com.parkcare.parking.application.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    Optional<Ticket> findFirstByPlateNumber(String plateNumber);

    List<Ticket> findAllByOrderByCheckInAtDesc();

    Boolean existsByPlateNumber(String plateNumber);
}
