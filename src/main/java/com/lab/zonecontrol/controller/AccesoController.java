package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.dto.LecturaCredencialRequest;
import com.lab.zonecontrol.dto.LecturaCredencialResponse;
import com.lab.zonecontrol.service.AccesoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/acceso", "/acceso"})
public class AccesoController {

    private final AccesoService accesoService;

    public AccesoController(AccesoService accesoService) {
        this.accesoService = accesoService;
    }

    @PostMapping("/lectura")
    public ResponseEntity<LecturaCredencialResponse> lectura(@Valid @RequestBody LecturaCredencialRequest request) {
        return ResponseEntity.ok(accesoService.procesarLectura(request.getNumeroIdentificacion()));
    }
}
