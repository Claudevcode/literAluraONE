package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.*;
import com.aluracursos.literalura.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado;
    private ConsumoAPI consumoAPI;
    private ConvierteDatos conversor;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private List<Libro> libro;


    @Autowired
    private LibrosRepository librosRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private EstadisticasService estadisticasService;


    @PostConstruct
    public void init() {
        teclado = new Scanner(System.in);
        consumoAPI = new ConsumoAPI();
        conversor = new ConvierteDatos();
    }


    public void muestraMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \nElija la opción deseada:\n
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Listar libros por título
                    7 - Listar autores por nombre
                    8 - Buscar los 5 libros más descargados
                    9 - Mostrar estadisticas de la base de datos

                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnDeterminadoAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    listarLibrosPorTitulo();
                    break;
                case 7:
                    listarAutoresPorNombre();
                    break;
                case 8:
                    buscarTop5LibrosDescargados();
                    break;
                case 9:
                    mostrarEstadisticas();
                    break;
                case 0:
                    System.out.println("\n\nCerrando aplicación...\n\n");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }


    public void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que quieres buscar: ");
        String tituloLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+").toLowerCase());

        libroService.buscarLibroPorTitulo(tituloLibro, json);
    }


    private void listarLibrosRegistrados() {
        List<Libro> libros = librosRepository.findAll();
        libros.forEach(System.out::println);
    }


    private void listarAutoresRegistrados() {
        List<String> sortedAutores = autorService.listarAutoresRegistrados();

        System.out.println("\nLISTADO DE AUTORES REGISTRADOS:\n──────────────────────────────");
        sortedAutores.forEach(System.out::println);
    }


    private void listarAutoresVivosEnDeterminadoAnio() {
        System.out.println("En esta opción busque autores vivos por año." +
                "\n¿De qué año quiere encontrar autores vivos?");

        int anio = teclado.nextInt();
        autorService.listarAutoresVivosEnAnio(anio);
    }


    private void listarLibrosPorIdioma() {
        System.out.println("Aquí puede buscar libros escritos en un idioma determinado. \n" +
                "¿En qué idioma quiere buscar?");
        String idiomaStr = teclado.nextLine().toLowerCase();


        idiomaStr = LibroService.eliminarTildes(idiomaStr);


        if ("español".equalsIgnoreCase(idiomaStr)) {
            idiomaStr = "CASTELLANO";
        }

        try {
            Idiomas idioma = Idiomas.valueOf(idiomaStr.toUpperCase());
            libroService.listarLibrosPorIdioma(idioma);
        } catch (IllegalArgumentException e) {
            System.out.println("El idioma ingresado es inválido.");
        }
    }


    private void listarLibrosPorTitulo() {
        System.out.println("Aquí puede buscar libros registrados en la base de datos. \n" +
                "¿Qué titulo desea buscar?");
        String titulo = teclado.nextLine();
        List<Libro> libro = librosRepository.findByTituloContainingIgnoreCase(titulo);

        if (libro.isEmpty()) {
            System.out.println("No se encontraron libros con el titulo: " + titulo);
        } else {
            System.out.println("\nLIBRO ENCONTRADO:\n────────────────");
            libro.forEach(System.out::println);
        }
    }


    private void listarAutoresPorNombre() {
        System.out.println("Aquí puede buscar autores registrados en la base de datos. \n" +
                "¿Qué nombre o apellido de autor quiere buscar?");

        String nombreAutor = teclado.nextLine();


        List<Autor> autores = autorService.listarAutoresPorNombre(nombreAutor);


        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con el nombre: " + nombreAutor);
        } else {
            System.out.println("\nAUTOR ENCONTRADO:\n────────────────");
            autores.forEach(System.out::println);
        }
    }


    private void buscarTop5LibrosDescargados() {
        List<Libro> libros = librosRepository.findAll();


        List<Libro> top5Libros = libroService.obtenerTop5LibrosMasDescargados(libros);

        System.out.println("\nTop 5, libros más descargados.\nCANTIDAD     TITULOS\nVECES        MAS DESCARGADOS\n────────     ───────────");
        top5Libros.forEach(libro -> System.out.println(libro.getNumeroDescargas() + "        " + libro.getTitulo().toUpperCase()));
    }


    private void mostrarEstadisticas() {
        estadisticasService.mostrarEstadisticas();
    }
}


