package com.lab.zonecontrol.controller;

import com.lab.zonecontrol.dto.ExportarArchivoIntercambioRequest;
import com.lab.zonecontrol.dto.EnviarReporteIntercambioRequest;
import com.lab.zonecontrol.dto.ExportacionHistorialDTO;
import com.lab.zonecontrol.dto.GenerarArchivoIntercambioRequest;
import com.lab.zonecontrol.dto.ReporteActividadDepartamentoDTO;
import com.lab.zonecontrol.entity.Exportacion;
import com.lab.zonecontrol.service.HistorialService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping({"/api/archivo-intercambio", "/archivo-intercambio"})
public class ArchivoIntercambioController {
    private static final Logger logger = LoggerFactory.getLogger(ArchivoIntercambioController.class);

    private final HistorialService historialService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean mailEnabled;
    private final String mailFrom;

    public ArchivoIntercambioController(
            HistorialService historialService,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.from:no-reply@zonecontrol.local}") String mailFrom) {
        this.historialService = historialService;
        this.mailSenderProvider = mailSenderProvider;
        this.mailEnabled = mailEnabled;
        this.mailFrom = mailFrom;
    }

    @GetMapping("/exportaciones")
    public ResponseEntity<List<ExportacionHistorialDTO>> listarExportaciones() {
        return ResponseEntity.ok(historialService.listarExportaciones());
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generar(
            @Valid @RequestBody GenerarArchivoIntercambioRequest request,
            @RequestParam(value = "descargar", required = false, defaultValue = "false") boolean descargar,
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestParam(value = "formato", required = false, defaultValue = "CSV") String formato) {

        if (descargar || (acceptHeader != null && acceptHeader.contains("text/csv"))) {
            String formatoNormalizado = historialService.normalizarFormato(formato);
            byte[] archivo = historialService.generarReporte(request, formatoNormalizado);
            String extension = formatoNormalizado.equals("XLSX") ? "xlsx" : "csv";
            MediaType contentType = formatoNormalizado.equals("XLSX")
                    ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    : MediaType.parseMediaType("text/csv; charset=UTF-8");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"archivo_intercambio." + extension + "\"")
                    .contentType(contentType)
                    .body(archivo);
        }

        List<ReporteActividadDepartamentoDTO> reporte = historialService.generarReporteActividad(request);
        return ResponseEntity.ok(reporte);
    }

    @PostMapping("/exportar")
    public ResponseEntity<Map<String, Object>> exportar(
            @Valid @RequestBody ExportarArchivoIntercambioRequest request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        Exportacion exp = historialService.registrarExportacion(request, username);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("id_exportacion", exp.getIdExportacion());
        resp.put("mensaje", "Exportación registrada exitosamente");
        resp.put("fecha_hora", exp.getFechaHoraGeneracion());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, Object>> enviar(
            @Valid @RequestBody EnviarReporteIntercambioRequest request,
            Authentication authentication) {
        if (!mailEnabled) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El envío de reportes por correo no está habilitado");
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio SMTP no está configurado");
        }

        String formato = historialService.normalizarFormato(request.getFormato());
        GenerarArchivoIntercambioRequest periodo =
                new GenerarArchivoIntercambioRequest(request.getPeriodoInicio(), request.getPeriodoFin());
        byte[] archivo = historialService.generarReporte(periodo, formato);
        String extension = formato.equals("XLSX") ? "xlsx" : "csv";
        String contentType = formato.equals("XLSX")
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(request.getCorreoDestino());
            helper.setSubject("Reporte de actividad de acceso - Zone Control");
            helper.setText("Se adjunta el reporte de actividad por departamento para el periodo "
                    + request.getPeriodoInicio() + " al " + request.getPeriodoFin() + ".");
            helper.addAttachment("archivo_intercambio." + extension,
                    new org.springframework.core.io.ByteArrayResource(archivo), contentType);
            mailSender.send(message);
        } catch (MailException | MessagingException exception) {
            logger.error("No se pudo enviar el reporte de intercambio al destinatario configurado", exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo enviar el reporte. Verifique la configuración del servicio de correo.");
        }

        String username = authentication != null ? authentication.getName() : null;
        ExportarArchivoIntercambioRequest exportacion = new ExportarArchivoIntercambioRequest(
                request.getPeriodoInicio(), request.getPeriodoFin(), formato);
        Exportacion registrada = historialService.registrarExportacion(
                exportacion, username, request.getCorreoDestino());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id_exportacion", registrada.getIdExportacion());
        response.put("mensaje", "Reporte enviado correctamente");
        response.put("formato", formato);
        return ResponseEntity.ok(response);
    }
}
