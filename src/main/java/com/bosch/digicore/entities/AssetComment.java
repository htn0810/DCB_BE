package com.bosch.digicore.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "asset_comments")
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetComment extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "asset_id")
    private Long assetId;
}
