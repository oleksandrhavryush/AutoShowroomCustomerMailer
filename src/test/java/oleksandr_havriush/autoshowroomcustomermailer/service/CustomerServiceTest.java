package oleksandr_havriush.autoshowroomcustomermailer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.CustomerNotFoundException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.ValidationException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private Address address;

    @BeforeEach
    public void setUp() {
        address = new Address(1L, "New York Avenue", "125A", "Bonn", "47523", "Germany");
        customer = new Customer(1L, "Mark", "Bensberg", address);
    }

    @Test
    @DisplayName("Test finding all customers")
    public void testFindAll() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));
        List<Customer> result = customerService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(customer, result.get(0));
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Test finding customer by ID")
    public void testFindById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Test creating a new customer")
    public void testCreate() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer result = customerService.create(new Customer(null, "Mark", "Bensberg", address));
        assertNotNull(result);
        assertEquals("Mark", result.getFirstName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Test updating a customer")
    public void testUpdate() {
        when(customerRepository.existsById(anyLong())).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        customer.setFirstName("Johnathan");
        Customer result = customerService.update(customer);
        assertNotNull(result);
        assertEquals("Johnathan", result.getFirstName());
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Test deleting customer by ID")
    public void testDeleteById() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);
        customerService.deleteById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Test finding customer by ID returns empty")
    public void testFindById_ReturnsEmpty() {
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.findById(2L);
        assertFalse(result.isPresent());
        verify(customerRepository).findById(2L);
    }

    @Test
    @DisplayName("Test deleting non-existing customer by ID")
    public void testDeleteById_NonExisting() {
        Long nonExistentId = 2L;
        when(customerRepository.existsById(nonExistentId)).thenReturn(false);

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> customerService.deleteById(nonExistentId));
        assertTrue(exception.getMessage().contains("Customer with ID: " + nonExistentId + " does not exist"));

        verify(customerRepository, never()).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("Test findById with null ID")
    public void testFindById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> customerService.findById(null));
    }

    @Test
    @DisplayName("Test create with null customer")
    public void testCreate_NullCustomer() {
        assertThrows(IllegalArgumentException.class, () -> customerService.create(null));
    }

    @Test
    @DisplayName("Test update with non-existing customer")
    public void testUpdate_NonExistingCustomer() {
        Customer nonExistingCustomer = new Customer(99L, "NonExisting", "Customer", address);
        when(customerRepository.existsById(99L)).thenReturn(false);
        assertThrows(CustomerNotFoundException.class, () -> customerService.update(nonExistingCustomer));
    }

    @Test
    @DisplayName("Test deleteById verifies deletion")
    public void testDeleteById_VerifiesDeletion() {
        Long customerId = 1L;
        when(customerRepository.existsById(1L)).thenReturn(true);
        customerService.deleteById(customerId);
        verify(customerRepository).deleteById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    @DisplayName("Test update customer with null fields")
    public void testUpdateCustomer_NullFields() {
        Customer customerWithNullFields = new Customer(1L, null, null, null);
        when(customerRepository.existsById(1L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> customerService.update(customerWithNullFields));
    }

    @Test
    @DisplayName("Test create customer with existing ID")
    public void testCreateCustomer_ExistingId() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        Customer customerWithExistingId = new Customer(1L, "Mark", "Bensberg", address);
        assertThrows(IllegalArgumentException.class, () -> customerService.create(customerWithExistingId));
    }

    @Test
    @DisplayName("Test delete customer with null ID")
    public void testDeleteCustomer_NullId() {
        assertThrows(IllegalArgumentException.class, () -> customerService.deleteById(null));
    }

    @Test
    @DisplayName("Test findAll returns empty list when no customers are present")
    public void testFindAll_NoCustomers() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        List<Customer> result = customerService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test update customer with invalid data")
    public void testUpdateCustomer_InvalidData() {
        Customer invalidCustomer = new Customer(1L, "", "", address);
        when(customerRepository.existsById(1L)).thenReturn(true);
        assertThrows(ValidationException.class, () -> customerService.update(invalidCustomer));
    }
}
