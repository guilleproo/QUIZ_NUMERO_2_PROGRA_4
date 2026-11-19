package com.ejemplo.personas.controller;

import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.service.DepartamentoService;
import com.ejemplo.personas.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;
    private final EmpleadoService empleadoService;

    public DepartamentoController(DepartamentoService departamentoService,
                                  EmpleadoService empleadoService) {
        this.departamentoService = departamentoService;
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Departamento> departamentos = departamentoService.listarTodos();

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        Map<Long, BigDecimal> planillas = new LinkedHashMap<>();
        Map<Long, BigDecimal> disponibles = new LinkedHashMap<>();

        for (Departamento dept : departamentos) {
            int cantidad = empleadoService.listarPorDepartamento(dept.getId()).size();
            BigDecimal planilla = empleadoService.listarPorDepartamento(dept.getId()).stream()
                    .map(e -> e.getSalario() != null ? e.getSalario() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal presupuesto = dept.getPresupuesto() != null ? dept.getPresupuesto() : BigDecimal.ZERO;
            BigDecimal disponible = presupuesto.subtract(planilla);

            cantidades.put(dept.getId(), cantidad);
            planillas.put(dept.getId(), planilla);
            disponibles.put(dept.getId(), disponible);
        }

        model.addAttribute("departamentos", departamentos);
        model.addAttribute("cantidades", cantidades);
        model.addAttribute("planillas", planillas);
        model.addAttribute("disponibles", disponibles);
        return "departamentos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("departamento", new Departamento());
        return "departamentos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("departamento") Departamento departamento,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "departamentos/formulario";
        }

        try {
            departamentoService.crear(departamento);
            redirectAttributes.addFlashAttribute("mensajeExito", "Departamento guardado correctamente");
            return "redirect:/departamentos";
        } catch (IllegalArgumentException ex) {
            result.rejectValue("nombre", "error.departamento", ex.getMessage());
            return "departamentos/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            Departamento departamento = departamentoService.buscarPorId(id);
            model.addAttribute("departamento", departamento);
            return "departamentos/formulario";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/departamentos";
        }
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("departamento") Departamento departamento,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        departamento.setId(id);

        if (result.hasErrors()) {
            return "departamentos/formulario";
        }

        try {
            departamentoService.actualizar(id, departamento);
            redirectAttributes.addFlashAttribute("mensajeExito", "Departamento actualizado correctamente");
            return "redirect:/departamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorNegocio", ex.getMessage());
            return "departamentos/formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departamentoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Departamento eliminado correctamente");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/departamentos";
    }
}
