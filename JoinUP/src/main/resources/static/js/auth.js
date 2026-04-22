// Gestionar las Sesiones de los usuarios

/**
 * Verifica si hay un usuario logueado
 * @returns {boolean} true si está logueado, false si no
 */
function isUserLoggedIn() {
    return localStorage.getItem("isLoggedIn") === "true";
}

/**
 * Obtiene los datos del usuario logueado
 * @returns {Object|null} Datos del usuario o null si no está logueado
 */
function getLoggedUser() {
    if (isUserLoggedIn()) {
        const userData = localStorage.getItem("usuario");
        return userData ? JSON.parse(userData) : null;
    }
    return null;
}

/**
 * Cierra la sesión del usuario
 */
function logout() {
    localStorage.removeItem("usuario");
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("userId");
    window.location.href = "/login/login.html";
}

/**
 * Redirige a login si no hay usuario logueado
 */
function requireLogin() {
    if (!isUserLoggedIn()) {
        window.location.href = "/login/login.html";
    }
}

/**
 * Actualiza el menú según si el usuario está logueado o no
 */
function updateNavbar() {
    const usuario = getLoggedUser();
    
    // Buscar el enlace de cuenta y el icono
    const cuentaLink = document.getElementById("cuentaLink");
    const cuentaIcono = document.getElementById("cuentaIcono");
    
    if (usuario && cuentaLink) {
        // Si está logueado, actualizar contenido preservando estructura
        cuentaLink.href = "/area_personal/usuario.html";
        
        // Buscar si hay un span interno (página crear-eventos)
        const spanTexto = cuentaLink.querySelector("span");
        if (spanTexto) {
            spanTexto.textContent = `Hola, ${usuario.nombre}`;
        } else {
            // Si no hay span, actualizar directamente (index, busqueda)
            cuentaLink.textContent = `Hola, ${usuario.nombre}`;
        }
        
        // Actualizar imagen de perfil si existe
        if (cuentaIcono && usuario.imagen) {
            cuentaIcono.src = usuario.imagen;
            cuentaIcono.style.borderRadius = "50%"; // Hacer circular la imagen
            cuentaIcono.style.objectFit = "cover"; // Ajustar imagen
        }
    } else if (cuentaLink) {
        // Si no está logueado, mostrar "Mi cuenta"
        cuentaLink.href = "/login/login.html";
        
        const spanTexto = cuentaLink.querySelector("span");
        if (spanTexto) {
            spanTexto.textContent = "Mi cuenta";
        } else {
            cuentaLink.textContent = "Mi cuenta";
        }
    }
}

// Ejecutar al cargar la página
document.addEventListener("DOMContentLoaded", () => {
    updateNavbar();
});
