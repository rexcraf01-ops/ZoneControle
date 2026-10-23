package com.lab.zonecontrol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_autorizacion")
public class EstadoAutorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_autorizacion")
    private Integer idEstadoAutorizacion;

    @Column(name = "nombre", length = 30, nullable = false, unique = true)
    private String nombre;

    public EstadoAutorizacion() {
    }

    public EstadoAutorizacion(Integer idEstadoAutorizacion, String nombre) {
        this.idEstadoAutorizacion = idEstadoAutorizacion;
        this.nombre = nombre;
    }

    public Integer getIdEstadoAutorizacion() {
        return idEstadoAutorizacion;
    }

    public void setIdEstadoAutorizacion(Integer idEstadoAutorizacion) {
        this.idEstadoAutorizacion = idEstadoAutorizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
