package com.example.cafenea.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(IllegalArgumentException ex, Model model) {
        logger.error("Resursa negasita sau operatie invalida: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler({NoResourceFoundException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRouteNotFound(Exception ex, Model model) {
        logger.error("Ruta negasita: {}", ex.getMessage());
        model.addAttribute("message", "Pagina cautata nu exista.");
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        logger.error("Acces refuzat: {}", ex.getMessage());
        model.addAttribute("message", "Nu ai drepturi pentru aceasta operatie.");
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        logger.error("Eroare neasteptata in aplicatie.", ex);
        model.addAttribute("message", "A aparut o problema neasteptata. Te rugam sa incerci din nou.");
        return "error/500";
    }
}
