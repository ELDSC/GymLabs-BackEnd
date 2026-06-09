package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Notificacion;
import com.GYMLABS.proyecto.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> buscarPorId(Integer id) {
        return notificacionRepository.findById(id);
    }

    public Notificacion guardar(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public void eliminar(Integer id) {
        notificacionRepository.deleteById(id);
    }
}
