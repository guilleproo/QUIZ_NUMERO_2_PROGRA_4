package com.ejemplo.personas.controller;

import com.ejemplo.personas.model.Especie;
import com.ejemplo.personas.model.Mascota;
import com.ejemplo.personas.service.MascotaService;
import com.ejemplo.personas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    public MascotaController(MascotaService mascotaService, UsuarioService usuarioService) {
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
    }

    // LISTAR
    @GetMapping
    public String listar(@RequestParam(required = false) Long usuarioId, Model model) {
        if (usuarioId != null) {
            model.addAttribute("mascotas", mascotaService.listarPorUsuario(usuarioId));
            model.addAttribute("dueno", usuarioService.buscarPorId(usuarioId));
        } else {
            model.addAttribute("mascotas", mascotaService.listar());
        }
        return "mascotas/lista";
    }

    // CREAR
    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long usuarioId, Model model) {
        model.addAttribute("mascota", new Mascota());
        prepararFormulario(model, "crear", usuarioId);   // usuarioId pre-selecciona el dueño
        return "mascotas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("mascota") Mascota mascota,
                          BindingResult errores,
                          @RequestParam(required = false) Long usuarioId,
                          Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            prepararFormulario(model, "crear", usuarioId);
            return "mascotas/formulario";
        }
        try {
            mascotaService.crear(mascota, usuarioId);
            flash.addFlashAttribute("mensaje", "Mascota registrada correctamente");
            return "redirect:/mascotas?usuarioId=" + usuarioId;
        } catch (IllegalArgumentException e) {
            prepararFormulario(model, "crear", usuarioId);
            model.addAttribute("error", e.getMessage());
            return "mascotas/formulario";
        }
    }

    // ACTUALIZAR
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id);
        model.addAttribute("mascota", mascota);
        prepararFormulario(model, "editar", mascota.getUsuario().getId());
        return "mascotas/formulario";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("mascota") Mascota mascota,
                             BindingResult errores,
                             @RequestParam(required = false) Long usuarioId,
                             Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            mascota.setId(id);
            prepararFormulario(model, "editar", usuarioId);
            return "mascotas/formulario";
        }
        try {
            mascotaService.actualizar(id, mascota, usuarioId);
            flash.addFlashAttribute("mensaje", "Mascota actualizada");
            return "redirect:/mascotas?usuarioId=" + usuarioId;
        } catch (IllegalArgumentException e) {
            mascota.setId(id);
            prepararFormulario(model, "editar", usuarioId);
            model.addAttribute("error", e.getMessage());
            return "mascotas/formulario";
        }
    }

    // ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        mascotaService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Mascota eliminada");
        return "redirect:/mascotas";
    }

    private void prepararFormulario(Model model, String modo, Long usuarioId) {
        model.addAttribute("modo", modo);
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("especies", Especie.values());
        model.addAttribute("usuarioId", usuarioId);
    }
}