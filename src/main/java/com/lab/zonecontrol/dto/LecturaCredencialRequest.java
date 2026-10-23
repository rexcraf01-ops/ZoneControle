package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LecturaCredencialRequest {
    @NotBlank(message = "El número de identificación es obligatorio")
    @JsonProperty("numero_identificacion")
    @JsonAlias({"numeroIdentificacion", "numero_identificacion", "identificacion"})
    private String numeroIdentificacion;

    public LecturaCredencialRequest() {
    }

    public LecturaCredencialRequest(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }
}
