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
        MockitoAnnotations.openMocks(this);
        assertNotNull(mockDocument);

        Address address = new Address(1L, "New York Avenue", "125A", "Bonn", "47523", "Germany");
        customer = new Customer(1L, "Mark", "Bensberg", address);
        carList = Arrays.asList(
                new Car(1L, "Car", "Octavia", "Skoda", 85, 20000.0),
                new Car(2L, "Truck", "Actros", "Mercedes-Benz", 250, 50000.0),
                new Car(3L, "Motorcycle", "CBR600RR", "Honda", 85, 15000.0),
                new Car(4L, "Car", "Model S", "Tesla", 100, 75000.0),
                new Car(5L, "Motorcycle", "Ninja ZX-10R", "Kawasaki", 200, 16000.0)
        );
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
