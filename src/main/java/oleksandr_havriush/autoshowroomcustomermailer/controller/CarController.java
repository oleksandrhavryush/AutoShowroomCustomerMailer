package oleksandr_havriush.autoshowroomcustomermailer.controller;

import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.FileProcessingException;
import oleksandr_havriush.autoshowroomcustomermailer.exeptions.NoCarsToSaveException;
import oleksandr_havriush.autoshowroomcustomermailer.model.Car;
import oleksandr_havriush.autoshowroomcustomermailer.model.VehicleList;
import oleksandr_havriush.autoshowroomcustomermailer.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller class for handling car-related operations.
 */
@Controller
@RequiredArgsConstructor
@SessionAttributes("carList")
public class CarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);
    private final VehicleService<Car> carService;

    /**
     * Handles GET requests for the car upload page.
     *
     * @return the name of the upload view
     */
    @GetMapping("/cars")
    public String showUploadPage() {
        LOGGER.info("Accessed the cars upload page.");
        return "upload";
    }

    /**
     * Handles POST requests for file upload.
     *
     * @param file the uploaded file
     * @param model the model object
     * @param redirectAttributes attributes for redirect scenarios
     * @return the redirect view name
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        LOGGER.info("Received a file upload request. File name: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            LOGGER.warn("Attempt to upload an empty file.");
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/uploadStatus";
        }

        try {
            VehicleList<Car> carList = carService.processFile(file);
            LOGGER.info("XML file '{}' uploaded and processed successfully.", file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("carList", carList);
            return "redirect:/displayCars";
        } catch (FileProcessingException e) {
            LOGGER.error("Error processing XML file '{}': ", file.getOriginalFilename(), e);
            redirectAttributes.addFlashAttribute("message", "Error processing XML file.");
            return "redirect:/uploadStatus";
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected error: ", e);
            redirectAttributes.addFlashAttribute("message", "Error processing XML file.");
            return "redirect:/uploadStatus";
        }
    }

    /**
     * Displays the list of cars.
     *
     * @param carList the list of cars
     * @param model the model object
     * @return the name of the display cars view
     */
    @GetMapping("/displayCars")
    public String displayCars(@ModelAttribute("carList") VehicleList<Car> carList, Model model) {
        model.addAttribute("carList", carList);
        return "displayCars";
    }

    /**
     * Handles POST requests to save the list of cars to the database.
     *
     * @param carList the list of cars
     * @param redirectAttributes attributes for redirect scenarios
     * @return the redirect view name
     */
    @PostMapping("/save")
    public String saveCars(@ModelAttribute("carList") VehicleList<Car> carList, RedirectAttributes redirectAttributes) {
        try {
            carService.saveVehicles(carList);
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

    /**
     * Displays the upload status page.
     *
     * @return the name of the upload status view
     */
    @GetMapping("/uploadStatus")
    public String showUploadStatus() {
        return "uploadStatus";
    }
}
