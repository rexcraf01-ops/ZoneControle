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

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "exportacion")
public class Exportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exportacion")
    private Integer idExportacion;

    @Column(name = "fecha_hora_generacion", nullable = false)
    private OffsetDateTime fechaHoraGeneracion;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "formato", length = 20, nullable = false)
    private String formato;

    @Column(name = "correo_destino", length = 254)
    private String correoDestino;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Exportacion() {
        this.fechaHoraGeneracion = OffsetDateTime.now();
        this.formato = "CSV";
    }

    public Exportacion(Integer idExportacion, OffsetDateTime fechaHoraGeneracion, LocalDate periodoInicio,
                       LocalDate periodoFin, String formato, Usuario usuario) {
        this.idExportacion = idExportacion;
        this.fechaHoraGeneracion = fechaHoraGeneracion;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.formato = formato;
        this.usuario = usuario;
    }

    public Integer getIdExportacion() {
        return idExportacion;
    }

    public void setIdExportacion(Integer idExportacion) {
        this.idExportacion = idExportacion;
    }

    public OffsetDateTime getFechaHoraGeneracion() {
        return fechaHoraGeneracion;
    }

    public void setFechaHoraGeneracion(OffsetDateTime fechaHoraGeneracion) {
        this.fechaHoraGeneracion = fechaHoraGeneracion;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getCorreoDestino() {
        return correoDestino;
    }

    public void setCorreoDestino(String correoDestino) {
        this.correoDestino = correoDestino;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
