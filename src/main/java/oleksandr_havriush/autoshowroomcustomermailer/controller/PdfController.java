package oleksandr_havriush.autoshowroomcustomermailer.controller;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.service.CustomerService;
import oleksandr_havriush.autoshowroomcustomermailer.service.PdfGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PdfController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfController.class);
    private final PdfGenerationService pdfGenerationService;
    private final CustomerService customerService;

    @GetMapping(value = "/generatePdf")
    public String generatePdfForAllCustomers(RedirectAttributes redirectAttributes) {
        List<Customer> customers = customerService.findAll();
        List<String> errorMessages = new ArrayList<>();

        customers.forEach(customer -> generatePdfAndHandleErrors(customer, errorMessages));

        if (!errorMessages.isEmpty()) {
            String combinedErrorMessage = String.join("\n", errorMessages);
            redirectAttributes.addFlashAttribute("message", combinedErrorMessage);
        } else {
            redirectAttributes.addFlashAttribute("message", "PDF files saved to directory src/main/resources/generated_mails successfully for all customers.");
        }
        return "redirect:/uploadStatus";
    }


    private boolean generatePdfAndHandleErrors(Customer customer, List<String> errorMessages) {
        try {
            pdfGenerationService.createPdfForCustomer(customer.getId());
            return true;
        } catch (Exception e) {
            String errorMessage = String.format("Error generating PDF for customer %s %s: %s",
                    customer.getLastName(), customer.getFirstName(), e.getMessage());
            LOGGER.error(errorMessage, e);
            errorMessages.add(errorMessage);
            return false;
        }
    }

}
