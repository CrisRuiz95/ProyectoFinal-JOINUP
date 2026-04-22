// ====================================
// CONFIGURACIÓN DE LA API
// ====================================
const API_URL = "http://localhost:8081/api/usuarios";

// ====================================
// LOGIN NORMAL (EMAIL + PASSWORD)
// ====================================
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault(); // Evitar que el formulario se envíe de forma tradicional

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMessage = document.getElementById("errorMessage");

    // Limpiar mensaje de error previo
    errorMessage.style.display = "none";
    errorMessage.textContent = "";

    try {
        // Llamada al endpoint de login
        const response = await fetch(`${API_URL}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        if (response.ok) {
            // Login exitoso
            const usuario = await response.json();
            
            // Guardar información del usuario en localStorage
            localStorage.setItem("usuario", JSON.stringify(usuario));
            localStorage.setItem("isLoggedIn", "true");
            localStorage.setItem("userId", usuario.idCliente);
            
            console.log("Login exitoso:", usuario);
            
            // Redirigir a la página principal o área personal
            window.location.href = "../index.html";
            
        } else if (response.status === 401) {
            // Credenciales inválidas
            errorMessage.textContent = "Email o contraseña incorrectos";
            errorMessage.style.display = "block";
            
        } else {
            // Otro error del servidor
            errorMessage.textContent = "Error en el servidor. Intenta de nuevo más tarde.";
            errorMessage.style.display = "block";
        }
        
    } catch (error) {
        console.error("Error al hacer login:", error);
        errorMessage.textContent = "No se pudo conectar con el servidor";
        errorMessage.style.display = "block";
    }
});


// ====================================
// LOGIN CON GOOGLE OAUTH (OPCIONAL)
// ====================================
const GOOGLE_CLIENT_ID = "TU_CLIENT_ID_AQUI";

document.getElementById("token-google").onclick = () => {
    google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: handleGoogleResponse
    });

    google.accounts.id.prompt(); // Abre el cuadro de Google
};

function handleGoogleResponse(response) {
    const id_token = response.credential;

    console.log("Token recibido:", id_token);

    // Enviar token a tu backend para autenticarlo
    fetch(`${API_URL}/google-login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: id_token })
    })
    .then(r => r.json())
    .then(data => {
        console.log("Respuesta del backend:", data);
        
        // Guardar sesión y redirigir
        localStorage.setItem("usuario", JSON.stringify(data));
        localStorage.setItem("isLoggedIn", "true");
        window.location.href = "../index.html";
    })
    .catch(err => console.error("Error:", err));
}
