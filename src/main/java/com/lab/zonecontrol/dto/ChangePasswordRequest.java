package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotBlank
    @JsonProperty("contrasena_actual")
    @JsonAlias({"contrasenaActual", "currentPassword"})
    private String contrasenaActual;

    @NotBlank
    @Size(min = 8, max = 128)
    @JsonProperty("nueva_contrasena")
    @JsonAlias({"nuevaContrasena", "newPassword"})
    private String nuevaContrasena;

    public String getContrasenaActual() {
        return contrasenaActual;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }
}
