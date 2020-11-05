package ru.louamphibi.nextbasin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "nb_confirmation_token")
@ToString
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name= "confirmation_token")
    @Getter
    @Setter
    private String confirmationToken;

    @OneToOne(targetEntity = Employee.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "employee_id")
    @Getter
    @Setter
    private Employee employee;

    public ConfirmationToken() {}

    public ConfirmationToken(Employee employee) {
        this.employee = employee;
        confirmationToken = UUID.randomUUID().toString();
    }
}
