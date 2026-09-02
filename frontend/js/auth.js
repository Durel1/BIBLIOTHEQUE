import { apiRequest } from "./api.js";


/*
 * Authentication pages should not be shown
 * to an already authenticated user.
 */
const existingToken =
        localStorage.getItem("token");

if (existingToken) {
    window.location.href =
            "index.html";
}


/* =========================================================
   LOGIN
   ========================================================= */

const loginForm =
        document.getElementById("login-form");


if (loginForm) {

    loginForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();

            const errorElement =
                    document.getElementById(
                            "login-error"
                    );

            const submitButton =
                    loginForm.querySelector(
                            'button[type="submit"]'
                    );

            errorElement.textContent =
                    "";

            submitButton.disabled =
                    true;

            submitButton.textContent =
                    "Connexion...";


            const email =
                    document
                            .getElementById("email")
                            .value
                            .trim();

            const password =
                    document
                            .getElementById("password")
                            .value;


            try {

                const response =
                        await apiRequest(
                                "/api/auth/login",
                                {
                                    method: "POST",

                                    body:
                                            JSON.stringify({
                                                email,
                                                password
                                            })
                                }
                        );


                localStorage.setItem(
                        "token",
                        response.token
                );


                window.location.href =
                        "index.html";


            } catch (error) {

                errorElement.textContent =
                        error.message;

                submitButton.disabled =
                        false;

                submitButton.textContent =
                        "Se connecter";
            }
        }
    );
}


/* =========================================================
   REGISTER
   ========================================================= */

const registerForm =
        document.getElementById(
                "register-form"
        );


if (registerForm) {

    registerForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();


            const errorElement =
                    document.getElementById(
                            "register-error"
                    );

            const successElement =
                    document.getElementById(
                            "register-success"
                    );

            const submitButton =
                    registerForm.querySelector(
                            'button[type="submit"]'
                    );


            errorElement.textContent =
                    "";

            successElement.textContent =
                    "";

            submitButton.disabled =
                    true;

            submitButton.textContent =
                    "Création...";


            const username =
                    document
                            .getElementById("username")
                            .value
                            .trim();

            const email =
                    document
                            .getElementById("email")
                            .value
                            .trim();

            const password =
                    document
                            .getElementById("password")
                            .value;


            try {

                await apiRequest(
                        "/api/auth/register",
                        {
                            method: "POST",

                            body:
                                    JSON.stringify({
                                        username,
                                        email,
                                        password
                                    })
                        }
                );


                registerForm.reset();

                successElement.textContent =
                        "Compte créé. Redirection vers la connexion...";


                /*
                 * Give the user enough time to see
                 * the confirmation message.
                 */
                setTimeout(
                        () => {

                            window.location.href =
                                    "login.html";

                        },
                        1200
                );


            } catch (error) {

                errorElement.textContent =
                        error.message;

                submitButton.disabled =
                        false;

                submitButton.textContent =
                        "Créer mon compte";
            }
        }
    );
}