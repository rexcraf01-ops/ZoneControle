package com.lab.zonecontrol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resultado_ingreso")
public class ResultadoIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado_ingreso")
    private Integer idResultadoIngreso;

    @Column(name = "nombre", length = 30, nullable = false, unique = true)
    private String nombre;

    public ResultadoIngreso() {
    }

    public ResultadoIngreso(Integer idResultadoIngreso, String nombre) {
        this.idResultadoIngreso = idResultadoIngreso;
        this.nombre = nombre;
    }

    public Integer getIdResultadoIngreso() {
        return idResultadoIngreso;
    }

    public void setIdResultadoIngreso(Integer idResultadoIngreso) {
        this.idResultadoIngreso = idResultadoIngreso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
