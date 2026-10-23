package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioDTO {
    @JsonProperty("id_usuario")
    private Integer idUsuario;

    @JsonProperty("nombre_usuario")
    private String nombreUsuario;

    @JsonProperty("email")
    private String email;

    @JsonProperty("id_rol")
    private Integer idRol;

    @JsonProperty("rol")
    private String rolNombre;

    @JsonProperty("id_personal")
    private Integer idPersonal;

    @JsonProperty("personal_nombre")
    private String personalNombre;

    @JsonProperty("numero_identificacion")
    private String numeroIdentificacion;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer idUsuario, String nombreUsuario, Integer idRol, String rolNombre,
                      Integer idPersonal, String personalNombre, String numeroIdentificacion) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idRol = idRol;
        this.rolNombre = rolNombre;
        this.idPersonal = idPersonal;
        this.personalNombre = personalNombre;
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public UsuarioDTO(Integer idUsuario, String nombreUsuario, String email, Integer idRol, String rolNombre,
                      Integer idPersonal, String personalNombre, String numeroIdentificacion) {
        this(idUsuario, nombreUsuario, idRol, rolNombre, idPersonal, personalNombre, numeroIdentificacion);
        this.email = email;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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
        this.email = email;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public Integer getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(Integer idPersonal) {
        this.idPersonal = idPersonal;
    }

    public String getPersonalNombre() {
        return personalNombre;
    }

    public void setPersonalNombre(String personalNombre) {
        this.personalNombre = personalNombre;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }
}
