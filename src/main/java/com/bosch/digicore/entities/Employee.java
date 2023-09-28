package com.bosch.digicore.entities;

import com.bosch.digicore.constants.EmployeeType;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ntid", nullable = false, unique = true)
    private String ntid;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EmployeeType type;

    @Column(name = "ldap_synchronized")
    private Boolean ldapSynchronized;

    @Column(name = "ldap_created_date")
    private Instant ldapCreatedDate;

    @Column(name = "ldap_last_modified_date")
    private Instant ldapLastModifiedDate;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employees_units",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id")
    )
    private List<Unit> units;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employees_organizations",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private List<Organization> organizations;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "deleted")
    private Boolean deleted = true;
}
