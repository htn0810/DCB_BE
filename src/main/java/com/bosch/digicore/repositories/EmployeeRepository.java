package com.bosch.digicore.repositories;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByNtid(String ntid);

    Optional<Employee> findByNtidAndDeletedIsFalse(String ntid);

    Optional<Employee> findByEmailAndDeletedIsFalse(String email);

    List<Employee> findAllByIdIn(List<Long> ids);

    List<Employee> findAllByUnitsInAndDeletedIsFalse(List<Unit> units);

    List<Employee> findAllByOrganizationsInAndDeletedIsFalse(List<Organization> organizations);

    Page<Employee> findAllByDeletedIsFalse(Pageable pageable);

    Page<Employee> findAllByDisplayNameContainsIgnoreCaseAndDeletedIsFalse(String name, Pageable pageable);

    Page<Employee> findAllByDisplayNameContainsIgnoreCaseAndTypeInAndDeletedIsFalse(String name, List<EmployeeType> types, Pageable pageable);

    Page<Employee> findDistinctByUnitsInAndDeletedIsFalse(List<Unit> units, Pageable pageable);

    Page<Employee> findDistinctByOrganizationsInAndDeletedIsFalse(List<Organization> organizations, Pageable pageable);

    Page<Employee> findDistinctByUnitsInAndTypeInAndDeletedIsFalse(List<Unit> units, List<EmployeeType> types, Pageable pageable);

    Page<Employee> findDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndDeletedIsFalse(List<Unit> units, String name, Pageable pageable);

    Page<Employee> findDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeInAndDeletedIsFalse(List<Unit> units, String name, List<EmployeeType> types, Pageable pageable);

    Page<Employee> findAllByTypeInAndDeletedIsFalse(List<EmployeeType> types, Pageable pageable);

    long countAllByDeletedIsFalse();

    long countDistinctByUnitsInAndDeletedIsFalse(List<Unit> units);

    long countAllByTypeAndDeletedIsFalse(EmployeeType employeeType);

    long countDistinctByUnitsInAndTypeAndDeletedIsFalse(List<Unit> units, EmployeeType type);

    long countDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(List<Unit> units, String name, EmployeeType type);

    long countAllByDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(String name, EmployeeType type);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "update employees set active = false where ntid in :ntids")
    int updateInactiveStatusForDeletedEmployeesInLDAP (@Param("ntids") List<String> ntids);
}
