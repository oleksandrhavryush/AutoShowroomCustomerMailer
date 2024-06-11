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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final MessageSource messageSource;

    @GetMapping("/customer")
    public String showCustomerForm() {
        return "customer";
    }

    @GetMapping("/customerList")
    public String getCustomerList(Model model) {
        List<Customer> customerList = this.customerService.findAll();
        model.addAttribute("customerList", customerList);
        return "customerList";
    }

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

    @GetMapping("/customerDetails/{customerId}")
    public String showCustomerDetails(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("errors.customer.not_found"));
        model.addAttribute("customer", customer);
        return "customerDetails";
    }

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
            LOGGER.info("Customer with ID {} updated successfully.", id);
            return "redirect:/customerDetails/" + id;
        }
    }


    @PostMapping("/customer/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteById(id);
        return "redirect:/";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        LOGGER.error("An error occurred while processing customer: ", exception);
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                messageSource.getMessage(exception.getMessage(), null, exception.getMessage(), locale));
        return "errors/404";
    }
}
