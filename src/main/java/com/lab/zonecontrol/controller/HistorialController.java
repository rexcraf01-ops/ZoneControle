package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.dto.HistorialAccesoDTO;
import com.lab.zonecontrol.service.HistorialService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/historial-accesos", "/historial-accesos"})
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public ResponseEntity<List<HistorialAccesoDTO>> listar(
            @RequestParam(value = "idPersonal", required = false) String idPersonal,
            @RequestParam(value = "desde", required = false) String desde,
            @RequestParam(value = "hasta", required = false) String hasta) {
        return ResponseEntity.ok(historialService.consultarHistorial(idPersonal, desde, hasta));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(
            @RequestParam(value = "idPersonal", required = false) String idPersonal,
            @RequestParam(value = "desde", required = false) String desde,
            @RequestParam(value = "hasta", required = false) String hasta) {
        byte[] csv = historialService.exportarHistorialCSV(idPersonal, desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"historial_accesos.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
