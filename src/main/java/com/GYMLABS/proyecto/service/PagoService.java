package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Pago;
import com.GYMLABS.proyecto.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    public Pago guardar(Pago pago) {
        return pagoRepository.save(pago);
    }

    public void eliminar(Integer id) {
        pagoRepository.deleteById(id);
    }
}
