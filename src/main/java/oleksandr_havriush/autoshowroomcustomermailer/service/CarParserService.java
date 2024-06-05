package oleksandr_havriush.autoshowroomcustomermailer.service;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.util.ParserXmlFileToCarList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CarParserService {
    private final ParserXmlFileToCarList parser;

    public CarList xmlFileToCar(MultipartFile file) {
        try {
            String xml = new String(file.getBytes(), StandardCharsets.UTF_8);
            return parser.toObject(xml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

