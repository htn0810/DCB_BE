package com.bosch.digicore.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class LdapUserDTO {

    private String ntid;

    private String firstName;

    private String lastName;

    private String displayName;

    private String email;

    private List<String> department;

    private Instant createdDate;

    private Instant lastModifiedDate;

    private String corporate;

}
