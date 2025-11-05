🅿️ **ParkCare Backend**

This backend is part of the ParkCare parking management system.

🧩 **Tech Stack**

Java: 21
Framework: Spring Boot (with Spring Data JPA)
Build Tool: Maven
Database: MySQL

⚙️ **Configuration**

All configurations are defined in src/main/resources/application.properties.

Since spring.jpa.hibernate.ddl-auto is set to update, you only need to create a new database named:

    parkcare_parking_application

If you want to use a different name, update the following line accordingly:

spring.datasource.url=jdbc:mysql://localhost:3306/<your_database_name>

Also update your MySQL credentials:

    spring.datasource.username=<your_username>
    spring.datasource.password=<your_password>

The parking rate per hour is configurable in:

    parking.rate.per.hour=3000

🧱 Database Schema (DDL)

If database tables is not generated correctly, this is the DDL structur that are used in this project

    CREATE TABLE tickets (
        id VARCHAR(36) PRIMARY KEY,
        plate_number VARCHAR(25) NOT NULL,
        check_in_at TIMESTAMP NOT NULL,
        check_out_at TIMESTAMP,
        price INT
    ) ENGINE = InnoDB;

    CREATE TABLE ticket_histories (
        id VARCHAR(36) PRIMARY KEY,
        plate_number VARCHAR(25) NOT NULL,
        check_in_at TIMESTAMP NOT NULL,
        check_out_at TIMESTAMP,
        price INT,
        rate_per_hour INT,
        id_reference VARCHAR(36)
    ) ENGINE = InnoDB;

📄 **Endpoints**

Detailed API specifications are available at
👉 /docs/ticket.md