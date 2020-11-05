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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.repositoty.UserRepo;

@RunWith(SpringRunner.class)
public class RegistrationServiceIntegrationTest {

    @TestConfiguration
    static class RegistrationServiceTestContextConfiguration {
        @Bean
        public RegistrationService passwordService() {
            return new RegistrationService();
        }
    }

    @Autowired
    private RegistrationService registrationService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private final String role = "ROLE_USER";
    private final String username = "username";
    private final String email = "email";

    private Employee testEmployee;
    private EmployeeForm testEmployeeForm;

    @Before
    public void setUp() {
        testEmployee = new Employee();

        testEmployeeForm = new EmployeeForm();
        testEmployeeForm.setUsername(username);
        testEmployeeForm.setEmail(email);
    }

    @Test
    public void saveUserWithEmployeeFromDBEqualsNullAndEmployeeWithSameEmailEqualsNullShouldReturnTrue() {
        Assert.assertEquals(true, registrationService.saveUser(testEmployeeForm, role));
    }

    @Test
    public void saveUserWithEmployeeFromDbNotEqualNullShouldReturnFalse() {
        Mockito.when(userRepo.findByUsername(username)).thenReturn(testEmployee);

        Assert.assertEquals(false, registrationService.saveUser(testEmployeeForm, role));
    }

    @Test
    public void saveUserWithEmployeeWithSameEmailNotEqualsNullShouldReturnFalse() {
        Mockito.when(userRepo.findByEmail(email)).thenReturn(testEmployee);

        Assert.assertEquals(false, registrationService.saveUser(testEmployeeForm, role));
    }
}
