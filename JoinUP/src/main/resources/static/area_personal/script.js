// Script para manejar la navegación de pestañas en el área personal******************************
    const tabs = document.querySelectorAll(".menu-item");
    const pantallas = document.querySelectorAll(".pantalla");

    tabs.forEach((tab, index) => {
        tab.addEventListener("click", () => {

            tabs.forEach(btn => btn.classList.remove("active"));
            tab.classList.add("active");
   
            pantallas.forEach(p => p.classList.remove("visible"));

            switch(index) {
                case 0:
                    document.getElementById("pantalla-info").classList.add("visible");
                    break;
                case 1:
                    document.getElementById("pantalla-deseados").classList.add("visible");
                    break;                      
                case 2:
                    document.getElementById("pantalla-ajustes").classList.add("visible");
                    break;
            }
        });
    });

//************** Recargar usuario desde el backend al iniciar ***************************************
async function recargarUsuarioDesdeBackend() {
    const usuario = getLoggedUser();
    if (usuario && usuario.idCliente) {
        try {
            const response = await fetch(`http://localhost:8081/api/usuarios/${usuario.idCliente}`);
            if (response.ok) {
                const usuarioActualizado = await response.json();
                localStorage.setItem('usuario', JSON.stringify(usuarioActualizado));
                console.log('Usuario recargado desde el backend');
                
                // Ocultar "Hacer premium" si el usuario ya es PREMIUM
                const btnPremium = document.querySelector('a[href="../pagos/pagos-1.html"]');
                if (usuarioActualizado.rol === "PREMIUM" && btnPremium) {
                    btnPremium.style.display = "none";
                }
            }
        } catch (error) {
            console.error('Error al recargar usuario:', error);
        }
    }
}

// Ejecutar al cargar la página
recargarUsuarioDesdeBackend();

//************** Script para cambiar la foto de perfil ***************************************
const avatarImg = document.querySelector(".avatar");
const avatarInput = document.getElementById("avatar-input");
const editarIcono = document.querySelector(".editar-icono");
const borrarIcono = document.querySelector(".borrar-foto");

// Guardar la imagen original
const originalAvatar = avatarImg.src;

// Variable para almacenar la imagen actual
let imagenActual = null;

editarIcono.addEventListener("click", () => {
    avatarInput.click();
});


avatarInput.addEventListener("change", function () {
    const file = this.files[0];

    if (file) {
        // Validar tamaño (máximo 2MB)
        if (file.size > 2 * 1024 * 1024) {
            alert("La imagen es demasiado grande. Máximo 2MB");
            return;
        }
        
        // Validar tipo de archivo
        if (!file.type.startsWith('image/')) {
            alert("Por favor selecciona un archivo de imagen válido");
            return;
        }
        
        const reader = new FileReader();
        reader.onload = function (e) {
            // Redimensionar la imagen antes de guardar
            redimensionarYGuardarImagen(e.target.result);
        };
        reader.readAsDataURL(file);
    }
});

// Función para redimensionar la imagen
function redimensionarYGuardarImagen(imagenBase64) {
    const img = new Image();
    img.onload = function() {
        const canvas = document.createElement('canvas');
        const maxWidth = 400;
        const maxHeight = 400;
        
        let width = img.width;
        let height = img.height;
        
        // Calcular nuevas dimensiones manteniendo proporción
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
        
        // Convertir a Base64 con calidad reducida
        const imagenRedimensionada = canvas.toDataURL('image/jpeg', 0.7);
        
        // Mostrar en el avatar
        avatarImg.src = imagenRedimensionada;
        imagenActual = imagenRedimensionada;
        
        // Guardar en el backend
        guardarImagenPerfil(imagenRedimensionada);
    };
    img.src = imagenBase64;
}

// Función para guardar la imagen en el backend
async function guardarImagenPerfil(imagenBase64) {
    const usuario = getLoggedUser();
    if (!usuario) return;
    
    try {
        const datosActualizados = {
            ...usuario,
            password: null,
            imagen: imagenBase64
        };
        
        const response = await fetch(`http://localhost:8081/api/usuarios/${usuario.idCliente}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datosActualizados)
        });
        
        if (response.ok) {
            // Actualizar localStorage
            localStorage.setItem("usuario", JSON.stringify(datosActualizados));
            imagenActual = imagenBase64;
            alert("Imagen de perfil guardada correctamente");
        } else {
            const errorText = await response.text();
            console.error("Error del servidor:", errorText);
            alert("Error al guardar la imagen. Revisa la consola para más detalles.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("No se pudo guardar la imagen: " + error.message);
    }
}

// ekiminar la foto de perfil y restaurar la original
borrarIcono.addEventListener("click", async () => {
    avatarImg.src = originalAvatar;  
    avatarInput.value = "";
    imagenActual = null;
    
    // Eliminar del backend
    const usuario = getLoggedUser();
    if (usuario) {
        const datosActualizados = {
            ...usuario,
            password: null,
            imagen: null
        };
        
        try {
            const response = await fetch(`http://localhost:8081/api/usuarios/${usuario.idCliente}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(datosActualizados)
            });
            
            if (response.ok) {
                localStorage.setItem("usuario", JSON.stringify(datosActualizados));
                alert("Imagen eliminada correctamente");
            }
        } catch (error) {
            console.error("Error:", error);
        }
    }
});

