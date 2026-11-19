package com.ejemplo.personas.config;

import com.ejemplo.personas.model.Especie;
import com.ejemplo.personas.model.Mascota;
import com.ejemplo.personas.model.Usuario;
import com.ejemplo.personas.repository.UsuarioRepository;
import com.ejemplo.personas.service.MascotaService;
import com.ejemplo.personas.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatos(UsuarioRepository repo,
                                  UsuarioService usuarioService,
                                  MascotaService mascotaService) {
        return args -> {
            if (repo.count() == 0) {
                Usuario admin = usuarioService.crear(
                        new Usuario("Administrador", "admin", "admin@ejemplo.com", "admin123", "ADMIN"));
                Usuario maria = usuarioService.crear(
                        new Usuario("María Pérez", "maria", "maria@ejemplo.com", "maria123", "USER"));

                // María tiene 2 mascotas, el admin tiene 1 -> relación 1 a muchos
                mascotaService.crear(new Mascota("Firulais", Especie.PERRO, "Zaguate",
                        LocalDate.of(2020, 3, 15)), maria.getId());
                mascotaService.crear(new Mascota("Michi", Especie.GATO, "Siamés",
                        LocalDate.of(2022, 7, 1)), maria.getId());
                mascotaService.crear(new Mascota("Rocky", Especie.PERRO, "Labrador",
                        LocalDate.of(2019, 11, 20)), admin.getId());
            }
        };
    }
}