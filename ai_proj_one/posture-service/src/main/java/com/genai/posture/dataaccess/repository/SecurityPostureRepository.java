package com.genai.posture.dataaccess.repository;

import com.genai.posture.dataaccess.entity.SecurityPostureEntity;
import com.genai.posture.dataaccess.entity.SecurityPostureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityPostureRepository extends JpaRepository<SecurityPostureEntity, SecurityPostureId> {
    Optional<SecurityPostureEntity> findByServiceIdAndEnvironment(String serviceId, String environment);
}