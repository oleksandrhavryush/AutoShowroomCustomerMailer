package oleksandr_havriush.autoshowroomcustomermailer.repository;

import oleksandr_havriush.autoshowroomcustomermailer.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing vehicles of type T.
 *
 * @param <T> the type of vehicles managed by this repository, must extend Vehicle
 */
@Repository
public interface VehicleRepository<T extends Vehicle> extends JpaRepository<T, Long> {
}
