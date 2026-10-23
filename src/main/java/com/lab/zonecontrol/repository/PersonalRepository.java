package com.lab.zonecontrol.repository;

import com.lab.zonecontrol.entity.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Integer> {
    Optional<Personal> findByNumeroIdentificacion(String numeroIdentificacion);

    boolean existsByNumeroIdentificacion(String numeroIdentificacion);

    @Query("SELECT p FROM Personal p WHERE " +
           "(:idDepartamento IS NULL OR p.departamento.idDepartamento = :idDepartamento) AND " +
           "(:q IS NULL OR :q = '' OR " +
           "LOWER(p.numeroIdentificacion) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY p.apellidos, p.nombres")
    List<Personal> buscarPersonal(@Param("q") String q, @Param("idDepartamento") Integer idDepartamento);
}
