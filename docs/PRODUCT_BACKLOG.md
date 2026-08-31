   # Product Backlog — BIBLIOTHEQUE V1

## Vision

BIBLIOTHEQUE est une application web permettant à chaque utilisateur
de créer et gérer sa propre bibliothèque personnelle en ligne.

## User Stories

| ID | User Story | Priorité |
|---|---|---|
| US01 | En tant que visiteur, je veux créer un compte afin d'avoir une bibliothèque personnelle | Haute |
| US02 | En tant qu'utilisateur, je veux me connecter afin d'accéder à ma bibliothèque | Haute |
| US03 | En tant qu'utilisateur, je veux me déconnecter afin de sécuriser ma session | Haute |
| US04 | En tant qu'utilisateur, je veux ajouter un livre à ma bibliothèque | Haute |
| US05 | En tant qu'utilisateur, je veux consulter la liste de mes livres | Haute |
| US06 | En tant qu'utilisateur, je veux consulter les détails d'un livre | Haute |
| US07 | En tant qu'utilisateur, je veux modifier un de mes livres | Haute |
| US08 | En tant qu'utilisateur, je veux supprimer un de mes livres | Haute |
| US09 | En tant qu'utilisateur, je veux rechercher un livre par titre ou auteur | Moyenne |
| US10 | En tant qu'utilisateur, je veux ajouter une couverture à un livre | Moyenne |

## Règles métier principales

- Une bibliothèque est privée.
- Chaque utilisateur voit uniquement ses propres livres.
- Un livre appartient obligatoirement à un utilisateur.
- Le titre et l'auteur d'un livre sont obligatoires.
- L'année, le genre, la description et la couverture sont facultatifs.
- Un utilisateur ne peut pas modifier ou supprimer le livre d'un autre utilisateur.
- Il n'y aura pas de rôle administrateur dans la V1.