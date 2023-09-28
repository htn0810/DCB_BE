package com.bosch.digicore.repositories;

import com.bosch.digicore.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	Optional<Organization> findByIdAndDeletedIsFalse(Long id);

	Optional<Organization> findByUuidAndDeletedIsFalse(String uuid);

	List<Organization> findAllByDeletedIsFalse();

	List<Organization> findAllByNameContainingIgnoreCaseAndDeletedIsFalse(String name);

	long countAllByDeletedIsFalse();

	List<Organization> findAllByIdIn(List<Long> ids);
}
