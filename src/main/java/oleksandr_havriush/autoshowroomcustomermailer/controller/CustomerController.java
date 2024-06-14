package oleksandr_havriush.autoshowroomcustomermailer.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.dto.NewCustomerPayload;
import oleksandr_havriush.autoshowroomcustomermailer.dto.UpdateCustomerPayload;
import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Controller class for handling customer-related operations.
 */
@Controller
@RequiredArgsConstructor
public class CustomerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final MessageSource messageSource;

    /**
     * Displays the customer form page.
     *
     * @return the name of the customer form view
     */
    @GetMapping("/customer")
    public String showCustomerForm() {
        return "customer";
    }

    /**
     * Displays the list of customers.
     *
     * @param model the model object
     * @return the name of the customer list view
     */
    @GetMapping("/customerList")
    public String getCustomerList(Model model) {
        List<Customer> customerList = this.customerService.findAll();
        model.addAttribute("customerList", customerList);
        return "customerList";
    }

    /**
     * Handles the creation of a new customer.
     *
     * @param payload the new customer payload
     * @param bindingResult the result of binding the request parameters to the payload
     * @param model the model object
     * @return the redirect view name or the customer form view in case of errors
     */
    @PostMapping("/customer")
    public String createCustomer(@Valid NewCustomerPayload payload,
                                 BindingResult bindingResult,
                                 Model model) {
        LOGGER.info("Received request to create a new customer.");
        if (bindingResult.hasErrors()) {
            LOGGER.error("Validation errors occurred while creating a new customer: {}", bindingResult.getAllErrors());
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "customer";
        } else {
            LOGGER.debug("Creating customer with details: {}", payload);

            Address address = new Address();
            address.setStreet(payload.street());
            address.setHouseNumber(payload.houseNumber());
            address.setCity(payload.city());
            address.setPostalCode(payload.postalCode());
            address.setCountry(payload.country());

            Customer customer = new Customer(null, payload.firstName(), payload.lastName(), address);
            customerService.create(customer);
            LOGGER.info("Customer created successfully with ID: {}", customer.getId());
            return "redirect:/customerDetails/" + customer.getId();
        }
    }

    /**
     * Displays the details of a customer.
     *
     * @param customerId the ID of the customer
     * @param model the model object
     * @return the name of the customer details view
     */
    @GetMapping("/customerDetails/{customerId}")
    public String showCustomerDetails(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("errors.customer.not_found"));
        model.addAttribute("customer", customer);
        return "customerDetails";
    }

    /**
     * Displays the edit form for a customer.
     *
     * @param id the ID of the customer to edit
     * @param model the model object
     * @return the name of the edit customer view
     */
    @GetMapping("/customer/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("errors.customer.not_found"));
        UpdateCustomerPayload payload = new UpdateCustomerPayload(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress().getStreet(),
                customer.getAddress().getHouseNumber(),
                customer.getAddress().getCity(),
                customer.getAddress().getPostalCode(),
                customer.getAddress().getCountry()
        );
        model.addAttribute("customer", customer);
        model.addAttribute("payload", payload);
        return "editCustomer";
    }

    /**
     * Handles updating of a customer's details.
     *
     * @param id the ID of the customer to update
     * @param payload the updated customer payload
     * @param bindingResult the result of binding the request parameters to the payload
     * @param model the model object
     * @return the redirect view name or the edit customer view in case of errors
     */
    @PostMapping("/customer/edit/{id}")
    public String updateCustomer(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute UpdateCustomerPayload payload,
                                 BindingResult bindingResult,
                                 Model model) {
        LOGGER.info("Received request to update customer with ID: {}", id);
        if (bindingResult.hasErrors()) {
            LOGGER.error("Validation errors occurred while updating customer ID {}: {}", id, bindingResult.getAllErrors());
            Customer customer = customerService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("errors.customer.not_found"));
            model.addAttribute("customer", customer);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            LOGGER.debug("Customer found for update: {}", customer);
            return "editCustomer";
        } else {
            LOGGER.debug("Updating customer details for ID {}: {}", id, payload);
            Customer customer = customerService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("errors.customer.not_found"));
            Address address = customer.getAddress();
            address.setStreet(payload.street());
            address.setHouseNumber(payload.houseNumber());
            address.setCity(payload.city());
            address.setPostalCode(payload.postalCode());
            address.setCountry(payload.country());

            customer.setFirstName(payload.firstName());
            customer.setLastName(payload.lastName());
            customer.setAddress(address);
            customerService.update(customer);
            LOGGER.info("Customer with ID {} updated successfully.", id);
            return "redirect:/customerDetails/" + id;
        }
    }

    /**
     * Handles deleting a customer.
     *
     * @param id the ID of the customer to delete
     * @return the redirect view name
     */
    @PostMapping("/customer/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        customerService.deleteById(id);
        LOGGER.info("Customer deleted from database successfully.");
        redirectAttributes.addFlashAttribute("message", "Customer deleted from database successfully.");
        return "redirect:/uploadStatus";
    }

    /**
     * Exception handler for NoSuchElementException.
     *
     * @param exception the thrown NoSuchElementException
     * @param model the model object
     * @param response the HTTP servlet response
     * @param locale the current locale
     * @return the name of the error view
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        LOGGER.error("An error occurred while processing customer: ", exception);
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                messageSource.getMessage(exception.getMessage(), null, exception.getMessage(), locale));
        return "errors/404";
    }

    /**
     * Exception handler for RuntimeException.
     *
     * @param ex the thrown RuntimeException
     * @return a ResponseEntity with an appropriate error message and HTTP status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        LOGGER.error("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected server error occurred");
    }
}
