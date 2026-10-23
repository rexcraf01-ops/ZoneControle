package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReporteActividadDepartamentoDTO {
    @JsonProperty("departamento")
    private String departamento;

    @JsonProperty("total_intentos")
    private long totalIntentos;

    @JsonProperty("autorizados")
    private long autorizados;

    @JsonProperty("denegados")
    private long denegados;

    @JsonProperty("no_registrados")
    private long noRegistrados;

    public ReporteActividadDepartamentoDTO() {
    }

    public ReporteActividadDepartamentoDTO(String departamento, long totalIntentos, long autorizados, long denegados, long noRegistrados) {
        this.departamento = departamento;
        this.totalIntentos = totalIntentos;
        this.autorizados = autorizados;
        this.denegados = denegados;
        this.noRegistrados = noRegistrados;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public long getTotalIntentos() {
        return totalIntentos;
    }

    public void setTotalIntentos(long totalIntentos) {
        this.totalIntentos = totalIntentos;
    }

    public long getAutorizados() {
        return autorizados;
    }

    public void setAutorizados(long autorizados) {
        this.autorizados = autorizados;
    }

    public long getDenegados() {
        return denegados;
    }

    public void setDenegados(long denegados) {
        this.denegados = denegados;
    }

    public long getNoRegistrados() {
        return noRegistrados;
    }

    public void setNoRegistrados(long noRegistrados) {
        this.noRegistrados = noRegistrados;
    }
}
