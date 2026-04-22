// Definimos la constante de la URL de la api
const API_URL = "http://localhost:8081/api/usuarios";


// Creamos los datos que vamos a recoger para pasar al back
let datosRegistro = {
    nombre: "",
    ap1: "",
    ap2: "",
    email: "",
    password: "",
    numTelefono: "",
    fecNac: "",
    rol: "GRATUITO",
    poblacion: "",
    provincia: "",
    intV1: "",
    intV2: "",
    intV3: ""
};

// Navegación entre los distintos panneles del registr
document.querySelectorAll(".btn[data-paso]").forEach(btn => {
    btn.addEventListener("click", () => {
        const n = parseInt(btn.dataset.paso);

        // Validar datos antes de avanzar
        if (n === 2 && !validarPanel1()) return;
        if (n === 3 && !validarPanel2()) return;
        if (n === 4) {
            capturarPanel3();
            mostrarResumen();
        }

        // Actualizar círculos de progreso
        document.querySelectorAll(".paso").forEach(s => s.classList.remove("activo"));
        document.getElementById("pasoCirculo" + n).classList.add("activo");
    });
});

// Validamos datos del Panel 1 y los recogemos

function validarPanel1() {
    const nombre = document.getElementById("nombre").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const telefono = document.getElementById("telefono").value.trim();
    const fechaNac = document.getElementById("fechaNacimiento").value;
    const errorMensaje = document.getElementById("errorMensaje");

    errorMensaje.style.display = "none";

    if (!nombre || !email || !password || !telefono || !fechaNac) {
        errorMensaje.textContent = "Por favor, completa todos los campos obligatorios";
        errorMensaje.style.display = "block";
        return false;
    }

    // Validar formato de teléfono  (9 dígitos)
    const telefonoRegex = /^\d{9}$/;
    if (!telefonoRegex.test(telefono)) {
        errorMensaje.textContent = "El teléfono debe contener exactamente 9 dígitos";
        errorMensaje.style.display = "block";
        return false;
    }

    // Validar contraseñ para que concuerde con la vlidacion del back (mínimo 8 caracteres, mayúsculas, minúsculas, número y carácter especial)
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-]).{8,}$/;
    if (!passwordRegex.test(password)) {
        errorMensaje.textContent = "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, un número y un carácter especial";
        errorMensaje.style.display = "block";
        return false;
    }

    // Guardamos los datos validados en el objeto que le pasaremos a la api
    datosRegistro.nombre = nombre;
    datosRegistro.ap1 = document.getElementById("ap1").value.trim();
    datosRegistro.ap2 = document.getElementById("ap2").value.trim();
    datosRegistro.email = email;
    datosRegistro.password = password;
    datosRegistro.numTelefono = telefono;
    datosRegistro.fecNac = fechaNac;
    datosRegistro.poblacion = document.getElementById("ciudad").value.trim();
    datosRegistro.provincia = document.getElementById("provincia").value;

    return true;
}

// Validamos datos del Panel 2 y los recogemos
const intereses = document.querySelectorAll('label.interes');
let seleccionados = [];

intereses.forEach(interes => {
    interes.addEventListener('click', () => {
        if (interes.classList.contains('selected')) {
            interes.classList.remove('selected');
            seleccionados = seleccionados.filter(i => i !== interes);
            return;
        }

        if (seleccionados.length >= 3) {
            const primero = seleccionados.shift();
            primero.classList.remove('selected');
        }

        interes.classList.add('selected');
        seleccionados.push(interes);
    });
});

function validarPanel2() {
    if (seleccionados.length < 3) {
        alert("Por favor, selecciona 3 intereses");
        return false;
    }

    // Guardar intereses
    datosRegistro.intV1 = seleccionados[0]?.dataset.value || "";
    datosRegistro.intV2 = seleccionados[1]?.dataset.value || "";
    datosRegistro.intV3 = seleccionados[2]?.dataset.value || "";

    return true;
}

// Recogemos la opción seleccionada del Panel 3
function capturarPanel3() {
    const tipoSeleccionado = document.querySelector('input[name="tipo"]:checked');
    datosRegistro.rol = tipoSeleccionado ? tipoSeleccionado.value : "GRATUITO";
}


// Mostramos el resumen de la info recogide durante el registro.
function mostrarResumen() {
    const resumen = document.getElementById("resumenDatos");
    resumen.innerHTML = `
        <p><strong>Nombre:</strong> ${datosRegistro.nombre} ${datosRegistro.ap1} ${datosRegistro.ap2}</p>
        <p><strong>Email:</strong> ${datosRegistro.email}</p>
        <p><strong>Teléfono:</strong> ${datosRegistro.numTelefono}</p>
        <p><strong>Ubicación:</strong> ${datosRegistro.poblacion}, ${datosRegistro.provincia}</p>
        <p><strong>Intereses:</strong> ${datosRegistro.intV1}, ${datosRegistro.intV2}, ${datosRegistro.intV3}</p>
        <p><strong>Tipo de cuenta:</strong> ${datosRegistro.rol}</p>
    `;
}

// Enviamos la info recogida al back y mostramos feedback de si ha sido un exito o no.
document.querySelector(".btn-panel-cuatro").addEventListener("click", async () => {
    const mensajeExito = document.getElementById("mensajeExito");
    const mensajeError = document.getElementById("mensajeError");
    
    mensajeExito.style.display = "none";
    mensajeError.style.display = "none";

    // Guardar si el usuario seleccionó Premium
    const seleccionoPremium = datosRegistro.rol === "PREMIUM";
    
    // Crear usuario siempre como GRATUITO primero
    const datosParaCrear = { ...datosRegistro, rol: "GRATUITO" };

    try {
        const response = await fetch(`${API_URL}/create`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datosParaCrear)
        });

        if (response.ok) {
            const usuarioCreado = await response.json();
            mensajeExito.textContent = "¡Registro exitoso!";
            mensajeExito.style.display = "block";
            
            // Hacer login automático
            const loginResponse = await fetch(`${API_URL}/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: datosRegistro.email,
                    password: datosRegistro.password
                })
            });

            if (loginResponse.ok) {
                const usuario = await loginResponse.json();
                
                // Guardar en localStorage
                localStorage.setItem("usuario", JSON.stringify(usuario));
                localStorage.setItem("isLoggedIn", "true");
                localStorage.setItem("userId", usuario.idCliente);
                
                // Redirigir según la selección
                setTimeout(() => {
                    if (seleccionoPremium) {
                        // Redirigir a la pasarela de pago
                        window.location.href = "../pagos/pagos-1.html";
                    } else {
                        // Redirigir al área personal
                        window.location.href = "../area_personal/usuario.html";
                    }
                }, 1500);
            } else {
                mensajeError.textContent = "Usuario creado pero error en login automático. Por favor, inicia sesión manualmente.";
                mensajeError.style.display = "block";
                setTimeout(() => {
                    window.location.href = "../login/login.html";
                }, 3000);
            }
            
        } else {
            const errorData = await response.json();
            mensajeError.textContent = errorData.message || "Error al crear la cuenta. Verifica los datos.";
            mensajeError.style.display = "block";
        }
        
    } catch (error) {
        console.error("Error:", error);
        mensajeError.textContent = "No se pudo conectar con el servidor";
        mensajeError.style.display = "block";
    }
});
