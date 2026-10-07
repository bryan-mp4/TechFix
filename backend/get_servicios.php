<?php
// ==============================================================================
// Endpoint de Catálogo de Servicios (GET) - TechFix API REST
// ==============================================================================

require_once __DIR__ . '/db.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    sendJsonResponse(true, 'OK', null, 200);
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendJsonResponse(false, 'Método HTTP no permitido. Debe ser GET.', null, 405);
}

try {
    $db = getDbConnection();

    // Consultar todos los servicios activos
    $stmt = $db->prepare('
        SELECT id, nombre, descripcion, tipo_dispositivo, precio_base, icono, activo
        FROM servicios
        WHERE activo = 1
        ORDER BY id ASC
    ');
    $stmt->execute();
    $servicios = $stmt->fetchAll();

    // Castear tipos de datos numéricos
    foreach ($servicios as &$servicio) {
        $servicio['id']          = (int) $servicio['id'];
        $servicio['precio_base'] = (float) $servicio['precio_base'];
        $servicio['activo']      = (int) $servicio['activo'];
    }

    sendJsonResponse(true, 'Catálogo de servicios obtenido exitosamente.', $servicios, 200);

} catch (PDOException $e) {
    sendJsonResponse(false, 'Error interno al consultar los servicios: ' . $e->getMessage(), null, 500);
}
