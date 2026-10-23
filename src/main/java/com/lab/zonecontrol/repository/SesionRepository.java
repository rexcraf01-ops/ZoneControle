package com.lab.zonecontrol.repository;

import com.lab.zonecontrol.entity.Sesion;
import com.lab.zonecontrol.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, UUID> {
    Optional<Sesion> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
    void deleteByUsuario(Usuario usuario);
}