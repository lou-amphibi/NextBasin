package ru.louamphibi.nextbasin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "nb_role")
@ToString(exclude = "employees")
public class Role implements GrantedAuthority {

    @Id
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Employee> employees;

    public Role() {}

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
