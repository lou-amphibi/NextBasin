package ru.louamphibi.nextbasin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.louamphibi.nextbasin.controller.testUtils.MockFormFieldSetter;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.Role;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.AdminService;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.ManagerService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private AdminService adminService;

    private Employee admin1;
    private Employee admin2;
    private EmployeeForm employeeForm;

    private final Long testId = 1L;

    @Before
    public void setUp() {
        admin1 = new Employee();
        admin1.setId(testId);

        admin2 = new Employee();

        employeeForm = new EmployeeForm();
        MockFormFieldSetter.setFormFields(employeeForm);

        given(managerService.findById(admin1.getId())).willReturn(admin1);
    }

    @Test
    public void testMainPageSuccessResponse() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    public void testAdministrationPageSuccessResponse() throws Exception {
        List<Employee> adminList = Arrays.asList(admin1, admin2);

        given(adminService.findAllAdmins()).willReturn(Arrays.asList(admin1, admin2));

        mvc.perform(get("/administration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("adminList"))
                .andExpect(model().attribute("adminList", adminList));
    }

    @Test
    public void editProfileShouldPutCorrectModelAttribute() throws Exception {
        mvc.perform(get("/profile").with(user(admin1)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attributeExists("currUser"));
    }

    @Test
    public void updateProfileShouldPutCorrectAttributeIfBindingResultHasErrors() throws Exception {
        mvc.perform(post("/updateProfile").with(user(admin1)).with(csrf())
                .flashAttr("userForm", new EmployeeForm()).flashAttr("userId", admin1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attributeExists("currUser"));
    }

    @Test
    public void updateProfileShouldPutCorrectAttributeIfUserWithSameNameAlreadyExist() throws Exception {
        given(adminService.updateEmployee(admin1, employeeForm)).willReturn(false);

        mvc.perform(post("/updateProfile").with(user(admin1)).with(csrf())
                .flashAttr("userForm", employeeForm).flashAttr("userId", admin1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attributeExists("currUser"))
                .andExpect(model().attributeExists("usernameOrEmailError"));
    }

    @Test
    public void updateProfileShouldRedirectAtMainPageIfSuccess() throws Exception {
        given(adminService.updateEmployee(admin1, employeeForm)).willReturn(true);

        admin1.setRoles(Collections.singleton(new Role(3L, "ROLE_ADMIN")));

        mvc.perform(post("/updateProfile").with(user(admin1)).with(csrf())
                .flashAttr("userForm", employeeForm).flashAttr("userId", admin1.getId()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

}
