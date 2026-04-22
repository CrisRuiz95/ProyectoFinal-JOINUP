
//  Ajustamos el padding-top para no tapar el contenido 
window.addEventListener("load", function () {
    const navbar = document.getElementById("navbar");
    document.body.style.paddingTop = navbar.offsetHeight + "px";
});


/************************** Código para el menú **********************************************************/

var posPreviaScroll = document.documentElement.scrollTop;

window.onscroll = function() { EsconderMostrarMenu(); };

function EsconderMostrarMenu() {

    var posActualScroll = document.documentElement.scrollTop;

    // No ocultar el menú hasta pasar 300px
    if (posActualScroll < 300) {
        document.getElementById("navbar").style.top = "0";
        posPreviaScroll = posActualScroll;
        return; 
    }
   //ocultar menu despues de los 300px 
    if (posPreviaScroll > posActualScroll) {
        document.getElementById("navbar").style.top = "0";
    } else {
        document.getElementById("navbar").style.top = "-170px";
    }

    posPreviaScroll = posActualScroll;
}

/************************************BARRA DE BÚSQUEDA****************************************************/
/*  Mostrar panel de filtros */
const btnFiltro = document.querySelector(".btn-filtro");
const panelFiltro = document.querySelector(".panel-filtro");

btnFiltro.addEventListener("click", () => {
    panelFiltro.style.display =
        panelFiltro.style.display === "flex" ? "none" : "flex";
});
document.addEventListener("click", e => {
    if (!e.target.closest(".container-filtro"))
        panelFiltro.style.display = "none";
});

/* Categoría desplegable */
const desplegableBtn = document.querySelector(".desplegable-btn");
const desplegableMenu = document.querySelector(".desplegable-menu");

desplegableBtn.addEventListener("click", () => {
    desplegableMenu.style.display =
        desplegableMenu.style.display === "block" ? "none" : "block";
});
document.addEventListener("click", e => {
    if (!e.target.closest(".desplegable"))
        desplegableMenu.style.display = "none";
});

/*  Ubicación  */
const localidades = [
    "Álava", "Albacete", "Alicante",  "Almería","Asturias", "Ávila","Badajoz",
    "Baleares","Barcelona","Burgos", "Cáceres", "Cádiz", "Cantabria", "Castellón",
    "Ceuta", "Ciudad Real", "Córdoba", "Cuenca", "Gerona", "Granada", "Guadalajara",
    "Guipúzcoa","Huelva", "Huesca", "Jaén", "La Coruña", "La Rioja", "Las Palmas",
    "León", "Lérida", "Lugo", "Madrid", "Málaga", "Melilla", "Murcia", "Navarra", "Orense",
    "Palencia", "Pontevedra", "Salamanca", "Segovia", "Sevilla", "Soria", "Tarragona", "Tenerife",
    "Teruel", "Toledo", "Valencia", "Valladolid", "Vizcaya", "Zamora", "Zaragoza"
];


const input = document.querySelector(".autocompletar-input");
const box = document.querySelector(".autocompletar-caja");

input.addEventListener("input", () => {
    const text = input.value.toLowerCase();
    box.innerHTML = "";

    if (!text) { box.style.display = "none"; return; }

    const matches = localidades.filter(l => l.toLowerCase().includes(text));

    matches.forEach(ciudad => {
        const item = document.createElement("div");
        item.classList.add("autocompletar-item");
        item.textContent = ciudad;
        item.onclick = () => {
            input.value = ciudad;
            box.style.display = "none";
        };
        box.appendChild(item);
    });

    box.style.display = matches.length ? "block" : "none";
});

document.addEventListener("click", e => {
    if (!e.target.closest(".autocompletar")) box.style.display = "none";
});

// Selección de categoría
const categoriaBtn = document.querySelector(".desplegable-btn");
const categoriaItems = document.querySelectorAll(".desplegable-item");

categoriaItems.forEach(item => {
    item.addEventListener("click", () => {
        categoriaBtn.textContent = item.textContent;  // cambia el texto
        document.querySelector(".desplegable-menu").style.display = "none";  // cierra menú
    });
});

