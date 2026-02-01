package com.ride.mechanic.repository;

import com.ride.mechanic.entity.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

    List<Center> findByStatus(Center.CenterStatus status);

    @Query("SELECT c FROM Center c WHERE c.status = 'ACTIVE'")
    List<Center> findActiveCenters();

    @Query("SELECT c FROM Center c WHERE c.city = :city AND c.status = 'ACTIVE'")
    List<Center> findActiveCentersByCity(@Param("city") String city);

    @Query("SELECT c FROM Center c WHERE " +
           "c.status = 'ACTIVE' AND " +
           "c.id NOT IN (" +
           "  SELECT m.assignedCenter.id FROM Mechanic m " +
           "  WHERE m.skillType = :skillType AND m.assignedCenter.id = c.id" +
           ")")
    List<Center> findCentersWithAvailableSlot(@Param("skillType") String skillType);

    @Query("SELECT COUNT(m) FROM Mechanic m WHERE m.assignedCenter.id = :centerId AND m.skillType = :skillType")
    Integer countMechanicsBySkillAndCenter(@Param("centerId") Long centerId, @Param("skillType") String skillType);
}
