package oleksandr_havriush.autoshowroomcustomermailer.util;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.XmlParsingException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;

public class XmlToCarListConverterTest {

    private XmlToCarListConverter converter;

    private MockedStatic<JAXBContext> mockedJaxbContext;
    private MockedStatic<LoggerFactory> mockedLoggerFactory;
    private Logger mockedLogger;

    @BeforeEach
    public void setUp() {
        converter = new XmlToCarListConverter();

        mockedLogger = mock(Logger.class);
        mockedLoggerFactory = Mockito.mockStatic(LoggerFactory.class);
        mockedLoggerFactory.when(() -> LoggerFactory.getLogger(XmlToCarListConverter.class)).thenReturn(mockedLogger);

        mockedJaxbContext = Mockito.mockStatic(JAXBContext.class);
    }

    @AfterEach
    public void tearDown() {
        mockedJaxbContext.close();
        mockedLoggerFactory.close();
    }

    @Test
    @DisplayName("Test successful XML to CarList conversion")
    public void testConvert_Success() throws JAXBException {
        String xml = "<carList><cars><car><id>1</id><type>Car</type><model>Octavia</model><brand>Skoda</brand><horsepower>85</horsepower><price>20000.0</price></car></cars></carList>";
        Car car1 = Car.builder()
                .name("Octavia")
                .manufacturer("Skoda")
                .price(20000.0)
                .type("Sedan")
                .power(85)
                .build();

        CarList expectedCarList = new CarList();
        expectedCarList.setCars(List.of(car1));

        JAXBContext jaxbContext = mock(JAXBContext.class);
        Unmarshaller unmarshaller = mock(Unmarshaller.class);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshal(any(StringReader.class))).thenReturn(expectedCarList);

        mockedJaxbContext.when(() -> JAXBContext.newInstance(CarList.class)).thenReturn(jaxbContext);

        CarList carList = converter.convert(xml);
        assertNotNull(carList);
        assertEquals(expectedCarList.getCars().size(), carList.getCars().size());
        assertEquals(expectedCarList.getCars().get(0).getName(), carList.getCars().get(0).getName());
    }

    @Test
    @DisplayName("Test XML parsing error")
    public void testConvert_XmlParsingError() throws JAXBException {
        String invalidXml = "<carList><cars><car><id>1</id><type>Car</type><model>Octavia</model><brand>Skoda</brand><horsepower>85</horsepower><price>20000.0</price></car></cars>";  // Missing closing tag

        JAXBContext jaxbContext = mock(JAXBContext.class);
        Unmarshaller unmarshaller = mock(Unmarshaller.class);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshal(any(StringReader.class))).thenThrow(new JAXBException("Test exception"));

        mockedJaxbContext.when(() -> JAXBContext.newInstance(CarList.class)).thenReturn(jaxbContext);

        XmlParsingException exception = assertThrows(XmlParsingException.class, () -> converter.convert(invalidXml));
        assertEquals("Error parsing XML", exception.getMessage());
    }
}
