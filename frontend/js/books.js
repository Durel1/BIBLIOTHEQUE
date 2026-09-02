import { apiRequest } from "./api.js";


const token =
        localStorage.getItem("token");


if (!token) {

    window.location.href =
            "login.html";
}


/* =========================================================
   ELEMENTS
   ========================================================= */

const booksList =
        document.getElementById(
                "books-list"
        );

const emptyLibrary =
        document.getElementById(
                "empty-library"
        );

const booksLoading =
        document.getElementById(
                "books-loading"
        );

const booksError =
        document.getElementById(
                "books-error"
        );

const bookCount =
        document.getElementById(
                "book-count"
        );

const logoutButton =
        document.getElementById(
                "logout-button"
        );

const bookForm =
        document.getElementById(
                "book-form"
        );

const bookFormTitle =
        document.getElementById(
                "book-form-title"
        );

const bookSubmitButton =
        document.getElementById(
                "book-submit-button"
        );

const cancelEditButton =
        document.getElementById(
                "cancel-edit-button"
        );

const bookFormError =
        document.getElementById(
                "book-form-error"
        );

const bookFormMessage =
        document.getElementById(
                "book-form-message"
        );


let editingBookId =
        null;


/* =========================================================
   LOAD BOOKS
   ========================================================= */

async function loadBooks() {

    booksError.textContent =
            "";

    booksLoading.hidden =
            false;


    try {

        const books =
                await apiRequest(
                        "/api/books"
                );

        displayBooks(
                books
        );


    } catch (error) {

        booksError.textContent =
                error.message;

    } finally {

        booksLoading.hidden =
                true;
    }
}


/* =========================================================
   DISPLAY BOOKS
   ========================================================= */

function displayBooks(books) {

    booksList.innerHTML =
            "";


    updateBookCount(
            books.length
    );


    if (books.length === 0) {

        emptyLibrary.hidden =
                false;

        return;
    }


    emptyLibrary.hidden =
            true;


    books.forEach(
            book => {

                booksList.appendChild(
                        createBookCard(book)
                );
            }
    );
}


/**
 * Builds one complete visual card.
 */
function createBookCard(book) {

    const article =
            document.createElement(
                    "article"
            );

    article.className =
            "book-card";


    /* ---------- COVER ---------- */

    const cover =
            document.createElement(
                    "div"
            );

    cover.className =
            "book-cover";


    if (book.coverUrl) {

        const image =
                document.createElement(
                        "img"
                );

        image.src =
                book.coverUrl;

        image.alt =
                `Couverture de ${book.title}`;


        /*
         * If the external cover URL fails,
         * replace it with our placeholder.
         */
        image.addEventListener(
                "error",
                () => {

                    cover.innerHTML =
                            "";

                    cover.appendChild(
                            createCoverPlaceholder()
                    );
                }
        );


        cover.appendChild(
                image
        );

    } else {

        cover.appendChild(
                createCoverPlaceholder()
        );
    }


    /* ---------- CONTENT ---------- */

    const content =
            document.createElement(
                    "div"
            );

    content.className =
            "book-content";


    const title =
            document.createElement(
                    "h3"
            );

    title.textContent =
            book.title;


    const author =
            document.createElement(
                    "p"
            );

    author.className =
            "book-author";

    author.textContent =
            book.author;


    content.appendChild(
            title
    );

    content.appendChild(
            author
    );


    /* ---------- META ---------- */

    const meta =
            document.createElement(
                    "div"
            );

    meta.className =
            "book-meta";


    if (book.publishedYear) {

        meta.appendChild(
                createTag(
                        book.publishedYear
                )
        );
    }


    if (book.genre) {

        meta.appendChild(
                createTag(
                        book.genre
                )
        );
    }


    if (meta.children.length > 0) {

        content.appendChild(
                meta
        );
    }


    /* ---------- DESCRIPTION ---------- */

    if (book.description) {

        const description =
                document.createElement(
                        "p"
                );

        description.className =
                "book-description";

        description.textContent =
                book.description;

        content.appendChild(
                description
        );
    }


    /* ---------- ACTIONS ---------- */

    const actions =
            document.createElement(
                    "div"
            );

    actions.className =
            "book-actions";


    const editButton =
            document.createElement(
                    "button"
            );

    editButton.type =
            "button";

    editButton.className =
            "secondary-button";

    editButton.textContent =
            "Modifier";


    editButton.addEventListener(
            "click",
            () => startEditing(book)
    );


    const deleteButton =
            document.createElement(
                    "button"
            );

    deleteButton.type =
            "button";

    deleteButton.className =
            "danger-button";

    deleteButton.textContent =
            "Supprimer";


    deleteButton.addEventListener(
            "click",
            () => deleteBook(book)
    );


    actions.appendChild(
            editButton
    );

    actions.appendChild(
            deleteButton
    );


    content.appendChild(
            actions
    );


    article.appendChild(
            cover
    );

    article.appendChild(
            content
    );


    return article;
}


