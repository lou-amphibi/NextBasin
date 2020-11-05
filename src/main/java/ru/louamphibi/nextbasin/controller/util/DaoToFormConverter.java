package ru.louamphibi.nextbasin.controller.util;

import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.form.EmployeeForm;

public class DaoToFormConverter {

    public static EmployeeForm userToForm(Employee currentUser) {
        EmployeeForm userForm = new EmployeeForm(
                currentUser.getUsername(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getPassword(),
                currentUser.getPassword(),
                currentUser.getEmail(),
                currentUser.getManagerId()
        );

        return userForm;
    }
}
