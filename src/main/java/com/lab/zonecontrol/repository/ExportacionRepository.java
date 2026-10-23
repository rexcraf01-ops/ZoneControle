package com.lab.zonecontrol.repository;

import com.lab.zonecontrol.entity.Exportacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportacionRepository extends JpaRepository<Exportacion, Integer> {
    List<Exportacion> findAllByOrderByFechaHoraGeneracionDesc();
}
