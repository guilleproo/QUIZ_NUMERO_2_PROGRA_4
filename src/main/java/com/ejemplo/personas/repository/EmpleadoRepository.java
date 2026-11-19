package com.ejemplo.personas.repository;

import com.ejemplo.personas.model.Mascota;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    //SELECT * FROM MASCOTAS;
    @Override 
    @EntityGraph (attributePaths = "usuario")
    List <Mascota> findAll();


    //SELECT * FROM MASCOTAS WHERE usuario_id =***
    @EntityGraph (attributePaths = "usuario")
    List<Mascota> findByUsuarioId(Long usuarioId);

    //al crear una mascota: existe una mascota con un nombre dado y un mismo dueño? 
    boolean existsByUsuarioIdAndNombreIgnoreCase(Long usuarioId, String nombre);

    //similar, pero para editar/actualizar
    boolean existsByUsuarioIdAndNombreIgnoreCaseAndIdNot(Long usuarioId, String nombre, Long id);
}
