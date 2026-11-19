package com.ejemplo.personas.controller;

import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.model.Empleado;
import com.ejemplo.personas.model.Puesto;
import com.ejemplo.personas.service.DepartamentoService;
import com.ejemplo.personas.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final DepartamentoService departamentoService;

    public EmpleadoController(EmpleadoService empleadoService,
                              DepartamentoService departamentoService) {
        this.empleadoService = empleadoService;
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "departamentoId", required = false) Long departamentoId,
                         Model model) {
        List<Empleado> empleados = empleadoService.listarPorDepartamento(departamentoId);
        model.addAttribute("empleados", empleados);
        model.addAttribute("departamentos", departamentoService.listarTodos());
        model.addAttribute("departamentoIdSeleccionado", departamentoId);
        return "empleados/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(@RequestParam(name = "departamentoId", required = false) Long departamentoId,
                                         Model model) {
        Empleado empleado = new Empleado();
        if (departamentoId != null) {
            Departamento dept = departamentoService.buscarPorId(departamentoId);
            empleado.setDepartamento(dept);
        }

        model.addAttribute("empleado", empleado);
        model.addAttribute("departamentoId", departamentoId);
        cargarCatalogos(model);
        return "empleados/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("empleado") Empleado empleado,
                          BindingResult result,
                          @RequestParam(name = "departamentoId", required = false) Long departamentoId,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departamentoId", departamentoId);
            cargarCatalogos(model);
            return "empleados/formulario";
        }

        try {
            empleadoService.crear(empleado, departamentoId);
            redirectAttributes.addFlashAttribute("mensajeExito", "Empleado guardado correctamente");
            return "redirect:/empleados";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorNegocio", ex.getMessage());
            model.addAttribute("departamentoId", departamentoId);
            cargarCatalogos(model);
            return "empleados/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            Empleado empleado = empleadoService.buscarPorId(id);
            Long deptoId = empleado.getDepartamento() != null ? empleado.getDepartamento().getId() : null;
            model.addAttribute("empleado", empleado);
            model.addAttribute("departamentoId", deptoId);
            cargarCatalogos(model);
            return "empleados/formulario";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/empleados";
        }
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("empleado") Empleado empleado,
                             BindingResult result,
                             @RequestParam(name = "departamentoId", required = false) Long departamentoId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        empleado.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("departamentoId", departamentoId);
            cargarCatalogos(model);
            return "empleados/formulario";
        }

        try {
            empleadoService.actualizar(id, empleado, departamentoId);
            redirectAttributes.addFlashAttribute("mensajeExito", "Empleado actualizado correctamente");
            return "redirect:/empleados";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorNegocio", ex.getMessage());
            model.addAttribute("departamentoId", departamentoId);
            cargarCatalogos(model);
            return "empleados/formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Empleado eliminado correctamente");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/empleados";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("departamentos", departamentoService.listarTodos());
        model.addAttribute("puestos", Puesto.values());
    }
}