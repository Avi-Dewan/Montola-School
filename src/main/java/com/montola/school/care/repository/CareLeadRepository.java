package com.montola.school.care.repository;

import com.montola.school.care.model.CareLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 */
@Repository
public interface CareLeadRepository extends JpaRepository<CareLead, Long> {

    List<CareLead> findByIsDeletedFalseOrderByCreatedAtDesc();
}
