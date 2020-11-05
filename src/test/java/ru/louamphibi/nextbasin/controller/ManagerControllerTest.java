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
import ru.louamphibi.nextbasin.form.EmployeeTaskForm;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.ManagerService;
import ru.louamphibi.nextbasin.service.TaskService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ManagerController.class)
public class ManagerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private TaskService taskService;

    @MockBean
    private ManagerService managerService;

    private Employee manager;
    private Employee attachEmployee;
    private Employee freeEmployee;
    private EmployeeTask employeeTask;
    private EmployeeTaskForm employeeTaskForm;


    @Before
    public void setUp() {
        manager = new Employee();
        MockFormFieldSetter.setEmployeeField(manager, 1L, 2L, "ROLE_MANAGER");

        employeeTask = new EmployeeTask();
        MockFormFieldSetter.setEmployeeTaskFields(employeeTask);

        attachEmployee = new Employee();
        attachEmployee.setId(2L);
        attachEmployee.setEmployeeTasks(Arrays.asList(employeeTask));

        freeEmployee = new Employee();
        freeEmployee.setEmployeeTasks(Arrays.asList(employeeTask));

        employeeTaskForm = new EmployeeTaskForm();
        employeeTaskForm.setText("test");
        employeeTaskForm.setStartDateTime(LocalDateTime.now().plusDays(1));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().plusDays(2));

        given(managerService.findById(attachEmployee.getId())).willReturn(attachEmployee);
        given(taskService.findById(employeeTask.getId())).willReturn(employeeTask);
    }


    @Test
    public void employeeListShouldReturnCorrectModelAttribute() throws Exception {
        given(managerService.findAllEmployeesByManagerId(manager.getId())).willReturn(Arrays.asList(attachEmployee));
        given(managerService.findFreeEmployees()).willReturn(Arrays.asList(freeEmployee));

        mvc.perform(get("/manager").with(user(manager)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employeeList"))
                .andExpect(model().attributeExists("freeEmployeeList"));
    }

    @Test
    public void showEmployeeTaskShouldReturnCorrectModelAttribute() throws Exception {
        given(managerService.findEmployeeTask(attachEmployee.getId())).willReturn(attachEmployee.getEmployeeTasks());

        mvc.perform(get("/manager/showEmployeeTasks").with(user(manager)).with(csrf())
                .param("employeeId", attachEmployee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("taskList"));
    }

    @Test
    public void addTaskShouldReturnCorrectModelAttribute() throws Exception {
        mvc.perform(get("/manager/showEmployeeTasks/formForAddTasks").with(user(manager)).with(csrf())
                .param("employeeId", attachEmployee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskForm"))
                .andExpect(model().attributeExists("currEmployee"));

    }

    @Test
    public void saveTaskShouldReturnCorrectModelAttributeIfBindingResultHasErrors() throws Exception {
        mvc.perform(post("/manager/showEmployeeTasks/saveTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", new EmployeeTaskForm())
                .flashAttr("employeeId", attachEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"));
    }

    @Test
    public void saveTaskShouldReturnTimeErrorInModelAttributeIfTaskStartTimeIsAfterEndTaskTime() throws Exception {
        employeeTaskForm.setStartDateTime(LocalDateTime.now().plusDays(1));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/manager/showEmployeeTasks/saveTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("timeError"));
    }

    @Test
    public void saveTaskShouldReturnTimeErrorInModelAttributeIfTaskEndTimeIsBeforeLocalDateTimeNow() throws Exception {
        employeeTaskForm.setStartDateTime(LocalDateTime.now().minusDays(2));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/manager/showEmployeeTasks/saveTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("timeError"));
    }

    @Test
    public void saveTaskShouldReturnTimeErrorInModelAttributeIfTaskStartTimeIsBeforeLocalDateTimeNow() throws Exception {
        employeeTaskForm.setStartDateTime(LocalDateTime.now().minusDays(2));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().plusDays(2));

        mvc.perform(post("/manager/showEmployeeTasks/saveTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("timeError"));
    }

    @Test
    public void saveTaskShouldReturnCorrectErrorModelAttributeIfTaskServiceReturnFalse() throws Exception {
        given(taskService.saveTask(employeeTaskForm, attachEmployee)).willReturn(false);

        mvc.perform(post("/manager/showEmployeeTasks/saveTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskSaveError"))
                .andExpect(model().attributeExists("currEmployee"));
    }

    @Test
    public void saveTaskShouldReturnCorrectRedirectUrl() throws Exception {
        given(taskService.saveTask(employeeTaskForm, attachEmployee)).willReturn(true);

        mvc.perform(post("/manager/showEmployeeTasks/saveTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/manager/showEmployeeTasks?employeeId=" + attachEmployee.getId()));
    }

    @Test
    public void updateTaskStatusShouldReturnCorrectModelAttributeAndRedirectUrl() throws Exception {
        mvc.perform(get("/manager/updateTaskStatus").with(user(manager)).with(csrf())
                .param("taskId", employeeTask.getId().toString())
                .param("employeeId", attachEmployee.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(model().attributeExists("employeeId"))
                .andExpect(redirectedUrl("/manager/showEmployeeTasks?employeeId=" + attachEmployee.getId()));
    }

    @Test
    public void confirmTaskShouldReturnCorrectModelAttributeAndRedirectUrl() throws Exception {
        mvc.perform(get("/manager/confirmTask").with(user(manager)).with(csrf())
                .param("taskId", employeeTask.getId().toString())
                .param("employeeId", attachEmployee.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(model().attributeExists("employeeId"))
                .andExpect(redirectedUrl("/manager/showEmployeeTasks?employeeId=" + attachEmployee.getId()));
    }

    @Test
    public void attachEmployeeShouldReturnCorrectRedirectUrl() throws Exception {
        mvc.perform(get("/manager/attachEmployee").with(user(manager)).with(csrf())
                .param("employeeId", attachEmployee.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/manager"));
    }

    @Test
    public void unpinEmployeeShouldReturnCorrectRedirectUrl() throws Exception {
        mvc.perform(get("/manager/unpinEmployee").with(user(manager)).with(csrf())
                .param("employeeId", attachEmployee.getId().toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/manager"));
    }

    @Test
    public void  showFormForUpdateTaskShouldReturnCorrectModelAttribute() throws Exception {
        mvc.perform(get("/manager/showEmployeeTasks/formForUpdateTask").with(user(manager)).with(csrf())
            .param("taskId", employeeTask.getId().toString())
            .param("employeeId", attachEmployee.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("currEmployee"))
            .andExpect(model().attributeExists("taskForm"))
            .andExpect(model().attributeExists("currTask"));

    }

    @Test
    public void updateTaskShouldReturnCorrectModelAttributeIfBindingResultHasErrors() throws Exception {
        mvc.perform(post("/manager/showEmployeeTasks/updateTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", new EmployeeTaskForm())
                .flashAttr("employeeId", attachEmployee.getId())
                .flashAttr("taskId", employeeTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("taskForm"))
                .andExpect(model().attributeExists("currTask"))
                .andExpect(view().name("manager/update-task-form"));
    }

    @Test
    public void updateTaskShouldReturnTimeErrorInModelAttributeIfTaskEndTimeIsBeforeLocalDateTimeNow() throws Exception {
        employeeTaskForm.setStartDateTime(LocalDateTime.now().plusDays(1));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/manager/showEmployeeTasks/updateTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId())
                .flashAttr("taskId", employeeTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("timeError"))
                .andExpect(model().attributeExists("taskForm"))
                .andExpect(model().attributeExists("currTask"))
                .andExpect(view().name("manager/update-task-form"));
    }

    @Test
    public void updateTaskShouldReturnTimeErrorInModelAttributeIfTaskStartTimeIsAfterEndTaskTime() throws Exception {
        employeeTaskForm.setStartDateTime(LocalDateTime.now().minusDays(2));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/manager/showEmployeeTasks/updateTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId())
                .flashAttr("taskId", employeeTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("timeError"))
                .andExpect(model().attributeExists("taskForm"))
                .andExpect(model().attributeExists("currTask"))
                .andExpect(view().name("manager/update-task-form"));
    }

    @Test
    public void updateTaskShouldReturnTimeErrorInModelAttributeIfTaskStartTimeIsBeforeLocalDateTimeNow() throws Exception {
        employeeTaskForm.setStartDateTime(LocalDateTime.now().minusDays(2));
        employeeTaskForm.setEndDateTime(LocalDateTime.now().plusDays(2));

        mvc.perform(post("/manager/showEmployeeTasks/updateTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId())
                .flashAttr("taskId", employeeTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currEmployee"))
                .andExpect(model().attributeExists("timeError"))
                .andExpect(model().attributeExists("taskForm"))
                .andExpect(model().attributeExists("currTask"))
                .andExpect(view().name("manager/update-task-form"));
    }

    @Test
    public void updateTaskShouldReturnCorrectRedirectUrl() throws Exception {
        mvc.perform(post("/manager/showEmployeeTasks/updateTask").with(user(manager)).with(csrf())
                .flashAttr("taskForm", employeeTaskForm)
                .flashAttr("employeeId", attachEmployee.getId())
                .flashAttr("taskId", employeeTask.getId()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/manager/showEmployeeTasks?employeeId=" + attachEmployee.getId()));
    }

    @Test
    public void otherManagersShouldReturnCorrectModelAttribute() throws Exception {
        given(managerService.findOtherManagers(manager.getId())).willReturn(Arrays.asList(manager));

        mvc.perform(get("/manager/otherManagers").with(user(manager)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("managerList"));
    }

    @Test
    public void secureRedirectTest() throws Exception {
        mvc.perform(get("/login").with(user(manager)).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void requestForRegistrationPageShouldReturnForbiddenErrorIfUserAuth() throws Exception {
        mvc.perform(get("/registration").with(user(manager)).with(csrf()))
                .andExpect(status().isForbidden());
    }

}
