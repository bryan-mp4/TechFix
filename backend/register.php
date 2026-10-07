<?php
// ==============================================================================
// Endpoint de Registro de Usuarios (POST) - TechFix API REST
// ==============================================================================

require_once __DIR__ . '/db.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    sendJsonResponse(true, 'OK', null, 200);
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse(false, 'Método HTTP no permitido. Debe ser POST.', null, 405);
}

$data = getRequestData();

$nombre   = trim($data['nombre'] ?? '');
$email    = trim($data['email'] ?? '');
$password = $data['password'] ?? '';
$telefono = trim($data['telefono'] ?? '');

// 1. Validaciones requeridas de entrada
if (empty($nombre) || empty($email) || empty($password) || empty($telefono)) {
    sendJsonResponse(false, 'Todos los campos (nombre, email, contraseña, teléfono) son requeridos.', null, 400);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendJsonResponse(false, 'El correo electrónico ingresado no tiene un formato válido.', null, 400);
}

if (strlen($password) < 6) {
    sendJsonResponse(false, 'La contraseña debe tener al menos 6 caracteres.', null, 400);
}

try {
    $db = getDbConnection();

    // 2. Verificar que el email no esté registrado previamente (sentencia preparada)
    $stmtCheck = $db->prepare('SELECT id FROM usuarios WHERE email = :email LIMIT 1');
    $stmtCheck->execute([':email' => $email]);

    if ($stmtCheck->fetch()) {
        sendJsonResponse(false, 'El correo electrónico ya se encuentra registrado.', null, 409); // 409 Conflict
    }

    // 3. Hashear la contraseña con BCRYPT
    $passwordHash = password_hash($password, PASSWORD_BCRYPT);

    // 4. Insertar el nuevo cliente con rol por defecto 'cliente'
    $stmtInsert = $db->prepare('
        INSERT INTO usuarios (nombre, email, password_hash, telefono, rol)
        VALUES (:nombre, :email, :password_hash, :telefono, "cliente")
    ');

    $stmtInsert->execute([
        ':nombre'        => $nombre,
        ':email'         => $email,
        ':password_hash' => $passwordHash,
        ':telefono'      => $telefono
    ]);

    $userId = (int) $db->lastInsertId();

    // 5. Consultar los datos del usuario recién creado para devolverlo en la respuesta (sin el hash)
    $stmtUser = $db->prepare('SELECT id, nombre, email, telefono, rol, fecha_registro FROM usuarios WHERE id = :id');
    $stmtUser->execute([':id' => $userId]);
    $user = $stmtUser->fetch();
    $user['id'] = (int) $user['id'];

    sendJsonResponse(true, 'Registro de usuario exitoso.', $user, 201); // 201 Created

} catch (PDOException $e) {
    sendJsonResponse(false, 'Error interno en el servidor: ' . $e->getMessage(), null, 500);
}
