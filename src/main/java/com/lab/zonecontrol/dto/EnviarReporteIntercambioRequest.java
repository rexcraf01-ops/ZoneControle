package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class EnviarReporteIntercambioRequest {
    @NotNull(message = "El periodo de inicio es obligatorio")
    @JsonProperty("periodo_inicio")
    @JsonAlias({"periodoInicio", "desde"})
    private LocalDate periodoInicio;

    @NotNull(message = "El periodo de fin es obligatorio")
    @JsonProperty("periodo_fin")
    @JsonAlias({"periodoFin", "hasta"})
    private LocalDate periodoFin;

    @NotBlank(message = "El correo Gmail de destino es obligatorio")
    @Email(message = "El correo de destino no es válido")
    @Pattern(regexp = "(?i)^[A-Z0-9._%+-]+@gmail\\.com$",
            message = "El correo de destino debe ser una cuenta Gmail")
    @JsonProperty("correo_destino")
    @JsonAlias({"correoDestino", "destinatario"})
    private String correoDestino;

    @NotBlank(message = "El formato de exportación es obligatorio")
    private String formato;

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

    public String getCorreoDestino() {
        return correoDestino;
    }

    public void setCorreoDestino(String correoDestino) {
        this.correoDestino = correoDestino == null ? null : correoDestino.trim();
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato == null ? null : formato.trim().toUpperCase();
    }
}
