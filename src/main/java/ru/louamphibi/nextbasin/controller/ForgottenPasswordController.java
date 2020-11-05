package ru.louamphibi.nextbasin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.louamphibi.nextbasin.controller.util.DaoToFormConverter;
import ru.louamphibi.nextbasin.entity.ConfirmationToken;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.ManagerService;
import ru.louamphibi.nextbasin.service.PasswordService;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/forgotPassword")
public class ForgottenPasswordController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private ManagerService managerService;


    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, trimmerEditor);
    }

    @GetMapping()
    public String sendNewPasswordForm(){
        return "service/forgot-password-form";
    }

    @PostMapping()
    public String sendNewPassword(@ModelAttribute("email") String email) throws MessagingException {

        Employee employee = passwordService.findByEmail(email);

        if (employee == null) {
            return "redirect:/forgotPassword?emailError";
        }

        ConfirmationToken confirmationToken;

        if ((confirmationToken = passwordService.findByEmployeeId(employee.getId())) == null) {
            confirmationToken = passwordService.saveConfirmationToken(employee);
        }

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        helper.setText(" <h1 align='center' > <img src='cid:nbLogo'> <span style='font-size: 50px'> NextBasin </span> </h1>"
                +"<h2 align='center' style='font-size: 20px'> Complete Password Reset </h2>" +
                "Hi <b>" + employee.getUsername() + "</b>! To complete the password reset process, please click here: " +
                "http://localhost:8080/forgotPassword/confirmReset?token=" + confirmationToken.getConfirmationToken() +  "</p>", true);


        helper.addInline("nbLogo", new ClassPathResource("static/images/nbLogo.png"));

        helper.setTo(email);

        helper.setSubject("New password for NextBasin");

        mailSender.send(message);

        return "redirect:/?passwordChange";
    }

    @GetMapping("/confirmReset")
    public String validateResetToken(Model model, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = passwordService.findByConfirmationToken(confirmationToken);

        if (token == null) {
            return "redirect:/?resetPasswordError";
        }

        Employee employee = managerService.findById(token.getEmployee().getId());

        EmployeeForm employeeForm = DaoToFormConverter.userToForm(employee);

        model.addAttribute("currEmployee", employee);

        if(!model.containsAttribute("employeeForm"))
            model.addAttribute("employeeForm", employeeForm);

        model.addAttribute("token", token.getConfirmationToken());

        return "service/reset-password";
    }

    @PostMapping("/reset")
    public String resetUserPassword(@Valid @ModelAttribute("employeeForm")  EmployeeForm employeeForm,
                                    BindingResult bindingResult,
                                    @ModelAttribute("employeeId") Long employeeId,
                                    @ModelAttribute("token") String token,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employeeForm", bindingResult);
            redirectAttributes.addFlashAttribute("employeeForm", employeeForm);
            redirectAttributes.addAttribute("token", token);
            return "redirect:/forgotPassword/confirmReset";
        }

        if(!employeeForm.getPassword().equals(employeeForm.getPasswordConfirm())) {
            redirectAttributes.addFlashAttribute("passwordConfirmedError", "Passwords do not match");
            redirectAttributes.addFlashAttribute("employeeForm", employeeForm);
            redirectAttributes.addAttribute("token", token);
            return "redirect:/forgotPassword/confirmReset";
        }

        Employee tokenEmployee = managerService.findById(employeeId);

        passwordService.changePassword(tokenEmployee, employeeForm.getPassword());

        passwordService.deleteConfirmationToken(passwordService.findByConfirmationToken(token));

        return "redirect:/?passwordChangeConfirm";
    }
}
