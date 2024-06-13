package oleksandr_havriush.autoshowroomcustomermailer.model;

import java.util.List;

public interface VehicleList<T> {
    List<T> getVehicles();
    void setVehicles(List<T> vehicles);
}
