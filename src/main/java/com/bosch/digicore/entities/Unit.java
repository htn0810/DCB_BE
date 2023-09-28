package com.bosch.digicore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "units")
@Setter
@Getter
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Unit parentUnit;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "units_oragnizations",
            joinColumns = @JoinColumn(name = "unit_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id"))
    private List<Organization> organizations;

    @Column(name = "last_synchronized_sub_units_date")
    private Instant lastSynchronizedSubUnitsDate;

    @Column(name = "last_synchronized_employees_date")
    private Instant lastSynchronizedEmployeesDate;

    @Column(name = "total_employee_of_each_level")
    private Integer totalEmployeesOfEachLevel;

    @Column(name = "fixedterm_employees_count")
    private Integer fixedtermEmployeesCount;

    @Column(name = "external_employees_count")
    private Integer externalEmployeesCount;

    @Column(name = "internal_employees_count")
    private Integer internalEmployeesCount;

    @Column(name = "deleted")
    private boolean deleted = false;
}
