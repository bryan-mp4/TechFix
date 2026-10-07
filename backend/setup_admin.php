<?php
/*
==============================================================================
 AVISO IMPORTANTE DE SEGURIDAD:
 Este script es de UN SOLO USO para inicializar la cuenta del Administrador.
 UNA VEZ EJECUTADO Y VERIFICADO, DEBE ELIMINARSE ESTE ARCHIVO DEL SERVIDOR.
==============================================================================
*/

require_once __DIR__ . '/db.php';

$adminEmail    = 'admin@techfix.com';
$adminPass     = 'Admin12345*';
$adminNombre   = 'Administrador TechFix';
$adminTelefono = '3000000000';

try {
    $db = getDbConnection();

    // Verificar si ya existe
    $stmtCheck = $db->prepare('SELECT id FROM usuarios WHERE email = :email LIMIT 1');
    $stmtCheck->execute([':email' => $adminEmail]);

    if ($stmtCheck->fetch()) {
        sendJsonResponse(true, 'El usuario administrador ya existe en la base de datos.', [
            'email' => $adminEmail,
            'rol'   => 'admin'
        ], 200);
    }

    // Generar hash seguro BCRYPT
    $hash = password_hash($adminPass, PASSWORD_BCRYPT);

    $stmtInsert = $db->prepare('
        INSERT INTO usuarios (nombre, email, password_hash, telefono, rol)
        VALUES (:nombre, :email, :password_hash, :telefono, "admin")
    ');

    $stmtInsert->execute([
        ':nombre'        => $adminNombre,
        ':email'         => $adminEmail,
        ':password_hash' => $hash,
        ':telefono'      => $adminTelefono
    ]);

    sendJsonResponse(true, 'Usuario Administrador creado exitosamente. ¡RECUERDA BORRAR ESTE ARCHIVO!', [
        'email'             => $adminEmail,
        'password_temporal' => $adminPass,
        'rol'               => 'admin'
    ], 201);

} catch (PDOException $e) {
    sendJsonResponse(false, 'Error al crear el usuario administrador: ' . $e->getMessage(), null, 500);
}