// Función para cargar datos actuales en el formulario de ajustes
function cargarDatosAjustes() {
    const usuario = getLoggedUser();
    if (!usuario) return;

    // Cargar fecha de nacimiento
    document.getElementById("ajuste-fecha").value = usuario.fecNac || "";
    
    // Cargar ciudad y provincia
    document.getElementById("ajuste-ciudad").value = usuario.poblacion || "";
    document.getElementById("ajuste-provincia").value = usuario.provincia || "";
    
    // Cargar teléfono
    document.getElementById("ajuste-telefono").value = usuario.numTelefono || "";
    
    // Cargar redes sociales
    cargarRedesSociales();
    
    // Cargar intereses (marcar los que ya tiene el usuario)
    const interesesAjustes = document.querySelectorAll('#pantalla-ajustes .interes');
    interesesAjustes.forEach(interes => {
        interes.classList.remove('selected');
    });
    
    // Reiniciar seleccionados
    seleccionados = [];
    
    // Marcar los intereses del usuario
    const interesesUsuario = [usuario.intV1, usuario.intV2, usuario.intV3].filter(Boolean);
    interesesUsuario.forEach(interesValor => {
        const interesElement = Array.from(interesesAjustes).find(
            el => el.dataset.value === interesValor
        );
        if (interesElement) {
            interesElement.classList.add('selected');
            seleccionados.push(interesElement);
        }
    });
}

// Objeto para almacenar las redes sociales temporalmente
let redesSociales = {
    urlInstagram: "",
    urlFacebook: "",
    urlTwitter: "",
    urlLinkedin: ""
};

// Función para cargar las redes sociales del usuario
function cargarRedesSociales() {
    const usuario = getLoggedUser();
    if (!usuario) return;
    
    // Cargar las URLs desde el usuario
    redesSociales.urlInstagram = usuario.urlInstagram || "";
    redesSociales.urlFacebook = usuario.urlFacebook || "";
    redesSociales.urlTwitter = usuario.urlTwitter || "";
    redesSociales.urlLinkedin = usuario.urlLinkedin || "";
    
    // Mostrar la URL de la red social seleccionada por defecto (Instagram)
    mostrarURLRedSeleccionada();
}

// Mapeo de tipos de red social a campos
const mapeoTipos = {
    "instagram": "urlInstagram",
    "facebook": "urlFacebook",
    "twitter": "urlTwitter",
    "linkedin": "urlLinkedin"
};

// Función para mostrar la URL de la red social seleccionada
function mostrarURLRedSeleccionada() {
    const tipo = document.getElementById("social-type").value;
    const urlInput = document.getElementById("social-url");
    const campo = mapeoTipos[tipo];
    
    // Mostrar la URL actual de esa red social (si existe)
    urlInput.value = redesSociales[campo] || "";
}

// Cuando cambie el dropdown, mostrar la URL correspondiente
document.getElementById("social-type").addEventListener("change", mostrarURLRedSeleccionada);

// Botón para añadir/actualizar red social
document.getElementById("add-social").addEventListener("click", () => {
    const tipo = document.getElementById("social-type").value;
    const url = document.getElementById("social-url").value.trim();
    
    // Si la URL está vacía, eliminar esa red social
    if (!url) {
        const campo = mapeoTipos[tipo];
        redesSociales[campo] = "";
        alert("Red social eliminada - Recuerda GUARDAR CAMBIOS");
        return;
    }
    
    // Validar formato básico de URL
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        alert("La URL debe comenzar con http:// o https://");
        return;
    }
    
    // Guardar la URL en el campo correspondiente
    const campo = mapeoTipos[tipo];
    redesSociales[campo] = url;
    
    alert("Red social guardada - Recuerda GUARDAR CAMBIOS");
});

