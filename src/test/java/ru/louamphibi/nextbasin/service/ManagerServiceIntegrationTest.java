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
import ru.louamphibi.nextbasin.repositoty.TaskRepo;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class ManagerServiceIntegrationTest {

    @TestConfiguration
    static class ManagerServiceTestContextConfiguration {
        @Bean
        public ManagerService managerService() {
            return new ManagerService();
        }
    }

    @Autowired
    private ManagerService managerService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private TaskRepo taskRepo;

    private Employee employee;

    private Long testNLPId = 48L;
    private Long testEmployeeId = 1L;
    private Long testManagerId = 2L;

    @Before
    public void setUp() {
        employee = new Employee();
        employee.setId(testEmployeeId);

        Mockito.when(userRepo.findById(employee.getId())).thenReturn(Optional.of(employee));
    }

    @Test(expected = NullPointerException.class)
    public void findByIdShouldThrowNLPWhenEmployeeNotFound() {
        managerService.findById(testNLPId);
    }

    @Test
    public void attachEmployeeShouldSetManagerIdToInputValue() {
        managerService.attachEmployee(testEmployeeId, testManagerId);

        Assert.assertEquals(testManagerId, employee.getManagerId());
    }

    @Test
    public void unpinEmployeeShouldSetManagerIdToNull() {
        managerService.unpinEmployee(testEmployeeId);

        Assert.assertEquals(null, employee.getManagerId());
    }


}
