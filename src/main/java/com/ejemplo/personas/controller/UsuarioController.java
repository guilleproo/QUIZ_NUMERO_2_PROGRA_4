package com.ejemplo.personas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class UsuarioController {

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", List.of(
                Map.of("username", "admin", "rol", "ADMIN", "estado", "Activo"),
                Map.of("username", "user", "rol", "USER", "estado", "Activo")
        ));
        return "usuarios/lista";
    }
}