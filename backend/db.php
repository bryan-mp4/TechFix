<?php
// ==============================================================================
// Conexión Centralizada PDO a MySQL - TechFix API REST
// ==============================================================================

require_once __DIR__ . '/helpers.php';

$configFile = __DIR__ . '/config.php';
if (!file_exists($configFile)) {
    $exampleConfig = __DIR__ . '/config.example.php';
    if (file_exists($exampleConfig)) {
        require_once $exampleConfig;
    } else {
        sendJsonResponse(false, 'Error de configuración. No se encontró el archivo config.php', null, 500);
    }
} else {
    require_once $configFile;
}

/**
 * Retorna la instancia Singleton de conexión PDO a MySQL.
 */
function getDbConnection(): PDO {
    static $pdo = null;

    if ($pdo === null) {
        $dsn = sprintf('mysql:host=%s;dbname=%s;charset=%s', DB_HOST, DB_NAME, DB_CHARSET);
        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ];

        try {
            $pdo = new PDO($dsn, DB_USER, DB_PASS, $options);
        } catch (PDOException $e) {
            sendJsonResponse(false, 'Error al conectar con la base de datos: ' . $e->getMessage(), null, 500);
        }
    }

    return $pdo;
}
