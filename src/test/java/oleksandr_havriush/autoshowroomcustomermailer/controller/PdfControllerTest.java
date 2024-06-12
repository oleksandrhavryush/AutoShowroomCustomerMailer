package oleksandr_havriush.autoshowroomcustomermailer.controller;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.DirectoryCreationException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.PdfGenerationException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.service.CustomerService;
import oleksandr_havriush.autoshowroomcustomermailer.service.PdfGenerationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PdfController.class)
class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfGenerationService pdfGenerationService;

    @MockBean
    private CustomerService customerService;

    private List<Customer> customers;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "John", "Doe", null);
        customers = List.of(customer);
    }

    @Test
    @DisplayName("Test generatePdfForAllCustomers with successful PDF generation")
    void testGeneratePdfForAllCustomers_Success() throws Exception {
        when(customerService.findAll()).thenReturn(customers);
        doNothing().when(pdfGenerationService).createPdfForCustomer(anyLong());

        mockMvc.perform(get("/generatePdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "PDF files saved to directory src/main/resources/generated_mails successfully for all customers."));
    }

    @Test
    @DisplayName("Test generatePdfForAllCustomers with PDF generation failure")
    void testGeneratePdfForAllCustomers_Failure() throws Exception {
        when(customerService.findAll()).thenReturn(customers);
        doThrow(RuntimeException.class).when(pdfGenerationService).createPdfForCustomer(anyLong());

        mockMvc.perform(get("/generatePdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", Matchers.stringContainsInOrder(Arrays.asList("Error generating PDF for customer"))));
    }

    @Test
    @DisplayName("Test generatePdfForAllCustomers with no customers")
    void testGeneratePdfForAllCustomers_NoCustomers() throws Exception {
        when(customerService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/generatePdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "No customers available for PDF generation."));
    }

    @Test
    @DisplayName("Test generatePdfForAllCustomers with save PDF error")
    void testGeneratePdfForAllCustomers_SavePdfError() throws Exception {
        when(customerService.findAll()).thenReturn(customers);
        doThrow(new PdfGenerationException("Error saving PDF")).when(pdfGenerationService).createPdfForCustomer(anyLong());

        mockMvc.perform(get("/generatePdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Error generating PDF for customer Doe John: Error saving PDF"));
    }

    @Test
    @DisplayName("Test generatePdfForAllCustomers with directory creation error")
    void testGeneratePdfForAllCustomers_DirectoryCreationError() throws Exception {
        when(customerService.findAll()).thenReturn(customers);
        doThrow(new DirectoryCreationException("Failed to create directory")).when(pdfGenerationService).createPdfForCustomer(anyLong());

        mockMvc.perform(get("/generatePdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Error generating PDF for customer Doe John: Failed to create directory"));
    }

    @Test
    @DisplayName("Test generatePdfForAllCustomers with partial success")
    void testGeneratePdfForAllCustomers_PartialSuccess() throws Exception {
        when(customerService.findAll()).thenReturn(customers);
        doAnswer(invocation -> {
            Long customerId = invocation.getArgument(0);
            if (customerId.equals(1L)) {
                throw new RuntimeException("Error generating PDF");
            }
            return null;
        }).when(pdfGenerationService).createPdfForCustomer(anyLong());

        mockMvc.perform(get("/generatePdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Error generating PDF for customer Doe John: Error generating PDF"));
    }
}
