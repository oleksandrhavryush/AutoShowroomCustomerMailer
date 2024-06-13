package oleksandr_havriush.autoshowroomcustomermailer.util;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;


import oleksandr_havriush.autoshowroomcustomermailer.exeptions.PdfGenerationException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Address;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PdfReportGeneratorTest {

    @InjectMocks
    private PdfReportGenerator pdfReportGenerator;
    private MockedStatic<PdfWriter> mockedStatic;

    @Mock
    private Document mockDocument;

    @Mock
    private PdfWriter mockPdfWriter;

    private Customer customer;
    private List<Car> carList;

    @BeforeEach
    public void setUp() {
        Address address = new Address(1L, "New York Avenue", "125A", "Bonn", "47523", "Germany");
        customer = new Customer(1L, "Mark", "Bensberg", address);

        Car car1 = Car.builder()
                .id(1L) // Додано ID
                .name("Octavia")
                .manufacturer("Skoda")
                .price(20000.0)
                .type("Sedan")
                .power(85)
                .build();
        Car car2 = Car.builder()
                .id(2L) // Додано ID
                .name("Actros")
                .manufacturer("Mercedes-Benz")
                .price(50000.0)
                .type("Truck")
                .power(250)
                .build();
        Car car3 = Car.builder()
                .id(3L) // Додано ID
                .name("CBR600RR")
                .manufacturer("Honda")
                .price(15000.0)
                .type("Sport")
                .power(85)
                .build();
        Car car4 = Car.builder()
                .id(4L) // Додано ID
                .name("Model S")
                .manufacturer("Tesla")
                .price(75000.0)
                .type("Electric")
                .power(100)
                .build();
        Car car5 = Car.builder()
                .id(5L) // Додано ID
                .name("Ninja ZX-10R")
                .manufacturer("Kawasaki")
                .price(16000.0)
                .type("Sport")
                .power(200)
                .build();

        carList = Arrays.asList(car1, car2, car3, car4, car5);

        mockedStatic = Mockito.mockStatic(PdfWriter.class);
        mockedStatic.when(() -> PdfWriter.getInstance(any(Document.class), any(ByteArrayOutputStream.class)))
                .thenReturn(mockPdfWriter);
    }


    @AfterEach
    public void tearDown() {
        mockedStatic.close();
    }

    private ByteArrayInputStream generatePdf(Optional<Customer> customerOpt, List<Car> carList) {
        return pdfReportGenerator.createCustomerPdfReport(customerOpt, carList);
    }

    @Test
    @DisplayName("Verify PDF report creation with customer data")
    public void testCreateCustomerPdfReport_WithPresentCustomer() {
        ByteArrayInputStream reportStream = generatePdf(Optional.of(customer), carList);
        assertNotNull(reportStream);
    }

    @Test
    @DisplayName("Verify exception handling during PDF report generation")
    public void testCreateCustomerPdfReport_ThrowsException() {
        mockedStatic.when(() -> PdfWriter.getInstance(any(Document.class), any(ByteArrayOutputStream.class)))
                .thenThrow(new DocumentException());

        assertThrows(PdfGenerationException.class, () -> generatePdf(Optional.of(customer), carList));
    }

    @Test
    @DisplayName("Verify PDF creation with valid customer and car list")
    void testCreateCustomerPdfReportWithValidCustomerAndCarList() {
        ByteArrayInputStream pdfStream = generatePdf(Optional.of(customer), carList);
        assertNotNull(pdfStream);
    }

    @Test
    @DisplayName("Verify PDF creation with empty car list")
    void testCreateCustomerPdfReportWithEmptyCarList() {
        ByteArrayInputStream pdfStream = generatePdf(Optional.of(customer), Collections.emptyList());
        assertNotNull(pdfStream);
    }

    @Test
    @DisplayName("Verify PDF creation with missing customer")
    void testCreateCustomerPdfReportWithMissingCustomer() {
        ByteArrayInputStream pdfStream = generatePdf(Optional.empty(), carList);
        assertNotNull(pdfStream);
    }

    @Test
    @DisplayName("Verify PDF creation with null customer and empty car list")
    void testCreateCustomerPdfReportWithNullCustomerAndEmptyCarList() {
        ByteArrayInputStream pdfStream = generatePdf(Optional.empty(), Collections.emptyList());
        assertNotNull(pdfStream);
    }
}
