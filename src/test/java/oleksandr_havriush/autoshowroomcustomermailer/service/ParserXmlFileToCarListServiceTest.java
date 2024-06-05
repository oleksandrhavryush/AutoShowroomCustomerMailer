package oleksandr_havriush.autoshowroomcustomermailer.service;

import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.util.ParserXmlFileToCarList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserXmlFileToCarListServiceTest {
    @Mock
    private ParserXmlFileToCarList parser;

    private CarParserService carParserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        carParserService = new CarParserService(parser);
    }

    @Test
    public void testParseXmlFileToCar() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String xml = new String(Files.readAllBytes(Paths.get("src/test/resources/cars.xml")));
        when(multipartFile.getBytes()).thenReturn(xml.getBytes());

        Car car1 = new Car(null, "Car", "Octavia", "Skoda", 85, 20000.0);
        Car car2 = new Car(null, "Truck", "Actros", "Mercedes-Benz", 250, 50000.0);
        Car car3 = new Car(null, "Motorcycle", "CBR600RR", "Honda", 85, 15000.0);
        Car car4 = new Car(null, "Car", "Model S", "Tesla", 100, 75000.0);
        Car car5 = new Car(null, "Motorcycle", "Ninja ZX-10R", "Kawasaki", 200, 16000.0);

        CarList expected = new CarList(Arrays.asList(car1, car2, car3, car4, car5));

        when(parser.toObject(xml)).thenReturn(expected);

        CarList actual = carParserService.xmlFileToCar(multipartFile);

        assertEquals(expected, actual);

        System.out.println(actual);
        System.out.println(expected);
    }
}
