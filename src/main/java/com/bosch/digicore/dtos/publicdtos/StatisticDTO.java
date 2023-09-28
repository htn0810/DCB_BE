package com.bosch.digicore.dtos.publicdtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDTO {
    private long numberOrganizations;
    private long numberAssets;
    private long numberEmployees;
}
