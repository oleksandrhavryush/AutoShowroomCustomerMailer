package oleksandr_havriush.autoshowroomcustomermailer.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.dto.NewCustomerPayload;
import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.service.CustomerService;
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
        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "customer";
        } else {

            Address address = new Address();
            address.setStreet(payload.street());
            address.setHouseNumber(payload.houseNumber());
            address.setCity(payload.city());
            address.setPostalCode(payload.postalCode());
            address.setCountry(payload.country());

            Customer customer = customerService.create(payload.firstName(), payload.lastName(), address);

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
        model.addAttribute("customer", customer);
        return "editCustomer";
    }

    @PostMapping("/customer/edit/{id}")
    public String updateCustomer(@PathVariable("id") Long id, @ModelAttribute Customer customer) {
        customerService.update(id, customer);
        return "redirect:/customerDetails/" + id;
    }

    @PostMapping("/customer/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteById(id);
        return "redirect:/";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0],
                        exception.getMessage(), locale));
        return "errors/404";
    }
}
