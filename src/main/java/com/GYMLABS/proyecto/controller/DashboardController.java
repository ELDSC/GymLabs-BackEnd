package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.dto.DashboardDTO;
import com.GYMLABS.proyecto.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardDTO getStats() {
        return dashboardService.getDashboardStats();
    }
}
