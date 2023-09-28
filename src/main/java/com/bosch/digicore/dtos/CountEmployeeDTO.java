package com.bosch.digicore.dtos;

import com.bosch.digicore.constants.EmployeeType;
import lombok.Data;

import java.util.Map;

@Data
public class CountEmployeeDTO {

    long all;
    long internal;
    long external;
    long fixedTerm;

    public CountEmployeeDTO(long internal, long external, long fixedTerm) {
        this.internal = internal;
        this.external = external;
        this.fixedTerm = fixedTerm;
        this.all = internal + external + fixedTerm;
    }

    public CountEmployeeDTO(Map<EmployeeType, Long> countMap) {
        this.internal = countMap.get(EmployeeType.INTERNAL) != null ? countMap.get(EmployeeType.INTERNAL) : 0;
        this.external = countMap.get(EmployeeType.EXTERNAL) != null ? countMap.get(EmployeeType.EXTERNAL) : 0;
        this.fixedTerm = countMap.get(EmployeeType.FIXED_TERM) != null ? countMap.get(EmployeeType.FIXED_TERM) : 0;
        this.all = this.internal + this.external + this.fixedTerm;
    }
}
