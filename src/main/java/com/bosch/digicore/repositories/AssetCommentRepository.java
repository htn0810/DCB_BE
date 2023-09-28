package com.bosch.digicore.repositories;

import com.bosch.digicore.entities.AssetComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetCommentRepository extends JpaRepository<AssetComment, Long>{

	List<AssetComment> findAllByAssetId(Long assetId);
}
