# Cas de Tests — Banking API

## Description du projet

API REST bancaire Spring Boot exposant deux groupes d'endpoints :
- **Admin** (`/admin/accounts`) : gestion des comptes (création, désactivation, listage)
- **Client** (`/client/accounts`) : opérations bancaires (solde, crédit, débit)

---

## Tableau des cas de tests

| ID    | Description | Entrées (Input) | Résultat Attendu | Type |
|-------|-------------|-----------------|------------------|------|
| CT-01 | Créer un compte avec des données valides | `POST /admin/accounts` — `{ "fullname": "Jean Dupont", "email": "jean@email.com", "phone": "0600000001" }` | HTTP 201 — compte créé avec `balance = 0` et `isActive = true` | Fonctionnel — Nominal |
| CT-02 | Créer un compte avec un champ obligatoire manquant (`fullname` absent) | `POST /admin/accounts` — `{ "email": "jean@email.com", "phone": "0600000001" }` | HTTP 400 — message : "Le nom complet est obligatoire" | Fonctionnel — Erreur |
| CT-03 | Créer un compte avec un email déjà enregistré | `POST /admin/accounts` — email `jean@email.com` déjà existant en base | HTTP 409 — message d'erreur sur le champ email | Fonctionnel — Erreur |
| CT-04 | Désactiver un compte existant et actif | `DELETE /admin/accounts/{id}` — `id` valide | HTTP 200 — message : "Compte désactivé avec succès" ; `isActive` passe à `false` | Fonctionnel — Nominal |
| CT-05 | Désactiver un compte inexistant | `DELETE /admin/accounts/{id}` — `id` inexistant en base | HTTP 404 — message : compte non trouvé | Fonctionnel — Erreur |
| CT-06 | Consulter le solde d'un compte actif | `GET /client/accounts/{id}/balance` — `id` d'un compte actif | HTTP 200 — `{ "account_id": id, "balance": valeur }` | Fonctionnel — Nominal |
| CT-07 | Créditer un compte actif avec un montant valide | `POST /client/accounts/{id}/credit` — `id` valide, `{ "amount": 500.00 }` | HTTP 200 — transaction `SUCCESS`, solde augmenté de 500.00 | Fonctionnel — Nominal |
| CT-08 | Créditer un compte inactif | `POST /client/accounts/{id}/credit` — `id` d'un compte avec `isActive = false`, `{ "amount": 100.00 }` | HTTP 403 — message : compte inactif | Fonctionnel — Erreur |
| CT-09 | Débiter un compte avec un solde exactement égal au montant demandé | `POST /client/accounts/{id}/debit` — `id` valide, `{ "amount": X }` avec `balance == X` | HTTP 200 — transaction `SUCCESS`, `balance` passe à `0` | Fonctionnel — Limite |
| CT-10 | Débiter un compte avec un solde insuffisant | `POST /client/accounts/{id}/debit` — `id` valide, `{ "amount": X }` avec `balance < X` | HTTP 422 — transaction `FAILED` enregistrée, message : solde insuffisant | Fonctionnel — Erreur |
