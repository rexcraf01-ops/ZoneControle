package com.lab.zonecontrol.service;

import com.lab.zonecontrol.dto.LecturaCredencialResponse;
import com.lab.zonecontrol.entity.IntentoIngreso;
import com.lab.zonecontrol.entity.Personal;
import com.lab.zonecontrol.entity.ResultadoIngreso;
import com.lab.zonecontrol.repository.IntentoIngresoRepository;
import com.lab.zonecontrol.repository.PersonalRepository;
import com.lab.zonecontrol.repository.ResultadoIngresoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class AccesoService {

    private final PersonalRepository personalRepository;
    private final ResultadoIngresoRepository resultadoIngresoRepository;
    private final IntentoIngresoRepository intentoIngresoRepository;

    public AccesoService(PersonalRepository personalRepository,
                         ResultadoIngresoRepository resultadoIngresoRepository,
                         IntentoIngresoRepository intentoIngresoRepository) {
        this.personalRepository = personalRepository;
        this.resultadoIngresoRepository = resultadoIngresoRepository;
        this.intentoIngresoRepository = intentoIngresoRepository;
    }

    @Transactional
    public LecturaCredencialResponse procesarLectura(String numeroIdentificacion) {
        if (numeroIdentificacion == null || numeroIdentificacion.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de identificación no puede estar vacío");
        }

        String idLimpio = numeroIdentificacion.trim();
        Optional<Personal> personalOpt = personalRepository.findByNumeroIdentificacion(idLimpio);
        OffsetDateTime ahora = OffsetDateTime.now();

        if (personalOpt.isEmpty()) {
            // Persona no registrada
            ResultadoIngreso resNoRegistrado = resultadoIngresoRepository.findByNombreIgnoreCase("No registrado")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Configuración de resultado 'No registrado' faltante"));

            IntentoIngreso intento = new IntentoIngreso();
            intento.setNumeroIdentificacionIngresado(idLimpio);
            intento.setPersonal(null);
            intento.setResultadoIngreso(resNoRegistrado);
            intento.setFechaHora(ahora);

            intentoIngresoRepository.save(intento);

            return new LecturaCredencialResponse(
                    "NO_REGISTRADO",
                    "Persona no registrada",
                    idLimpio,
                    null,
                    null,
                    ahora
            );
        }

        Personal personal = personalOpt.get();
        String estado = personal.getEstadoAutorizacion().getNombre();
        String nombreCompleto = (personal.getNombres() + " " + personal.getApellidos()).trim();
        String depto = personal.getDepartamento() != null ? personal.getDepartamento().getNombre() : null;

        if ("Otorgada".equalsIgnoreCase(estado)) {
            // Autorizado
            ResultadoIngreso resAutorizado = resultadoIngresoRepository.findByNombreIgnoreCase("Autorizado")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Configuración de resultado 'Autorizado' faltante"));

            IntentoIngreso intento = new IntentoIngreso();
            intento.setNumeroIdentificacionIngresado(idLimpio);
            intento.setPersonal(personal);
            intento.setResultadoIngreso(resAutorizado);
            intento.setFechaHora(ahora);

            intentoIngresoRepository.save(intento);

            return new LecturaCredencialResponse(
                    "AUTORIZADO",
                    "Ingreso autorizado",
                    idLimpio,
                    nombreCompleto,
                    depto,
                    ahora
            );
        } else {
            // Denegado (Revocada o Suspendida)
            ResultadoIngreso resDenegado = resultadoIngresoRepository.findByNombreIgnoreCase("Denegado")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Configuración de resultado 'Denegado' faltante"));

            IntentoIngreso intento = new IntentoIngreso();
            intento.setNumeroIdentificacionIngresado(idLimpio);
            intento.setPersonal(personal);
            intento.setResultadoIngreso(resDenegado);
            intento.setFechaHora(ahora);

            intentoIngresoRepository.save(intento);

            return new LecturaCredencialResponse(
                    "DENEGADO",
                    "Ingreso denegado",
                    idLimpio,
                    nombreCompleto,
                    depto,
                    ahora
            );
        }
    }
}
