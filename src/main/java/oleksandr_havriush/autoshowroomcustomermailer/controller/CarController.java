package oleksandr_havriush.autoshowroomcustomermailer.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CarController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);
    private final CarService carService;

    @GetMapping("/cars")
    public String index() {
        LOGGER.info("Accessed the cars upload page.");
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        LOGGER.info("Received a file upload request. File name: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            LOGGER.warn("Attempt to upload an empty file.");
            model.addAttribute("message", "Please select a file to upload.");
            return "uploadStatus";
        }

        try {
            LOGGER.debug("Attempting to parse the XML file.");
            CarList carList = carService.xmlFileToCar(file);
            LOGGER.debug("Parsed car list: {}", carList);
            model.addAttribute("carList", carList);
            session.setAttribute("carList", carList);
            LOGGER.info("XML file '{}' uploaded and parsed successfully.", file.getOriginalFilename());
            return "displayCars";
        } catch (Exception e) {
            LOGGER.error("Error processing XML file '{}': ", file.getOriginalFilename(), e);
            model.addAttribute("message", "Error processing XML file.");
            return "uploadStatus";
        }
    }

    @PostMapping("/save")
    public String saveCarsToDb(HttpSession session, RedirectAttributes redirectAttributes) {
        LOGGER.info("Attempting to save cars to the database from session.");
        CarList carList = (CarList) session.getAttribute("carList");
        if (carList == null) {
            LOGGER.warn("No car list found in session, cannot save to database.");
            redirectAttributes.addFlashAttribute("message", "No cars to save.");
            return "redirect:/uploadStatus";
        }

        try {
            LOGGER.debug("Saving car list to database: {}", carList);
            carService.saveCarsToDb(carList);
            LOGGER.info("Cars saved to database successfully.");
            redirectAttributes.addFlashAttribute("message", "Cars saved to database successfully.");
            return "redirect:/uploadStatus";
        } catch (Exception e) {
            LOGGER.error("Error saving cars to the database: ", e);
            redirectAttributes.addFlashAttribute("message", "Error saving cars to database.");
            return "redirect:/uploadStatus";
        }
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        LOGGER.info("Accessed the upload status page.");
        return "uploadStatus";
    }
}
