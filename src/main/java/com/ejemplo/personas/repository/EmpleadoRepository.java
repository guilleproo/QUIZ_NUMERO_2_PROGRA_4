package com.ejemplo.personas.repository;

import com.ejemplo.personas.model.Empleado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Override
    @EntityGraph(attributePaths = "departamento")
    List<Empleado> findAll();

    @EntityGraph(attributePaths = "departamento")
    List<Empleado> findByDepartamentoId(Long departamentoId);

    @EntityGraph(attributePaths = "departamento")
    Optional<Empleado> findWithDepartamentoById(Long id);

    boolean existsByCedula(String cedula);

    boolean existsByCedulaAndIdNot(String cedula, Long id);

    long countByDepartamentoId(Long departamentoId);

    @Query("SELECT COALESCE(SUM(e.salario), 0) FROM Empleado e WHERE e.departamento.id = :departamentoId")
    BigDecimal sumarSalariosPorDepartamento(@Param("departamentoId") Long departamentoId);
}