package oleksandr_havriush.autoshowroomcustomermailer.service;

import oleksandr_havriush.autoshowroomcustomermailer.model.VehicleList;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface VehicleService<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    VehicleList<T> processFile(MultipartFile file);
    void saveVehicles(VehicleList<T> vehicles);
}
