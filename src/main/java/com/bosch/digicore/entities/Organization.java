package com.bosch.digicore.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Organization extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "org_url")
    private String orgUrl;

    @ManyToMany
    @JoinTable(
            name = "organizations_owners",
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id")
    )
    private List<Employee> owners;

    @Column(name = "deleted")
    private boolean deleted = false;

    public Organization(String uuid, String name, String description, String orgUrl, List<Employee> owners) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.orgUrl = orgUrl;
        this.owners = owners;
    }
}
