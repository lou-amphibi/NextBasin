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
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.*;

@RunWith(SpringRunner.class)
public class AdminServiceIntegrationTest {

    @TestConfiguration
    static class AdminServiceTestContextConfiguration {
        @Bean
        public AdminService adminService() {
            return new AdminService();
        }
    }

    @Autowired
    private AdminService adminService;

    @MockBean
    private UserRepo userRepo;

    private final Long testManagerId1 = 1L;
    private final Long testManagerId2 = 2L;

    private final Long testEmployeeId1 = 3L;
    private final Long testEmployeeId2 = 4L;
    private final Long testEmployeeId3 = 5L;

    private Employee manager1;
    private Employee manager2;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;

    private final String testUsername = "testUsername";
    private final String testEmail = "testEmail";

    private final String testFormFirstName = "testFormFirstName";
    private final String testFormLastName = "testFormLastName";


    @Before
    public void setUp() {
        manager1 = createTestManagers(testManagerId1, manager1);
        manager2 = createTestManagers(testManagerId2, manager2);

        manager1.setUsername(testUsername);
        manager1.setEmail(testEmail);

        employee1 = createTestEmployee(testEmployeeId1, testManagerId1, employee1);
        employee2 = createTestEmployee(testEmployeeId2, testManagerId2, employee2);
        employee3 = createTestEmployee(testEmployeeId3, null, employee3);

        Mockito.when(userRepo.findByUsername(testUsername)).thenReturn(manager1);
        Mockito.when(userRepo.findByEmail(testEmail)).thenReturn(manager1);

        List<Employee> employeesOfManager1 = Arrays.asList(employee1);
        Mockito.when(userRepo.findByManagerIdOrderByLastNameAscFirstName(manager1.getId()))
                .thenReturn(employeesOfManager1);

    }

    @Test
    public void findEmployeesManagersShouldReturnValidManagerMap() {

        List<Employee> employeeList = Arrays.asList(employee1, employee2, employee3);

        Map<Long, Employee> managerMap = adminService.findEmployeesManagers(employeeList);

        String actual = managerMap.toString();

        Map<Long, Employee> employeeMap = new HashMap<>();
        employeeMap.put(employee1.getId(), manager1);
        employeeMap.put(employee2.getId(), manager2);
        employeeMap.put(employee3.getId(), null);

        String expected = employeeMap.toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void updateEmployeeShouldUpdateEmployeeFields() {
        EmployeeForm employeeForm = new EmployeeForm();

        employeeForm.setUsername(manager1.getUsername());
        employeeForm.setEmail(manager1.getEmail());

        employeeForm.setFirstName(testFormFirstName);
        employeeForm.setLastName(testFormLastName);

        adminService.updateEmployee(manager1, employeeForm);

        Assert.assertEquals(employeeForm.getFirstName() + employeeForm.getLastName(),
                manager1.getFirstName() + manager1.getLastName());
    }

    @Test
    public void deleteManagerShouldSetManagerIdAttachesEmployeeToNull() {
        adminService.deleteManager(manager1.getId());

        Assert.assertEquals(null, employee1.getManagerId());
    }

    private Employee createTestEmployee(Long employeeId, Long managerId, Employee employee) {
        employee = new Employee();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        return employee;
    }

    private Employee createTestManagers(Long managerId, Employee manager) {
        manager = new Employee();
        manager.setId(managerId);
        Mockito.when(userRepo.findById(manager.getId())).thenReturn(Optional.of(manager));

        return manager;
    }
}