/*  Botón guardar
const btnGuardar = document.querySelector(".btn-guardar-filtros");

btnGuardar.addEventListener("click", () => {

    const categoria = document.querySelector(".desplegable-btn").textContent;
    const fecha = document.querySelector(".seleccion-fecha").value;
    const ubicacion = document.querySelector(".autocompletar-input").value;
    const precio = document.querySelector("#toggle-precio").checked 
                   ? "De pago"
                   : "Gratuito";

    console.log("Filtros guardados:");
    console.log("Categoría:", categoria);
    console.log("Fecha:", fecha);
    console.log("Ubicación:", ubicacion);
    console.log("Precio:", precio);

    // Cerrar panel
    document.querySelector(".panel-filtro").style.display = "none";
});

/***************************CAMBIAR IMAGEN DE FAVORITO *************************************************/

// Seleccionamos todos los iconos de favorito
const favoritos = document.querySelectorAll(".favorito");

favoritos.forEach(fav => {
    fav.addEventListener("click", () => {

        // Ruta actual de la imagen
        const srcActual = fav.getAttribute("src");

        // Si está vacía cambiamos a llena
        if (srcActual.includes("favorito.png")) {
            fav.setAttribute("src", "../imagenes/favorito-completo.png");
        } 
        // Si está llena cambiarmos a vacía
        else {
            fav.setAttribute("src", "../imagenes/favorito.png");
        }
    });
});


/***************************CAMBIAR IMAGEN DE FAVORITO *************************************************/

const botonesUnirse = document.querySelectorAll(".unirse");

botonesUnirse.forEach(boton => {
    boton.addEventListener("click", () => {

        if (boton.textContent.trim() === "Unirse") {
            boton.textContent = "Unido";
            boton.style.backgroundColor = "var(--boton-secundario)";
        } else {
            boton.textContent = "Unirse";
            boton.style.backgroundColor = "var(--color-marca)";
        }

    });
});

/********************************* FILTRAR EVENTOS *************************************/
//DELAY a la hora de buscar eventos
function debounce(func, delay = 300) {
    let timeout;
    return (...args) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => func(...args), delay);
    };
}

//declaración de variables
const inputBuscador = document.querySelector(".buscador-input");
const btnGuardar = document.querySelector(".btn-guardar-filtros");
const btnReset = document.querySelector(".btn-reset-filtros");

const contenedorFiltrados = document.getElementById("contenedor-filtrados");
const contenedorOriginal = document.getElementById("contenedor-original");
const seccionRecientes = document.querySelector(".eventos-dos");
const mensajeNoResultados = document.getElementById("mensaje-no-resultados");


