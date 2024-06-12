package oleksandr_havriush.autoshowroomcustomermailer.service;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.FileProcessingException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.NoCarsToSaveException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.XmlParsingException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.repository.CarRepository;
import oleksandr_havriush.autoshowroomcustomermailer.util.XmlToCarListConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private XmlToCarListConverter parser;

    @InjectMocks
    private CarService carService;

    private CarList carList;
    private List<Car> carListAsList;

    @BeforeEach
    public void setUp() {
        Car car1 = new Car(null, "Car", "Octavia", "Skoda", 85, 20000.0);
        Car car2 = new Car(null, "Truck", "Actros", "Mercedes-Benz", 250, 50000.0);
        Car car3 = new Car(null, "Motorcycle", "CBR600RR", "Honda", 85, 15000.0);
        Car car4 = new Car(null, "Car", "Model S", "Tesla", 100, 75000.0);
        Car car5 = new Car(null, "Motorcycle", "Ninja ZX-10R", "Kawasaki", 200, 16000.0);

        carListAsList = Arrays.asList(car1, car2, car3, car4, car5);
        carList = new CarList(carListAsList);
    }

    @Test
    @DisplayName("Test finding all cars")
    public void testFindAll() {
        when(carRepository.findAll()).thenReturn(carListAsList);
        List<Car> result = carService.findAll();
        assertEquals(carListAsList, result);
        verify(carRepository).findAll();
    }

    @Test
    @DisplayName("Test saving a list of cars to the database")
    public void testSaveCarsToDb() {
        carService.saveCars(carList);
        verify(carRepository).saveAll(carList.getCars());
    }

    @Test
    @DisplayName("Test converting an XML file to a list of cars")
    public void testXmlFileToCar() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String xmlContent = "<cars>...</cars>"; // Simplified XML content
        when(multipartFile.getBytes()).thenReturn(xmlContent.getBytes(StandardCharsets.UTF_8));
        when(parser.convert(xmlContent)).thenReturn(carList);

        CarList result = carService.processXmlFile(multipartFile);
        assertEquals(carList, result);
        verify(parser).convert(xmlContent);
    }

    @Test
    @DisplayName("Test handling IOException when reading an XML file")
    public void testXmlFileToCar_IOException() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        try {
            when(multipartFile.getBytes()).thenThrow(new IOException("Test exception"));
            assertThrows(FileProcessingException.class, () -> carService.processXmlFile(multipartFile));
        } catch (IOException e) {
            fail("IOException should not occur in this test");
        }
    }

    @Test
    @DisplayName("Test saving an empty list of cars to the database")
    public void testSaveCarsToDb_EmptyList() {
        CarList emptyCarList = new CarList(Arrays.asList());

        assertThrows(NoCarsToSaveException.class, () -> carService.saveCars(emptyCarList));

        verify(carRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Test parsing of invalid XML file throws XmlParsingException")
    public void testXmlFileToCar_InvalidXml() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String invalidXmlContent = "<cars><car></car>"; // Неповний XML
        try {
            when(multipartFile.getBytes()).thenReturn(invalidXmlContent.getBytes(StandardCharsets.UTF_8));
            when(parser.convert(invalidXmlContent)).thenThrow(new XmlParsingException("Invalid XML"));
            assertThrows(XmlParsingException.class, () -> carService.processXmlFile(multipartFile));
        } catch (IOException e) {
            fail("IOException should not occur in this test");
        }
    }

    @Test
    @DisplayName("Test saveCarsToDb throws exception when saveAll fails")
    public void testSaveCarsToDb_SaveAllThrowsException() {
        doThrow(new DataAccessException("...") {
        }).when(carRepository).saveAll(any());
        assertThrows(DataAccessException.class, () -> carService.saveCars(carList));
    }

    @Test
    @DisplayName("Test findAll returns empty list when no cars are present")
    public void testFindAll_NoCars() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList());
        List<Car> result = carService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test processing null file throws exception")
    public void testProcessXmlFile_NullFile() {
        assertThrows(NullPointerException.class, () -> carService.processXmlFile(null));
    }

    @Test
    @DisplayName("Test saving empty CarList throws exception")
    public void testSaveCars_EmptyCarList() {
        CarList emptyCarList = new CarList(new ArrayList<>());
        assertThrows(NoCarsToSaveException.class, () -> carService.saveCars(emptyCarList));
    }

    @Test
    @DisplayName("Test handling invalid car ID")
    public void testHandleInvalidCarId() {
        Long invalidId = -1L; // Припустимо, що від'ємні ID є невалідними
        assertThrows(IllegalArgumentException.class, () -> carService.findById(invalidId));
    }

    @Test
    @DisplayName("Test saving null CarList throws exception")
    public void testSaveCars_NullCarList() {
        assertThrows(NoCarsToSaveException.class, () -> carService.saveCars(null));
    }
}
