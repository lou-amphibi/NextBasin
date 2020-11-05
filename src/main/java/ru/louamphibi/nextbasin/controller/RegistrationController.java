package ru.louamphibi.nextbasin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.RegistrationService;
import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RegistrationService registrationService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, trimmerEditor);
    }

    @GetMapping("/registration")
    public String registration(Model model) {

        model.addAttribute("employeeForm", new EmployeeForm());

        model.addAttribute("managersList", employeeService.findAllManagers());

        return "auth/registration";
    }

    @PostMapping("/registration")
    public String addEmployee(@Valid @ModelAttribute("employeeForm")  EmployeeForm employeeForm,
                              BindingResult bindingResult, Model model) {
        if(employeeForm.getManagerId() == -1) {
            employeeForm.setManagerId(null);
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("managersList", employeeService.findAllManagers());
            return "auth/registration";
        }

        if(!employeeForm.getPassword().equals(employeeForm.getPasswordConfirm())) {
            model.addAttribute("managersList", employeeService.findAllManagers());
            model.addAttribute("passwordConfirmedError", "Passwords do not match");
            return "auth/registration";
        }

        if(!registrationService.saveUser(employeeForm, "ROLE_USER")) {
            model.addAttribute("managersList", employeeService.findAllManagers());
            model.addAttribute("usernameOrEmailError", "A user with the same name or email already exists");
            return "auth/registration";
        }

        model.addAttribute("SuccessfulRegistration", "You have been successfully registered!");

        return "auth/login";
    }
}
