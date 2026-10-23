package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class ActualizarUsuarioRequest {
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @JsonProperty("nombre_usuario")
    @JsonAlias({"nombreUsuario", "nombre_usuario"})
    private String nombreUsuario;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 254, message = "El correo no puede superar 254 caracteres")
    private String email;
    private boolean emailProvided;

    @JsonProperty("contrasena")
    @JsonAlias({"contrasena", "password"})
    private String contrasena;

    @JsonProperty("id_rol")
    @JsonAlias({"idRol", "id_rol"})
    private Integer idRol;

    @JsonProperty("id_personal")
    @JsonAlias({"idPersonal", "id_personal"})
    private Integer idPersonal;

    public ActualizarUsuarioRequest() {
    }

    public ActualizarUsuarioRequest(String nombreUsuario, String contrasena, Integer idRol, Integer idPersonal) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.idPersonal = idPersonal;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.emailProvided = true;
        this.email = email == null || email.isBlank() ? null : email.trim();
    }

    public boolean isEmailProvided() {
        return emailProvided;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Integer getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(Integer idPersonal) {
        this.idPersonal = idPersonal;
    }
}
