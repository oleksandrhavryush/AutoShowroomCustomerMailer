package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.FileProcessingException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CarRepository;
import oleksandr_havriush.autoshowroomcustomermailer.util.XmlToCarListConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);
    private final CarRepository carRepository;
    private final XmlToCarListConverter parser;

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public CarList xmlFileToCar(MultipartFile file) {
        try {
            String xml = new String(file.getBytes(), StandardCharsets.UTF_8);
            return parser.convert(xml);
        } catch (IOException e) {
            LOGGER.error("Error reading XML file", e);
            throw new FileProcessingException("Error reading XML file", e);
        }
    }

    public void saveCarsToDb(CarList carList) {
        carRepository.saveAll(carList.getCars());
        LOGGER.info("Cars saved to database successfully.");
    }
}

