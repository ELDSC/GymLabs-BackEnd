package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Plan;
import com.GYMLABS.proyecto.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public List<Plan> listarTodos(Integer empresaId) {
        if (empresaId != null) {
            return planRepository.findByEmpresaIdEmpresa(empresaId);
        }
        return planRepository.findAll();
    }

    public Optional<Plan> buscarPorId(Integer id) {
        return planRepository.findById(id);
    }

    public Plan guardar(Plan plan) {
        return planRepository.save(plan);
    }

    public void eliminar(Integer id) {
        planRepository.deleteById(id);
    }
}
