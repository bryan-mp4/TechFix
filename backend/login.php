<?php
// ==============================================================================
// Endpoint de Inicio de Sesión (POST) - TechFix API REST
// ==============================================================================

require_once __DIR__ . '/db.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    sendJsonResponse(true, 'OK', null, 200);
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse(false, 'Método HTTP no permitido. Debe ser POST.', null, 405);
}

$data = getRequestData();

$email    = trim($data['email'] ?? '');
$password = $data['password'] ?? '';

// 1. Validaciones requeridas de entrada
if (empty($email) || empty($password)) {
    sendJsonResponse(false, 'Por favor ingrese su correo y contraseña.', null, 400);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendJsonResponse(false, 'El correo electrónico ingresado no tiene un formato válido.', null, 400);
}

try {
    $db = getDbConnection();

    // 2. Buscar el usuario por correo con sentencia preparada
    $stmt = $db->prepare('SELECT id, nombre, email, password_hash, telefono, rol, fecha_registro FROM usuarios WHERE email = :email LIMIT 1');
    $stmt->execute([':email' => $email]);
    $user = $stmt->fetch();

    if (!$user) {
        sendJsonResponse(false, 'Credenciales de acceso incorrectas.', null, 401); // 401 Unauthorized
    }

    // 3. Verificar el hash de la contraseña suministrada
    if (!password_verify($password, $user['password_hash'])) {
        sendJsonResponse(false, 'Credenciales de acceso incorrectas.', null, 401);
    }

    // 4. Remover el hash de la respuesta por seguridad
    unset($user['password_hash']);
    $user['id'] = (int) $user['id'];

    sendJsonResponse(true, 'Inicio de sesión exitoso.', $user, 200);

} catch (PDOException $e) {
    sendJsonResponse(false, 'Error interno en el servidor: ' . $e->getMessage(), null, 500);
}
