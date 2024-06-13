package oleksandr_havriush.autoshowroomcustomermailer.repository;

import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing cars, extending VehicleRepository for Car entities.
 */
@Repository
public interface CarRepository extends VehicleRepository<Car> {
}
