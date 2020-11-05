package ru.louamphibi.nextbasin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.louamphibi.nextbasin.controller.util.DaoToFormConverter;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.AdminService;
import ru.louamphibi.nextbasin.service.ManagerService;
import javax.validation.Valid;


@Controller
public class MainController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private AdminService adminService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, trimmerEditor);
    }

    @GetMapping
    public String welcomePage(@AuthenticationPrincipal Employee employee) {

        if (employee == null)
            return "welcome-page";

        String role = employee.getRoles().toString();

        if (role.contains("ROLE_MANAGER"))
            return "redirect:/manager";
        else if (role.contains("ROLE_ADMIN"))
            return "redirect:/admin";
        else
            return "redirect:/user-workspace";
    }


    @GetMapping("/profile")
    public String editProfile(@AuthenticationPrincipal Employee currentUser, Model model) {
        Employee employeeFromDb = managerService.findById(currentUser.getId());

        EmployeeForm userForm = DaoToFormConverter.userToForm(employeeFromDb);

        model.addAttribute("userForm", userForm);

        model.addAttribute("currUser", currentUser);

        return "service/my-profile-form";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@Valid @ModelAttribute("userForm") EmployeeForm userForm,
                                BindingResult bindingResult,
                                @ModelAttribute("userId") Long userId,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userForm", userForm);
            model.addAttribute("currUser", managerService.findById(userId));

            return "service/my-profile-form";
        }

        if (!adminService.updateEmployee(managerService.findById(userId), userForm)) {
            model.addAttribute("userForm", userForm);
            model.addAttribute("currUser", managerService.findById(userId));
            model.addAttribute("usernameOrEmailError", "A user with the same name or email already exists");

            return "service/my-profile-form";
        }

        return "redirect:/";
    }

    @GetMapping("/administration")
    public String showAdministration(Model model) {
        model.addAttribute("adminList", adminService.findAllAdmins());

        return "service/administration";
    }
}


