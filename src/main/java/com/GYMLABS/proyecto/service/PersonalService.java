package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Personal;
import com.GYMLABS.proyecto.repository.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public List<Personal> listarTodos() {
        return personalRepository.findAll();
    }

    public Optional<Personal> buscarPorId(Integer id) {
        return personalRepository.findById(id);
    }

    public Personal guardar(Personal personal) {
        if (personal.getPassword() != null && !personal.getPassword().startsWith("$2a$")) {
            personal.setPassword(passwordEncoder.encode(personal.getPassword()));
        }
        return personalRepository.save(personal);
    }

    public void eliminar(Integer id) {
        personalRepository.deleteById(id);
    }
}
