package ru.louamphibi.nextbasin.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.louamphibi.nextbasin.entity.Employee;
import java.util.List;

public interface UserRepo extends JpaRepository<Employee, Long> {

    List<Employee> findByManagerIdOrderByLastNameAscFirstName(Long managerId);

    Employee findByUsername(String username);

    Employee findByEmail(String email);

    @Query(value = "select e.id, e.username, e.password, " +
            "e.first_name, e.last_name, e.email, e.manager_id " +
            "from nb_employee_roles er left join" +
            " nb_employee e on e.id=er.employee_id " +
            "where e.manager_id is null and er.roles_id=1 order by e.last_name, e.first_name;", nativeQuery = true)
    List<Employee> findFreeEmployees();

    @Query(value = "select e.id, e.username, e.password, e.first_name, e.last_name, e.email, " +
            "e.manager_id from nb_employee_roles er left join nb_employee e " +
            "on e.id=er.employee_id where er.roles_id=1 order by e.last_name, e.first_name;", nativeQuery = true)
    List<Employee> findAllEmployees();

    @Query(value = "select * from nb_employee_roles er left join nb_employee e" +
            " on e.id=er.employee_id where er.roles_id=2  order by e.last_name, e.first_name;", nativeQuery = true)
    List<Employee> findAllManagers();

    @Query(value = " select * from nb_employee_roles er left join nb_employee e " +
            "on e.id=er.employee_id where er.roles_id=2 and e.id != :id order by e.last_name, e.first_name", nativeQuery = true)
    List<Employee> findOtherManagers(Long id);


    @Query(value = "select * from nb_employee_roles er left join nb_employee e" +
            " on e.id=er.employee_id where er.roles_id=3 order by e.last_name, e.first_name;", nativeQuery = true)
    List<Employee> findAllAdmins();

    Long countAllByManagerId(Long id);
}