function filtrarEventos() {

    const texto = inputBuscador.value.trim().toLowerCase();
    const categoria = document.querySelector(".desplegable-btn").textContent.trim().toLowerCase();
    const fecha = document.querySelector(".seleccion-fecha").value.trim();
    const ubicacion = document.querySelector(".autocompletar-input").value.trim().toLowerCase();
    const precio = document.querySelector("#toggle-precio").checked ? "de pago" : "gratuito";

    const filtrosActivos =
        texto ||
        categoria !== "selecciona la categoría" ||
        fecha !== "" ||
        ubicacion !== "" ||
        document.querySelector("#toggle-precio").checked;

    // Limpieza previa
    contenedorFiltrados.innerHTML = "";
    mensajeNoResultados.style.display = "none";

    if (!filtrosActivos) {
        // Restaurar todo (boton restaurar)
        contenedorFiltrados.style.display = "none";
        contenedorOriginal.style.display = "grid";
        seccionRecientes.style.display = "block";
        document.getElementById("titulo-populares").textContent = "Eventos Populares";
        return;
    }

    // Si hay filtros (cambiamos el h2)
    contenedorOriginal.style.display = "none";
    seccionRecientes.style.display = "none";
    contenedorFiltrados.style.display = "grid";
    document.getElementById("titulo-populares").textContent = "Eventos Filtrados";

    let coincidencias = 0;

    document.querySelectorAll(".evento-pop").forEach(evento => {

        const titulo = evento.querySelector("h3").textContent.trim().toLowerCase();
        const cat = evento.dataset.categoria.toLowerCase();
        const fec = evento.dataset.fecha;
        const ubi = evento.dataset.ubicacion.toLowerCase();
        const pre = evento.dataset.precio.toLowerCase();

        let coincide = true;

        if (texto && !titulo.includes(texto)) coincide = false;
        if (categoria !== "selecciona la categoría" && cat !== categoria) coincide = false;
        if (fecha && fec !== fecha) coincide = false;
        if (ubicacion && !ubi.includes(ubicacion)) coincide = false;
        if (precio !== pre) coincide = false;

        if (coincide) {
            coincidencias++;

            const clon = evento.cloneNode(true);

            // Reactivar botones de favorito y unirse
            activarBotonesEnClon(clon);

            // Fade-in
            clon.classList.add("fade-in");

            contenedorFiltrados.appendChild(clon);
        }
    });

    //Cuando no hay resultados
    if (coincidencias === 0) {
        mensajeNoResultados.style.display = "block";
    }
}


/* LISTENERS */

// Guardar filtros
btnGuardar.addEventListener("click", () => {
    filtrarEventos();
    panelFiltro.style.display = "none";
});

// Buscador con debounce
inputBuscador.addEventListener("input", debounce(() => {
    filtrarEventos();
}, 350));

// Resetear todo
btnReset.addEventListener("click", () => {
    inputBuscador.value = "";
    document.querySelector(".desplegable-btn").textContent = "Selecciona la categoría";
    document.querySelector(".seleccion-fecha").value = "";
    document.querySelector(".autocompletar-input").value = "";
    document.querySelector("#toggle-precio").checked = false;

    mensajeNoResultados.style.display = "none";

    filtrarEventos(); // restaura vista normal

    panelFiltro.style.display = "none";
});

/* RESTABLECER FILTROS*/
btnReset.addEventListener("click", () => {

    document.getElementById("titulo-populares").textContent = "Eventos Populares";

    // Mostrar secciones normales
    contenedorOriginal.style.display = "grid";
    seccionRecientes.style.display = "block";

    // Ocultar filtrados
    contenedorFiltrados.innerHTML = "";
    contenedorFiltrados.style.display = "none";

    mensajeNoResultados.style.display = "none";

    // Restablecer valores
    document.querySelector(".desplegable-btn").textContent = "Selecciona la categoría";
    document.querySelector(".seleccion-fecha").value = "";
    document.querySelector(".autocompletar-input").value = "";
    document.querySelector("#toggle-precio").checked = false;

    // Cerrar panel
    panelFiltro.style.display = "none";
});


/** ACTIVAR BOTONES EN TARJETAS CLONADAS (FAVORITO Y UNIRSE) *******/

function activarBotonesEnClon(tarjeta) {

    const fav = tarjeta.querySelector(".favorito");
    fav.addEventListener("click", () => {
        const src = fav.getAttribute("src");
        fav.setAttribute(
            "src",
            src.includes("favorito.png")
                ? "../imagenes/favorito-completo.png"
                : "../imagenes/favorito.png"
        );
    });

    const btnUnirse = tarjeta.querySelector(".unirse");
    btnUnirse.addEventListener("click", () => {
        if (btnUnirse.textContent === "Unirse") {
            btnUnirse.textContent = "Unido";
            btnUnirse.style.backgroundColor = "var(--boton-secundario)";
        } else {
            btnUnirse.textContent = "Unirse";
            btnUnirse.style.backgroundColor = "var(--color-marca)";
        }
    });
}


/*******************BOTON PARA REDIRIGIR A CREAR EVENTO **********************/
 document.getElementById("btn-ce").addEventListener("click", function() {
    window.location.href = "../crear_evento/crear-eventos.html";
});