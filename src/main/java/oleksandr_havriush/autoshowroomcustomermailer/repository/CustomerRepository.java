package oleksandr_havriush.autoshowroomcustomermailer.repository;

import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
