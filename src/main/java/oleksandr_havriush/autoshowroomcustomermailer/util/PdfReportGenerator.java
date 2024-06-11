package oleksandr_havriush.autoshowroomcustomermailer.util;

import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.Customer;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PdfReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfReportGenerator.class);
    private static final String LOGO_PATH = "src/main/resources/logo.jpg";
    private static final String DEALERSHIP_INFO = "Car Dealership GmbH\nZoellner 40\nOverath, 51491\nGermany";

    public static ByteArrayInputStream createCustomerPdfReport(Optional<Customer> customer, List<Car> carList) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            addDealershipLogo(document);
            addDealershipAddress(document);
            addCustomerAddress(document, customer);
            addLetterBody(document, customer);
            addCarTable(document, carList);
            addSignature(document);
            document.close();
        } catch (DocumentException | IOException ex) {
            LOGGER.error("Error occurred: {}", ex);
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private static void addDealershipLogo(Document document) throws IOException, DocumentException {
        Image logo = Image.getInstance(LOGO_PATH);
        float pageWidth = document.getPageSize().getWidth();
        float rightOffset = 50;
        float topOffset = 56.7f;
        float logoWidth = 150;
        float pageHeight = document.getPageSize().getHeight();
        logo.setAbsolutePosition(pageWidth - logoWidth - rightOffset, pageHeight - logoWidth - topOffset);
        logo.scaleToFit(150, 150);
        document.add(logo);
    }

    private static void addDealershipAddress(Document document) throws DocumentException {
        Paragraph address = new Paragraph(DEALERSHIP_INFO);
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(address);
    }

    private static void addCustomerAddress(Document document, Optional<Customer> customer) throws DocumentException {
        if (customer.isPresent()) {
            Customer cust = customer.get();
            Paragraph customerAddress = new Paragraph(String.format("%s %s\n%s %s\n%s %s\n%s",
                    cust.getFirstName(), cust.getLastName(),
                    cust.getAddress().getStreet(), cust.getAddress().getHouseNumber(),
                    cust.getAddress().getPostalCode(), cust.getAddress().getCity(),
                    cust.getAddress().getCountry()));
            customerAddress.setAlignment(Element.ALIGN_LEFT);
            // Set the position to match the windowed envelope fold
            customerAddress.setIndentationLeft(50);
            customerAddress.setSpacingBefore(50); // Adjust spacing to fit the windowed envelope
            document.add(customerAddress);
        }
    }

    private static void addLetterBody(Document document, Optional<Customer> customer) throws DocumentException {
        if (customer.isPresent()) {
            Paragraph greeting = new Paragraph(String.format("\nDear %s %s,", customer.get().getFirstName(), customer.get().getLastName()));
            greeting.setSpacingBefore(100);
            document.add(greeting);

            Paragraph body = new Paragraph("\nWe are pleased to inform you about the latest updates in our dealership. "
                    + "Please find below the details of the new car models and the special offers we have for you:\n\n");
            document.add(body);
        }
    }

    private static void addCarTable(Document document, List<Car> carList) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 3, 3, 3, 2, 3});
        table.setWidthPercentage(100);
        addTableHeader(table);
        addTableRows(table, carList);
        document.add(table);
    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("Id", "Type", "Name", "Manufacturer", "Power", "Price")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addTableRows(PdfPTable table, List<Car> carList) {
        carList.forEach(car -> {
            table.addCell(car.getId().toString());
            table.addCell(car.getType());
            table.addCell(car.getName());
            table.addCell(car.getManufacturer());
            table.addCell(String.valueOf(car.getPower()));
            table.addCell(String.format("%.2f", car.getPrice()));
        });
    }

    private static void addSignature(Document document) throws DocumentException {
        Paragraph signature = new Paragraph("\nSincerely,\n\n" + "Your Car Dealership Team");
        signature.setSpacingBefore(50);
        document.add(signature);
    }
}
