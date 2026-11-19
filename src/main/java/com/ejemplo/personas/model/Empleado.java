package com.ejemplo.personas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity 
@Table(name = "mascotas")
public class Mascota {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id; 

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 50, message = "Maximo 50 caracteres")
    private String nombre; 

    @NotNull (message = "Seleccione una especie")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) 
    private Especie especie;

    @Size (max = 50, message = "Maximo 50 caracteres")
    private String raza;

    @NotNull (message = "La fecha de nacimiento es obligatorio")
    @PastOrPresent (message = "La fecha no puede ser futura")
    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) //YYYY-MM-DD
    private LocalDate fechaNacimiento;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Mascota(){}

    public Mascota(String nombre, Especie especie, String raza, LocalDate fechaNacimiento ){
        this.nombre = nombre; 
        this.especie = especie;
        this.raza = raza; 
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public Especie getEspecie(){
        return especie;
    }

    public void setEspecie(Especie especie){
        this.especie = especie;
    }

    public String getRaza(){
        return raza;
    }

    public void setRaza(String raza){
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento(){
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento){
        this.fechaNacimiento = fechaNacimiento;
    }

    public Usuario getUsuario(){
        return usuario;
    }

    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }
}
