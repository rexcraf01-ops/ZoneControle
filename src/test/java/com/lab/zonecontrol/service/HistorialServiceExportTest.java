package com.lab.zonecontrol.service;

import com.lab.zonecontrol.dto.GenerarArchivoIntercambioRequest;
import com.lab.zonecontrol.repository.ExportacionRepository;
import com.lab.zonecontrol.repository.IntentoIngresoRepository;
import com.lab.zonecontrol.repository.UsuarioRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HistorialServiceExportTest {
    private IntentoIngresoRepository intentoIngresoRepository;
    private HistorialService historialService;

    @BeforeEach
    void setUp() {
        intentoIngresoRepository = mock(IntentoIngresoRepository.class);
        historialService = new HistorialService(
                intentoIngresoRepository,
                mock(ExportacionRepository.class),
                mock(UsuarioRepository.class));
        when(intentoIngresoRepository.resumenPorDepartamento(any(), any()))
                .thenReturn(Collections.singletonList(new Object[]{"Sistemas", 12L, 8L, 3L, 1L}));
    }

    @Test
    void generaExcelConPlantillaYColumnasEsperadas() throws Exception {
        byte[] content = historialService.generarReporte(
                new GenerarArchivoIntercambioRequest(
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)),
                "XLSX");

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            var sheet = workbook.getSheet("Reporte de actividad");
            assertEquals("Zone Control | Reporte de actividad", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Departamento", sheet.getRow(4).getCell(0).getStringCellValue());
            assertEquals("Total de intentos", sheet.getRow(4).getCell(1).getStringCellValue());
            assertEquals("Sistemas", sheet.getRow(5).getCell(0).getStringCellValue());
            assertEquals(12, (int) sheet.getRow(5).getCell(1).getNumericCellValue());
        }
    }

    @Test
    void generaCsvConLaMismaPlantillaDeColumnas() {
        String csv = new String(historialService.generarReporte(
                new GenerarArchivoIntercambioRequest(
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)),
                "CSV"), StandardCharsets.UTF_8);

        assertTrue(csv.contains("Departamento,Total de intentos,Autorizados,Denegados,No registrados"));
        assertTrue(csv.contains("\"Sistemas\",12,8,3,1"));
    }

    @Test
    void rechazaFormatoDesconocido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> historialService.normalizarFormato("PDF"));
        assertEquals(400, exception.getStatusCode().value());
    }
}
