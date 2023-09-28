package com.bosch.digicore.repositories;

import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    List<Unit> findAllByDeletedIsFalse();

    List<Unit> findAllByIdIn(List<Long> ids);

    List<Unit> findAllByNameIn(List<String> names);

    List<Unit> findAllByNameContainsIgnoreCaseAndDeletedIsFalse(String name);

    List<Unit> findAllByParentUnit_IdAndDeletedIsFalse(Long parentId);

    List<Unit> findAllByParentUnitAndDeletedIsFalse(Unit parentUnit);

    List<Unit> findAllByOrganizationsInAndDeletedIsFalse(List<Organization> organizations);

    @Query(nativeQuery = true, value = "WITH RECURSIVE cte_unit AS " +
            "(SELECT parent_unit.* " +
            "FROM units parent_unit " +
            "WHERE parent_unit.id = :parentId " +
            "UNION ALL " +
            "SELECT children_unit.* " +
            "FROM units children_unit " +
            "INNER JOIN cte_unit cte ON cte.id = children_unit.parent_id) " +
            "SELECT * FROM cte_unit;")
    List<Unit> findUnitAndAllChildrenUnitsByUnitId(@Param("parentId") Long parentId);
}
