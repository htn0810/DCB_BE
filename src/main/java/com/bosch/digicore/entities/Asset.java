package com.bosch.digicore.entities;

import com.bosch.digicore.constants.AssetStatus;
import com.bosch.digicore.constants.AssetType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "assets")
@Data
@EqualsAndHashCode(callSuper = true)
public class Asset extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "assets_owners",
            joinColumns = @JoinColumn(name = "asset_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id")
    )
    private List<Employee> owners;

    @ManyToMany
    @JoinTable(
            name = "assets_developers",
            joinColumns = @JoinColumn(name = "asset_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id")
    )
    private List<Employee> developers;

    @Column(name = "app_url")
    private String appUrl;

    @Column(name = "repo_url")
    private String repoUrl;

    @JsonIgnore
    @Column(name = "other_url")
    private String otherUrls;

    private Instant publishedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssetStatus status = AssetStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AssetType type;

    @ManyToMany
    @JoinTable(
            name = "assets_organizations",
            joinColumns = @JoinColumn(name = "asset_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private List<Organization> organizations;

    @Column(name = "deleted")
    private boolean deleted = false;
}
