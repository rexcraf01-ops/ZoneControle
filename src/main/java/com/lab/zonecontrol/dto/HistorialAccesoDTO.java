package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class HistorialAccesoDTO {
    @JsonProperty("id_intento")
    private Integer idIntento;

    @JsonProperty("numero_identificacion")
    private String numeroIdentificacion;

    @JsonProperty("nombres")
    private String nombres;

    @JsonProperty("apellidos")
    private String apellidos;

    @JsonProperty("persona")
    private String persona;

    @JsonProperty("departamento")
    private String departamento;

    @JsonProperty("resultado")
    private String resultado;

    @JsonProperty("fecha_hora")
    private OffsetDateTime fechaHora;

    public HistorialAccesoDTO() {
    }

    public HistorialAccesoDTO(Integer idIntento, String numeroIdentificacion, String nombres, String apellidos,
                              String departamento, String resultado, OffsetDateTime fechaHora) {
        this.idIntento = idIntento;
        this.numeroIdentificacion = numeroIdentificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.persona = (nombres != null || apellidos != null)
                ? ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim()
                : "No registrado";
        this.departamento = departamento != null ? departamento : "-";
        this.resultado = resultado;
        this.fechaHora = fechaHora;
    }

    public Integer getIdIntento() {
        return idIntento;
    }

    public void setIdIntento(Integer idIntento) {
        this.idIntento = idIntento;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
