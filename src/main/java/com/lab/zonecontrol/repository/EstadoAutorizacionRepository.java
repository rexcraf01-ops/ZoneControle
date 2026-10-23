package com.lab.zonecontrol.repository;

import com.lab.zonecontrol.entity.EstadoAutorizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoAutorizacionRepository extends JpaRepository<EstadoAutorizacion, Integer> {
    Optional<EstadoAutorizacion> findByNombreIgnoreCase(String nombre);
}
