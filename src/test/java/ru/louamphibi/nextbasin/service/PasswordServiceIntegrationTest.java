package ru.louamphibi.nextbasin.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.repositoty.ConfirmationTokenRepo;
import ru.louamphibi.nextbasin.repositoty.UserRepo;

@RunWith(SpringRunner.class)
public class PasswordServiceIntegrationTest {

    @TestConfiguration
    static class PasswordServiceTestContextConfiguration {
        @Bean
        public PasswordService passwordService() {
            return new PasswordService();
        }
    }

    @Autowired
    private PasswordService passwordService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ConfirmationTokenRepo confirmationTokenRepo;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private final String testPassword = "testPassword";

    private final String newPassword = "newPassword";

    @Test
    public void changePasswordShouldChangeEmployeePassword() {
        Employee employee = new Employee();

        employee.setPassword(testPassword);

        passwordService.changePassword(employee, newPassword);

        Assert.assertEquals(true, employee.getPassword() != testPassword);
    }
}
