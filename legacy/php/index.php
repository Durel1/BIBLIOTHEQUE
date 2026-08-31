<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bibliothèque</title>
    <link rel="stylesheet" href="styles.css">
</head>

<body>
    <div class="container">
        <?php
        session_start(); // Assurez-vous que la session est démarrée
        require 'db.php'; // Inclure le fichier de connexion à la base de données

        $page = $_GET['page'] ?? 'login'; // Page par défaut

        // Fonction pour vérifier si l'utilisateur est authentifié
        function isAuthenticated() {
            return isset($_SESSION['user_id']);
        }

        // Gérer les différentes pages
        switch ($page) {
            case 'login':
                require 'login.php';
                break;
            case 'register':
                require 'register.php';
                break;
            case 'books':
                if (!isAuthenticated()) {
                    header('Location: index.php?page=login'); // Rediriger sans le chemin absolu
                    exit();
                }
                require 'books.php';
                break;
            case 'books/add':
                if (!isAuthenticated()) {
                    header('Location: index.php?page=login'); // Rediriger sans le chemin absolu
                    exit();
                }
                require 'add_book.php';
                break;
            case 'books/details':
                if (!isAuthenticated()) {
                    header('Location: index.php?page=login'); // Rediriger sans le chemin absolu
                    exit();
                }
                require 'book_details.php';
                break;
            default:
                require '404.php'; // Page non trouvée
        }
        ?>
    </div>
</body>

</html>