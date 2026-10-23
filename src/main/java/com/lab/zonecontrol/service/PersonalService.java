package com.lab.zonecontrol.service;

import com.lab.zonecontrol.dto.ActualizarPersonalRequest;
import com.lab.zonecontrol.dto.CambiarAutorizacionRequest;
import com.lab.zonecontrol.dto.CrearPersonalRequest;
import com.lab.zonecontrol.dto.PersonalDTO;
import com.lab.zonecontrol.dto.ResumenCargaMasivaDTO;
import com.lab.zonecontrol.entity.Departamento;
import com.lab.zonecontrol.entity.EstadoAutorizacion;
import com.lab.zonecontrol.entity.Personal;
import com.lab.zonecontrol.repository.DepartamentoRepository;
import com.lab.zonecontrol.repository.EstadoAutorizacionRepository;
import com.lab.zonecontrol.repository.PersonalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonalService {

    private final PersonalRepository personalRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;

    public PersonalService(PersonalRepository personalRepository,
                           DepartamentoRepository departamentoRepository,
                           EstadoAutorizacionRepository estadoAutorizacionRepository) {
        this.personalRepository = personalRepository;
        this.departamentoRepository = departamentoRepository;
        this.estadoAutorizacionRepository = estadoAutorizacionRepository;
    }

    @Transactional(readOnly = true)
    public List<PersonalDTO> listar(String q, Integer idDepartamento) {
        String query = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        return personalRepository.buscarPersonal(query, idDepartamento).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonalDTO obtenerPorId(Integer id) {
        Personal p = personalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal no encontrado"));
        return convertirADTO(p);
    }

    @Transactional
    public PersonalDTO crear(CrearPersonalRequest req) {
        String identificacion = req.getNumeroIdentificacion().trim();
        if (personalRepository.existsByNumeroIdentificacion(identificacion)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de identificación ya está registrado");
        }

        Departamento dep = departamentoRepository.findById(req.getIdDepartamento())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El departamento especificado no existe"));

        EstadoAutorizacion estado;
        if (req.getIdEstadoAutorizacion() != null) {
            estado = estadoAutorizacionRepository.findById(req.getIdEstadoAutorizacion())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado de autorización especificado no existe"));
        } else {
            estado = estadoAutorizacionRepository.findByNombreIgnoreCase("Otorgada")
                    .orElseGet(() -> estadoAutorizacionRepository.findAll().get(0));
        }

        Personal personal = new Personal();
        personal.setNumeroIdentificacion(identificacion);
        personal.setNombres(req.getNombres().trim());
        personal.setApellidos(req.getApellidos().trim());
        personal.setDepartamento(dep);
        personal.setEstadoAutorizacion(estado);

        Personal guardado = personalRepository.save(personal);
        return convertirADTO(guardado);
    }

    @Transactional
    public PersonalDTO actualizar(Integer id, ActualizarPersonalRequest req) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal no encontrado"));

        String identificacion = req.getNumeroIdentificacion().trim();
        Optional<Personal> existente = personalRepository.findByNumeroIdentificacion(identificacion);
        if (existente.isPresent() && !existente.get().getIdPersonal().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de identificación ya está registrado por otra persona");
        }

        Departamento dep = departamentoRepository.findById(req.getIdDepartamento())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El departamento especificado no existe"));

        if (req.getIdEstadoAutorizacion() != null) {
            EstadoAutorizacion estado = estadoAutorizacionRepository.findById(req.getIdEstadoAutorizacion())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado de autorización especificado no existe"));
            personal.setEstadoAutorizacion(estado);
        }

        personal.setNumeroIdentificacion(identificacion);
        personal.setNombres(req.getNombres().trim());
        personal.setApellidos(req.getApellidos().trim());
        personal.setDepartamento(dep);

        Personal guardado = personalRepository.save(personal);
        return convertirADTO(guardado);
    }

    @Transactional
    public PersonalDTO cambiarAutorizacion(Integer id, CambiarAutorizacionRequest req) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal no encontrado"));

        EstadoAutorizacion estado = null;
        if (req.getIdEstadoAutorizacion() != null) {
            estado = estadoAutorizacionRepository.findById(req.getIdEstadoAutorizacion())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado de autorización no existe"));
        } else if (req.getEstado() != null && !req.getEstado().isBlank()) {
            estado = estadoAutorizacionRepository.findByNombreIgnoreCase(req.getEstado().trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado '" + req.getEstado() + "' no es válido. Debe ser Otorgada, Revocada o Suspendida"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe especificar el nuevo estado de autorización");
        }

        personal.setEstadoAutorizacion(estado);
        Personal guardado = personalRepository.save(personal);
        return convertirADTO(guardado);
    }

    @Transactional
    public ResumenCargaMasivaDTO procesarCargaMasiva(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo CSV es requerido y no puede estar vacío");
        }

        String contenido;
        try {
            contenido = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo CSV", e);
        }
        if (contenido.startsWith("\ufeff")) {
            contenido = contenido.substring(1);
        }

        List<List<String>> registros;
        try {
            registros = parseCsv(contenido);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato CSV inválido: " + e.getMessage(), e);
        }

        if (registros.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo CSV está vacío");
        }

        List<String> encabezado = registros.get(0).stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        if (encabezado.size() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estructura de archivo inválida. Se esperan 4 columnas: numero_identificacion,nombres,apellidos,departamento");
        }

        if (!encabezado.equals(List.of("numero_identificacion", "nombres", "apellidos", "departamento"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Encabezado inválido. Las columnas requeridas y en orden son: numero_identificacion,nombres,apellidos,departamento");
        }

        EstadoAutorizacion estadoOtorgada = estadoAutorizacionRepository.findByNombreIgnoreCase("Otorgada")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No está configurado el estado de autorización 'Otorgada'"
                ));

        Set<String> identificacionesEnArchivo = new HashSet<>();
        List<Personal> entidadesAGuardar = new ArrayList<>();
        List<ResumenCargaMasivaDTO.FilaErrorDTO> errores = new ArrayList<>();

        int totalFilas = registros.size() - 1;

        for (int i = 1; i < registros.size(); i++) {
            int numFila = i + 1;
            List<String> partes = registros.get(i);

            if (partes.size() != 4) {
                String identificacionFila = partes.isEmpty() ? "" : partes.get(0).trim();
                errores.add(new ResumenCargaMasivaDTO.FilaErrorDTO(
                        numFila, identificacionFila, "Se requieren exactamente 4 columnas"
                ));
                continue;
            }

            String identificacion = partes.get(0).trim();
            String nombres = partes.get(1).trim();
            String apellidos = partes.get(2).trim();
            String nombreDepto = partes.get(3).trim();

            // Validación de campos vacíos
            if (identificacion.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || nombreDepto.isEmpty()) {
                errores.add(new ResumenCargaMasivaDTO.FilaErrorDTO(numFila, identificacion, "Campos obligatorios faltantes o vacíos"));
                continue;
            }

            // Validación de duplicado en el mismo archivo
            if (identificacionesEnArchivo.contains(identificacion)) {
                errores.add(new ResumenCargaMasivaDTO.FilaErrorDTO(numFila, identificacion, "Número de identificación duplicado en el mismo archivo"));
                continue;
            }

            // Validación de duplicado en base de datos
            if (personalRepository.existsByNumeroIdentificacion(identificacion)) {
                errores.add(new ResumenCargaMasivaDTO.FilaErrorDTO(numFila, identificacion, "Número de identificación ya registrado en el sistema"));
                continue;
            }

            // Validación de departamento existente
            Optional<Departamento> deptoOpt = departamentoRepository.findByNombreIgnoreCase(nombreDepto);
            if (deptoOpt.isEmpty()) {
                errores.add(new ResumenCargaMasivaDTO.FilaErrorDTO(numFila, identificacion, "El departamento '" + nombreDepto + "' no existe en el catálogo"));
                continue;
            }

            identificacionesEnArchivo.add(identificacion);

            Personal nuevo = new Personal();
            nuevo.setNumeroIdentificacion(identificacion);
            nuevo.setNombres(nombres);
            nuevo.setApellidos(apellidos);
            nuevo.setDepartamento(deptoOpt.get());
            nuevo.setEstadoAutorizacion(estadoOtorgada);
            entidadesAGuardar.add(nuevo);
        }

        // Incorporación dentro de la transacción
        if (!entidadesAGuardar.isEmpty()) {
            personalRepository.saveAll(entidadesAGuardar);
        }

        return new ResumenCargaMasivaDTO(totalFilas, entidadesAGuardar.size(), errores.size(), errores);
    }

    private List<List<String>> parseCsv(String contenido) {
        List<List<String>> registros = new ArrayList<>();
        List<String> fila = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        boolean comillaCerrada = false;

        for (int i = 0; i < contenido.length(); i++) {
            char caracter = contenido.charAt(i);
            if (entreComillas) {
                if (caracter == '"') {
                    if (i + 1 < contenido.length() && contenido.charAt(i + 1) == '"') {
                        campo.append('"');
                        i++;
                    } else {
                        entreComillas = false;
                        comillaCerrada = true;
                    }
                } else {
                    campo.append(caracter);
                }
                continue;
            }

            if (comillaCerrada) {
                if (caracter == ',') {
                    agregarCampo(fila, campo);
                    comillaCerrada = false;
                } else if (caracter == '\r' || caracter == '\n') {
                    agregarRegistro(registros, fila, campo);
                    comillaCerrada = false;
                    if (caracter == '\r' && i + 1 < contenido.length() && contenido.charAt(i + 1) == '\n') i++;
                } else if (!Character.isWhitespace(caracter)) {
                    throw new IllegalArgumentException("hay caracteres después de un campo entrecomillado");
                }
                continue;
            }

            if (caracter == '"') {
                if (campo.length() != 0) {
                    throw new IllegalArgumentException("comilla inesperada en un campo sin entrecomillar");
                }
                entreComillas = true;
            } else if (caracter == ',') {
                agregarCampo(fila, campo);
            } else if (caracter == '\r' || caracter == '\n') {
                agregarRegistro(registros, fila, campo);
                if (caracter == '\r' && i + 1 < contenido.length() && contenido.charAt(i + 1) == '\n') i++;
            } else {
                campo.append(caracter);
            }
        }

        if (entreComillas) {
            throw new IllegalArgumentException("campo entrecomillado sin cierre");
        }
        if (campo.length() > 0 || !fila.isEmpty() || comillaCerrada) {
            agregarRegistro(registros, fila, campo);
        }
        return registros;
    }

    private void agregarCampo(List<String> fila, StringBuilder campo) {
        fila.add(campo.toString());
        campo.setLength(0);
    }

    private void agregarRegistro(List<List<String>> registros, List<String> fila, StringBuilder campo) {
        agregarCampo(fila, campo);
        if (fila.stream().anyMatch(valor -> !valor.isBlank())) {
            registros.add(new ArrayList<>(fila));
        }
        fila.clear();
    }

    private PersonalDTO convertirADTO(Personal p) {
        return new PersonalDTO(
                p.getIdPersonal(),
                p.getNumeroIdentificacion(),
                p.getNombres(),
                p.getApellidos(),
                p.getDepartamento().getIdDepartamento(),
                p.getDepartamento().getNombre(),
                p.getEstadoAutorizacion().getIdEstadoAutorizacion(),
                p.getEstadoAutorizacion().getNombre()
        );
    }
}
