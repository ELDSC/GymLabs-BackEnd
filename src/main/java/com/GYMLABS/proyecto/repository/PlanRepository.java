package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    java.util.List<Plan> findByEmpresaIdEmpresa(Integer idEmpresa);
}
