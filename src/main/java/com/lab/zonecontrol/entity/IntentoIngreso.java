package com.lab.zonecontrol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "intento_ingreso")
public class IntentoIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intento")
    private Integer idIntento;

    @Column(name = "numero_identificacion_ingresado", length = 20, nullable = false)
    private String numeroIdentificacionIngresado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_personal")
    private Personal personal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_resultado_ingreso", nullable = false)
    private ResultadoIngreso resultadoIngreso;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    public IntentoIngreso() {
        this.fechaHora = OffsetDateTime.now();
    }

    public IntentoIngreso(Integer idIntento, String numeroIdentificacionIngresado, Personal personal,
                          ResultadoIngreso resultadoIngreso, OffsetDateTime fechaHora) {
        this.idIntento = idIntento;
        this.numeroIdentificacionIngresado = numeroIdentificacionIngresado;
        this.personal = personal;
        this.resultadoIngreso = resultadoIngreso;
        this.fechaHora = fechaHora;
    }

    public Integer getIdIntento() {
        return idIntento;
    }

    public void setIdIntento(Integer idIntento) {
        this.idIntento = idIntento;
    }

    public String getNumeroIdentificacionIngresado() {
        return numeroIdentificacionIngresado;
    }

    public void setNumeroIdentificacionIngresado(String numeroIdentificacionIngresado) {
        this.numeroIdentificacionIngresado = numeroIdentificacionIngresado;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public ResultadoIngreso getResultadoIngreso() {
        return resultadoIngreso;
    }

    public void setResultadoIngreso(ResultadoIngreso resultadoIngreso) {
        this.resultadoIngreso = resultadoIngreso;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