// Manejar el envío del formulario de ajustes
document.getElementById("form-ajustes").addEventListener("submit", async (e) => {
    e.preventDefault();
    
    const usuario = getLoggedUser();
    if (!usuario) return;
    
    // Validar que haya exactamente 3 intereses seleccionados
    if (seleccionados.length !== 3) {
        alert("Debes seleccionar exactamente 3 intereses");
        return;
    }
    
    // Recoger los datos del formulario
    const datosActualizados = {
        idCliente: usuario.idCliente,
        nombre: usuario.nombre,
        ap1: usuario.ap1,
        ap2: usuario.ap2,
        email: usuario.email,
        password: null,
        fecNac: document.getElementById("ajuste-fecha").value,
        poblacion: document.getElementById("ajuste-ciudad").value,
        provincia: document.getElementById("ajuste-provincia").value,
        numTelefono: document.getElementById("ajuste-telefono").value,
        rol: usuario.rol,
        intV1: seleccionados[0].dataset.value,
        intV2: seleccionados[1].dataset.value,
        intV3: seleccionados[2].dataset.value,
        urlInstagram: redesSociales.urlInstagram,
        urlFacebook: redesSociales.urlFacebook,
        urlTwitter: redesSociales.urlTwitter,
        urlLinkedin: redesSociales.urlLinkedin
    };
    
    try {
        const response = await fetch(`http://localhost:8081/api/usuarios/${usuario.idCliente}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datosActualizados)
        });
        
        if (response.ok) {
            // Actualizar localStorage con los nuevos datos
            localStorage.setItem("usuario", JSON.stringify(datosActualizados));
            
            // Recargar la información personal
            cargarInformacionPersonal();
            
            // Actualizar nombre y email en el panel izquierdo
            document.querySelector(".usuario-nombre").textContent = datosActualizados.nombre;
            document.querySelector(".usuario-email").textContent = datosActualizados.email;
            
            alert("¡Cambios guardados correctamente!");
        } else {
            alert("Error al guardar los cambios. Intenta de nuevo.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("No se pudo conectar con el servidor.");
    }
});

//JS para los intereses
const intereses = document.querySelectorAll('label.interes');
let seleccionados = []; // Guardará las etiquetas seleccionadas

intereses.forEach(interes => {
    interes.addEventListener('click', () => {

        // Si ya está seleccionado lo podemos quitar
        if (interes.classList.contains('selected')) {
            interes.classList.remove('selected');
            seleccionados = seleccionados.filter(i => i !== interes);
            return;
        }

        // Si ya hay 3 seleccionados, eliminamos el primero
        if (seleccionados.length >= 3) {
            const primero = seleccionados.shift(); 
            primero.classList.remove('selected');
        }

        // Agregamos el nuevo interés
        interes.classList.add('selected');
        seleccionados.push(interes);
    });
});


//Cerrar sesión
document.querySelector(".btn-logout").addEventListener("click", () => {
    logout(); // Función definida en auth.js
});


// Función para cargar datos en la pantalla de información personal
function cargarInformacionPersonal() {
    const usuario = getLoggedUser();
    if (!usuario) return;

    // Fecha de nacimiento
    document.getElementById("info-fecha").textContent = usuario.fecNac || "-";
    
    // Ciudad y provincia
    document.getElementById("info-ciudad").textContent = usuario.poblacion || "-";
    document.getElementById("info-provincia").textContent = usuario.provincia || "-";
    
    // Teléfono
    document.getElementById("info-telefono").textContent = usuario.numTelefono || "-";
    
    // Redes sociales
    const redes = [];
    if (usuario.urlInstagram) redes.push(`Instagram: <a href="${usuario.urlInstagram}" target="_blank">${usuario.urlInstagram}</a>`);
    if (usuario.urlFacebook) redes.push(`Facebook: <a href="${usuario.urlFacebook}" target="_blank">${usuario.urlFacebook}</a>`);
    if (usuario.urlTwitter) redes.push(`Twitter: <a href="${usuario.urlTwitter}" target="_blank">${usuario.urlTwitter}</a>`);
    if (usuario.urlLinkedin) redes.push(`LinkedIn: <a href="${usuario.urlLinkedin}" target="_blank">${usuario.urlLinkedin}</a>`);
    
    document.getElementById("info-redes").innerHTML = redes.length > 0 ? redes.join("<br>") : "-";
    
    // Intereses
    const interesesDiv = document.getElementById("info-intereses");
    const interesesUsuario = [];
    if (usuario.intV1) interesesUsuario.push(usuario.intV1);
    if (usuario.intV2) interesesUsuario.push(usuario.intV2);
    if (usuario.intV3) interesesUsuario.push(usuario.intV3);
    
    if (interesesUsuario.length > 0) {
        interesesDiv.innerHTML = interesesUsuario.map(int => 
            `<span class="interes-badge">${int}</span>`
        ).join(" - ");
    } else {
        interesesDiv.textContent = "-";
    }
}

//Para evitar acceso directo por URL dde un usuario no registrado, redirigimos al login si no hay usuario logueado
document.addEventListener("DOMContentLoaded", () => {
    requireLogin(); 
    
    // Cargar datos del usuario en la página
    const usuario = getLoggedUser();
    if (usuario) {
        document.querySelector(".usuario-nombre").textContent = usuario.nombre;
        document.querySelector(".usuario-email").textContent = usuario.email;
        
        // Cargar imagen de perfil si existe
        if (usuario.imagen) {
            avatarImg.src = usuario.imagen;
            imagenActual = usuario.imagen;
        }
        
        // Cargar información personal en la primera pantalla
        cargarInformacionPersonal();
        
        // Cargar datos en los ajustes cuando se abre esa pantalla
        const tabs = document.querySelectorAll(".menu-item");
        tabs[2].addEventListener("click", cargarDatosAjustes);
        
        // Cargar datos de ajustes al inicio
        cargarDatosAjustes();
    }
});
