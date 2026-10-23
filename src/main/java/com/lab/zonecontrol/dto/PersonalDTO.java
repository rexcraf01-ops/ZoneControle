package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonalDTO {
    @JsonProperty("id_personal")
    private Integer idPersonal;

    @JsonProperty("numero_identificacion")
    private String numeroIdentificacion;

    @JsonProperty("nombres")
    private String nombres;

    @JsonProperty("apellidos")
    private String apellidos;

    @JsonProperty("id_departamento")
    private Integer idDepartamento;

    @JsonProperty("departamento")
    private String departamento;

    @JsonProperty("id_estado_autorizacion")
    private Integer idEstadoAutorizacion;

    @JsonProperty("estado_autorizacion")
    private String estadoAutorizacion;

    public PersonalDTO() {
    }

    public PersonalDTO(Integer idPersonal, String numeroIdentificacion, String nombres, String apellidos,
                       Integer idDepartamento, String departamento, Integer idEstadoAutorizacion, String estadoAutorizacion) {
        this.idPersonal = idPersonal;
        this.numeroIdentificacion = numeroIdentificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.idDepartamento = idDepartamento;
        this.departamento = departamento;
        this.idEstadoAutorizacion = idEstadoAutorizacion;
        this.estadoAutorizacion = estadoAutorizacion;
    }

    public Integer getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(Integer idPersonal) {
        this.idPersonal = idPersonal;
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Integer getIdEstadoAutorizacion() {
        return idEstadoAutorizacion;
    }

    public void setIdEstadoAutorizacion(Integer idEstadoAutorizacion) {
        this.idEstadoAutorizacion = idEstadoAutorizacion;
    }

    public String getEstadoAutorizacion() {
        return estadoAutorizacion;
    }

    public void setEstadoAutorizacion(String estadoAutorizacion) {
        this.estadoAutorizacion = estadoAutorizacion;
    }
}
