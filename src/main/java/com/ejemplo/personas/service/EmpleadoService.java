package com.ejemplo.personas.service;

import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.model.Empleado;
import com.ejemplo.personas.repository.DepartamentoRepository;
import com.ejemplo.personas.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository,
                           DepartamentoRepository departamentoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarPorDepartamento(Long departamentoId) {
        if (departamentoId == null) {
            return empleadoRepository.findAll();
        }
        return empleadoRepository.findByDepartamentoId(departamentoId);
    }

    @Transactional(readOnly = true)
    public Empleado buscarPorId(Long id) {
        return empleadoRepository.findWithDepartamentoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con id: " + id));
    }

    public Empleado crear(Empleado empleado, Long departamentoId) {
        String cedulaLimpia = empleado.getCedula().trim();
        if (empleadoRepository.existsByCedula(cedulaLimpia)) {
            throw new IllegalArgumentException("La cedula ya está registrada");
        }

        Departamento departamento = buscarDepartamento(departamentoId);

        BigDecimal planillaActual = empleadoRepository.sumarSalariosPorDepartamento(departamento.getId());
        BigDecimal nuevaPlanilla = planillaActual.add(empleado.getSalario());

        if (nuevaPlanilla.compareTo(departamento.getPresupuesto()) > 0) {
            throw new IllegalArgumentException("El salario excede el presupuesto disponible del departamento");
        }

        empleado.setNombre(empleado.getNombre().trim());
        empleado.setCedula(cedulaLimpia);
        empleado.setEmail(empleado.getEmail().trim());
        departamento.agregarEmpleado(empleado);

        return empleadoRepository.save(empleado);
    }

    public Empleado actualizar(Long id, Empleado datosActualizados, Long departamentoId) {
        Empleado existente = buscarPorId(id);

        String cedulaLimpia = datosActualizados.getCedula().trim();
        if (empleadoRepository.existsByCedulaAndIdNot(cedulaLimpia, id)) {
            throw new IllegalArgumentException("La cedula ya está registrada");
        }

        Departamento departamentoAnterior = existente.getDepartamento();
        Departamento departamentoDestino = buscarDepartamento(departamentoId);

        BigDecimal planillaDestino = empleadoRepository.sumarSalariosPorDepartamento(departamentoDestino.getId());
        if (departamentoAnterior != null && departamentoAnterior.getId().equals(departamentoDestino.getId())) {
            planillaDestino = planillaDestino.subtract(existente.getSalario());
        }

        BigDecimal nuevaPlanilla = planillaDestino.add(datosActualizados.getSalario());
        if (nuevaPlanilla.compareTo(departamentoDestino.getPresupuesto()) > 0) {
            throw new IllegalArgumentException("El salario excede el presupuesto disponible del departamento");
        }

        existente.setNombre(datosActualizados.getNombre().trim());
        existente.setCedula(cedulaLimpia);
        existente.setEmail(datosActualizados.getEmail().trim());
        existente.setPuesto(datosActualizados.getPuesto());
        existente.setSalario(datosActualizados.getSalario());
        existente.setFechaIngreso(datosActualizados.getFechaIngreso());

        if (departamentoAnterior == null || !departamentoAnterior.getId().equals(departamentoDestino.getId())) {
            if (departamentoAnterior != null) {
                departamentoAnterior.quitarEmpleado(existente);
            }
            departamentoDestino.agregarEmpleado(existente);
        }

        return empleadoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Empleado existente = buscarPorId(id);
        Departamento departamento = existente.getDepartamento();
        if (departamento != null) {
            departamento.quitarEmpleado(existente);
        }
        empleadoRepository.delete(existente);
    }

    private Departamento buscarDepartamento(Long departamentoId) {
        if (departamentoId == null) {
            throw new IllegalArgumentException("Debe seleccionar un departamento");
        }
        return departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado con id: " + departamentoId));
    }
}
