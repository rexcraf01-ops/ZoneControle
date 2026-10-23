package com.lab.zonecontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ResumenCargaMasivaDTO {
    @JsonProperty("total_procesadas")
    private int totalProcesadas;

    @JsonProperty("filas_incorporadas")
    private int filasIncorporadas;

    @JsonProperty("filas_con_error")
    private int filasConError;

    @JsonProperty("errores")
    private List<FilaErrorDTO> errores = new ArrayList<>();

    public ResumenCargaMasivaDTO() {
    }

    public ResumenCargaMasivaDTO(int totalProcesadas, int filasIncorporadas, int filasConError, List<FilaErrorDTO> errores) {
        this.totalProcesadas = totalProcesadas;
        this.filasIncorporadas = filasIncorporadas;
        this.filasConError = filasConError;
        this.errores = errores;
    }

    public int getTotalProcesadas() {
        return totalProcesadas;
    }

    public void setTotalProcesadas(int totalProcesadas) {
        this.totalProcesadas = totalProcesadas;
    }

    public int getFilasIncorporadas() {
        return filasIncorporadas;
    }

    public void setFilasIncorporadas(int filasIncorporadas) {
        this.filasIncorporadas = filasIncorporadas;
    }

    public int getFilasConError() {
        return filasConError;
    }

    public void setFilasConError(int filasConError) {
        this.filasConError = filasConError;
    }

    public List<FilaErrorDTO> getErrores() {
        return errores;
    }

    public void setErrores(List<FilaErrorDTO> errores) {
        this.errores = errores;
    }

    public static class FilaErrorDTO {
        @JsonProperty("fila")
        private int fila;

        @JsonProperty("identificacion")
        private String identificacion;

        @JsonProperty("motivo")
        private String motivo;

        public FilaErrorDTO() {
        }

        public FilaErrorDTO(int fila, String identificacion, String motivo) {
            this.fila = fila;
            this.identificacion = identificacion;
            this.motivo = motivo;
        }

        public int getFila() {
            return fila;
        }

        public void setFila(int fila) {
            this.fila = fila;
        }

        public String getIdentificacion() {
            return identificacion;
        }

        public void setIdentificacion(String identificacion) {
            this.identificacion = identificacion;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
    }
}
