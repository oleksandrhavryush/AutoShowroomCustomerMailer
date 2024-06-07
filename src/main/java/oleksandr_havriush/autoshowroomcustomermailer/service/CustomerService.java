package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return this.customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return this.customerRepository.findById(id);
    }

    @Transactional
    public Customer create(String firstName, String lastName, Address address) {
        return this.customerRepository.save(new Customer(null, firstName, lastName, address));
    }

    @Transactional
    public Customer update(Long id, Customer customer) {
        return this.customerRepository.save(customer);
    }

    public void deleteById(Long id) {
        this.customerRepository.deleteById(id);
    }
}
