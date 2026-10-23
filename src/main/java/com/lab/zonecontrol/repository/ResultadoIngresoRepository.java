package com.lab.zonecontrol.repository;

import com.lab.zonecontrol.entity.ResultadoIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoIngresoRepository extends JpaRepository<ResultadoIngreso, Integer> {
    Optional<ResultadoIngreso> findByNombreIgnoreCase(String nombre);
}
