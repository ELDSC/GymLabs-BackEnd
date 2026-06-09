package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Empresa;
import com.GYMLABS.proyecto.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Optional<Empresa> buscarPorId(Integer id) {
        return empresaRepository.findById(id);
    }

    public Empresa guardar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public void eliminar(Integer id) {
        empresaRepository.deleteById(id);
    }
}
