package oleksandr_havriush.autoshowroomcustomermailer.service;

import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CarRepository;
import oleksandr_havriush.autoshowroomcustomermailer.util.XmlToCarListConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.verify;

class CarDbServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private XmlToCarListConverter parser;

    private CarService carService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        carService = new CarService(carRepository, parser);
    }

    @Test
    public void testSaveCarsToDb() throws IOException {
        Car car1 = new Car(null, "Car", "Octavia", "Skoda", 85, 20000.0);
        Car car2 = new Car(null, "Truck", "Actros", "Mercedes-Benz", 250, 50000.0);
        Car car3 = new Car(null, "Motorcycle", "CBR600RR", "Honda", 85, 15000.0);
        Car car4 = new Car(null, "Car", "Model S", "Tesla", 100, 75000.0);
        Car car5 = new Car(null, "Motorcycle", "Ninja ZX-10R", "Kawasaki", 200, 16000.0);

        CarList carList = new CarList(Arrays.asList(car1, car2, car3, car4, car5));

        carService.saveCarsToDb(carList);

        verify(carRepository).saveAll(carList.getCars());
    }
}