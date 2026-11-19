package com.ejemplo.personas.service;

import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.repository.DepartamentoRepository;
import com.ejemplo.personas.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               EmpleadoRepository empleadoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarTodos() {
        return departamentoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Departamento buscarPorId(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado con id: " + id));
    }

    public Departamento crear(Departamento departamento) {
        String nombreLimpio = departamento.getNombre().trim();
        departamento.setNombre(nombreLimpio);
        departamento.setUbicacion(departamento.getUbicacion().trim());

        if (departamentoRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
        }

        return departamentoRepository.save(departamento);
    }

    public Departamento actualizar(Long id, Departamento datosActualizados) {
        Departamento existente = buscarPorId(id);

        String nombreLimpio = datosActualizados.getNombre().trim();
        if (departamentoRepository.existsByNombreIgnoreCaseAndIdNot(nombreLimpio, id)) {
            throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
        }

        BigDecimal planillaActual = empleadoRepository.sumarSalariosPorDepartamento(id);
        if (datosActualizados.getPresupuesto().compareTo(planillaActual) < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser menor a la planilla actual");
        }

        existente.setNombre(nombreLimpio);
        existente.setUbicacion(datosActualizados.getUbicacion().trim());
        existente.setPresupuesto(datosActualizados.getPresupuesto());

        return departamentoRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);

        if (empleadoRepository.countByDepartamentoId(id) > 0) {
            throw new IllegalStateException("No se puede eliminar un departamento con empleados asignados");
        }

        departamentoRepository.deleteById(id);
    }
}
