package com.bosch.digicore.constants;

import lombok.Getter;

@Getter
public enum LdapAttribute {
    CN("cn"),
    CO("co"),
    DEPARTMENT("department"),
    DISPLAY_NAME("displayName"),
    GIVEN_NAME("givenName"),
    MAIL("mail"),
    SN("sn"),
    WHEN_CREATED("whenCreated"),
    WHEN_CHANGED("whenChanged"),
    OBJECT_CLASS("objectClass"),
    PERSON("person"),
    VIETNAM("Vietnam");

    final String name;

    LdapAttribute(String name) {
        this.name = name;
    }
}
