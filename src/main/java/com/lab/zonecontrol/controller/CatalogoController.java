package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.entity.Departamento;
import com.lab.zonecontrol.entity.EstadoAutorizacion;
import com.lab.zonecontrol.repository.DepartamentoRepository;
import com.lab.zonecontrol.repository.EstadoAutorizacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogoController {

    private final DepartamentoRepository departamentoRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;

    public CatalogoController(DepartamentoRepository departamentoRepository,
                              EstadoAutorizacionRepository estadoAutorizacionRepository) {
        this.departamentoRepository = departamentoRepository;
        this.estadoAutorizacionRepository = estadoAutorizacionRepository;
    }

    @GetMapping({"/api/departamentos", "/departamentos"})
    public ResponseEntity<List<Departamento>> listarDepartamentos() {
        return ResponseEntity.ok(departamentoRepository.findAll());
    }

    @GetMapping({"/api/estados-autorizacion", "/estados-autorizacion"})
    public ResponseEntity<List<EstadoAutorizacion>> listarEstadosAutorizacion() {
        return ResponseEntity.ok(estadoAutorizacionRepository.findAll());
    }
}
