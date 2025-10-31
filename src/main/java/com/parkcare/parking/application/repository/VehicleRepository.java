package com.parkcare.parking.application.repository;

import com.parkcare.parking.application.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findFirstByPlateNumber(String plateNumber);

    Boolean existsByPlateNumber(String plateNumber);
}
