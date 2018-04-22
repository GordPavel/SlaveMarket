package spring.springControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @RequestMapping(value = "/errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("error_page");
        String errorMsg = "";
        String errorHead = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorHead = "Http Error Code: 400.";
                errorMsg = "Bad Request. Current request can not be processed. You might try returning to the " +
                        "<a href=\"/\">home page</a>.";
                break;
            }
            case 401: {
                errorHead = "Http Error Code: 401.";
                errorMsg = "Unauthorized access. Log in or you might try returning to the " +
                        "<a href=\"/\">home page</a>.";
                break;
            }
            case 404: {
                errorHead = "Http Error Code: 404.";
                errorMsg = "Resource not found. You might try returning to the " +
                        "<a href=\"/\">home page</a>.";
                break;
            }
            case 500: {
                errorHead = "Http Error Code: 500.";
                errorMsg = "Internal Server Error. Please contact system administrator. You might try returning to the " +
                        "<a href=\"/\">home page</a>.";
                break;
            }
        }
        errorPage.addObject("errorMsg", errorMsg);
        errorPage.addObject("errorHead", errorHead);
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }
}