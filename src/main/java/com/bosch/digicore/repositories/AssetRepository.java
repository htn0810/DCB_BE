package com.bosch.digicore.repositories;

import com.bosch.digicore.constants.AssetStatus;
import com.bosch.digicore.entities.Asset;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

	List<Asset> findAllByIdIn(List<Long> ids);

	List<Asset> findAllByDeletedIsFalse();

	List<Asset> findAllByOrganizationsInAndDeletedIsFalse(List<Organization> organizations);

	List<Asset> findAllByStatus(AssetStatus status);

	boolean existsByIdAndDeletedIsFalse(Long id);

	boolean existsByOrganizationsInAndDeletedIsFalse(List<Organization> organizations);

	long countAllByDeletedIsFalse();
}
