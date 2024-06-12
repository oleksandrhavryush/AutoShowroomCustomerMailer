package oleksandr_havriush.autoshowroomcustomermailer.util;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.XmlParsingException;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Component
public class XmlToCarListConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlToCarListConverter.class);

    public CarList convert(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CarList.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (CarList) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            LOGGER.error("Error parsing XML", e);
            throw new XmlParsingException("Error parsing XML", e);
        }
    }
}
