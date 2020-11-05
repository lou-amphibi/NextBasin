package ru.louamphibi.nextbasin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "nb_employee")
@ToString(exclude = "employeeTasks")
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "username")
    @Getter
    @Setter
    private String username;

    @Column(name = "first_name")
    @Getter
    @Setter
    private String firstName;

    @Column(name = "last_name")
    @Getter
    @Setter
    private String lastName;

    @Column(name = "password")
    @Getter
    @Setter
    private String password;

    @Column(name = "email")
    @Getter
    @Setter
    private String email;

    @Column(name = "manager_id")
    @Getter
    @Setter
    private Long managerId;

    @OneToMany( mappedBy = "employee",cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<EmployeeTask> employeeTasks;

    @ManyToMany(fetch = FetchType.EAGER)
    @Column(name = "roles")
    @Getter
    @Setter
    private Set<Role> roles;

    public Employee() {

    }

    public Employee(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
