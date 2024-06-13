package oleksandr_havriush.autoshowroomcustomermailer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for updating an existing customer.
 * This record holds customer information with validation constraints to ensure the data integrity.
 */
public record UpdateCustomerPayload(
        @NotNull(message = "{customer.create.errors.firstName_is_null}")
        @Size(min = 1, max = 50, message = "{customer.create.errors.firstName_size_is_invalid}")
        String firstName,

        @NotNull(message = "{customer.create.errors.lastName_is_null}")
        @Size(min = 1, max = 50, message = "{customer.create.errors.lastName_size_is_invalid}")
        String lastName,

        @NotNull(message = "{customer.create.errors.street_is_null}")
        @Size(min = 1, max = 100, message = "{customer.create.errors.street_size_is_invalid}")
        String street,

        @NotNull(message = "{customer.create.errors.houseNumber_is_null}")
        @Pattern(regexp = "^[0-9a-zA-Z-/]*$", message = "{customer.create.errors.houseNumber_is_invalid}")
        String houseNumber,

        @NotNull(message = "{customer.create.errors.city_is_null}")
        @Size(min = 1, max = 50, message = "{customer.create.errors.city_size_is_invalid}")
        String city,

        @NotNull(message = "{customer.create.errors.postalCode_is_null}")
        @Pattern(regexp = "^[0-9]{5}$", message = "{customer.create.errors.postalCode_is_invalid}")
        String postalCode,

        @NotNull(message = "{customer.create.errors.country_is_null}")
        @Size(min = 1, max = 50, message = "{customer.create.errors.country_size_is_invalid}")
        String country) {
}
