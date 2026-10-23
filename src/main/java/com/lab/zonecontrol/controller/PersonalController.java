package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.dto.ActualizarPersonalRequest;
import com.lab.zonecontrol.dto.CambiarAutorizacionRequest;
import com.lab.zonecontrol.dto.CrearPersonalRequest;
import com.lab.zonecontrol.dto.PersonalDTO;
import com.lab.zonecontrol.dto.ResumenCargaMasivaDTO;
import com.lab.zonecontrol.service.PersonalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/personal", "/personal"})
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping
    public ResponseEntity<List<PersonalDTO>> listar(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "idDepartamento", required = false) Integer idDepartamento) {
        return ResponseEntity.ok(personalService.listar(q, idDepartamento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalDTO> obtenerPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(personalService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PersonalDTO> crear(@Valid @RequestBody CrearPersonalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalDTO> actualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ActualizarPersonalRequest request) {
        return ResponseEntity.ok(personalService.actualizar(id, request));
    }

    @PatchMapping("/{id}/autorizacion")
    public ResponseEntity<PersonalDTO> cambiarAutorizacion(
            @PathVariable("id") Integer id,
            @RequestBody CambiarAutorizacionRequest request) {
        return ResponseEntity.ok(personalService.cambiarAutorizacion(id, request));
    }

    @PostMapping("/carga-masiva")
    public ResponseEntity<ResumenCargaMasivaDTO> cargaMasiva(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(personalService.procesarCargaMasiva(file));
    }
}
