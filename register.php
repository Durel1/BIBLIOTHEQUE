<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>

    <div class="container">
        
        <h1>Inscription</h1>
        <?php
        require 'db.php';

        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $username = $_POST['username'];
            $email = $_POST['email'];
            $password = $_POST['password'];

            // Hacher le mot de passe
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

            // Préparer la requête pour insérer l'utilisateur
            $stmt = $pdo->prepare("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
            
            // Essayer d'exécuter la requête
            try {
                $stmt->execute([$username, $email, $hashedPassword]);
                if ($stmt) {
                    header('Location: /bibliotheque/index.php?page=books');
                    exit();
                }
            } catch (PDOException $e) {
                echo "<p class='error'>Erreur : " . $e->getMessage() . "</p>";
            }
        }
        ?>

        <form method="POST">
            <input type="text" name="username" placeholder="Nom d'utilisateur" required>
            <input type="email" name="email" placeholder="E-mail" required>
            <input type="password" name="password" placeholder="Mot de passe" required>
            <button type="submit">S'inscrire</button>
            <p>aviez-vous deja un compte ? <a href="index.php?page=login">Connectez-vous</a>.</p>
        </form>
    </div>

</body>
</html>