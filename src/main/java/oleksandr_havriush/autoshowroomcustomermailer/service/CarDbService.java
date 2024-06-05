package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CarRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarDbService {
    private final CarRepository carRepository;

    public void saveCarsToDb(CarList carList) {
        carRepository.saveAll(carList.getCars());
    }
}

