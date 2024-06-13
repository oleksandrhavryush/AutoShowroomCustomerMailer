package oleksandr_havriush.autoshowroomcustomermailer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.DirectoryCreationException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.PdfGenerationException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.util.PdfReportGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PdfGenerationServiceTest {

    private PdfGenerationService pdfGenerationService;

    @Mock
    private CustomerService customerService;
    @Mock
    private VehicleService<Car> carService;

    private Path baseDirectory;
    private Customer customer;
    private Address address;
    private List<Car> cars;
    private ByteArrayInputStream pdfContentStream;
    private MockedStatic<PdfReportGenerator> mockedPdfReportGenerator;
    private MockedStatic<Files> mockedFiles;

    @BeforeEach
    public void setUp() throws IOException {
        String basePath = "target/test-classes/pdf";
        baseDirectory = Paths.get(basePath).toAbsolutePath();
        if (Files.notExists(baseDirectory)) {
            Files.createDirectories(baseDirectory);
        }

        address = new Address(1L, "New York Avenue", "125A", "Bonn", "47523", "Germany");
        customer = new Customer(1L, "John", "Doe", address);
        Car car1 = Car.builder()
                .name("Octavia")
                .manufacturer("Skoda")
                .price(20000.0)
                .type("Sedan")
                .power(85)
                .build();
        Car car2 = Car.builder()
                .name("Actros")
                .manufacturer("Mercedes-Benz")
                .price(50000.0)
                .type("Truck")
                .power(250)
                .build();

        Car car3 = Car.builder()
                .name("CBR600RR")
                .manufacturer("Honda")
                .price(15000.0)
                .type("Sport")
                .power(85)
                .build();

        Car car4 = Car.builder()
                .name("Model S")
                .manufacturer("Tesla")
                .price(75000.0)
                .type("Electric")
                .power(100)
                .build();

        Car car5 = Car.builder()
                .name("Ninja ZX-10R")
                .manufacturer("Kawasaki")
                .price(16000.0)
                .type("Sport")
                .power(200)
                .build();
        cars = Arrays.asList(car1, car2, car3, car4, car5);
        pdfContentStream = new ByteArrayInputStream(new byte[0]);

        pdfGenerationService = new PdfGenerationService(basePath, customerService, carService);

        lenient().when(carService.findAll()).thenReturn(cars);
        lenient().when(customerService.findById(anyLong())).thenReturn(Optional.of(customer));

        mockedPdfReportGenerator = Mockito.mockStatic(PdfReportGenerator.class);
        mockedPdfReportGenerator.when(() -> PdfReportGenerator.createCustomerPdfReport(any(), anyList())).thenReturn(pdfContentStream);

        mockedFiles = Mockito.mockStatic(Files.class);
        mockedFiles.when(() -> Files.newOutputStream(any(Path.class))).thenAnswer(invocation -> new ByteArrayOutputStream());
    }

    @AfterEach
    public void tearDown() {
        mockedPdfReportGenerator.close();
        mockedFiles.close();
    }

    @Test
    @DisplayName("Test successful PDF generation for existing customer")
    public void testCreatePdfForCustomer_Success() {
        pdfGenerationService.createPdfForCustomer(1L);
        verify(customerService).findById(1L);
        verify(carService).findAll();
    }

    @Test
    @DisplayName("Test PDF generation throws exception for non-existing customer")
    public void testCreatePdfForCustomer_CustomerNotFound() {
        when(customerService.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(PdfGenerationException.class, () -> pdfGenerationService.createPdfForCustomer(1L));
        verify(customerService).findById(1L);
        verify(carService, never()).findAll();
    }

    @Test
    @DisplayName("Test PDF file path is correct")
    public void testPdfFilePathIsCorrect() throws IOException {
        Path expectedPath = baseDirectory.resolve(customer.getLastName() + "_" + customer.getFirstName() + "_email.pdf");
        Files.deleteIfExists(expectedPath);

        pdfGenerationService.createPdfForCustomer(1L);

        mockedFiles.when(() -> Files.exists(expectedPath)).thenCallRealMethod();

        assertTrue(Files.exists(expectedPath));
    }

    @Test
    @DisplayName("Test exception when PDF file cannot be saved")
    public void testExceptionWhenPdfCannotBeSaved() {
        mockedFiles.when(() -> Files.newOutputStream(any(Path.class))).thenThrow(new IOException("Test exception"));
        assertThrows(PdfGenerationException.class, () -> pdfGenerationService.createPdfForCustomer(1L));
    }

    @Test
    @DisplayName("Test exception when directory cannot be created")
    public void testExceptionWhenDirectoryCannotBeCreated() {
        mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenThrow(new IOException("Test exception"));
        assertThrows(DirectoryCreationException.class, () -> new PdfGenerationService("invalid/path", customerService, carService));
    }

    @Test
    @DisplayName("Test exception when PDF generation fails")
    public void testExceptionWhenPdfGenerationFails() {
        mockedPdfReportGenerator.when(() -> PdfReportGenerator.createCustomerPdfReport(any(), anyList())).thenThrow(new RuntimeException("Test exception"));
        assertThrows(PdfGenerationException.class, () -> pdfGenerationService.createPdfForCustomer(1L));
    }

    @Test
    @DisplayName("Test PDF generation with empty car list")
    public void testCreatePdfForCustomer_EmptyCarList() {
        when(carService.findAll()).thenReturn(Collections.emptyList());
        pdfGenerationService.createPdfForCustomer(1L);
        verify(carService).findAll();
        mockedPdfReportGenerator.verify(() -> PdfReportGenerator.createCustomerPdfReport(any(), eq(Collections.emptyList())));
    }
}
