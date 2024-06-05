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
public class CarList {

    private List<Car> cars;

    @XmlElement(name = "car")
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        sb.append(String.format("%-20s %-20s %-20s %-10s %-10s", "Type of vehicle", "Name", "Manufacturer", "Power", "Price")).append(lineSeparator);
        sb.append("-----------------------------------------------------------------------------------").append(lineSeparator);

        for (Car car : cars) {
            sb.append(String.format("%-20s %-20s %-20s %-10d %-10.2f",
                    car.getType(),
                    car.getName(),
                    car.getManufacturer(),
                    car.getPower(),
                    car.getPrice())).append(lineSeparator);
        }

        return sb.toString();
    }
}
