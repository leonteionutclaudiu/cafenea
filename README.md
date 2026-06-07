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

Tabela `comanda_produse` este tabela de legatura generata automat de JPA pentru relatia `@ManyToMany` dintre `Comanda` si `Produs`.

## Diagrama ER

```mermaid
erDiagram
    UTILIZATOR ||--o| PROFIL_UTILIZATOR : are
    UTILIZATOR ||--o{ COMANDA : inregistreaza
    CATEGORIE_PRODUS ||--o{ PRODUS : contine
    PRODUS }o--o{ INGREDIENT : foloseste
    PRODUS }o--o{ COMANDA : este_comandat
    MASA ||--o{ COMANDA : gazduieste

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

    COMANDA_PRODUSE {
        long comanda_id FK
        long produs_id FK
    }

    PRODUS_INGREDIENTE {
        long produs_id FK
        long ingredient_id FK
    }
```

## Relatii JPA bifate

- `@OneToOne`: `Utilizator` - `ProfilUtilizator`
- `@OneToMany` / `@ManyToOne`: `CategorieProdus` - `Produs`
- `@OneToMany` / `@ManyToOne`: `Utilizator` - `Comanda`
- `@OneToMany` / `@ManyToOne`: `Masa` - `Comanda`
- `@ManyToMany`: `Comanda` - `Produs`
- `@ManyToMany`: `Produs` - `Ingredient`

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
