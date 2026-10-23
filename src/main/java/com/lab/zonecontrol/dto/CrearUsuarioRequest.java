package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class CrearUsuarioRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @JsonProperty("nombre_usuario")
    @JsonAlias({"nombreUsuario", "nombre_usuario", "username"})
    private String nombreUsuario;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 254, message = "El correo no puede superar 254 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    @JsonProperty("contrasena")
    @JsonAlias({"contrasena", "password"})
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    @JsonProperty("id_rol")
    @JsonAlias({"idRol", "id_rol"})
    private Integer idRol;

    @JsonProperty("id_personal")
    @JsonAlias({"idPersonal", "id_personal"})
    private Integer idPersonal;

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 20, message = "El número de identificación no puede superar 20 caracteres")
    @JsonProperty("numero_identificacion")
    @JsonAlias({"numeroIdentificacion", "identificacion"})
    private String numeroIdentificacion;

    @NotBlank(message = "Los nombres de la persona son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos de la persona son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
    private String apellidos;

    @NotNull(message = "El departamento es obligatorio")
    @JsonProperty("id_departamento")
    @JsonAlias({"idDepartamento", "id_departamento"})
    private Integer idDepartamento;

    public CrearUsuarioRequest() {
    }

    public CrearUsuarioRequest(String nombreUsuario, String contrasena, Integer idRol, Integer idPersonal) {
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
        this.email = email == null || email.isBlank() ? null : email.trim();
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

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }
}
