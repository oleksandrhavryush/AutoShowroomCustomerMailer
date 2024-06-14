package oleksandr_havriush.autoshowroomcustomermailer.controller;

import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private MessageSource messageSource;

    @Test
    @DisplayName("Test show edit customer form")
    void testShowEditCustomerForm() throws Exception {
        Address address = new Address(1L, "Main Street", "123", "Springfield", "12345", "Country");
        Customer customer = new Customer(1L, "John", "Doe", address);
        when(customerService.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customer/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("editCustomer"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("payload"));
    }

    @Test
    @DisplayName("Test update customer with validation errors")
    void testUpdateCustomer_ValidationErrors() throws Exception {
        // Створення тестового клієнта
        Address address = new Address(1L, "Main Street", "123", "Springfield", "12345", "Country");
        Customer customer = new Customer(1L, "John", "Doe", address);

        when(customerService.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/customer/edit/{id}", 1L)
                        .param("firstName", "John")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("editCustomer"))
                .andExpect(model().attributeHasErrors("updateCustomerPayload"));
    }

    @Test
    @DisplayName("Test delete customer with exception")
    void testDeleteCustomer_Exception() throws Exception {
        doThrow(new RuntimeException("Deletion exception")).when(customerService).deleteById(1L);

        mockMvc.perform(post("/customer/delete/{id}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("An unexpected server error occurred")));
    }

    @Test
    @DisplayName("Test handle NoSuchElement exception")
    void testHandleNoSuchElementException() throws Exception {
        when(customerService.findById(anyLong())).thenThrow(new NoSuchElementException("errors.customer.not_found"));

        mockMvc.perform(get("/customerDetails/{customerId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"));
    }

    @Test
    @DisplayName("Test create customer with invalid postal code")
    void testCreateCustomer_InvalidPostalCode() throws Exception {
        mockMvc.perform(post("/customer")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("street", "Main Street")
                        .param("houseNumber", "123")
                        .param("city", "Springfield")
                        .param("postalCode", "ABCDE") // Невалідний поштовий код
                        .param("country", "Country"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer"))
                .andExpect(model().attributeHasFieldErrors("newCustomerPayload", "postalCode"));
    }

    @Test
    @DisplayName("Test create customer successfully")
    void testCreateCustomer_Success() throws Exception {
        mockMvc.perform(post("/customer")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("street", "Main Street")
                        .param("houseNumber", "123")
                        .param("city", "Springfield")
                        .param("postalCode", "12345")
                        .param("country", "Country"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/customerDetails/*"));
    }

    @Test
    @DisplayName("Test create customer with service exception")
    void testCreateCustomer_ServiceException() throws Exception {
        when(customerService.create(any())).thenThrow(new RuntimeException("Service exception"));

        mockMvc.perform(post("/customer")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("street", "Main Street")
                        .param("houseNumber", "123")
                        .param("city", "Springfield")
                        .param("postalCode", "12345")
                        .param("country", "Country"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("An unexpected server error occurred")));
    }

    @Test
    @DisplayName("Test delete customer successfully")
    void testDeleteCustomer_Success() throws Exception {
        doNothing().when(customerService).deleteById(anyLong());

        mockMvc.perform(post("/customer/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"));
    }

    @Test
    @DisplayName("Test show customer form")
    void testShowCustomerForm() throws Exception {
        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer"));
    }

    @Test
    @DisplayName("Test get customer list")
    void testGetCustomerList() throws Exception {
        List<Customer> customerList = new ArrayList<>();
        when(customerService.findAll()).thenReturn(customerList);

        mockMvc.perform(get("/customerList"))
                .andExpect(status().isOk())
                .andExpect(view().name("customerList"))
                .andExpect(model().attribute("customerList", customerList));
    }
}