/* =========================================================
   SMALL UI HELPERS
   ========================================================= */

function createCoverPlaceholder() {

    const placeholder =
            document.createElement(
                    "div"
            );

    placeholder.className =
            "book-cover-placeholder";

    placeholder.textContent =
            "B";

    return placeholder;
}


function createTag(value) {

    const tag =
            document.createElement(
                    "span"
            );

    tag.className =
            "book-tag";

    tag.textContent =
            value;

    return tag;
}


function updateBookCount(count) {

    bookCount.textContent =
            count === 1
                    ? "1 livre"
                    : `${count} livres`;
}


/* =========================================================
   FORM
   ========================================================= */

function buildBookRequest() {

    const title =
            document
                    .getElementById("title")
                    .value
                    .trim();

    const author =
            document
                    .getElementById("author")
                    .value
                    .trim();

    const publishedYear =
            document
                    .getElementById(
                            "published-year"
                    )
                    .value;

    const genre =
            document
                    .getElementById("genre")
                    .value
                    .trim();

    const description =
            document
                    .getElementById(
                            "description"
                    )
                    .value
                    .trim();

    const coverUrl =
            document
                    .getElementById(
                            "cover-url"
                    )
                    .value
                    .trim();


    return {

        title,

        author,

        publishedYear:
                publishedYear
                        ? Number(
                                publishedYear
                        )
                        : null,

        genre:
                genre || null,

        description:
                description || null,

        coverUrl:
                coverUrl || null
    };
}


/* =========================================================
   CREATE / UPDATE
   ========================================================= */

bookForm.addEventListener(
        "submit",
        async event => {

            event.preventDefault();


            bookFormError.textContent =
                    "";

            bookFormMessage.textContent =
                    "";


            const request =
                    buildBookRequest();


            bookSubmitButton.disabled =
                    true;


            try {

                if (editingBookId === null) {

                    bookSubmitButton.textContent =
                            "Ajout...";


                    await apiRequest(
                            "/api/books",
                            {
                                method: "POST",

                                body:
                                        JSON.stringify(
                                                request
                                        )
                            }
                    );


                    resetBookForm();


                    bookFormMessage.textContent =
                            "Livre ajouté avec succès.";


                } else {

                    bookSubmitButton.textContent =
                            "Enregistrement...";


                    await apiRequest(
                            `/api/books/${editingBookId}`,
                            {
                                method: "PUT",

                                body:
                                        JSON.stringify(
                                                request
                                        )
                            }
                    );


                    resetBookForm();


                    bookFormMessage.textContent =
                            "Livre modifié avec succès.";
                }


                await loadBooks();


            } catch (error) {

                bookFormError.textContent =
                        error.message;


            } finally {

                bookSubmitButton.disabled =
                        false;
            }
        }
);


/* =========================================================
   EDIT
   ========================================================= */

function startEditing(book) {

    editingBookId =
            book.id;


    document.getElementById(
            "title"
    ).value =
            book.title;


    document.getElementById(
            "author"
    ).value =
            book.author;


    document.getElementById(
            "published-year"
    ).value =
            book.publishedYear ?? "";


    document.getElementById(
            "genre"
    ).value =
            book.genre ?? "";


    document.getElementById(
            "description"
    ).value =
            book.description ?? "";


    document.getElementById(
            "cover-url"
    ).value =
            book.coverUrl ?? "";


    bookFormTitle.textContent =
            "Modifier le livre";


    bookSubmitButton.textContent =
            "Enregistrer";


    cancelEditButton.hidden =
            false;


    bookFormMessage.textContent =
            "";

    bookFormError.textContent =
            "";


    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


function resetBookForm() {

    editingBookId =
            null;


    bookForm.reset();


    bookFormTitle.textContent =
            "Ajouter un livre";


    bookSubmitButton.textContent =
            "Ajouter le livre";


    cancelEditButton.hidden =
            true;
}


cancelEditButton.addEventListener(
        "click",
        () => {

            resetBookForm();

            bookFormError.textContent =
                    "";

            bookFormMessage.textContent =
                    "";
        }
);


/* =========================================================
   DELETE
   ========================================================= */

async function deleteBook(book) {

    const confirmed =
            window.confirm(
                    `Voulez-vous vraiment supprimer "${book.title}" ?`
            );


    if (!confirmed) {
        return;
    }


    try {

        await apiRequest(
                `/api/books/${book.id}`,
                {
                    method: "DELETE"
                }
        );


        if (
            editingBookId ===
            book.id
        ) {

            resetBookForm();
        }


        await loadBooks();


    } catch (error) {

        booksError.textContent =
                error.message;
    }
}


/* =========================================================
   LOGOUT
   ========================================================= */

logoutButton.addEventListener(
        "click",
        () => {

            localStorage.removeItem(
                    "token"
            );


            window.location.href =
                    "login.html";
        }
);


/* =========================================================
   START
   ========================================================= */

loadBooks();