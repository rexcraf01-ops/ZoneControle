package com.lab.zonecontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.zonecontrol.repository.DepartamentoRepository;
import com.lab.zonecontrol.repository.RolRepository;
import com.lab.zonecontrol.dto.LecturaCredencialRequest;
import com.lab.zonecontrol.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class ZoneControlApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Test
    @DisplayName("1. Login con credenciales válidas y erróneas")
    void testLoginExitosoYFallido() throws Exception {
        // Login exitoso con admin / 1234
        LoginRequest reqOk = new LoginRequest("admin", "1234");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqOk)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.nombre_usuario", is("admin")))
                .andExpect(jsonPath("$.rol", is("Administrador del sistema")))
                .andExpect(header().string("Cache-Control", containsString("no-cache")))
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("Cache-Control", containsString("must-revalidate")))
                .andExpect(header().string("Pragma", is("no-cache")))
                .andExpect(header().string("Expires", is("0")))
                .andReturn();

        jakarta.servlet.http.Cookie sessionCookie = loginResult.getResponse().getCookie("session_token");
        assertNotNull(sessionCookie);
        org.junit.jupiter.api.Assertions.assertTrue(loginResult.getResponse().getHeader("Set-Cookie").contains("HttpOnly"));
        org.junit.jupiter.api.Assertions.assertFalse(loginResult.getResponse().getContentAsString().contains("\"token\""));
        mockMvc.perform(get("/api/auth/me").cookie(sessionCookie))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/logout").cookie(sessionCookie))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/auth/me").cookie(sessionCookie))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("Pragma", is("no-cache")))
                .andExpect(header().string("Expires", is("0")));

        // Login fallido con contraseña incorrecta
        LoginRequest reqFail = new LoginRequest("admin", "clave_erronea");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqFail)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.mensaje", is("usuario o contraseña inválidos")));
    }

    @Test
    @DisplayName("2. Rol sin permiso devuelve 403 con 'La función no corresponde a su rol'")
    @WithMockUser(username = "gestor", roles = {"GESTOR", "GESTOR_DE_PERSONAL"})
    void testRolSinPermisoDevuelve403() throws Exception {
        // Gestor intentando acceder a ruta de Administrador (/api/usuarios-internos)
        mockMvc.perform(get("/api/usuarios-internos"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.mensaje", is("La función no corresponde a su rol")));
    }

    @Test
    @DisplayName("3. Lector de credenciales: AUTORIZADO, DENEGADO y NO_REGISTRADO")
    void testLecturaCredencialesTresResultados() throws Exception {
        // 1. Autorizado (12345678 - Carlos Mendoza, Otorgada)
        LecturaCredencialRequest reqAut = new LecturaCredencialRequest("12345678");
        mockMvc.perform(post("/api/acceso/lectura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqAut)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado", is("AUTORIZADO")))
                .andExpect(jsonPath("$.mensaje", is("Ingreso autorizado")));

        // 2. Denegado (11223344 - Ana Martínez, Revocada)
        LecturaCredencialRequest reqDen = new LecturaCredencialRequest("11223344");
        mockMvc.perform(post("/api/acceso/lectura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDen)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado", is("DENEGADO")))
                .andExpect(jsonPath("$.mensaje", is("Ingreso denegado")));

        // 3. No registrado (identificación no existente)
        LecturaCredencialRequest reqNoReg = new LecturaCredencialRequest("00000000");
        mockMvc.perform(post("/api/acceso/lectura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqNoReg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado", is("NO_REGISTRADO")))
                .andExpect(jsonPath("$.mensaje", is("Persona no registrada")));
    }

    @Test
    @DisplayName("4. Gestor puede operar personal pero no ejecutar carga masiva")
    @WithMockUser(username = "gestor", roles = {"GESTOR"})
    void testGestorNoPuedeUsarCargaMasiva() throws Exception {
        mockMvc.perform(get("/api/personal"))
                .andExpect(status().isOk());

        MockMultipartFile csv = new MockMultipartFile(
                "file", "valido.csv", "text/csv",
                "numero_identificacion,nombres,apellidos,departamento\n".getBytes()
        );
        mockMvc.perform(multipart("/api/personal/carga-masiva").file(csv))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje", is("La función no corresponde a su rol")));
    }

    @Test
    @DisplayName("5. Administrador tiene acceso global y puede ejecutar carga masiva")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdministradorTieneAccesoGlobalIncluidaCargaMasiva() throws Exception {
        mockMvc.perform(get("/api/personal"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/historial-accesos"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios-internos"))
                .andExpect(status().isOk());

        // Archivo con encabezado incorrecto -> 400
        MockMultipartFile csvInvalido = new MockMultipartFile(
                "file", "invalido.csv", "text/csv",
                "col1,col2,col3\n1,2,3".getBytes()
        );
        mockMvc.perform(multipart("/api/personal/carga-masiva").file(csvInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("Estructura de archivo inválida")));

    }

    @Test
    @DisplayName("Supervisor puede consultar el historial de exportaciones")
    @WithMockUser(username = "supervisor", roles = {"SUPERVISOR"})
    void testSupervisorConsultaHistorialExportaciones() throws Exception {
        mockMvc.perform(get("/api/archivo-intercambio/exportaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("6. Crear usuario registra y vincula nombre e identificación de personal")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    void testCrearUsuarioConDatosDePersonal() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String username = "it-" + suffix;
        String identificacion = "IT" + suffix;
        Integer roleId = rolRepository.findByNombre("Administrador del sistema")
                .orElseThrow()
                .getIdRol();
        Integer departmentId = departamentoRepository.findByNombreIgnoreCase("Sistemas")
                .orElseThrow()
                .getIdDepartamento();

        String payload = objectMapper.writeValueAsString(Map.of(
                "nombre_usuario", username,
                "contrasena", "clave-temporal",
                "id_rol", roleId,
                "numero_identificacion", identificacion,
                "nombres", "Persona",
                "apellidos", "De prueba",
                "id_departamento", departmentId
        ));

        mockMvc.perform(post("/api/usuarios-internos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre_usuario", is(username)))
                .andExpect(jsonPath("$.personal_nombre", is("Persona De prueba")))
                .andExpect(jsonPath("$.numero_identificacion", is(identificacion)));

        mockMvc.perform(post("/api/usuarios-internos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre_usuario", username + "-otro",
                                "contrasena", "clave-temporal",
                                "id_rol", roleId,
                                "numero_identificacion", identificacion,
                                "nombres", "Persona",
                                "apellidos", "De prueba",
                                "id_departamento", departmentId
                        ))))
                .andExpect(status().isBadRequest());
    }
}
