package oleksandr_havriush.autoshowroomcustomermailer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Controller class for handling requests to the home page.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private final MessageSource messageSource;

    /**
     * Handles GET requests to the root URL ("/") and returns the "index" view.
     * Logs an informational message when the home page is accessed.
     *
     * @return the name of the view to be rendered, in this case "index"
     */
    @GetMapping("/")
    public String index(Model model, Locale locale) {
        LOGGER.info("Home page accessed");
        String welcomeMessage = messageSource.getMessage("welcome.message", null, locale);
        String welcomeDescription = messageSource.getMessage("welcome.description", null, locale);
        model.addAttribute("welcomeMessage", welcomeMessage);
        model.addAttribute("welcomeDescription", welcomeDescription);
        return "index";
    }

}
