package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class ExportacionHistorialDTO {
    @JsonProperty("id_exportacion")
    private Integer idExportacion;

    @JsonProperty("fecha_hora_generacion")
    private OffsetDateTime fechaHoraGeneracion;

    @JsonProperty("periodo_inicio")
    private LocalDate periodoInicio;

    @JsonProperty("periodo_fin")
    private LocalDate periodoFin;

    @JsonProperty("formato")
    private String formato;

    @JsonProperty("correo_destino")
    private String correoDestino;

    @JsonProperty("nombre_usuario")
    private String nombreUsuario;

    public ExportacionHistorialDTO(Integer idExportacion, OffsetDateTime fechaHoraGeneracion,
                                   LocalDate periodoInicio, LocalDate periodoFin, String formato,
                                   String correoDestino, String nombreUsuario) {
        this.idExportacion = idExportacion;
        this.fechaHoraGeneracion = fechaHoraGeneracion;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.formato = formato;
        this.correoDestino = correoDestino;
        this.nombreUsuario = nombreUsuario;
    }

    public Integer getIdExportacion() {
        return idExportacion;
    }

    public OffsetDateTime getFechaHoraGeneracion() {
        return fechaHoraGeneracion;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public String getFormato() {
        return formato;
    }

    public String getCorreoDestino() {
        return correoDestino;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
