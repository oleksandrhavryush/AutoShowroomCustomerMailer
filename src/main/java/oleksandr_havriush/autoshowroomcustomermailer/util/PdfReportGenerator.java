package oleksandr_havriush.autoshowroomcustomermailer.util;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.PdfGenerationException;
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

/**
 * Utility class for generating PDF reports for customers.
 */
public class PdfReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfReportGenerator.class);
    private static final String LOGO_PATH = "src/main/resources/logo.jpg";
    private static final String DEALERSHIP_INFO = "Car Dealership GmbH\nZoellner 40\nOverath, 51491\nGermany";

    /**
     * Generates a PDF report containing customer details and a list of cars.
     *
     * @param customer the optional customer details to include in the report
     * @param carList  the list of cars to include in the report
     * @return a ByteArrayInputStream containing the generated PDF content
     * @throws PdfGenerationException if an error occurs during PDF generation
     */
    public static ByteArrayInputStream createCustomerPdfReport(Optional<Customer> customer, List<Car> carList) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.setMargins(57.6f, 57.6f, 57.6f, 57.6f);
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
            throw new PdfGenerationException("Error during PDF creation", ex);
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Adds the dealership logo to the PDF document.
     *
     * @param document the PDF document to which the logo is added
     * @throws IOException     if there is an error reading the logo image
     * @throws DocumentException if there is an error adding the logo to the document
     */
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

    /**
     * Adds the dealership address to the PDF document.
     *
     * @param document the PDF document to which the address is added
     * @throws DocumentException if there is an error adding the address to the document
     */
    private static void addDealershipAddress(Document document) throws DocumentException {
        Paragraph address = new Paragraph(DEALERSHIP_INFO);
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(address);
    }

    /**
     * Adds the customer address to the PDF document.
     *
     * @param document the PDF document to which the customer address is added
     * @param customer the optional customer details to include in the address
     * @throws DocumentException if there is an error adding the customer address to the document
     */
    private static void addCustomerAddress(Document document, Optional<Customer> customer) throws DocumentException {
        if (customer.isPresent()) {
            Customer cust = customer.get();
            Paragraph customerAddress = new Paragraph(String.format("%s %s\n%s %s\n%s %s\n%s",
                    cust.getFirstName(), cust.getLastName(),
                    cust.getAddress().getStreet(), cust.getAddress().getHouseNumber(),
                    cust.getAddress().getPostalCode(), cust.getAddress().getCity(),
                    cust.getAddress().getCountry()));
            customerAddress.setAlignment(Element.ALIGN_LEFT);
            customerAddress.setIndentationLeft(50);
            customerAddress.setSpacingBefore(50);
            document.add(customerAddress);
        }
    }

    /**
     * Adds the letter body to the PDF document.
     *
     * @param document the PDF document to which the letter body is added
     * @param customer the optional customer details to include in the letter body
     * @throws DocumentException if there is an error adding the letter body to the document
     */
    private static void addLetterBody(Document document, Optional<Customer> customer) throws DocumentException {
        if (customer.isPresent()) {
            Paragraph greeting = new Paragraph(String.format("\nDear %s %s,", customer.get().getFirstName(), customer.get().getLastName()));
            greeting.setSpacingBefore(100);
            document.add(greeting);

            Paragraph body = new Paragraph("\nWe are delighted to announce that today is a momentous day at Car Dealership GmbH. " +
                    "We are pleased to present our latest range of vehicles. Each model represents a pinnacle of automotive craftsmanship, " +
                    "designed to elevate your driving experience.\n" +
                    "We invite you to view our selection of new car models, along with the exclusive offers " +
                    "we have created for discerning customers like you.\n");
            document.add(body);
        }
    }

    /**
     * Adds the car table to the PDF document.
     *
     * @param document the PDF document to which the car table is added
     * @param carList  the list of cars to include in the table
     * @throws DocumentException if there is an error adding the car table to the document
     */
    private static void addCarTable(Document document, List<Car> carList) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{3, 3, 3, 2, 3});
        table.setWidthPercentage(100);
        table.setSpacingBefore(30);
        addTableHeader(table);
        addTableRows(table, carList);
        document.add(table);
    }

    /**
     * Adds the header row to the car table.
     *
     * @param table the PdfPTable to which the header row is added
     */
    private static void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Stream.of("Type", "Name", "Manufacturer", "Power", "Price")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setFixedHeight(35);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    header.setPhrase(new Phrase(columnTitle, headerFont));
                    table.addCell(header);
                });
    }

    /**
     * Adds the rows with car details to the car table.
     *
     * @param table   the PdfPTable to which the car rows are added
     * @param carList the list of cars to include in the table rows
     */
    private static void addTableRows(PdfPTable table, List<Car> carList) {
        Font textFont = new Font(Font.FontFamily.HELVETICA, 12);
        carList.forEach(car -> {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(car.getType(), textFont));
            cell.setFixedHeight(25);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(car.getName(), textFont));
            cell.setFixedHeight(25);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(car.getManufacturer(), textFont));
            cell.setFixedHeight(25);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(car.getPower()), textFont));
            cell.setFixedHeight(25);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.format("%.2f", car.getPrice()), textFont));
            cell.setFixedHeight(25);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);
        });
    }

    /**
     * Adds the signature to the PDF document.
     *
     * @param document the PDF document to which the signature is added
     * @throws DocumentException if there is an error adding the signature to the document
     */
    private static void addSignature(Document document) throws DocumentException {
        Paragraph signature = new Paragraph("\nBest regards,\n" + "Car Dealership GmbH Family");
        signature.setSpacingBefore(30);
        document.add(signature);
    }
}
