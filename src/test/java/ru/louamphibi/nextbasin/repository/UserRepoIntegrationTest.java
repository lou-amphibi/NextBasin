package ru.louamphibi.nextbasin.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.Role;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.Collections;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepoIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepo userRepo;

    private final Long userRoleId = 1L;
    private final Long managerRoleId = 2L;
    private final Long adminRoleId = 3L;

    private Role roleUser = new Role(userRoleId, "ROLE_USER");
    private Role roleManager = new Role(managerRoleId, "ROLE_MANAGER");
    private Role roleAdmin = new Role(adminRoleId, "ROLE_ADMIN");

    @Test
    public void findByUsernameShouldReturnEmployee() {
        Employee employee = new Employee();
        employee.setUsername("testUser");

        entityManager.persistAndFlush(employee);

        Employee found = userRepo.findByUsername(employee.getUsername());

        Assert.assertEquals(found.getUsername(), employee.getUsername());
    }

    @Test
    public void findByManagerIdShouldReturnListOfManagersEmployee() {
        Employee employee1 = new Employee();
        employee1.setManagerId(1L);

        Employee employee2 = new Employee();
        employee2.setManagerId(2L);

        Employee employee3 = new Employee();
        employee3.setManagerId(2L);

        persistAndFlushTestEmployees(employee1, employee2, employee3);

        Object[] employees = userRepo.findByManagerIdOrderByLastNameAscFirstName(2L).toArray();

        Assert.assertArrayEquals(employees, new Employee[] {employee2, employee3});
    }

    @Test
    public void findFreeEmployeesShouldReturnEmployeesWithManagerIdEqualsNullAndRoleIdEqualsOne() {
        Employee employee1 = employeeWithDifferentRoleFactory(userRoleId);
        employee1.setManagerId(1L);

        Employee manager = employeeWithDifferentRoleFactory(managerRoleId);

        Employee employee2 = employeeWithDifferentRoleFactory(userRoleId);

        persistAndFlushTestEmployees(employee1, manager, employee2);

        Object[] employees = userRepo.findFreeEmployees().toArray();

        Assert.assertArrayEquals(employees, new Employee[] {employee2});
    }

    @Test
    public void findAllEmployeesShouldReturnAllEmployeesWithIdEqualsOne() {
        Employee employee1 = employeeWithDifferentRoleFactory(userRoleId);
        Employee manager = employeeWithDifferentRoleFactory(managerRoleId);
        Employee employee2 = employeeWithDifferentRoleFactory(userRoleId);

        persistAndFlushTestEmployees(employee1, manager, employee2);

        Object[] employees = userRepo.findFreeEmployees().toArray();

        Assert.assertArrayEquals(employees, new Employee[] {employee1, employee2});
    }

    @Test
    public void findAllManagersShouldReturnAllEmployeesWithIdEqualsTwo() {
        Employee employee1 = employeeWithDifferentRoleFactory(userRoleId);
        Employee manager1 = employeeWithDifferentRoleFactory(managerRoleId);
        Employee manager2 = employeeWithDifferentRoleFactory(managerRoleId);

        persistAndFlushTestEmployees(employee1, manager1, manager2);

        Object[] employees = userRepo.findAllManagers().toArray();

        Assert.assertArrayEquals(employees, new Employee[] {manager1, manager2});
    }

    @Test
    public void findOtherManagerShouldReturnAllEmployeesWithRoleIdEqualsTwoExceptCurrentEmployee() {
        Employee manager1 = employeeWithDifferentRoleFactory(managerRoleId);
        Employee employee = employeeWithDifferentRoleFactory(userRoleId);
        Employee manager2 = employeeWithDifferentRoleFactory(managerRoleId);
        Employee admin = employeeWithDifferentRoleFactory(adminRoleId);

        persistAndFlushTestEmployees(manager1, employee, manager2, admin);

        Object[] employees = userRepo.findOtherManagers(manager1.getId()).toArray();

        Assert.assertArrayEquals(employees, new Employee[] {manager2});
    }

    @Test
    public void findAllAdminsShouldReturnAllEmployeesWithRoleIdEqualsThree() {
        Employee employee = employeeWithDifferentRoleFactory(userRoleId);
        Employee admin = employeeWithDifferentRoleFactory(adminRoleId);
        Employee manager = employeeWithDifferentRoleFactory(managerRoleId);

        persistAndFlushTestEmployees(employee, admin, manager);

        Object[] employees = userRepo.findAllAdmins().toArray();

        Assert.assertArrayEquals(employees, new Employee[] {admin});
    }

    private void persistAndFlushTestEmployees(Employee ... employees) {
        for (int i=0; i<employees.length; i++) {
            if (employees[i].getRoles() != null)
                entityManager.persistAndFlush(employees[i].getRoles().stream().findAny().get());
            entityManager.persistAndFlush(employees[i]);
        }
    }

    private Employee employeeWithDifferentRoleFactory(Long roleId) {
        Employee  employee = new Employee();
        if (roleId == 1L) {
            employee.setRoles(Collections.singleton(roleUser));
            roleUser.setEmployees(Collections.singleton(employee));
        } else if (roleId == 2L) {
            employee.setRoles(Collections.singleton(roleManager));
            roleManager.setEmployees(Collections.singleton(employee));
        } else {
            employee.setRoles(Collections.singleton(roleAdmin));
            roleAdmin.setEmployees(Collections.singleton(employee));
        }
        return employee;
    }

}
