package com.lab.zonecontrol.repository;

import com.lab.zonecontrol.entity.IntentoIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface IntentoIngresoRepository extends JpaRepository<IntentoIngreso, Integer> {

    @Query("SELECT i FROM IntentoIngreso i WHERE " +
           "(:idPersonal IS NULL OR i.personal.idPersonal = :idPersonal OR i.numeroIdentificacionIngresado = :identificacion) AND " +
           "(CAST(:desde AS string) IS NULL OR i.fechaHora >= :desde) AND " +
           "(CAST(:hasta AS string) IS NULL OR i.fechaHora <= :hasta) " +
           "ORDER BY i.fechaHora DESC")
    List<IntentoIngreso> findHistorial(
            @Param("idPersonal") Integer idPersonal,
            @Param("identificacion") String identificacion,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta
    );

    @Query(value = "SELECT " +
            "COALESCE(d.nombre, 'Sin departamento asignado') AS departamento, " +
            "COUNT(ii.id_intento) AS total_intentos, " +
            "COUNT(*) FILTER (WHERE ri.nombre = 'Autorizado') AS autorizados, " +
            "COUNT(*) FILTER (WHERE ri.nombre = 'Denegado') AS denegados, " +
            "COUNT(*) FILTER (WHERE ri.nombre = 'No registrado') AS no_registrados " +
            "FROM intento_ingreso ii " +
            "LEFT JOIN personal p ON ii.id_personal = p.id_personal " +
            "LEFT JOIN departamento d ON p.id_departamento = d.id_departamento " +
            "INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso " +
            "WHERE ii.fecha_hora >= :desde AND ii.fecha_hora <= :hasta " +
            "GROUP BY d.nombre " +
            "ORDER BY total_intentos DESC", nativeQuery = true)
    List<Object[]> resumenPorDepartamento(
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta
    );
}
