package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LecturaCredencialResponse {
    @JsonProperty("resultado")
    private String resultado; // AUTORIZADO, DENEGADO, NO_REGISTRADO

    @JsonProperty("mensaje")
    private String mensaje; // "Ingreso autorizado", "Ingreso denegado", "Persona no registrada"

    @JsonProperty("numero_identificacion")
    private String numeroIdentificacion;

    @JsonProperty("persona")
    private String persona;

    @JsonProperty("departamento")
    private String departamento;

    @JsonProperty("fecha_hora")
    private OffsetDateTime fechaHora;

    public LecturaCredencialResponse() {
    }

    public LecturaCredencialResponse(String resultado, String mensaje, String numeroIdentificacion,
                                     String persona, String departamento, OffsetDateTime fechaHora) {
        this.resultado = resultado;
        this.mensaje = mensaje;
        this.numeroIdentificacion = numeroIdentificacion;
        this.persona = persona;
        this.departamento = departamento;
        this.fechaHora = fechaHora;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
