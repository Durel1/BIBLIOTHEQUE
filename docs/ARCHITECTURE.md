# Architecture — BIBLIOTHEQUE

## Architecture cible

```text
Utilisateur
    |
    v
Frontend
HTML / CSS / JavaScript
    |
    | HTTP / JSON
    v
REST API
Spring Boot
    |
    +---- Spring Security / JWT
    |
    +---- Services métier
    |
    +---- Spring Data JPA / Hibernate
              |
              v
          PostgreSQL
          Supabase

Couvertures de livres
    |
    v
Supabase Storage
```

## Déploiement prévu

- Frontend : Vercel
- Backend : Render
- Base de données : PostgreSQL / Supabase
- Stockage des images : Supabase Storage
- Code source : GitHub
