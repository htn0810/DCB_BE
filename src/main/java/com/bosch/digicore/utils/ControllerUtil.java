package com.bosch.digicore.utils;

import com.bosch.digicore.exceptions.BadRequestException;

import java.util.Arrays;
import java.util.Objects;

public class ControllerUtil {

    private ControllerUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkParametersWhenOnlyNeedOne(Object... parameters) {
        long count = Arrays.stream(parameters).filter(Objects::nonNull).count();
        if (count > 1) {
            throw new BadRequestException("Only need 1 parameter");
        }
    }
}
