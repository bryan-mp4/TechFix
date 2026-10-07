<?php
// ==============================================================================
// Helpers y Funciones Utilitarias - TechFix API REST
// Respuestas JSON estandarizadas y manejo de CORS
// ==============================================================================

/**
 * Envia una respuesta JSON estandarizada con las cabeceras HTTP adecuadas.
 *
 * Estructura de respuesta:
 * {
 *   "success": bool,
 *   "message": string,
 *   "data": object|array|null
 * }
 */
function sendJsonResponse(bool $success, string $message, $data = null, int $statusCode = 200): void {
    // Limpiar cualquier búfer de salida accidental previo (ej. espacios en blanco)
    if (ob_get_length()) {
        ob_clean();
    }

    // Cabeceras HTTP y configuración de CORS
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    header('Content-Type: application/json; charset=utf-8');

    // Responder inmediatamente a peticiones preflight OPTIONS
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        exit();
    }

    http_response_code($statusCode);

    $response = [
        'success' => $success,
        'message' => $message,
        'data'    => $data
    ];

    echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit();
}

/**
 * Obtiene los datos de la petición HTTP recibida.
 * Soporta solicitudes x-www-form-urlencoded y JSON raw body (común en Retrofit).
 */
function getRequestData(): array {
    $rawInput = file_get_contents('php://input');
    $jsonData = json_decode($rawInput, true);

    if (is_array($jsonData)) {
        return array_merge($_POST, $jsonData);
    }

    return $_POST;
}
