package ru.louamphibi.nextbasin.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@SuppressWarnings("deprecation")
public class NbErrorController implements ErrorController {

    @RequestMapping("/error")
    public String errorHandle(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value())
                return "errors/error-404";
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
                return "errors/error-500";
            else if (statusCode == HttpStatus.FORBIDDEN.value())
                return "errors/error-403";
        }

        return "errors/error";
    }

    public String getErrorPath() {
        return null;
    }
}
