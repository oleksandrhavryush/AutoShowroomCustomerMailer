package oleksandr_havriush.autoshowroomcustomermailer.controller;

import oleksandr_havriush.autoshowroomcustomermailer.exeptions.FileProcessingException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.NoCarsToSaveException;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.service.CarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    @DisplayName("Test show upload page")
    void testShowUploadPage() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(view().name("upload"));
    }

    @Test
    @DisplayName("Test handle file upload with success")
    void testHandleFileUpload_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xml", "text/xml", "<cars></cars>".getBytes());
        CarList carList = new CarList();

        when(carService.processXmlFile(file)).thenReturn(carList);

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/displayCars"))
                .andExpect(flash().attribute("carList", carList));
    }

    @Test
    @DisplayName("Test handle file upload with empty file")
    void testHandleFileUpload_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xml", "text/xml", new byte[0]);

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Please select a file to upload."));
    }

    @Test
    @DisplayName("Test handle file upload with error")
    void testHandleFileUpload_Error() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xml", "text/xml", "<cars></cars>".getBytes());

        when(carService.processXmlFile(file)).thenThrow(new FileProcessingException("Error processing XML file"));

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Error processing XML file."));
    }

    @Test
    @DisplayName("Test display cars")
    void testDisplayCars() throws Exception {
        CarList carList = new CarList();

        mockMvc.perform(get("/displayCars").flashAttr("carList", carList))
                .andExpect(status().isOk())
                .andExpect(view().name("displayCars"))
                .andExpect(model().attribute("carList", carList));
    }

    @Test
    @DisplayName("Test save cars with success")
    void testSaveCars_Success() throws Exception {
        CarList carList = new CarList();

        doNothing().when(carService).saveCars(carList);

        mockMvc.perform(post("/save").flashAttr("carList", carList))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Cars saved to database successfully."));
    }

    @Test
    @DisplayName("Test save cars with no cars")
    void testSaveCars_NoCars() throws Exception {
        CarList carList = new CarList();

        doThrow(new NoCarsToSaveException("No cars to save")).when(carService).saveCars(carList);

        mockMvc.perform(post("/save").flashAttr("carList", carList))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "No cars to save."));
    }

    @Test
    @DisplayName("Test save cars with error")
    void testSaveCars_Error() throws Exception {
        CarList carList = new CarList();

        doThrow(new RuntimeException("Unexpected error")).when(carService).saveCars(carList);

        mockMvc.perform(post("/save").flashAttr("carList", carList))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Error saving cars to database."));
    }

    @Test
    @DisplayName("Test show upload status")
    void testShowUploadStatus() throws Exception {
        mockMvc.perform(get("/uploadStatus"))
                .andExpect(status().isOk())
                .andExpect(view().name("uploadStatus"));
    }

    @Test
    @DisplayName("Test handle file upload with invalid XML")
    void testHandleFileUpload_InvalidXml() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "invalid.xml", "text/xml", "<cars><car></car>".getBytes());
        when(carService.processXmlFile(file)).thenThrow(new FileProcessingException("Invalid XML file"));

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(flash().attribute("message", "Error processing XML file."));
    }

    @Test
    @DisplayName("Test session attribute after file upload")
    void testSessionAttributeAfterFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xml", "text/xml", "<cars></cars>".getBytes());
        CarList carList = new CarList();
        carList.setCars(new ArrayList<>());

        when(carService.processXmlFile(file)).thenReturn(carList);

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/displayCars"))
                .andExpect(flash().attributeExists("carList"))
                .andExpect(flash().attribute("carList", carList));
    }

    @Test
    @DisplayName("Test upload status page with message")
    void testUploadStatusPageWithMessage() throws Exception {
        mockMvc.perform(get("/uploadStatus").flashAttr("message", "Test message"))
                .andExpect(status().isOk())
                .andExpect(view().name("uploadStatus"))
                .andExpect(model().attribute("message", "Test message"));
    }
}
