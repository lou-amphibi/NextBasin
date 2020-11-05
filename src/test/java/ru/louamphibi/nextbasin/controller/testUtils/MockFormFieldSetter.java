package ru.louamphibi.nextbasin.controller.testUtils;

import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.entity.Role;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import java.time.LocalDateTime;
import java.util.Collections;

public class MockFormFieldSetter {

    public static void setFormFields(EmployeeForm employeeForm) {
        employeeForm.setUsername("testUsername");
        employeeForm.setFirstName("testFirstName");
        employeeForm.setLastName("testLastName");
        employeeForm.setPassword("testPassword");
        employeeForm.setPasswordConfirm("testPassword");
        employeeForm.setEmail("testEmail@mail.com");
    }

    public static void setEmployeeTaskFields(EmployeeTask employeeTask) {
        employeeTask.setId(1L);
        employeeTask.setStartDateTime(LocalDateTime.now());
        employeeTask.setEndDateTime(LocalDateTime.now());
    }

    public static void setEmployeeField(Employee employee, Long id, Long roleId, String role) {
        employee.setId(id);
        employee.setRoles(Collections.singleton(new Role(roleId, role)));
    }



}
