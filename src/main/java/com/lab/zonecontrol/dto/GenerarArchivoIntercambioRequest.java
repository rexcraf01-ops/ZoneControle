package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class GenerarArchivoIntercambioRequest {
    @NotNull(message = "El periodo de inicio es obligatorio")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonProperty("periodo_inicio")
    @JsonAlias({"periodoInicio", "periodo_inicio", "desde"})
    private LocalDate periodoInicio;

    @NotNull(message = "El periodo de fin es obligatorio")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonProperty("periodo_fin")
    @JsonAlias({"periodoFin", "periodo_fin", "hasta"})
    private LocalDate periodoFin;

    public GenerarArchivoIntercambioRequest() {
    }

    public GenerarArchivoIntercambioRequest(LocalDate periodoInicio, LocalDate periodoFin) {
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }
}
