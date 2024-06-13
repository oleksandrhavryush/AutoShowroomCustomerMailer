package oleksandr_havriush.autoshowroomcustomermailer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an address entity with details such as street, house number, city, postal code, and country.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    @Pattern(regexp = "^[0-9a-zA-Z-/]*$", message = "House number is invalid")
    private String houseNumber;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    @NotNull(message = "{customer.create.errors.postalCode_is_null}")
    @Pattern(regexp = "^\\d{5}$", message = "{customer.create.errors.postalCode_is_invalid}")
    private String postalCode;


    @Column(name = "country")
    private String country;
}
