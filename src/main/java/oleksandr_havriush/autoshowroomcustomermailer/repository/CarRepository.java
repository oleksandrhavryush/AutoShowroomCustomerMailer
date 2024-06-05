package oleksandr_havriush.autoshowroomcustomermailer.repository;

import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

}
