package oleksandr_havriush.autoshowroomcustomermailer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "cars")
public class CarList implements VehicleList<Car> {
    private List<Car> cars;

    @Override
    public List<Car> getVehicles() {
        return cars;
    }

    @Override
    @XmlElement(name = "car")
    public void setVehicles(List<Car> vehicles) {
        this.cars = vehicles;
    }
}
