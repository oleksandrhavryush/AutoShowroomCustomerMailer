package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.DirectoryCreationException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.PdfGenerationException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import oleksandr_havriush.autoshowroomcustomermailer.util.PdfReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class PdfGenerationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfGenerationService.class);
    private final CustomerService customerService;
    private final CarService carService;
    private final Path baseDirectory;

    public PdfGenerationService(@Value("${pdf.generated-mails-path}") String basePath,
                                CustomerService customerService, CarService carService) {
        this.baseDirectory = Paths.get(basePath);
        this.customerService = customerService;
        this.carService = carService;
        ensureDirectoryExists(this.baseDirectory);
    }

    public void createPdfForCustomer(Long customerId) {
        LOGGER.info("Starting PDF generation for customer ID: {}", customerId);
        Optional<Customer> customerOpt = customerService.findById(customerId);
        if (!customerOpt.isPresent()) {
            LOGGER.error("Customer with ID {} not found", customerId);
            throw new PdfGenerationException("Customer not found for ID: " + customerId);
        }

        Customer customer = customerOpt.get();
        List<Car> cars = carService.findAll();
        ByteArrayInputStream pdfContentStream = PdfReportGenerator.createCustomerPdfReport(Optional.of(customer), cars);

        savePdfToFile(customer, pdfContentStream);
        LOGGER.info("PDF for customer ID: {} created successfully.", customerId);
    }

    private void savePdfToFile(Customer customer, ByteArrayInputStream pdfContentStream) {
        Path pdfFilePath = baseDirectory.resolve(customer.getLastName() + "_" + customer.getFirstName() + "_email.pdf");
        try (OutputStream outputStream = Files.newOutputStream(pdfFilePath)) {
            byte[] buffer = new byte[pdfContentStream.available()];
            pdfContentStream.read(buffer);
            outputStream.write(buffer);
            LOGGER.info("PDF file for customer ID: {} saved successfully.", customer.getId());
        } catch (IOException e) {
            String errorMessage = String.format("Error saving PDF for customer ID %s: %s", customer.getId(), e.getMessage());
            LOGGER.error(errorMessage, e);
            throw new PdfGenerationException(errorMessage, e);
        }
    }

    private void ensureDirectoryExists(Path directoryPath) {
        if (Files.notExists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                LOGGER.info("Directory created at: {}", directoryPath);
            } catch (IOException e) {
                String errorMessage = String.format("Failed to create directory: %s", directoryPath);
                LOGGER.error(errorMessage, e);
                throw new DirectoryCreationException(errorMessage, e);
            }
        }
    }
}

