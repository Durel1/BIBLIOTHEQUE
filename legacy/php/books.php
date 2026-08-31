<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Livres</title>
    <link rel="stylesheet" href="books.css">
</head>
<body>
    <div class="container">
        <h1>Gestion des Livres</h1>

        <!-- Barre de recherche -->
        <input type="text" id="searchBar" placeholder="Rechercher un livre par titre ou auteur...">

        <!-- Bouton pour afficher le formulaire d'ajout -->
        <button id="showAddFormButton">Ajouter un livre</button>

        <!-- Formulaire pour ajouter un livre (masqué par défaut) -->
        <div id="addBookForm">
            <h2>Ajouter un Livre</h2>
            <form method="POST" enctype="multipart/form-data">
                <label for="title">Titre</label>
                <input type="text" name="title" placeholder="Titre" required>

                <label for="author">Auteur</label>
                <input type="text" name="author" placeholder="Auteur" required>

                <label for="published_year">Année de publication</label>
                <input type="number" name="published_year" placeholder="Année de publication" required>

                <label for="genre">Genre</label>
                <input type="text" name="genre" placeholder="Genre" required>

                <label for="image">Image</label>
                <input type="file" name="image" id="addImage" accept="image/*" required>

                <!-- Aperçu de l'image -->
                <div class="image-preview">
                    <img id="addImagePreview" src="#" alt="Aperçu de l'image" style="display: none;">
                </div>

                <div class="form-buttons">
                    <button type="submit" name="add">Ajouter</button>
                    <button type="button" id="cancelAddFormButton">Annuler</button>
                </div>
            </form>
        </div>

        <?php
        require 'db.php';

        // Traitement des formulaires pour ajouter, modifier ou supprimer des livres
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            // Ajouter un livre
            if (isset($_POST['add'])) {
                $title = $_POST['title'];
                $author = $_POST['author'];
                $published_year = $_POST['published_year'];
                $genre = $_POST['genre'];

                // Gestion de l'image
                $imagePath = '';
                if (isset($_FILES['image']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                    $uploadsDir = 'uploads/';
                    if (!is_dir($uploadsDir)) {
                        mkdir($uploadsDir, 0777, true);
                    }
                    $imagePath = $uploadsDir . basename($_FILES['image']['name']);
                    move_uploaded_file($_FILES['image']['tmp_name'], $imagePath);
                }

                $stmt = $pdo->prepare("INSERT INTO books (title, author, published_year, genre, image) VALUES (?, ?, ?, ?, ?)");
                $stmt->execute([$title, $author, $published_year, $genre, $imagePath]);
            }

            // Supprimer un livre
            if (isset($_POST['delete'])) {
                $id = $_POST['book_id'];
                $stmt = $pdo->prepare("DELETE FROM books WHERE id = ?");
                $stmt->execute([$id]);
            }

            // Modifier un livre
            if (isset($_POST['edit'])) {
                $id = $_POST['book_id'];
                $title = $_POST['title'];
                $author = $_POST['author'];
                $published_year = $_POST['published_year'];
                $genre = $_POST['genre'];

                // Gestion de l'image
                $imagePath = $_POST['existing_image']; // Conserver l'image existante par défaut
                if (isset($_FILES['picture']) && $_FILES['picture']['error'] === UPLOAD_ERR_OK) {
                    $uploadsDir = 'uploads/';
                    if (!is_dir($uploadsDir)) {
                        mkdir($uploadsDir, 0777, true);
                    }
                    $imagePath = $uploadsDir . basename($_FILES['picture']['name']);
                    move_uploaded_file($_FILES['picture']['tmp_name'], $imagePath);
                }

                // Mettre à jour le livre
                $stmt = $pdo->prepare("UPDATE books SET title = ?, author = ?, published_year = ?, genre = ?, image = ? WHERE id = ?");
                $stmt->execute([$title, $author, $published_year, $genre, $imagePath, $id]);
            }
        }

        // Récupérer les livres
        $stmt = $pdo->query("SELECT * FROM books");
        $books = $stmt->fetchAll();
        ?>

        <!-- Liste des livres -->
        <div id="bookList">
            <h2>Liste des Livres</h2>
            <table>
                <thead>
                    <tr>
                        <th>Image</th>
                        <th>Titre</th>
                        <th>Auteur</th>
                        <th>Année</th>
                        <th>Genre</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="bookTableBody">
                    <?php foreach ($books as $book): ?>
                        <tr class="bookRow">
                            <td>
                                <?php if ($book['image']): ?>
                                    <img src="<?php echo htmlspecialchars($book['image']); ?>" alt="Image de <?php echo htmlspecialchars($book['title']); ?>">
                                <?php endif; ?>
                            </td>
                            <td class="bookTitle"><?php echo htmlspecialchars($book['title']); ?></td>
                            <td class="bookAuthor"><?php echo htmlspecialchars($book['author']); ?></td>
                            <td><?php echo htmlspecialchars($book['published_year']); ?></td>
                            <td><?php echo htmlspecialchars($book['genre']); ?></td>
                            <td>
                                <button onclick="showDetail(<?php echo $book['id']; ?>, '<?php echo addslashes($book['title']); ?>', '<?php echo addslashes($book['author']); ?>', '<?php echo $book['published_year']; ?>', '<?php echo addslashes($book['genre']); ?>', '<?php echo htmlspecialchars($book['image']); ?>')">Détail</button>
                                <button style="background-color: #ffae02;" onclick="showEditForm(<?php echo $book['id']; ?>, '<?php echo addslashes($book['title']); ?>', '<?php echo addslashes($book['author']); ?>', '<?php echo $book['published_year']; ?>', '<?php echo addslashes($book['genre']); ?>', '<?php echo htmlspecialchars($book['image']); ?>')">Modifier</button>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
        </div>

        <!-- Formulaire pour modifier un livre (masqué par défaut) -->
        <div id="editBookForm">
            <h2>Modifier un Livre</h2>
            <form method="POST" enctype="multipart/form-data">
                <input type="hidden" name="book_id" id="editBookId">
                <input type="hidden" name="existing_image" id="editExistingImage">
                <label for="editTitle">Titre</label>
                <input type="text" name="title" id="editTitle" placeholder="Titre" required>

                <label for="editAuthor">Auteur</label>
                <input type="text" name="author" id="editAuthor" placeholder="Auteur" required>

                <label for="editPublishedYear">Année de publication</label>
                <input type="number" name="published_year" id="editPublishedYear" placeholder="Année de publication" required>

                <label for="editGenre">Genre</label>
                <input type="text" name="genre" id="editGenre" placeholder="Genre" required>

                <label for="editPicture">Nouvelle image</label>
                <input type="file" name="picture" id="editPicture">

                <!-- Aperçu de l'image -->
                <div class="image-preview">
                    <img id="editImagePreview" src="#" alt="Aperçu de l'image" style="display: none;">
                </div>

                <div class="form-buttons">
                    <button type="submit" name="edit">Enregistrer</button>
                    <button type="submit" name="delete" style="background-color:rgb(218, 48, 18);">Supprimer</button>
                    <button type="button" id="cancelEditFormButton">Annuler</button>
                </div>
            </form>
        </div>

        <!-- Détails du livre (masqué par défaut) -->
        <div id="bookDetail">
            <h2>Détails du Livre</h2>
            <img id="detailImage" src="#" alt="Image du livre" style="display: none;">
            <p><strong>Titre:</strong> <span id="detailTitle"></span></p>
            <p><strong>Auteur:</strong> <span id="detailAuthor"></span></p>
            <p><strong>Année de publication:</strong> <span id="detailPublishedYear"></span></p>
            <p><strong>Genre:</strong> <span id="detailGenre"></span></p>
            <button onclick="closeDetail()">Retour</button>
        </div>
    </div>

    <script>
        // Gestion de l'affichage du formulaire d'ajout
        document.getElementById('showAddFormButton').addEventListener('click', function () {
            document.getElementById('addBookForm').style.display = 'block';
            document.getElementById('bookList').classList.add('hidden');
            document.getElementById('searchBar').classList.add('hidden');
            document.getElementById('showAddFormButton').classList.add('hidden');
            document.getElementById('editBookForm').style.display = 'none';
            document.getElementById('bookDetail').style.display = 'none';
        });

        // Gestion de l'annulation du formulaire d'ajout
        document.getElementById('cancelAddFormButton').addEventListener('click', function () {
            document.getElementById('addBookForm').style.display = 'none';
            document.getElementById('bookList').classList.remove('hidden');
            document.getElementById('searchBar').classList.remove('hidden');
            document.getElementById('showAddFormButton').classList.remove('hidden');
        });

        // Fonction pour afficher le formulaire de modification
        function showEditForm(id, title, author, publishedYear, genre, image) {
            document.getElementById('editBookId').value = id;
            document.getElementById('editTitle').value = title;
            document.getElementById('editAuthor').value = author;
            document.getElementById('editPublishedYear').value = publishedYear;
            document.getElementById('editGenre').value = genre;
            document.getElementById('editExistingImage').value = image;

            // Afficher l'image actuelle
            const preview = document.getElementById('editImagePreview');
            if (image) {
                preview.src = image;
                preview.style.display = 'block';
            } else {
                preview.style.display = 'none';
            }

            // Masquer les autres éléments et afficher le formulaire de modification
            document.getElementById('editBookForm').style.display = 'block';
            document.getElementById('bookList').classList.add('hidden');
            document.getElementById('searchBar').classList.add('hidden');
            document.getElementById('showAddFormButton').classList.add('hidden');
            document.getElementById('addBookForm').style.display = 'none';
            document.getElementById('bookDetail').style.display = 'none';
        }

        // Gestion de l'annulation du formulaire de modification
        document.getElementById('cancelEditFormButton').addEventListener('click', function () {
            document.getElementById('editBookForm').style.display = 'none';
            document.getElementById('bookList').classList.remove('hidden');
            document.getElementById('searchBar').classList.remove('hidden');
            document.getElementById('showAddFormButton').classList.remove('hidden');
        });

        // Fonction pour afficher les détails du livre
        function showDetail(id, title, author, publishedYear, genre, image) {
            document.getElementById('detailTitle').textContent = title;
            document.getElementById('detailAuthor').textContent = author;
            document.getElementById('detailPublishedYear').textContent = publishedYear;
            document.getElementById('detailGenre').textContent = genre;

            // Afficher l'image
            const detailImage = document.getElementById('detailImage');
            if (image) {
                detailImage.src = image;
                detailImage.style.display = 'block';
            } else {
                detailImage.style.display = 'none';
            }

            // Masquer les autres éléments et afficher les détails du livre
            document.getElementById('bookDetail').style.display = 'block';
            document.getElementById('bookList').classList.add('hidden');
            document.getElementById('searchBar').classList.add('hidden');
            document.getElementById('showAddFormButton').classList.add('hidden');
            document.getElementById('addBookForm').style.display = 'none';
            document.getElementById('editBookForm').style.display = 'none';
        }

        // Fonction pour fermer les détails du livre
        function closeDetail() {
            document.getElementById('bookDetail').style.display = 'none';
            document.getElementById('bookList').classList.remove('hidden');
            document.getElementById('searchBar').classList.remove('hidden');
            document.getElementById('showAddFormButton').classList.remove('hidden');
        }

        // Fonction de recherche
        document.getElementById('searchBar').addEventListener('input', function () {
            const searchTerm = this.value.toLowerCase();
            const rows = document.querySelectorAll('#bookTableBody .bookRow');

            rows.forEach(row => {
                const title = row.querySelector('.bookTitle').textContent.toLowerCase();
                const author = row.querySelector('.bookAuthor').textContent.toLowerCase();

                if (title.includes(searchTerm) || author.includes(searchTerm)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });

        // Aperçu de l'image dans le formulaire d'ajout
        document.getElementById('addImage').addEventListener('change', function (event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    const preview = document.getElementById('addImagePreview');
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });

        // Aperçu de l'image dans le formulaire de modification
        document.getElementById('editPicture').addEventListener('change', function (event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    const preview = document.getElementById('editImagePreview');
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    </script>
</body>
</html>