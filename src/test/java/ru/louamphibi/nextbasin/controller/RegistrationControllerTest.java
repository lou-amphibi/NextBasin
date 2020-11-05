package ru.louamphibi.nextbasin.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.louamphibi.nextbasin.controller.testUtils.MockFormFieldSetter;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.RegistrationService;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private RegistrationService registrationService;

    private EmployeeForm employeeForm;

    @Before
    public void setUp() {
        employeeForm = new EmployeeForm();
        employeeForm.setManagerId(-1L);
    }

    @Test
    public void testRegistrationPageSuccessResponse() throws Exception {
        mvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(model().attributeExists("managersList"));
    }

    @Test public void testRegistrationPostWithBindingResultErrorsShouldPutCorrectModelAttribute() throws Exception {
        mvc.perform(post("/registration").flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("managersList"));

        Assert.assertEquals(null, employeeForm.getManagerId());
    }

    @Test
    public void testRegistrationPostWithPasswordConfirmErrorShouldPutCorrectModelAttribute() throws Exception {
        MockFormFieldSetter.setFormFields(employeeForm);
        employeeForm.setPassword("incorrectPassword");

        mvc.perform(post("/registration").flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("managersList"))
                .andExpect(model().attributeExists("passwordConfirmedError"));
    }

    @Test
    public void testRegistrationPostWithIncorrectUsernameOrEmail() throws Exception {
        MockFormFieldSetter.setFormFields(employeeForm);

        given(registrationService.saveUser(employeeForm, "ROLE_USER")).willReturn(false);

        mvc.perform(post("/registration").flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("managersList"))
                .andExpect(model().attributeExists("usernameOrEmailError"));
    }

    @Test
    public void testSuccessRegistration() throws Exception {
        MockFormFieldSetter.setFormFields(employeeForm);

        given(registrationService.saveUser(employeeForm, "ROLE_USER")).willReturn(true);

        mvc.perform(post("/registration").flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("SuccessfulRegistration"));
    }
}
