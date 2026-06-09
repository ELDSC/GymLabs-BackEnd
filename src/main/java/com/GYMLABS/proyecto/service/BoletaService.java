package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Boleta;
import com.GYMLABS.proyecto.repository.BoletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoletaService {

    @Autowired
    private BoletaRepository boletaRepository;

    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    public Optional<Boleta> buscarPorId(Integer id) {
        return boletaRepository.findById(id);
    }

    public Boleta guardar(Boleta boleta) {
        return boletaRepository.save(boleta);
    }

    public void eliminar(Integer id) {
        boletaRepository.deleteById(id);
    }
}
