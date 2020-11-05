package ru.louamphibi.nextbasin.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.repositoty.TaskRepo;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskRepoIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepo taskRepo;

    private final Long testManagerId = 2L;

    private EmployeeTask employeeTask1;
    private EmployeeTask employeeTask2;
    private EmployeeTask employeeTask3;

    @Before
    public void setUp(){
        employeeTask1 = new EmployeeTask();
        employeeTask2 = new EmployeeTask();
        employeeTask3 = new EmployeeTask();

        persistAndFlashEmployeeAndTask(testManagerId, employeeTask1, employeeTask2, employeeTask3);
    }

    @Test
    public void findAllTaskByManagerIdShouldReturnAllEmployeeTaskAttachedManagerEmployee() {

        Object[] tasks = taskRepo.findAllTaskByManagerId(testManagerId).toArray();

        Assert.assertArrayEquals(tasks, new EmployeeTask[] {employeeTask1, employeeTask2});
    }

    @Test
    public void findCountOfManagerTasksShouldReturnCorrectValue() {
        Long countOfTasks = taskRepo.findCountOfManagerTasks(testManagerId);
        Long expected = 2L;

        Assert.assertEquals(expected, countOfTasks);
    }

    private void persistAndFlashEmployeeAndTask(Long testManagerId, EmployeeTask ... employeeTask) {
        for (int i=0; i<employeeTask.length; i++) {

            Employee employeeForTask = new Employee();

            if (i<testManagerId)
                employeeForTask.setManagerId(testManagerId);

            List<EmployeeTask> tempTaskList = new ArrayList<>();

            tempTaskList.add(employeeTask[i]);

            employeeForTask.setEmployeeTasks(tempTaskList);

            employeeTask[i].setEmployee(employeeForTask);

            entityManager.persistAndFlush(employeeForTask);
            entityManager.persistAndFlush(employeeTask[i]);
        }

    }
}
