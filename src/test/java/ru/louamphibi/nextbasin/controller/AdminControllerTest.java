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
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private AdminService adminService;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private RegistrationService registrationService;

    private Employee admin;
    private Employee employee;
    private Employee manager;
    private EmployeeForm employeeForm;
    private EmployeeTask employeeTask;


    @Before
    public void setUp() {
        admin = new Employee();
        MockFormFieldSetter.setEmployeeField(admin, 1L, 3L, "ROLE_ADMIN");

        employee = new Employee();
        MockFormFieldSetter.setEmployeeField(employee, 2L, 1L, "ROLE_USER");

        manager = new Employee();
        MockFormFieldSetter.setEmployeeField(manager, 3L, 2L, "ROLE_MANAGER");

        employeeTask = new EmployeeTask();
        MockFormFieldSetter.setEmployeeTaskFields(employeeTask);
        employee.setEmployeeTasks(Arrays.asList(employeeTask));
        employeeTask.setEmployee(employee);

        employeeForm = new EmployeeForm();
        MockFormFieldSetter.setFormFields(employeeForm);
        employeeForm.setManagerId(-1L);

        given(managerService.findById(employee.getId())).willReturn(employee);
        given(employeeService.findAllManagers()).willReturn(Arrays.asList(manager));
        given(managerService.findById(manager.getId())).willReturn(manager);
    }

    @Test
    public void adminPageSuccessRequest() throws Exception {
        mvc.perform(get("/admin").with(user(admin)).with(csrf()))
                .andExpect(view().name("admin/admin"));
    }

    @Test
    public void employeePageShouldReturnCorrectModelAttributes() throws Exception {
        given(adminService.findAllEmployees()).willReturn(Arrays.asList(employee));
        given(adminService.findEmployeesManagers(Arrays.asList(employee))).willReturn(new HashMap<Long, Employee>());

        mvc.perform(get("/admin/employees").with(user(admin)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employeeList"))
                .andExpect(model().attributeExists("managerMap"));
    }

    @Test
    public void showEmployeeUpdatePageShouldReturnCorrectModelAttributes() throws Exception {
        mvc.perform(get("/admin/employees/updateEmployee").with(user(admin)).with(csrf())
                .param("employeeId", employee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("managersList"))
                .andExpect(model().attributeExists("employeeForm"));
    }

    @Test
    public void saveUpdatedEmployeeShouldReturnCorrectModelAttributeIfBindingResultHasErrors() throws Exception {
        employeeForm.setUsername(null);

        mvc.perform(post("/admin/employees/saveEmployee").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm)
                .flashAttr("employeeId", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(model().attributeExists("managersList"));
    }

    @Test
    public void saveUpdatedEmployeeShouldReturnCorrectModelAttributeIfAdminServiceReturnFalse() throws Exception {
        given(adminService.updateEmployee(managerService.findById(employee.getId()), employeeForm)).willReturn(false);

        mvc.perform(post("/admin/employees/saveEmployee").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm)
                .flashAttr("employeeId", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(model().attributeExists("managersList"))
                .andExpect(model().attributeExists("usernameOrEmailError"));
    }

    @Test
    public void saveUpdatedEmployeeShouldReturnCorrectModelAttribute() throws Exception {
        given(adminService.updateEmployee(managerService.findById(employee.getId()), employeeForm)).willReturn(true);

        mvc.perform(post("/admin/employees/saveEmployee").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm)
                .flashAttr("employeeId", employee.getId()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/employees"));
    }

    @Test
    public void deleteEmployeeShouldReturnCorrectRedirectUrl() throws Exception {
        mvc.perform(get("/admin//employees/deleteEmployee").with(user(admin)).with(csrf())
                .param("employeeId", employee.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/employees"));
    }

    @Test
    public void showManagerTasksShouldReturnCorrectRedirectUrlAndModelAttribute() throws Exception {
        given(taskService.findAllTaskByManagerId(manager.getId())).willReturn(employee.getEmployeeTasks());

        mvc.perform(get("/admin/employees/managerTasks").with(user(admin)).with(csrf())
                .param("managerId", manager.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskList"))
                .andExpect(model().attributeExists("currManager"));
    }

    @Test
    public void deleteTaskShouldReturnCorrectRedirectUrl() throws Exception {
        mvc.perform(get("/admin/employees/deleteTask").with(user(admin)).with(csrf())
                .param("taskId", employeeTask.getId().toString())
                .param("managerId", manager.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/employees/managerTasks?managerId=" + manager.getId()));
    }

    @Test
    public void managerPageShouldReturnCorrectModelAttribute() throws Exception {
        mvc.perform(get("/admin/managers").with(user(admin)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("managerList"))
                .andExpect(model().attributeExists("taskService"))
                .andExpect(model().attributeExists("adminService"));
    }

    @Test
    public void showManagerUpdatePageShouldReturnCorrectModelAttribute() throws Exception {
        mvc.perform(get("/admin/managers/updateManager").with(user(admin)).with(csrf())
                .param("managerId", manager.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currManager"))
                .andExpect(model().attributeExists("employeeForm"));
    }

    @Test
    public void saveUpdatedManagerShouldReturnCorrectModelAttributeIfBindingResultHasErrors() throws Exception {
        employeeForm.setUsername(null);

        mvc.perform(post("/admin/managers/saveManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm)
                .flashAttr("managerId", manager.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currManager"))
                .andExpect(model().attributeExists("employeeForm"));
    }

    @Test
    public void saveUpdatedManagerEmployeeShouldReturnCorrectModelAttributeIfAdminServiceReturnFalse() throws Exception {
        given(adminService.updateEmployee(managerService.findById(manager.getId()), employeeForm)).willReturn(false);

        mvc.perform(post("/admin/managers/saveManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm)
                .flashAttr("managerId", manager.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currManager"))
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(model().attributeExists("usernameOrEmailError"));
    }

    @Test
    public void saveUpdatedManagerShouldReturnCorrectModelAttribute() throws Exception {
        given(adminService.updateEmployee(managerService.findById(manager.getId()), employeeForm)).willReturn(true);

        mvc.perform(post("/admin/managers/saveManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm)
                .flashAttr("managerId", manager.getId()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/managers"));
    }

    @Test
    public void deleteManagerShouldReturnCorrectRedirectUrl() throws Exception {
        mvc.perform(get("/admin/managers/deleteManager").with(user(admin)).with(csrf())
                .param("managerId", manager.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/managers"));
    }

    @Test
    public void addNewManagerShouldReturnCorrectModelAttribute() throws Exception {
        mvc.perform(get("/admin/addNewManager").with(user(admin)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(view().name("admin/new-manager-form"));
    }

    @Test
    public void registrationManagerShouldReturnCorrectModelAttributeIfBindingResultHasErrors() throws Exception {
        employeeForm.setUsername(null);

        mvc.perform(post("/admin/registrationManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("admin/new-manager-form"));
    }

    @Test
    public void registrationManagerShouldReturnCorrectModelErrorAttributeIfPasswordsNotMatch() throws Exception {
        employeeForm.setPassword("incorrectPassword");

        mvc.perform(post("/admin/registrationManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordConfirmedError"))
                .andExpect(view().name("admin/new-manager-form"));
    }

    @Test
    public void registrationManagerShouldReturnCorrectModelErrorAttributeIfRegistrationServiceReturnFalse() throws Exception {
        given(registrationService.saveUser(employeeForm, "ROLE_MANAGER")).willReturn(false);

        mvc.perform(post("/admin/registrationManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("usernameOrEmailError"))
                .andExpect(view().name("admin/new-manager-form"));
    }

    @Test
    public void registrationManagerShouldReturnSuccessModelAttributeIfRequestHasNoErrors() throws Exception {
        given(registrationService.saveUser(employeeForm, "ROLE_MANAGER")).willReturn(true);
        given(taskService.findCountOfManagerTask(manager.getId())).willReturn(1L);

        mvc.perform(post("/admin/registrationManager").with(user(admin)).with(csrf())
                .flashAttr("employeeForm", employeeForm))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("SuccessfulRegistration"))
                .andExpect(model().attributeExists("managerList"))
                .andExpect(model().attributeExists("taskService"))
                .andExpect(model().attributeExists("adminService"))
                .andExpect(view().name("admin/managers"));
    }

    @Test
    public void secureRedirectTest() throws Exception {
        mvc.perform(get("/login").with(user(admin)).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void requestForRegistrationPageShouldReturnForbiddenErrorIfUserAuth() throws Exception {
        mvc.perform(get("/registration").with(user(admin)).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
