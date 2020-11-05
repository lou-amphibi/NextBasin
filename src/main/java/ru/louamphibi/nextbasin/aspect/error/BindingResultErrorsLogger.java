package ru.louamphibi.nextbasin.aspect.error;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.logging.Logger;

@Component
public class BindingResultErrorsLogger {
    private Logger logger = Logger.getLogger(getClass().getName());

    public void logBindingResultErrors(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error : errors ) {
            logger.warning("BindingResult log >>>> " + error.getObjectName() + " - " + error.getDefaultMessage());
        }
    }
}
