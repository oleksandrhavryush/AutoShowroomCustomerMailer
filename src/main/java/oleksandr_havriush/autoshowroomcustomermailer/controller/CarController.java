package oleksandr_havriush.autoshowroomcustomermailer.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import oleksandr_havriush.autoshowroomcustomermailer.model.CarList;
import oleksandr_havriush.autoshowroomcustomermailer.service.CarDbService;
import oleksandr_havriush.autoshowroomcustomermailer.service.CarParserService;
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
    private final CarDbService carDbService;
    private final CarParserService parser;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            return "uploadStatus";
        }

        //CarList carList = parser.toObject(xml);
        CarList carList = parser.xmlFileToCar(file);

        // Add the CarList to the model
        model.addAttribute("carList", carList);

        // Add the CarList to the session
        session.setAttribute("carList", carList);

        // Return the view name for the page that displays the CarList
        return "displayCars";
    }

    @PostMapping("/save")
    public String saveCarsToDb(HttpSession session, RedirectAttributes redirectAttributes) {
        // Get the CarList from the session
        CarList carList = (CarList) session.getAttribute("carList");

        // Save the cars to the database
        carDbService.saveCarsToDb(carList);

        redirectAttributes.addFlashAttribute("message", "Cars saved to database successfully.");
        return "redirect:/uploadStatus";
    }


    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
}
