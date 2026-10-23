package com.lab.zonecontrol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sesion")
public class Sesion {
    @Id
    @Column(name = "id_sesion")
    private UUID idSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "token_hash", length = 255, nullable = false)
    private String tokenHash;

    @Column(name = "expiracion", nullable = false)
    private Instant expiracion;

    @Column(name = "creado_en")
    private Instant creadoEn;

    public Sesion() {
        this.idSesion = UUID.randomUUID();
        this.creadoEn = Instant.now();
    }

    public Sesion(UUID idSesion, Usuario usuario, String tokenHash, Instant expiracion, Instant creadoEn) {
        this.idSesion = idSesion;
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.expiracion = expiracion;
        this.creadoEn = creadoEn;
    }

    public UUID getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(UUID idSesion) {
        this.idSesion = idSesion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public Instant getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(Instant expiracion) {
        this.expiracion = expiracion;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}