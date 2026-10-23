package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @JsonProperty("nombre_usuario")
    @JsonAlias({"nombreUsuario", "nombre_usuario", "username"})
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty("contrasena")
    @JsonAlias({"contrasena", "password"})
    private String contrasena;

    public LoginRequest() {
    }

    public LoginRequest(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}