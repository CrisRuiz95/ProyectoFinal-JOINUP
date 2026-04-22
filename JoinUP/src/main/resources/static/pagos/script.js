// Obtener la página actual
const paginaActual = window.location.pathname.split('/').pop();

// Verificar que el usuario está logueado
const usuario = getLoggedUser();
if (!usuario) {
    alert("Debes iniciar sesión para acceder a esta página");
    window.location.href = "../login/login.html";
}

// Lógica según la página
switch(paginaActual) {
    case 'pagos-1.html':
        // Manejar el formulario de pago
        const formPago = document.getElementById('formPago');
        const btnCancelar = document.getElementById('btnCancelar');
        
        formPago.addEventListener('submit', function(e) {
            e.preventDefault();
            // Validar que los campos no estén vacíos (validación básica)
            const inputs = formPago.querySelectorAll('input');
            let todosLlenos = true;
            
            inputs.forEach(input => {
                if (!input.value) {
                    todosLlenos = false;
                }
            });
            
            if (!todosLlenos) {
                alert('Por favor, completa todos los campos');
                return;
            }
            
            // Si todo está bien, ir a página 2
            window.location.href = "pagos-2.html";
        });
        
        btnCancelar.addEventListener('click', function() {
            window.location.href = "../area_personal/usuario.html";
        });
        break;
        
    case 'pagos-2.html':
        // Simular verificación de autenticación (2 segundos)
        setTimeout(() => {
            window.location.href = "pagos-3.html";
        }, 2000);
        break;
        
    case 'pagos-3.html':
        // Simular solicitud de autorización (2 segundos) y procesar pago
        setTimeout(async () => {
            await procesarPagoYActualizarUsuario();
            window.location.href = "pagos-4.html";
        }, 2000);
        break;
        
    case 'pagos-4.html':
        // Actualizar fecha actual en el resultado
        const fechaActual = new Date();
        const opciones = { 
            year: 'numeric', 
            month: '2-digit', 
            day: '2-digit', 
            hour: '2-digit', 
            minute: '2-digit' 
        };
        const fechaFormateada = fechaActual.toLocaleString('es-ES', opciones).replace(',', '');
        
        // Buscar el span de fecha y actualizarlo
        const spansFecha = document.querySelectorAll('.col-7.py-3.border-3 span');
        if (spansFecha.length >= 4) {
            spansFecha[3].textContent = fechaFormateada;
        }
        
        // Botón volver a home
        document.getElementById('btnVolverHome').addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = "../area_personal/usuario.html";
        });
        break;
}

// Función para procesar el pago en el backend
async function procesarPagoYActualizarUsuario() {
    try {
        const response = await fetch(`http://localhost:8081/api/pagos?idUsuario=${usuario.idCliente}&monto=10.00&moneda=EUR`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const pago = await response.json();
            console.log("Pago procesado correctamente:", pago);
            
            // Actualizar el usuario en localStorage con el nuevo rol
            const responseUsuario = await fetch(`http://localhost:8081/api/usuarios/${usuario.idCliente}`);
            if (responseUsuario.ok) {
                const usuarioActualizado = await responseUsuario.json();
                localStorage.setItem('usuario', JSON.stringify(usuarioActualizado));
                console.log("Usuario actualizado a PREMIUM");
            }
        } else {
            console.error("Error al procesar el pago");
            alert("Error al procesar el pago. Por favor, intenta de nuevo.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("No se pudo conectar con el servidor");
    }
}
