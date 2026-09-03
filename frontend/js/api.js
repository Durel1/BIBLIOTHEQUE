/*
 * Use the local backend during development
 * and the Render API in production.
 */
const API_BASE_URL =
        window.location.hostname === "localhost"
                ? "http://localhost:8080"
                : "https://bibliotheque-api-4oxh.onrender.com";

export async function apiRequest(
        path,
        options = {}
) {

    const token =
            localStorage.getItem(
                    "token"
            );


    const headers = {
        "Content-Type":
                "application/json",

        ...options.headers
    };


    if (token) {

        headers.Authorization =
                `Bearer ${token}`;
    }


    let response;


    try {

        response =
                await fetch(
                        `${API_BASE_URL}${path}`,
                        {
                            ...options,
                            headers
                        }
                );

    } catch (error) {

        throw new Error(
                "Impossible de contacter le serveur."
        );
    }


    let data =
            null;


    if (
        response.status !== 204
    ) {

        const contentType =
                response.headers.get(
                        "content-type"
                );


        if (
            contentType &&
            contentType.includes(
                    "application/json"
            )
        ) {

            data =
                    await response.json();
        }
    }


    if (!response.ok) {

        if (
            response.status === 401
        ) {

            localStorage.removeItem(
                    "token"
            );


            /*
             * Do not immediately redirect login requests.
             * A wrong password also returns HTTP 401.
             */
            if (
                path !==
                "/api/auth/login"
            ) {

                window.location.href =
                        "login.html";
            }
        }


        throw new Error(
                data?.message ||
                "Une erreur est survenue."
        );
    }


    return data;
}