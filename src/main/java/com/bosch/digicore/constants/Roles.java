package com.bosch.digicore.constants;

public class Roles {

    private Roles() {}

    // roles
    public static final String ROLE_SUPER_ADMIN = "ROLE_DIGICORE_SUPER_ADMIN";
    public static final String ROLE_ORG_ADMIN = "ROLE_DIGICORE_ORG_ADMIN";
    public static final String ROLE_USER = "ROLE_DIGICORE_USER";

    // spring security eval expressions
    private static final String HAS_ROLE_PREFIX = "hasRole('";
    private static final String HAS_ROLE_SUFFIX = "')";
    private static final String HAS_ROLE_OR = " or ";

    public static final String HAS_ROLE_SUPER_ADMIN = HAS_ROLE_PREFIX + ROLE_SUPER_ADMIN + HAS_ROLE_SUFFIX;
    public static final String HAS_ROLE_ORG_ADMIN = HAS_ROLE_PREFIX + ROLE_ORG_ADMIN + HAS_ROLE_SUFFIX;
    public static final String HAS_ROLE_SUPER_ADMIN_OR_ORG_ADMIN = HAS_ROLE_SUPER_ADMIN + HAS_ROLE_OR + HAS_ROLE_ORG_ADMIN;
}
