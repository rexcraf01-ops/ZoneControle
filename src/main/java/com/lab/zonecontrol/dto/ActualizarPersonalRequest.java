package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ActualizarPersonalRequest {
    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 20, message = "El número de identificación no puede superar 20 caracteres")
    @JsonProperty("numero_identificacion")
    @JsonAlias({"numeroIdentificacion", "numero_identificacion", "identificacion"})
    private String numeroIdentificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
    @JsonProperty("nombres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
    @JsonProperty("apellidos")
    private String apellidos;

    @NotNull(message = "El departamento es obligatorio")
    @JsonProperty("id_departamento")
    @JsonAlias({"idDepartamento", "id_departamento", "departamento"})
    private Integer idDepartamento;

    @JsonProperty("id_estado_autorizacion")
    @JsonAlias({"idEstadoAutorizacion", "id_estado_autorizacion", "estado_autorizacion"})
    private Integer idEstadoAutorizacion;

    public ActualizarPersonalRequest() {
    }

    public ActualizarPersonalRequest(String numeroIdentificacion, String nombres, String apellidos,
                                     Integer idDepartamento, Integer idEstadoAutorizacion) {
        this.numeroIdentificacion = numeroIdentificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.idDepartamento = idDepartamento;
        this.idEstadoAutorizacion = idEstadoAutorizacion;
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

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public Integer getIdEstadoAutorizacion() {
        return idEstadoAutorizacion;
    }

    public void setIdEstadoAutorizacion(Integer idEstadoAutorizacion) {
        this.idEstadoAutorizacion = idEstadoAutorizacion;
    }
}
