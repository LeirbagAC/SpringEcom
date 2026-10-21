package com.gabriel.SpringEcom.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        ProblemDetail problem = build(HttpStatus.CONFLICT, "Estoque insuficiente", ex.getMessage(), request);
        problem.setProperty("productName", ex.getProductName());
        problem.setProperty("currentStock", ex.getCurrentStock());
        problem.setProperty("requested", ex.getRequested());
        return problem;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomain(DomainException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", ex.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbidden(ForbiddenOperationException ex, HttpServletRequest request) {
        log.warn("Acesso negado em {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Acesso negado", ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex, HttpServletRequest request) {
        log.warn("Conflito em {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.CONFLICT, "Conflito", ex.getMessage(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Falha de autenticação em {}: {}", request.getRequestURI(), ex.getClass().getSimpleName());
        return build(HttpStatus.UNAUTHORIZED, "Não autenticado",
                "E-mail ou senha incorretos.", request);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail handleInvalidSort(PropertyReferenceException ex, HttpServletRequest request) {
        log.warn("Ordenação inválida em {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "O campo de ordenação informado não existe.", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        ProblemDetail problem = build(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Existem campos inválidos na requisição.",
                servletRequest
        );

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    // Para capturar erros de JSON malformado ou tipos inválidos, como "Failed to read request" e mostrar o erro no console
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // A causa técnica fica só no log
        log.warn("Corpo da requisição ilegível: {}", ex.getMostSpecificCause().getMessage());

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        ProblemDetail problem = build(
                HttpStatus.BAD_REQUEST,
                "Requisição ilegível",
                "O corpo da requisição está malformado ou contém valores de tipo inválido.",
                servletRequest
        );

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erro não tratado em {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor",
                "Ocorreu um erro inesperado.", request);
    }

    //Para caso um endpoint com @PreAuthorize o handler genérico captura e devolve 500 em vez de 403
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Acesso negado",
                "Você não tem permissão para acessar este recurso.", request);
    }

    //Para o 'Failed to read request' sair com timestamp
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        if (body instanceof ProblemDetail problem) {
            problem.setProperty("timestamp", Instant.now());
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ProblemDetail build(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}