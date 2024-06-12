package oleksandr_havriush.autoshowroomcustomermailer.controller;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.FileProcessingException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.NoCarsToSaveException;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@SessionAttributes("carList")
public class CarController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);
    private final CarService carService;

    @GetMapping("/cars")
    public String showUploadPage() {
        LOGGER.info("Accessed the cars upload page.");
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        LOGGER.info("Received a file upload request. File name: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            LOGGER.warn("Attempt to upload an empty file.");
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/uploadStatus";
        }

        try {
            CarList carList = carService.processXmlFile(file);
            LOGGER.info("XML file '{}' uploaded and processed successfully.", file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("carList", carList);
            return "redirect:/displayCars";
        } catch (FileProcessingException e) {
            LOGGER.error("Error processing XML file '{}': ", file.getOriginalFilename(), e);
            redirectAttributes.addFlashAttribute("message", "Error processing XML file.");
            return "redirect:/uploadStatus";
        }
    }

    @GetMapping("/displayCars")
    public String displayCars(@ModelAttribute("carList") CarList carList, Model model) {
        model.addAttribute("carList", carList);
        return "displayCars";
    }

    @PostMapping("/save")
    public String saveCars(@ModelAttribute("carList") CarList carList, RedirectAttributes redirectAttributes) {
        try {
            carService.saveCars(carList);
            LOGGER.info("Cars saved to database successfully.");
            redirectAttributes.addFlashAttribute("message", "Cars saved to database successfully.");
            return "redirect:/uploadStatus";
        } catch (NoCarsToSaveException e) {
            LOGGER.error("No cars to save: ", e);
            redirectAttributes.addFlashAttribute("message", "No cars to save.");
            return "redirect:/uploadStatus";
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected error: ", e);
            redirectAttributes.addFlashAttribute("message", "Error saving cars to database.");
            return "redirect:/uploadStatus";
        }
    }

    @GetMapping("/uploadStatus")
    public String showUploadStatus() {
        return "uploadStatus";
    }
}
