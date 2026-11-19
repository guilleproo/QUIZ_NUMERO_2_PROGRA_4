package com.ejemplo.personas.config;

import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.model.Empleado;
import com.ejemplo.personas.model.Puesto;
import com.ejemplo.personas.service.DepartamentoService;
import com.ejemplo.personas.service.EmpleadoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartamentoService departamentoService;
    private final EmpleadoService empleadoService;

    public DataInitializer(DepartamentoService departamentoService,
                           EmpleadoService empleadoService) {
        this.departamentoService = departamentoService;
        this.empleadoService = empleadoService;
    }

    @Override
    public void run(String... args) {
        if (!departamentoService.listarTodos().isEmpty()) {
            return;
        }

        Departamento tecnologia = new Departamento();
        tecnologia.setNombre("Tecnologia");
        tecnologia.setUbicacion("Edificio Central");
        tecnologia.setPresupuesto(new BigDecimal("10000.00"));
        tecnologia = departamentoService.crear(tecnologia);

        Departamento operaciones = new Departamento();
        operaciones.setNombre("Operaciones");
        operaciones.setUbicacion("Edificio Sur");
        operaciones.setPresupuesto(new BigDecimal("8000.00"));
        operaciones = departamentoService.crear(operaciones);

        Empleado emp1 = new Empleado();
        emp1.setNombre("Anita Solis Madrigal");
        emp1.setCedula("112340567");
        emp1.setEmail("anitasol@citi.com");
        emp1.setPuesto(Puesto.JEFATURA);
        emp1.setSalario(new BigDecimal("2800.00"));
        emp1.setFechaIngreso(LocalDate.of(2023, 3, 15));
        empleadoService.crear(emp1, tecnologia.getId());

        Empleado emp2 = new Empleado();
        emp2.setNombre("Carlos Sandi Flores");
        emp2.setCedula("113450678");
        emp2.setEmail("casandi@teletica.com");
        emp2.setPuesto(Puesto.DESARROLLADOR);
        emp2.setSalario(new BigDecimal("2100.00"));
        emp2.setFechaIngreso(LocalDate.of(2024, 1, 10));
        empleadoService.crear(emp2, tecnologia.getId());

        Empleado emp3 = new Empleado();
        emp3.setNombre("Valeria Mora Brenes");
        emp3.setCedula("114560789");
        emp3.setEmail("valeria.mora@ice.com");
        emp3.setPuesto(Puesto.ANALISTA);
        emp3.setSalario(new BigDecimal("1750.00"));
        emp3.setFechaIngreso(LocalDate.of(2024, 6, 1));
        empleadoService.crear(emp3, tecnologia.getId());

        Empleado emp4 = new Empleado();
        emp4.setNombre("Guillermo Corrales Giammattei");
        emp4.setCedula("117800639");
        emp4.setEmail("guillepro@citi.com");
        emp4.setPuesto(Puesto.SOPORTE);
        emp4.setSalario(new BigDecimal("1600.00"));
        emp4.setFechaIngreso(LocalDate.of(2025, 2, 20));
        empleadoService.crear(emp4, operaciones.getId());

        Empleado emp5 = new Empleado();
        emp5.setNombre("Heymi Lopez Vindas");
        emp5.setCedula("117803282");
        emp5.setEmail("hlopezm@vantive.com");
        emp5.setPuesto(Puesto.ANALISTA);
        emp5.setSalario(new BigDecimal("1850.00"));
        emp5.setFechaIngreso(LocalDate.of(2025, 5, 12));
        empleadoService.crear(emp5, operaciones.getId());
    }
}