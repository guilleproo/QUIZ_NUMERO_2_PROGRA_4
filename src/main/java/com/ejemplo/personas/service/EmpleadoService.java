package com.ejemplo.personas.service;

import com.ejemplo.personas.model.Mascota;
import com.ejemplo.personas.model.Usuario;
import com.ejemplo.personas.repository.MascotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service 
public class MascotaService {

    private final MascotaRepository mascotaRepo;
    private final UsuarioService usuarioService;

    public MascotaService(MascotaRepository mascotaRepo, UsuarioService usuarioService){
        this.mascotaRepo = mascotaRepo;
        this.usuarioService = usuarioService;
    }

    //Leer todas las mascotas
    public List<Mascota> listar(){
        return mascotaRepo.findAll();
    }

    //Obtener las mascotas de un usuario 
    public List<Mascota> listarPorUsuario(Long usuarioId){
        return mascotaRepo.findByUsuarioId(usuarioId);
    }

    //Obtener una sola mascota 
    public Mascota buscarPorId(Long id){
        return mascotaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada!" + id)); 
    }

    //CREAR MASCOTA 
    @Transactional 
    public Mascota crear(Mascota mascota, Long usuarioId){
        Usuario dueno = obtenerDueno(usuarioId);

        if (mascotaRepo.existsByUsuarioIdAndNombreIgnoreCase(usuarioId, mascota.getNombre())){
            throw new IllegalArgumentException(
                dueno.getNombre() + " ya tiene una mascota llamada" + mascota.getNombre());
            
        }
        dueno.agregarMascota(mascota);
        return mascotaRepo.save(mascota);
    }


    //ACTUALIZAR MASCOTA
    @Transactional 
    public Mascota actualizar(Long id, Mascota datos, Long usuarioId){
        Mascota existente = buscarPorId(id);
        Usuario nuevoDueno = obtenerDueno(usuarioId);

        if (mascotaRepo.existsByUsuarioIdAndNombreIgnoreCaseAndIdNot(usuarioId, datos.getNombre(), id)){
            throw new IllegalArgumentException(
                nuevoDueno.getNombre() + " ya tiene una mascota llamada " + datos.getNombre());
        }
        existente.setNombre(datos.getNombre());
        existente.setEspecie(datos.getEspecie());
        existente.setRaza(datos.getRaza());
        existente.setFechaNacimiento(datos.getFechaNacimiento());

        if(!existente.getUsuario().getId().equals(usuarioId)){
            existente.getUsuario().quitarMascota(existente);
            nuevoDueno.agregarMascota(existente);
        }

        return mascotaRepo.save(existente);
    }


    //ELIMINAR MASCOTA
    @Transactional 
    public void eliminar(Long id){
        Mascota mascota = buscarPorId(id);
        mascota.getUsuario().quitarMascota(mascota);
        mascotaRepo.delete(mascota);
    }


    //Obtener dueno, toda mascota debe tener un dueno 
    private Usuario obtenerDueno(Long usuarioId){
        if (usuarioId == null){
            throw new IllegalArgumentException("Debe seleccionar un dueño");
        }
        return usuarioService.buscarPorId(usuarioId);
    }
    
}
