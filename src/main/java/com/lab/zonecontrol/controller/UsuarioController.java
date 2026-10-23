package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.dto.ActualizarUsuarioRequest;
import com.lab.zonecontrol.dto.CrearUsuarioRequest;
import com.lab.zonecontrol.dto.UsuarioDTO;
import com.lab.zonecontrol.entity.Departamento;
import com.lab.zonecontrol.entity.EstadoAutorizacion;
import com.lab.zonecontrol.entity.Personal;
import com.lab.zonecontrol.entity.Rol;
import com.lab.zonecontrol.entity.Usuario;
import com.lab.zonecontrol.repository.DepartamentoRepository;
import com.lab.zonecontrol.repository.EstadoAutorizacionRepository;
import com.lab.zonecontrol.repository.PersonalRepository;
import com.lab.zonecontrol.repository.PasswordResetTokenRepository;
import com.lab.zonecontrol.repository.RolRepository;
import com.lab.zonecontrol.repository.SesionRepository;
import com.lab.zonecontrol.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/usuarios-internos", "/usuarios-internos"})
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PersonalRepository personalRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;
    private final SesionRepository sesionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             RolRepository rolRepository,
                             PersonalRepository personalRepository,
                             DepartamentoRepository departamentoRepository,
                             EstadoAutorizacionRepository estadoAutorizacionRepository,
                             SesionRepository sesionRepository,
                             PasswordResetTokenRepository passwordResetTokenRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.personalRepository = personalRepository;
        this.departamentoRepository = departamentoRepository;
        this.estadoAutorizacionRepository = estadoAutorizacionRepository;
        this.sesionRepository = sesionRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> lista = usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        if (usuarioRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya existe");
        }

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol especificado no existe"));

        String numeroIdentificacion = request.getNumeroIdentificacion().trim();
        Personal personal = request.getIdPersonal() == null
                ? personalRepository.findByNumeroIdentificacion(numeroIdentificacion)
                        .orElseGet(() -> crearPersonalVinculado(request, numeroIdentificacion))
                : personalRepository.findById(request.getIdPersonal())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "El personal especificado no existe"));

        if (!personal.getNumeroIdentificacion().equals(numeroIdentificacion)
                || !personal.getNombres().equalsIgnoreCase(request.getNombres().trim())
                || !personal.getApellidos().equalsIgnoreCase(request.getApellidos().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La identificación o el nombre no coinciden con el registro de personal");
        }
        if (usuarioRepository.existsByPersonal_IdPersonal(personal.getIdPersonal())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El personal ya está vinculado a otro usuario");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario().trim());
        comprobarCorreoDisponible(request.getEmail(), null);
        usuario.setEmail(request.getEmail());
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena().trim()));
        usuario.setRol(rol);
        usuario.setPersonal(personal);

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(guardado));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable("id") Integer id,
                                                 @Valid @RequestBody ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (request.getNombreUsuario() != null && !request.getNombreUsuario().isBlank()) {
            String nuevoNombre = request.getNombreUsuario().trim();
            Optional<Usuario> existente = usuarioRepository.findByNombreUsuario(nuevoNombre);
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya existe");
            }
            usuario.setNombreUsuario(nuevoNombre);
        }

        if (request.isEmailProvided()) {
            comprobarCorreoDisponible(request.getEmail(), id);
            if (!Objects.equals(usuario.getEmail(), request.getEmail())) {
                passwordResetTokenRepository.deleteByUsuario(usuario);
            }
            usuario.setEmail(request.getEmail());
        }

        if (request.getIdRol() != null) {
            Rol rol = rolRepository.findById(request.getIdRol())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol especificado no existe"));
            usuario.setRol(rol);
        }

        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena().trim()));
            sesionRepository.deleteByUsuario(usuario);
            passwordResetTokenRepository.deleteByUsuario(usuario);
        }

        if (request.getIdPersonal() != null) {
            Personal personal = personalRepository.findById(request.getIdPersonal())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El personal especificado no existe"));

            // Validar unicidad 1:1
            List<Usuario> existentes = usuarioRepository.findAll();
            for (Usuario u : existentes) {
                if (!u.getIdUsuario().equals(id) && u.getPersonal() != null && u.getPersonal().getIdPersonal().equals(request.getIdPersonal())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El personal ya está vinculado a otro usuario");
                }
            }
            usuario.setPersonal(personal);
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(convertirADTO(actualizado));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id, Authentication authentication) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (usuario.getNombreUsuario().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puede eliminar su propia cuenta");
        }
        sesionRepository.deleteByUsuario(usuario);
        usuarioRepository.delete(usuario);
        return ResponseEntity.noContent().build();
    }

    private UsuarioDTO convertirADTO(Usuario u) {
        Integer idPersonal = u.getPersonal() != null ? u.getPersonal().getIdPersonal() : null;
        String personalNombre = u.getPersonal() != null
                ? (u.getPersonal().getNombres() + " " + u.getPersonal().getApellidos()).trim()
                : null;
        String numIdentificacion = u.getPersonal() != null ? u.getPersonal().getNumeroIdentificacion() : null;

        return new UsuarioDTO(
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getEmail(),
                u.getRol().getIdRol(),
                u.getRol().getNombre(),
                idPersonal,
                personalNombre,
                numIdentificacion
        );
    }

    private Personal crearPersonalVinculado(CrearUsuarioRequest request, String numeroIdentificacion) {
        Departamento departamento = departamentoRepository.findById(request.getIdDepartamento())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El departamento especificado no existe"));
        EstadoAutorizacion estado = estadoAutorizacionRepository.findByNombreIgnoreCase("Otorgada")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "No está configurado el estado inicial de autorización"));

        Personal personal = new Personal();
        personal.setNumeroIdentificacion(numeroIdentificacion);
        personal.setNombres(request.getNombres().trim());
        personal.setApellidos(request.getApellidos().trim());
        personal.setDepartamento(departamento);
        personal.setEstadoAutorizacion(estado);
        return personalRepository.save(personal);
    }

    private void comprobarCorreoDisponible(String email, Integer idUsuario) {
        if (email == null) return;
        boolean ocupado = idUsuario == null
                ? usuarioRepository.findByEmailIgnoreCase(email).isPresent()
                : usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot(email, idUsuario);
        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya está asignado a otra cuenta");
        }
    }
}