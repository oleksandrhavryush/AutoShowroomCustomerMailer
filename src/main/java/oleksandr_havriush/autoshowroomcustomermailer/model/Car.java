package oleksandr_havriush.autoshowroomcustomermailer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents a car entity extending Vehicle, with additional attributes such as type and power.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "car")
public class Car extends Vehicle {

    @Column(name = "type")
    private String type;

    @Column(name = "power")
    private int power;
}
