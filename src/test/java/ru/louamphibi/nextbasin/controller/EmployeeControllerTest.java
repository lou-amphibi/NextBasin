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
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.ManagerService;
import ru.louamphibi.nextbasin.service.TaskService;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private TaskService taskService;

    private Employee employee;
    private EmployeeTask employeeTask;


    @Before
    public void setUp() {
        employee = new Employee();
        MockFormFieldSetter.setEmployeeField(employee, 1L, 1L, "ROLE_USER");

        employeeTask = new EmployeeTask();
        MockFormFieldSetter.setEmployeeTaskFields(employeeTask);
    }

    @Test
    public void  getUserTasksPageShouldPutCorrectModelAttributeIfManagerIdEqualsNull() throws Exception {
        given(managerService.findEmployeeTask(employee.getId())).willReturn(Arrays.asList(employeeTask));

        mvc.perform(get("/user-workspace").with(user(employee)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("manager"))
                .andExpect(model().attribute("manager", hasProperty("firstName", is("None"))))
                .andExpect(model().attribute("manager", hasProperty("lastName", is("Nobody"))))
                .andExpect(model().attribute("manager", hasProperty("email", is("None@nobody.com"))))
                .andExpect(model().attributeExists("taskList"));
    }

    @Test
    public void updateTaskStatusShouldChangeTaskStatusAndReturnCorrectRedirectUrl() throws Exception {
        given(taskService.findById(employeeTask.getId())).willReturn(employeeTask);

        mvc.perform(get("/user-workspace/updateTaskStatus").with(user(employee))
                .with(csrf())
                .param("taskId", employeeTask.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/user-workspace"));
    }

    @Test
    public void allManagersShouldReturnManagerList() throws Exception {
        given(employeeService.findAllManagers()).willReturn(Arrays.asList(new Employee()));

        mvc.perform(get("/user-workspace/allManagers").with(user(employee)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("managerList"));
    }

    @Test
    public void secureRedirectTest() throws Exception {
        mvc.perform(get("/login").with(user(employee)).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void requestForRegistrationPageShouldReturnForbiddenErrorIfUserAuth() throws Exception {
        mvc.perform(get("/registration").with(user(employee)).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
