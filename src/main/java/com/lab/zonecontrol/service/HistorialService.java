package com.lab.zonecontrol.service;

import com.lab.zonecontrol.dto.ExportarArchivoIntercambioRequest;
import com.lab.zonecontrol.dto.ExportacionHistorialDTO;
import com.lab.zonecontrol.dto.GenerarArchivoIntercambioRequest;
import com.lab.zonecontrol.dto.HistorialAccesoDTO;
import com.lab.zonecontrol.dto.ReporteActividadDepartamentoDTO;
import com.lab.zonecontrol.entity.Exportacion;
import com.lab.zonecontrol.entity.IntentoIngreso;
import com.lab.zonecontrol.entity.Usuario;
import com.lab.zonecontrol.entity.Departamento;
import com.lab.zonecontrol.repository.ExportacionRepository;
import com.lab.zonecontrol.repository.IntentoIngresoRepository;
import com.lab.zonecontrol.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class HistorialService {

    private final IntentoIngresoRepository intentoIngresoRepository;
    private final ExportacionRepository exportacionRepository;
    private final UsuarioRepository usuarioRepository;

    public HistorialService(IntentoIngresoRepository intentoIngresoRepository,
                            ExportacionRepository exportacionRepository,
                            UsuarioRepository usuarioRepository) {
        this.intentoIngresoRepository = intentoIngresoRepository;
        this.exportacionRepository = exportacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<HistorialAccesoDTO> consultarHistorial(String idPersonal, String desdeStr, String hastaStr) {
        OffsetDateTime desde = parseFechaInicio(desdeStr);
        OffsetDateTime hasta = parseFechaFin(hastaStr);

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha inicial no puede ser posterior a la fecha final");
        }

        Integer idNum = null;
        String identStr = null;
        if (idPersonal != null && !idPersonal.trim().isEmpty()) {
            String val = idPersonal.trim();
            identStr = val;
            try {
                idNum = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
            }
        }

        List<IntentoIngreso> lista = intentoIngresoRepository.findHistorial(idNum, identStr, desde, hasta);

        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] exportarHistorialCSV(String idPersonal, String desdeStr, String hastaStr) {
        List<HistorialAccesoDTO> lista = consultarHistorial(idPersonal, desdeStr, hastaStr);
        if (lista.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay registros de accesos para los filtros especificados");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("id_intento,numero_identificacion,persona,departamento,resultado,fecha_hora\n");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (HistorialAccesoDTO h : lista) {
            String fh = h.getFechaHora() != null ? h.getFechaHora().format(dtf) : "";
            sb.append(h.getIdIntento()).append(",")
              .append('"').append(h.getNumeroIdentificacion() != null ? h.getNumeroIdentificacion() : "").append("\",")
              .append('"').append(h.getPersona() != null ? h.getPersona() : "").append("\",")
              .append('"').append(h.getDepartamento() != null ? h.getDepartamento() : "").append("\",")
              .append('"').append(h.getResultado() != null ? h.getResultado() : "").append("\",")
              .append('"').append(fh).append("\"\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public List<ReporteActividadDepartamentoDTO> generarReporteActividad(GenerarArchivoIntercambioRequest req) {
        if (req.getPeriodoInicio() == null || req.getPeriodoFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las fechas de periodo inicio y fin son obligatorias");
        }

        if (req.getPeriodoInicio().isAfter(req.getPeriodoFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El periodo de inicio no puede ser posterior al periodo de fin");
        }

        OffsetDateTime desde = req.getPeriodoInicio().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = req.getPeriodoFin().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        List<Object[]> filas = intentoIngresoRepository.resumenPorDepartamento(desde, hasta);

        List<ReporteActividadDepartamentoDTO> resultado = new ArrayList<>();
        long totalGeneral = 0;
        for (Object[] f : filas) {
            String depto = (String) f[0];
            long total = ((Number) f[1]).longValue();
            long auto = ((Number) f[2]).longValue();
            long deneg = ((Number) f[3]).longValue();
            long noReg = ((Number) f[4]).longValue();
            totalGeneral += total;
            resultado.add(new ReporteActividadDepartamentoDTO(depto, total, auto, deneg, noReg));
        }

        if (resultado.isEmpty() || totalGeneral == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se registró actividad de acceso en el periodo seleccionado");
        }

        return resultado;
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteCSV(GenerarArchivoIntercambioRequest req) {
        List<ReporteActividadDepartamentoDTO> reporte = generarReporteActividad(req);

        StringBuilder sb = new StringBuilder();
        sb.append('\uFEFF');
        sb.append("Plantilla Zone Control - Reporte de actividad\n");
        sb.append("Periodo desde,").append(req.getPeriodoInicio()).append("\n");
        sb.append("Periodo hasta,").append(req.getPeriodoFin()).append("\n\n");
        sb.append("Departamento,Total de intentos,Autorizados,Denegados,No registrados\n");
        for (ReporteActividadDepartamentoDTO r : reporte) {
            sb.append(csvValue(r.getDepartamento())).append(',')
              .append(r.getTotalIntentos()).append(",")
              .append(r.getAutorizados()).append(",")
              .append(r.getDenegados()).append(",")
              .append(r.getNoRegistrados()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteExcel(GenerarArchivoIntercambioRequest req) {
        List<ReporteActividadDepartamentoDTO> reporte = generarReporteActividad(req);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte de actividad");
            sheet.setColumnWidth(0, 36 * 256);
            for (int column = 1; column < 5; column++) sheet.setColumnWidth(column, 20 * 256);

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("Zone Control | Reporte de actividad");
            title.getCell(0).setCellStyle(titleStyle);
            sheet.createRow(1).createCell(0).setCellValue("Periodo desde: " + req.getPeriodoInicio());
            sheet.createRow(2).createCell(0).setCellValue("Periodo hasta: " + req.getPeriodoFin());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"Departamento", "Total de intentos", "Autorizados", "Denegados", "No registrados"};
            Row header = sheet.createRow(4);
            for (int column = 0; column < headers.length; column++) {
                Cell cell = header.createCell(column);
                cell.setCellValue(headers[column]);
                cell.setCellStyle(headerStyle);
            }
            int rowIndex = 5;
            for (ReporteActividadDepartamentoDTO item : reporte) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getDepartamento());
                row.createCell(1).setCellValue(item.getTotalIntentos());
                row.createCell(2).setCellValue(item.getAutorizados());
                row.createCell(3).setCellValue(item.getDenegados());
                row.createCell(4).setCellValue(item.getNoRegistrados());
            }
            sheet.createFreezePane(0, 5);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo generar el archivo Excel del reporte", exception);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporte(GenerarArchivoIntercambioRequest request, String formato) {
        return switch (normalizarFormato(formato)) {
            case "CSV" -> generarReporteCSV(request);
            case "XLSX" -> generarReporteExcel(request);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El formato debe ser CSV o XLSX");
        };
    }

    public String normalizarFormato(String formato) {
        String normalizado = formato == null ? "CSV" : formato.trim().toUpperCase();
        if (!normalizado.equals("CSV") && !normalizado.equals("XLSX")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El formato debe ser CSV o XLSX");
        }
        return normalizado;
    }

    private String csvValue(String value) {
        String escaped = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    @Transactional
    public Exportacion registrarExportacion(ExportarArchivoIntercambioRequest req, String username) {
        return registrarExportacion(req, username, null);
    }

    @Transactional
    public Exportacion registrarExportacion(
            ExportarArchivoIntercambioRequest req, String username, String correoDestino) {
        if (req.getPeriodoInicio() == null || req.getPeriodoFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las fechas de periodo son obligatorias");
        }

        if (req.getPeriodoInicio().isAfter(req.getPeriodoFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El periodo de inicio no puede ser posterior al periodo de fin");
        }

        Usuario usuario = null;
        if (username != null) {
            usuario = usuarioRepository.findByNombreUsuario(username).orElse(null);
        }

        Exportacion exp = new Exportacion();
        exp.setFechaHoraGeneracion(OffsetDateTime.now());
        exp.setPeriodoInicio(req.getPeriodoInicio());
        exp.setPeriodoFin(req.getPeriodoFin());
        exp.setFormato(normalizarFormato(req.getFormato()));
        exp.setCorreoDestino(correoDestino);
        exp.setUsuario(usuario);

        return exportacionRepository.save(exp);
    }

    @Transactional(readOnly = true)
    public List<ExportacionHistorialDTO> listarExportaciones() {
        return exportacionRepository.findAllByOrderByFechaHoraGeneracionDesc().stream()
                .map(exportacion -> new ExportacionHistorialDTO(
                        exportacion.getIdExportacion(),
                        exportacion.getFechaHoraGeneracion(),
                        exportacion.getPeriodoInicio(),
                        exportacion.getPeriodoFin(),
                        exportacion.getFormato(),
                        exportacion.getCorreoDestino(),
                        exportacion.getUsuario() != null
                                ? exportacion.getUsuario().getNombreUsuario()
                                : null
                ))
                .toList();
    }

    private OffsetDateTime parseFechaInicio(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            if (s.contains("T")) {
                return OffsetDateTime.parse(s.trim());
            }
            LocalDate ld = LocalDate.parse(s.trim());
            return ld.atStartOfDay().atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha 'desde' inválido: " + s);
        }
    }

    private OffsetDateTime parseFechaFin(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            if (s.contains("T")) {
                return OffsetDateTime.parse(s.trim());
            }
            LocalDate ld = LocalDate.parse(s.trim());
            return ld.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha 'hasta' inválido: " + s);
        }
    }

    private HistorialAccesoDTO convertirADTO(IntentoIngreso i) {
        String nombres = i.getPersonal() != null ? i.getPersonal().getNombres() : null;
        String apellidos = i.getPersonal() != null ? i.getPersonal().getApellidos() : null;
        String depto = (i.getPersonal() != null && i.getPersonal().getDepartamento() != null)
                ? i.getPersonal().getDepartamento().getNombre()
                : null;
        String res = i.getResultadoIngreso() != null ? i.getResultadoIngreso().getNombre() : "Desconocido";

        return new HistorialAccesoDTO(
                i.getIdIntento(),
                i.getNumeroIdentificacionIngresado(),
                nombres,
                apellidos,
                depto,
                res,
                i.getFechaHora()
        );
    }
}
