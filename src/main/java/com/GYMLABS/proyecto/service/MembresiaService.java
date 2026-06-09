package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Membresia;
import com.GYMLABS.proyecto.repository.MembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {

    @Autowired
    private MembresiaRepository membresiaRepository;

    public List<Membresia> listarTodas() {
        return membresiaRepository.findAll();
    }

    public Optional<Membresia> buscarPorId(Integer id) {
        return membresiaRepository.findById(id);
    }

    public Membresia guardar(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }

    public void eliminar(Integer id) {
        membresiaRepository.deleteById(id);
    }
}
