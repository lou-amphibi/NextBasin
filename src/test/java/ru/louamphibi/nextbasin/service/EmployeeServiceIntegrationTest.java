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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.repositoty.UserRepo;

@RunWith(SpringRunner.class)
public class EmployeeServiceIntegrationTest {

    @TestConfiguration
    static class EmployeeServiceTestContextConfiguration {
        @Bean
        public EmployeeService employeeService() {
            return new EmployeeService();
        }
    }

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private UserRepo userRepo;

    private final String username = "test";

    @Before
    public void setUp() {
        Employee employee = new Employee();
        employee.setUsername(username);

        Mockito.when(userRepo.findByUsername(employee.getUsername())).thenReturn(employee);
    }

    @Test
    public void loadByUsernameShouldLoadEmployeeWhenUsernameIsValid() {
        UserDetails found = employeeService.loadUserByUsername(username);

        Assert.assertEquals(found.getUsername(), username);
    }

}
