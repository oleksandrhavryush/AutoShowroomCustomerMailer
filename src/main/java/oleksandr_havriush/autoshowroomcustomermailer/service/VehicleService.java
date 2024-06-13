package oleksandr_havriush.autoshowroomcustomermailer.service;

import oleksandr_havriush.autoshowroomcustomermailer.model.VehicleList;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing vehicles of type T.
 *
 * @param <T> the type of vehicles managed by this service
 */
public interface VehicleService<T> {
    /**
     * Retrieves all vehicles of type T.
     *
     * @return a list of all vehicles
     */
    List<T> findAll();

    /**
     * Retrieves a vehicle of type T by its ID.
     *
     * @param id the ID of the vehicle to retrieve
     * @return an Optional containing the vehicle, or empty if not found
     */
    Optional<T> findById(Long id);

    /**
     * Processes a multipart file containing vehicle data.
     *
     * @param file the multipart file to process
     * @return a VehicleList containing processed vehicles
     */
    VehicleList<T> processFile(MultipartFile file);

    /**
     * Saves vehicles of type T.
     *
     * @param vehicles the list of vehicles to save
     */
    void saveVehicles(VehicleList<T> vehicles);
}
