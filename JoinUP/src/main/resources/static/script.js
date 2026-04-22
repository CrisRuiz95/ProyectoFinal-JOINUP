
/*****************************CARRUSEL************************************************/
const imagenes = [
   "imagenes/la-gente-surfeando-en-brasil.jpg",
   "imagenes/grupo-de-amigos-comiendo-en-restaurante.jpg",
   "imagenes/Equipo-futbol.jpg",
   "imagenes/Group-of-Friends-having-Fun.jpg",
   "imagenes/Music-and-mental-health.jpg",
   "imagenes/bailarinas.jpg",
   "imagenes/atletismo.jpeg",
   "imagenes/viaje-en-bus.jpg",
   "imagenes/mochileros.webp"
]
 
const imgEvento = document.getElementById("imgEvento");
const botonAnterior = document.getElementById("anterior");
const botonSiguiente = document.getElementById("siguiente");

let indice=0; 

function mostrarDestino(){
imgEvento.src = imagenes[indice];
}
botonAnterior.addEventListener ("click", function() {
    indice = (indice - 1 + imagenes.length) % imagenes.length;
   mostrarDestino();
});
botonSiguiente.addEventListener ("click",  function() {
    indice = (indice + 1) % imagenes.length;
   mostrarDestino();
});
//Poner el incio
mostrarDestino();

/********************** Código para el menú *****************************************/
var posPreviaScroll = document.documentElement.scrollTop;

window.onscroll = function() { EsconderMostrarMenu(); };

function EsconderMostrarMenu() {

    var posActualScroll = document.documentElement.scrollTop;

    // No ocultar el menú hasta pasar 200px
    if (posActualScroll < 200) {
        document.getElementById("navbar").style.top = "0";
        posPreviaScroll = posActualScroll;
        return; 
    }
   //ocultar menu despues de los 200px 
    if (posPreviaScroll > posActualScroll) {
        document.getElementById("navbar").style.top = "0";
    } else {
        document.getElementById("navbar").style.top = "-170px";
    }

    posPreviaScroll = posActualScroll;
}

/******************* BOTON DE ELIMINAR ARCHIVO ****************************************/ 
const inputArchivo = document.getElementById("archivo");
  const botonBorrar = document.getElementById("borrar-archivo");

  botonBorrar.addEventListener("click", () => {
    inputArchivo.value = ""; // Limpia el archivo seleccionado
  });


/*******************BOTON PARA REDIRIGIR A CREAR EVENTO **********************/
 document.getElementById("btn-ce").addEventListener("click", function() {
    window.location.href = "/crear_evento/crear-eventos.html";
});

/*******************BOTON PARA REDIRIGIR A BUSCAR EVENTO **********************/
 document.getElementById("btn-be").addEventListener("click", function() {
    window.location.href = "/busquedaEventos/busqueda.html";
});