package oleksandr_havriush.autoshowroomcustomermailer.service.impl;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.FileProcessingException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.NoCarsToSaveException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.VehicleList;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CarRepository;
import oleksandr_havriush.autoshowroomcustomermailer.service.VehicleService;
import oleksandr_havriush.autoshowroomcustomermailer.util.XmlToCarListConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements VehicleService<Car> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final XmlToCarListConverter parser;

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> findById(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID cannot be null or negative");
        }
        return carRepository.findById(id);
    }

    @Override
    public VehicleList<Car> processFile(MultipartFile file) throws FileProcessingException {
        if (file.isEmpty()) {
            throw new FileProcessingException("File is empty");
        }

        try {
            String xml = new String(file.getBytes(), StandardCharsets.UTF_8);
            return parser.convert(xml);
        } catch (IOException e) {
            LOGGER.error("Error reading XML file", e);
            throw new FileProcessingException("Error reading XML file", e);
        }
    }

    @Override
    public void saveVehicles(VehicleList<Car> carList) {
        if (carList == null || carList.getVehicles() == null || carList.getVehicles().isEmpty()) {
            LOGGER.info("No cars to save to database.");
            throw new NoCarsToSaveException("No cars to save");
        }

        carRepository.saveAll(carList.getVehicles());
        LOGGER.info("Cars saved to database successfully.");
    }
}
