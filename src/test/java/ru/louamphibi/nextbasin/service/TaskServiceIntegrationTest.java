package ru.louamphibi.nextbasin.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.form.EmployeeTaskForm;
import ru.louamphibi.nextbasin.repositoty.TaskRepo;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class TaskServiceIntegrationTest {

    @TestConfiguration
    static class TaskServiceTestContextConfiguration {
        @Bean
        public TaskService taskService() {
            return new TaskService();
        }
    }

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepo taskRepo;

    private EmployeeTaskForm employeeTaskForm;

    private EmployeeTask employeeTask;

    private final Long taskId = 1L;
    private final Long nlpExTaskId = 2L;
    private final String testTaskText = "test text";

    @Before
    public void setUp() {
        employeeTaskForm = new EmployeeTaskForm();
        employeeTaskForm.setText(testTaskText);
        employeeTaskForm.setStartDateTime(LocalDateTime.now());
        employeeTaskForm.setEndDateTime(LocalDateTime.now());

        employeeTask = new EmployeeTask();
        employeeTask.setId(taskId);

        Mockito.when(taskRepo.findById(taskId)).thenReturn(Optional.of(employeeTask));
    }

    @Test
    public void saveTaskWithEmployeeEqualsNullShouldReturnFalse() {
        Assert.assertEquals(false, taskService.saveTask(employeeTaskForm, null));
    }

    @Test
    public void saveTaskWithEmployeeNotEqualsNullShouldReturnTrue() {
        Assert.assertEquals(true, taskService.saveTask(employeeTaskForm, new Employee()));
    }

    @Test(expected = NullPointerException.class)
    public void findByIdWhenTaskNotFoundShouldThrowNullPointerException() {
        taskService.findById(nlpExTaskId);
    }

    @Test
    public void findByIdShouldReturnExpectedTask() {
        Assert.assertEquals(employeeTask, taskService.findById(taskId));
    }

    @Test
    public void changeTaskWhenStatusTrueStatusShouldBeSetToFalse() {
        employeeTask.setStatus(true);

        taskService.changeTaskStatus(employeeTask);

        Assert.assertEquals(false, employeeTask.isStatus());
    }

    @Test
    public void changeTaskWhenStatusFalseStatusShouldBeSetToTrue() {
        employeeTask.setStatus(false);

        taskService.changeTaskStatus(employeeTask);

        Assert.assertEquals(true, employeeTask.isStatus());
    }

    @Test
    public void updateTaskShouldUpdateEmployeeTaskFieldAndSerStatusToTrue() {
        taskService.updateTask(employeeTaskForm, employeeTask);

        Assert.assertEquals(testTaskText, employeeTask.getText());
        Assert.assertEquals(employeeTaskForm.getStartDateTime(), employeeTask.getStartDateTime());
        Assert.assertEquals(employeeTaskForm.getEndDateTime(), employeeTask.getEndDateTime());
        Assert.assertEquals(true, employeeTask.isStatus());
    }
}
