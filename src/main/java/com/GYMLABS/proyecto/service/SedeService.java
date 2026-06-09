package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Sede;
import com.GYMLABS.proyecto.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SedeService {

    @Autowired
    private SedeRepository sedeRepository;

    public List<Sede> listarTodas() {
        return sedeRepository.findAll();
    }

    public Optional<Sede> buscarPorId(Integer id) {
        return sedeRepository.findById(id);
    }

    public Sede guardar(Sede sede) {
        return sedeRepository.save(sede);
    }

    public void eliminar(Integer id) {
        sedeRepository.deleteById(id);
    }
}
