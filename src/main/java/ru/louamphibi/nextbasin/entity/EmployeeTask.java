package ru.louamphibi.nextbasin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nb_employee_task")
@ToString(exclude = "employee")
public class EmployeeTask {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "text")
    @Getter
    @Setter
    private String text;

    @Column(name = "start_date")
    @Getter
    @Setter
    private LocalDateTime startDateTime;

    @Column(name = "end_date")
    @Getter
    @Setter
    private LocalDateTime endDateTime;

    @Column(name = "status")
    @Getter
    @Setter
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @Getter
    @Setter
    private Employee employee;

    public EmployeeTask() {}
}
