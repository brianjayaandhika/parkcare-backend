package com.parkcare.parking.application.repository;

import com.parkcare.parking.application.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, String>, JpaSpecificationExecutor<TicketHistory> {

    Boolean existsByPlateNumber(String plateNumber);

    List<TicketHistory> findAllByOrderByCheckOutAtDesc();

}
