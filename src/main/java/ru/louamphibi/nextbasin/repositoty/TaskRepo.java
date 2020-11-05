package ru.louamphibi.nextbasin.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import java.util.List;

public interface TaskRepo extends JpaRepository<EmployeeTask, Long> {

    List<EmployeeTask> findByEmployeeIdOrderByEndDateTime(Long id);

    @Query(value = "select et.id, et.start_date, et.end_date, et.status, et.text, et.employee_id " +
            "from nb_employee_task et left join nb_employee e " +
            "on et.employee_id=e.id left join nb_employee ee on ee.id=e.id where ee.manager_id = :id" +
            " order by et.end_date", nativeQuery = true)
    List<EmployeeTask> findAllTaskByManagerId(Long id);

    @Query(value = "select count(*) as tasks from nb_employee_task et left join nb_employee e on " +
            "et.employee_id=e.id left join nb_employee ee on ee.id=e.id where ee.manager_id = :id", nativeQuery = true)
    Long findCountOfManagerTasks(Long id);
}
