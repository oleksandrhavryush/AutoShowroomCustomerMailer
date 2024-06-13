package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.CustomerNotFoundException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.ValidationException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing customer information.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    /**
     * Retrieves all customers from the database.
     *
     * @return a list of customers
     */
    public List<Customer> findAll() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            LOGGER.info("No customers found in the database.");
        }
        return customers;
    }

    /**
     * Finds a customer by their ID.
     *
     * @param id the unique identifier of the customer
     * @return an Optional containing the customer if found
     */
    public Optional<Customer> findById(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID cannot be null or negative");
        }
        Optional<Customer> customer = customerRepository.findById(id);
        if (!customer.isPresent()) {
            LOGGER.warn("Customer with ID: {} not found", id);
        }
        return customer;
    }

    /**
     * Creates a new customer.
     *
     * @param customer the customer object to be created
     * @return the newly created customer
     */
    @Transactional
    public Customer create(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getId() != null && customerRepository.existsById(customer.getId())) {
            throw new IllegalArgumentException("Customer with ID: " + customer.getId() + " already exists");
        }
        LOGGER.info("Creating new customer: {}", customer);
        return customerRepository.save(customer);
    }

    /**
     * Updates an existing customer's details.
     *
     * @param customer the customer object to update
     * @return the updated customer
     */
    @Transactional
    public Customer update(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getId() == null || !customerRepository.existsById(customer.getId())) {
            throw new CustomerNotFoundException("Customer with ID: " + customer.getId() + " does not exist");
        }
        if (customer.getFirstName() == null || customer.getLastName() == null || customer.getAddress() == null) {
            throw new IllegalArgumentException("Customer fields cannot be null");
        }

        LOGGER.info("Updating customer: {}", customer);
        return customerRepository.save(customer);
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param id the unique identifier of the customer to delete
     */
    public void deleteById(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID cannot be null or negative");
        }
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer with ID: " + id + " does not exist");
        }
        LOGGER.info("Deleting customer with ID: {}", id);
        customerRepository.deleteById(id);
    }
}
