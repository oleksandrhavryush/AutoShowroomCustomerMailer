package oleksandr_havriush.autoshowroomcustomermailer.model;

import java.util.List;

/**
 * Interface for a list of vehicles.
 *
 * @param <T> the type of vehicles in the list
 */
public interface VehicleList<T> {
    /**
     * Retrieves the list of vehicles.
     *
     * @return the list of vehicles
     */
    List<T> getVehicles();

    /**
     * Sets the list of vehicles.
     *
     * @param vehicles the list of vehicles to set
     */
    void setVehicles(List<T> vehicles);
}
