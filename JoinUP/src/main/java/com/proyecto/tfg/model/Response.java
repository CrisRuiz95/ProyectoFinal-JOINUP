package com.proyecto.tfg.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class Response {

    @Schema(description = "Descripción del request que causó el error")
    private String path;

    @Schema(description = "Código de estado HTTP")
    private HttpStatus status;

    @Schema(description = "Mensaje de error o descripción")
    private String message;

    @Schema(description = "Fecha y hora del suceso")
    private LocalDateTime timestamp;

    public Response() {}

    public Response(String path, HttpStatus status, String message, LocalDateTime timestamp) {
        this.path = path;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Response(String statusCode, String statusMsg) {
        this.path = null;         // opcional, puedes dejar null
        this.status = HttpStatus.CREATED;
        this.message = statusMsg;
        this.timestamp = LocalDateTime.now();
    }


    // Getters y Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Response{" +
                "path='" + path + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
