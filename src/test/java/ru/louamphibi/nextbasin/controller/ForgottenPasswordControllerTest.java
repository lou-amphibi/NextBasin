package ru.louamphibi.nextbasin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.louamphibi.nextbasin.controller.testUtils.MockFormFieldSetter;
import ru.louamphibi.nextbasin.entity.ConfirmationToken;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.ManagerService;
import ru.louamphibi.nextbasin.service.PasswordService;

import javax.mail.internet.MimeMessage;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ForgottenPasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ForgottenPasswordControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private PasswordService passwordService;

    @MockBean
    private ManagerService managerService;

    private ConfirmationToken token;
    private Employee employee;
    private EmployeeForm employeeForm;
    private MimeMessage message;

    private final String email = "email";
    private Long testEmployeeId = 1L;

    @Before
    public void setUp() {
        employee = new Employee();
        employee.setId(testEmployeeId);

        token = new ConfirmationToken();
        token.setConfirmationToken("token");
        token.setEmployee(employee);

        employeeForm = new EmployeeForm();
        MockFormFieldSetter.setFormFields(employeeForm);

        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        message = javaMailSender.createMimeMessage();
    }


    @Test
    public void testSuccessForgotPasswordFormResponse() throws Exception {
        mvc.perform(get("/forgotPassword"))
                .andExpect(status().isOk());
    }

    @Test
    public void sendNewPasswordPostShouldRedirectWithErrorParameterIfEmployeeEqualsNull() throws Exception {
        given(passwordService.findByEmail(email)).willReturn(null);

        mvc.perform(post("/forgotPassword").flashAttr("email", email))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/forgotPassword?emailError"));
    }


    @Test
    public void sendNewPasswordPostShouldCorrectRedirectUrl() throws Exception {
        given(passwordService.findByEmail(email)).willReturn(employee);
        given(passwordService.findByEmployeeId(employee.getId())).willReturn(token);
        given(mailSender.createMimeMessage()).willReturn(message);

        mvc.perform(post("/forgotPassword").flashAttr("email", email))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/?passwordChange"));
    }

    @Test
    public void validateResetTokenShouldRedirectToMainPageWithErrorParameterIfTokenEqualsNull() throws Exception {
        given(passwordService.findByConfirmationToken(token.getConfirmationToken())).willReturn(null);

        mvc.perform(get("/forgotPassword/confirmReset").param("token", token.getConfirmationToken()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/?resetPasswordError"));
    }

    @Test
    public void  validateResetTokenSuccess() throws Exception {
        given(passwordService.findByConfirmationToken(token.getConfirmationToken())).willReturn(token);
        given(managerService.findById(token.getEmployee().getId())).willReturn(employee);

        mvc.perform(get("/forgotPassword/confirmReset").param("token", token.getConfirmationToken()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("token"));
    }

    @Test
    public void  resetUserPasswordWithBindingResultErrorsShouldReturnCorrectModelAttributes() throws Exception {
        employeeForm.setUsername(null);

        mvc.perform(post("/forgotPassword/reset").flashAttr("employeeForm", employeeForm)
        .flashAttr("employeeId", employee.getId()).flashAttr("token", token.getConfirmationToken()))
                .andExpect(status().isFound())
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.employeeForm"
                ,"employeeForm"))
                .andExpect(model().attributeExists("token"))
                .andExpect(redirectedUrl("/forgotPassword/confirmReset?token=" + token.getConfirmationToken()));
    }

    @Test
    public void resetUserPasswordWithNotMathPasswordsShouldReturnCorrectModelAttributes() throws Exception {
        employeeForm.setPassword("incorrectPassword");

        mvc.perform(post("/forgotPassword/reset").flashAttr("employeeForm", employeeForm)
                .flashAttr("employeeId", employee.getId()).flashAttr("token", token.getConfirmationToken()))
                .andExpect(status().isFound())
                .andExpect(flash().attributeExists("passwordConfirmedError"
                        ,"employeeForm"))
                .andExpect(model().attributeExists("token"))
                .andExpect(redirectedUrl("/forgotPassword/confirmReset?token=" + token.getConfirmationToken()));
    }

    @Test
    public void resetUserPasswordShouldReturnSuccessRedirectUrl() throws Exception {
        given(managerService.findById(employee.getId())).willReturn(employee);

        mvc.perform(post("/forgotPassword/reset").flashAttr("employeeForm", employeeForm)
                .flashAttr("employeeId", employee.getId()).flashAttr("token", token.getConfirmationToken()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/?passwordChangeConfirm"));
    }
}
