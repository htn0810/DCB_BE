package com.bosch.digicore.cronjob;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.entities.Unit;
import com.bosch.digicore.repositories.UnitRepository;
import com.bosch.digicore.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnitCronJob {

    private final EmployeeService employeeService;
    private final UnitRepository unitRepository;

    @Scheduled(cron = "0 23 * * 1 4") //At 23h00, on every Monday and Thursday
    @Transactional
    public void countTotalNumOfEmpsInUnit() {

        final Set<Long> listEmployeeId = new HashSet<>();
        List<Unit> units = new ArrayList<>();

        log.debug("CRON-JOB COUNTING TOTAL EMPLOYEE BY TYPE FOR ALL UNITS FOLLOWING RECURSIVE LOGIC  _  " +
                    "START. . . .countingTotalNumberOfEmployeesOfEachUnit func()");
        unitRepository.findAll().forEach(unit -> {
            unit.setFixedtermEmployeesCount(employeeService.countTotalEmployeesInUnitAndSubUnits(unit, listEmployeeId, EmployeeType.FIXED_TERM));
            listEmployeeId.clear();

            unit.setExternalEmployeesCount(employeeService.countTotalEmployeesInUnitAndSubUnits(unit, listEmployeeId, EmployeeType.EXTERNAL));
            listEmployeeId.clear();

            unit.setInternalEmployeesCount(employeeService.countTotalEmployeesInUnitAndSubUnits(unit, listEmployeeId, EmployeeType.INTERNAL));
            listEmployeeId.clear();

            unit.setTotalEmployeesOfEachLevel(unit.getFixedtermEmployeesCount()
                                            + unit.getExternalEmployeesCount()
                                            + unit.getInternalEmployeesCount());

            units.add(unit);
        });
        log.debug("CRON-JOB COUNTING TOTAL EMPLOYEE BY TYPE FOR ALL UNITS FOLLOWING RECURSIVE LOGIC  _  " +
                    "STOP  !!!!  countingTotalNumberOfEmployeesOfEachUnit func()");

        unitRepository.saveAll(units);
    }
}
