package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer create(Customer customer) {
        LOGGER.info("Creating new customer: {}", customer);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer update(Customer customer) {
        LOGGER.info("Updating customer: {}", customer);
        return customerRepository.save(customer);
    }

    public void deleteById(Long id) {
        LOGGER.info("Deleting customer with ID: {}", id);
        customerRepository.deleteById(id);
    }
}

