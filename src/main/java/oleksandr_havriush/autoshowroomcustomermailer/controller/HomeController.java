package oleksandr_havriush.autoshowroomcustomermailer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for handling requests to the home page.
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    /**
     * Handles GET requests to the root URL ("/") and returns the "index" view.
     * Logs an informational message when the home page is accessed.
     *
     * @return the name of the view to be rendered, in this case "index"
     */
    @GetMapping("/")
    public String index() {
        LOGGER.info("Home page accessed");
        return "index";
    }
}
