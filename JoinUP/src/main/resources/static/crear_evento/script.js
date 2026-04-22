// MAPA

// crea mapa centrado en Madrid
const map = L.map('map').setView([40.4168, -3.7038], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19
}).addTo(map);

let marker;
let ubicacionSeleccionada = {
    lat: null,
    lng: null,
    provincia: null,
    poblacion: null
};

// al hacer click en el mapa, mover/crear marcador y guardar coordenadas
map.on('click', async function(e) {
    const { lat, lng } = e.latlng;

    if (marker) {
        marker.setLatLng(e.latlng);
    } else {
        marker = L.marker(e.latlng).addTo(map);
    }

    document.getElementById('lat').value = lat;
    document.getElementById('lng').value = lng;
    
    // Obtener provincia y población mediante geocodificación inversa
    try {
        const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&addressdetails=1`);
        const data = await response.json();
        
        if (data && data.address) {
            ubicacionSeleccionada.lat = lat;
            ubicacionSeleccionada.lng = lng;
            ubicacionSeleccionada.provincia = data.address.province || data.address.state || data.address.county || "";
            ubicacionSeleccionada.poblacion = data.address.city || data.address.town || data.address.village || data.address.municipality || "";
            
            console.log("Ubicación detectada:", ubicacionSeleccionada);
        }
    } catch (error) {
        console.error("Error al obtener ubicación:", error);
    }
});

// INPUT IMAGEN

const inputImagen = document.getElementById('imagenEvento');
const spanNombre = document.getElementById('nombre-imagen');

inputImagen.addEventListener('change', function () {
    if (this.files && this.files.length > 0) {
        spanNombre.textContent = this.files[0].name; // nombre del archivo
    } else {
        spanNombre.textContent = 'Adjuntar foto';    // texto por defecto
    }
});

// CREAR EVENTO - ENVIAR AL BACKEND

const API_URL = "http://localhost:8081/api/eventos";

async function crearEvento() {
    // Obtener el usuario logueado
    const usuario = getLoggedUser();
    if (!usuario) {
        alert("Debes iniciar sesión para crear un evento");
        window.location.href = "../login/login.html";
        return;
    }
    
    // Recoger datos del formulario
    const nombreEvento = document.getElementById("nombreEvento").value.trim();
    const categoriaEvento = document.getElementById("categoriaEvento").value;
    const fechaEvento = document.getElementById("fechaEvento").value;
    const horaEvento = document.getElementById("horaEvento").value;
    const descripcionEvento = document.getElementById("descripcionEvento").value.trim();
    const maxAsistentes = document.getElementById("maxAsistentesEvento").value;
    const acceso = document.querySelector('input[name="acceso"]:checked').value;
    
    // Validaciones básicas
    if (!nombreEvento || !fechaEvento || !descripcionEvento || !maxAsistentes) {
        alert("Por favor, completa todos los campos obligatorios");
        return;
    }
    
    if (!ubicacionSeleccionada.lat || !ubicacionSeleccionada.lng) {
        alert("Por favor, selecciona una ubicación en el mapa");
        return;
    }
    
    // Procesar imagen (convertir a Base64 si existe)
    let imagenBase64 = null;
    if (inputImagen.files && inputImagen.files[0]) {
        imagenBase64 = await convertirImagenABase64(inputImagen.files[0]);
    }
    
    // Combinar fecha y hora en un objeto Date
    const fechaCompleta = new Date(`${fechaEvento}T${horaEvento || '00:00'}`);
    
    // Crear objeto evento según el modelo del backend
    const evento = {
        idOrgan: usuario.idCliente,
        fecha: fechaCompleta.toISOString(),
        titulo: nombreEvento,
        imagen: imagenBase64,
        descripcion: descripcionEvento,
        maxParticipantes: parseInt(maxAsistentes),
        pro: false,
        tag1: categoriaEvento,
        tag2: null,
        tag3: null,
        precio: acceso === "pago" ? "Por definir" : "Gratuito",
        provincia: ubicacionSeleccionada.provincia,
        poblacion: ubicacionSeleccionada.poblacion,
        infoExtra: `Lat: ${ubicacionSeleccionada.lat}, Lng: ${ubicacionSeleccionada.lng}`
    };
    
    try {
        const response = await fetch(`${API_URL}/create`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(evento)
        });
        
        if (response.ok) {
            alert("¡Evento creado exitosamente!");
            // Redirigir a la página de búsqueda o limpiar el formulario
            window.location.href = "../busquedaEventos/busqueda.html";
        } else {
            const errorData = await response.json();
            alert("Error al crear el evento: " + (errorData.message || "Intenta de nuevo"));
        }
    } catch (error) {
        console.error("Error:", error);
        alert("No se pudo conectar con el servidor");
    }
}

// Función para actualizar el preview del evento
function actualizarPreview() {
    const titulo = document.getElementById('nombreEvento').value;
    const descripcion = document.getElementById('descripcionEvento').value;
    const fecha = document.getElementById('fechaEvento').value;
    const hora = document.getElementById('horaEvento').value;
    const imagenInput = document.getElementById('imagenEvento');
    
    // Actualizar título
    if (titulo) {
        document.getElementById('previewTitulo').textContent = titulo;
    }
    
    // Actualizar descripción
    if (descripcion) {
        document.getElementById('previewDescripcion').textContent = descripcion;
    }
    
    // Actualizar fecha (convertir formato)
    if (fecha) {
        const fechaObj = new Date(fecha + 'T00:00:00');
        const opciones = { day: 'numeric', month: 'long', year: 'numeric' };
        let textoFecha = fechaObj.toLocaleDateString('es-ES', opciones);
        
        // Agregar hora si está disponible
        if (hora) {
            textoFecha += ` a las ${hora}`;
        }
        
        document.getElementById('previewFecha').textContent = textoFecha;
    }
    
    // Actualizar ubicación
    if (ubicacionSeleccionada.poblacion) {
        const textoUbicacion = ubicacionSeleccionada.provincia 
            ? `${ubicacionSeleccionada.poblacion}, ${ubicacionSeleccionada.provincia}`
            : ubicacionSeleccionada.poblacion;
        document.getElementById('previewUbicacion').textContent = textoUbicacion;
    }
    
    // Actualizar imagen si se ha seleccionado una
    if (imagenInput.files && imagenInput.files[0]) {
        convertirImagenABase64(imagenInput.files[0]).then(imagenBase64 => {
            document.getElementById('previewImagen').src = imagenBase64;
        }).catch(error => {
            console.error("Error al convertir imagen para preview:", error);
        });
    }
}

// Manejadores de eventos de los botones
document.getElementById('btnContinuarPaso1').addEventListener('click', function(e) {
    e.preventDefault();
    actualizarPreview();
    alert("Preview actualizado. Puedes continuar con los detalles del evento");
});

document.getElementById('btnContinuar').addEventListener('click', function(e) {
    e.preventDefault();
    
    // Validar campos requeridos
    const titulo = document.getElementById('nombreEvento').value;
    const fecha = document.getElementById('fechaEvento').value;
    const descripcion = document.getElementById('descripcionEvento').value;
    
    if (!titulo || !fecha || !descripcion) {
        alert("Por favor completa al menos el título, fecha y descripción");
        return;
    }
    
    if (!ubicacionSeleccionada.lat || !ubicacionSeleccionada.lng) {
        alert("Por favor selecciona una ubicación en el mapa");
        return;
    }
    
    // Actualizar el preview
    actualizarPreview();
    alert("Preview actualizado. Revisa tu evento y pulsa 'Publicar' cuando esté listo");
});

document.getElementById('btnPublicar').addEventListener('click', async function(e) {
    e.preventDefault();
    await crearEvento();
});

document.getElementById('btnGuardarBorrador').addEventListener('click', function(e) {
    e.preventDefault();
    alert("Funcionalidad de guardar borrador próximamente");
});

// Función auxiliar para convertir imagen a Base64
function convertirImagenABase64(file) {
    return new Promise((resolve, reject) => {
        // Validar tamaño (máximo 2MB)
        if (file.size > 2 * 1024 * 1024) {
            alert("La imagen es demasiado grande. Máximo 2MB");
            reject("Imagen demasiado grande");
            return;
        }
        
        const reader = new FileReader();
        reader.onload = function(e) {
            // Redimensionar la imagen
            const img = new Image();
            img.onload = function() {
                const canvas = document.createElement('canvas');
                const maxWidth = 800;
                const maxHeight = 600;
                
                let width = img.width;
                let height = img.height;
                
                if (width > height) {
                    if (width > maxWidth) {
                        height *= maxWidth / width;
                        width = maxWidth;
                    }
                } else {
                    if (height > maxHeight) {
                        width *= maxHeight / height;
                        height = maxHeight;
                    }
                }
                
                canvas.width = width;
                canvas.height = height;
                
                const ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0, width, height);
                
                resolve(canvas.toDataURL('image/jpeg', 0.7));
            };
            img.src = e.target.result;
        };
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}