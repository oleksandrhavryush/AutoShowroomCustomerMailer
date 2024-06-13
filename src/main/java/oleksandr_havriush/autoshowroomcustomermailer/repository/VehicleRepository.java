package oleksandr_havriush.autoshowroomcustomermailer.repository;

import oleksandr_havriush.autoshowroomcustomermailer.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository<T extends Vehicle> extends JpaRepository<T, Long> {
}
