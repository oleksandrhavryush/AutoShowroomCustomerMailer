package oleksandr_havriush.autoshowroomcustomermailer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Represents a list of cars for XML serialization/deserialization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "cars")
public class CarList implements VehicleList<Car> {
    private List<Car> cars;

    /**
     * Retrieves the list of cars.
     *
     * @return the list of cars
     */
    @Override
    public List<Car> getVehicles() {
        return cars;
    }

    /**
     * Sets the list of cars.
     *
     * @param vehicles the list of cars to set
     */
    @Override
    @XmlElement(name = "car")
    public void setVehicles(List<Car> vehicles) {
        this.cars = vehicles;
    }
}
