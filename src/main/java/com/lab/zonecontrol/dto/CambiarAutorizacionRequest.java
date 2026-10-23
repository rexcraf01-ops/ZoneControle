package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CambiarAutorizacionRequest {
    @JsonProperty("id_estado_autorizacion")
    @JsonAlias({"idEstadoAutorizacion", "id_estado_autorizacion", "idEstado"})
    private Integer idEstadoAutorizacion;

    @JsonProperty("estado")
    @JsonAlias({"estado", "nombre"})
    private String estado;

    public CambiarAutorizacionRequest() {
    }

    public CambiarAutorizacionRequest(Integer idEstadoAutorizacion, String estado) {
        this.idEstadoAutorizacion = idEstadoAutorizacion;
        this.estado = estado;
    }

    public Integer getIdEstadoAutorizacion() {
        return idEstadoAutorizacion;
    }

    public void setIdEstadoAutorizacion(Integer idEstadoAutorizacion) {
        this.idEstadoAutorizacion = idEstadoAutorizacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
