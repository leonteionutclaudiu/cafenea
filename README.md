# CafeApp - Sistem Management Cafenea

Aplicatie web Spring Boot pentru administrarea unei cafenele: produse, categorii, ingrediente, mese, comenzi si utilizatori. Proiectul foloseste Spring MVC, Thymeleaf, Spring Data JPA, Spring Security, PostgreSQL pentru dezvoltare si H2 pentru teste.

## Functionalitati principale

- autentificare cu roluri `ADMIN` si `USER`
- administrare produse, categorii, ingrediente, mese si comenzi
- profil utilizator si schimbare parola
- validare server-side cu Bean Validation si mesaje afisate in formulare
- paginare si sortare pentru produse, ingrediente, comenzi si utilizatori
- logging cu fisiere separate pentru erori
- configurare multi-environment: `dev` si `test`

## Cont demo

La pornirea aplicatiei se creeaza automat un utilizator administrator:

- username: `manager`
- parola: `manager123`
- rol: `ADMIN`

## Setup local

1. Creati baza de date PostgreSQL:

```sql
CREATE DATABASE cafenea_db;
```

2. Verificati configurarea din `src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cafenea_db
    username: postgres
    password: root
```

3. Porniti aplicatia:

```bash
./mvnw spring-boot:run
```

Pe Windows:

```bat
mvnw.cmd spring-boot:run
```

Aplicatia este disponibila la:

```text
http://localhost:8080
```

## Rulare teste

```bash
./mvnw test
```

Profilul `test` foloseste H2 in-memory, configurat in `src/main/resources/application-test.yml`.

## Model de date

Entitati principale:

- `Utilizator`
- `ProfilUtilizator`
- `CategorieProdus`
- `Produs`
- `Ingredient`
- `Masa`
- `Comanda`
- `DetaliiComanda`

`DetaliiComanda` este o entitate interna pentru detalierea liniilor de comanda si nu este expusa separat in UI prin CRUD propriu.

## Diagrama ER

```mermaid
erDiagram
    UTILIZATOR ||--o| PROFIL_UTILIZATOR : are
    UTILIZATOR ||--o{ COMANDA : inregistreaza
    CATEGORIE_PRODUS ||--o{ PRODUS : contine
    PRODUS }o--o{ COMANDA : este_comandat
    MASA ||--o{ COMANDA : gazduieste
    COMANDA ||--o{ DETALII_COMANDA : are
    PRODUS ||--o{ DETALII_COMANDA : apare_in

    UTILIZATOR {
        long id PK
        string username
        string password
        string rol
    }

    PROFIL_UTILIZATOR {
        long id PK
        string numeComplet
        string adresa
        string telefon
    }

    CATEGORIE_PRODUS {
        long id PK
        string denumire
    }

    PRODUS {
        long id PK
        string nume
        double pret
        long categorie_id FK
    }

    INGREDIENT {
        long id PK
        string numeIngredient
        int cantitateStoc
    }

    MASA {
        long id PK
        int numarMasa
        string status
    }

    COMANDA {
        long id PK
        datetime dataComanda
        double totalPlata
        string status
        long utilizator_id FK
        long masa_id FK
    }

    DETALII_COMANDA {
        long id PK
        long comanda_id FK
        long produs_id FK
        int cantitate
        double pretSalvat
    }
```

## Relatii JPA bifate

- `@OneToOne`: `Utilizator` - `ProfilUtilizator`
- `@OneToMany` / `@ManyToOne`: `CategorieProdus` - `Produs`
- `@OneToMany` / `@ManyToOne`: `Utilizator` - `Comanda`
- `@OneToMany` / `@ManyToOne`: `Masa` - `Comanda`
- `@ManyToMany`: `Comanda` - `Produs`

## Cerinte obligatorii

| Cerinta | Implementare |
| --- | --- |
| Model de date | 8 entitati, relatii `OneToOne`, `OneToMany`, `ManyToOne`, `ManyToMany`, diagrama ER in README |
| CRUD complet | CRUD pentru produse, categorii, ingrediente, mese, comenzi si utilizatori/profil |
| Multi-environment | `application-dev.yml` PostgreSQL, `application-test.yml` H2 |
| Testing | teste unitare pentru service-uri si teste de integrare cu H2 |
| Views si validare | Thymeleaf, formulare CRUD, Bean Validation, mesaje user-friendly |
| Logging | SLF4J + Logback, fisiere separate pentru erori |
| Paginare si sortare | produse, ingrediente, comenzi, utilizatori |
| Spring Security | login custom, logout, BCrypt, roluri, protectie endpoint-uri, remember-me, CSRF activ |

## Arhitectura

Aplicatia este organizata pe straturi:

- `model`: entitati JPA si validari Bean Validation
- `repository`: repository-uri Spring Data JPA
- `service`: logica de business
- `controller`: rute MVC si integrare cu view-urile
- `templates`: pagini Thymeleaf
- `config`: configurare security si initializare date demo

## Deployment

Aplicatia poate fi rulata local cu PostgreSQL sau impachetata ca JAR:

```bash
./mvnw clean package
java -jar target/cafenea-0.0.1-SNAPSHOT.jar
```

Pentru deployment public, configurati variabilele de mediu pentru conexiunea PostgreSQL si actualizati `application-dev.yml` sau un profil dedicat.
