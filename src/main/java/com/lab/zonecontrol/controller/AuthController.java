package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.dto.AuthResponse;
import com.lab.zonecontrol.dto.ApiResponse;
import com.lab.zonecontrol.dto.ChangePasswordRequest;
import com.lab.zonecontrol.dto.ForgotPasswordRequest;
import com.lab.zonecontrol.dto.LoginRequest;
import com.lab.zonecontrol.dto.ResetPasswordRequest;
import com.lab.zonecontrol.entity.PasswordResetToken;
import com.lab.zonecontrol.entity.Sesion;
import com.lab.zonecontrol.entity.Usuario;
import com.lab.zonecontrol.repository.PasswordResetTokenRepository;
import com.lab.zonecontrol.repository.SesionRepository;
import com.lab.zonecontrol.repository.UsuarioRepository;
import com.lab.zonecontrol.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String RESET_REQUEST_MESSAGE =
            "Si el correo corresponde a una cuenta, recibirás instrucciones para restablecer la contraseña.";

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.security.cookie.secure:false}")
    private boolean secureCookie;
    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;
    @Value("${spring.mail.host:}")
    private String mailHost;
    @Value("${app.mail.from:no-reply@zonecontrol.local}")
    private String mailFrom;
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          SesionRepository sesionRepository,
                          PasswordResetTokenRepository passwordResetTokenRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderProvider = mailSenderProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getContrasena())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, null, null, null, "usuario o contraseña inválidos"));
        }

        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, null, null, null, "usuario o contraseña inválidos"));
        }

        final String jwt = jwtUtil.generateToken(usuario.getNombreUsuario(), usuario.getRol().getNombre());

        Sesion sesion = new Sesion();
        sesion.setIdSesion(UUID.randomUUID());
        sesion.setUsuario(usuario);
        sesion.setTokenHash(jwtUtil.hashToken(jwt));
        sesion.setExpiracion(jwtUtil.extractExpiration(jwt).toInstant());
        sesion.setCreadoEn(Instant.now());
        sesionRepository.save(sesion);

        ResponseCookie cookie = ResponseCookie.from("session_token", jwt)
                .path("/")
                .httpOnly(true)
                .secure(secureCookie)
                .maxAge(86400)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(true, null, usuario.getNombreUsuario(),
                        usuario.getRol().getNombre(), "Inicio de sesión exitoso"));
    }

    @PostMapping("/password/forgot")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> solicitarRestablecimiento(
            @Valid @RequestBody ForgotPasswordRequest request) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (!mailEnabled || mailHost == null || mailHost.isBlank() || mailSender == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("El servicio de recuperación por correo no está configurado."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(request.getEmail());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = generarToken();

            passwordResetTokenRepository.deleteByUsuario(usuario);
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setTokenHash(hashResetToken(token));
            resetToken.setUsuario(usuario);
            resetToken.setCreatedAt(Instant.now());
            resetToken.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
            passwordResetTokenRepository.saveAndFlush(resetToken);

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(usuario.getEmail());
                message.setSubject("Restablecimiento de contraseña - Zone Control");
                message.setText("Se solicitó restablecer la contraseña de tu cuenta. Abre este enlace dentro de "
                        + "30 minutos para elegir una nueva contraseña:\n\n"
                        + frontendUrl.replaceAll("/+$", "")
                        + "/restablecer-contrasena?token=" + token
                        + "\n\nSi no solicitaste este cambio, ignora este mensaje.");
                mailSender.send(message);
            } catch (MailException exception) {
                passwordResetTokenRepository.deleteByUsuario(usuario);
                logger.error("No se pudo enviar un correo de recuperación de contraseña.", exception);
            }
        }

        return ResponseEntity.accepted().body(ApiResponse.ok(RESET_REQUEST_MESSAGE, null));
    }

    @PostMapping("/password/reset")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> restablecerContrasena(
            @Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenHashForUpdate(hashResetToken(request.getToken()))
                .orElse(null);
        if (resetToken == null || !resetToken.getExpiresAt().isAfter(Instant.now())) {
            if (resetToken != null) passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity.badRequest().body(ApiResponse.error("El enlace es inválido o ha vencido."));
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setContrasenaHash(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);
        sesionRepository.deleteByUsuario(usuario);
        passwordResetTokenRepository.deleteByUsuario(usuario);
        return ResponseEntity.ok(ApiResponse.ok("La contraseña se restableció correctamente.", null));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(authentication.getName()).orElse(null);
        if (usuario == null || !passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasenaHash())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("La contraseña actual es incorrecta."));
        }
        if (passwordEncoder.matches(request.getNuevaContrasena(), usuario.getContrasenaHash())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("La nueva contraseña debe ser diferente."));
        }

        usuario.setContrasenaHash(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);
        sesionRepository.deleteByUsuario(usuario);
        passwordResetTokenRepository.deleteByUsuario(usuario);

        ResponseCookie cookie = ResponseCookie.from("session_token", "")
                .path("/")
                .httpOnly(true)
                .secure(secureCookie)
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok("La contraseña se actualizó. Inicie sesión nuevamente.", null));
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<AuthResponse> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                               HttpServletRequest request) {
        String token = obtenerToken(authHeader, request);
        if (token != null && !token.isBlank()) {
            sesionRepository.deleteByTokenHash(jwtUtil.hashToken(token));
        }

        ResponseCookie cookie = ResponseCookie.from("session_token", "")
                .path("/")
                .httpOnly(true)
                .secure(secureCookie)
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(true, null, null, null, "Sesión cerrada"));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                           HttpServletRequest request) {
        String token = obtenerToken(authHeader, request);

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, null, null, null, "No autenticado"));
        }

        final String username;
        try {
            username = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, null, null, null, "Token inválido o expirado"));
            }
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, null, null, null, "Token inválido o expirado"));
        }

        boolean sesionActiva = sesionRepository.findByTokenHash(jwtUtil.hashToken(token))
                .map(Sesion::getExpiracion)
                .map(expiracion -> expiracion.isAfter(Instant.now()))
                .orElse(false);
        if (!sesionActiva) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, null, null, null, "La sesión fue cerrada o expiró"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, null, null, null, "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        return ResponseEntity.ok(new AuthResponse(true, null, usuario.getNombreUsuario(),
                usuario.getRol().getNombre(), "Autenticado"));
    }

    private String obtenerToken(String authHeader, HttpServletRequest request) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("session_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashResetToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible.", exception);
        }
    }
}