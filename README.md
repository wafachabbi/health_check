# HPE Health Check — Plateforme de Supervision des Serveurs

> Projet de fin de formation — Supervision proactive des serveurs HPE ProLiant
> Développé par **Wafa Chabbi** — 2026

## Présentation

HPE Health Check est une solution complète de supervision et de gestion de parc serveurs HPE ProLiant. Elle centralise les KPI (CPU, RAM, disque, réseau), détecte proactivement les anomalies, génère des alertes automatiques et produit des rapports de disponibilité.

## Stack Technique

| Composant | Technologie | Version |
|---|---|---|
| Backend | Spring Boot | 3.2 |
| Langage | Java | 17 |
| Base de données | MySQL | 8.0 |
| Métriques | Prometheus | latest |
| Dashboards | Grafana | latest |
| Alertes | Alertmanager | latest |
| Collecte système | Node Exporter | latest |
| Collecte réseau | Blackbox Exporter | latest |
| Emails dev | MailHog | latest |
| Conteneurisation | Docker Compose | 1.29 |

## Fonctionnalités

- Inventaire serveurs : modèle, firmware, localisation, contact référent
- Import CSV, filtres par site / baie / environnement
- Contrôle de conformité firmware par rapport à une baseline
- Supervision temps réel CPU, RAM, disque, réseau
- Alertes automatiques avec statuts NOUVELLE / PRISE EN COMPTE / RÉSOLUE
- Notification email via MailHog
- Gestion utilisateurs avec rôles ADMIN / EXPLOITANT / LECTEUR
- Authentification JWT
- Journal d'audit complet
- Tendances 30 jours et indicateurs SLA
- Export CSV et rapports PDF automatiques (quotidien, hebdomadaire, mensuel)
- Purge automatique des données anciennes
- Sauvegarde MySQL planifiée

## Démarrage

    docker-compose -f healthcheck/docker-compose.yml up -d
    cd healthcheck-backend && mvn clean package -DskipTests
    docker restart backend

## Accès

| Interface | URL | Identifiants |
|---|---|---|
| Grafana | http://192.168.100.113:3000 | admin / admin123 |
| Prometheus | http://192.168.100.113:9090 | — |
| Alertmanager | http://192.168.100.113:9093 | — |
| MailHog | http://192.168.100.113:8025 | — |
| API REST | http://192.168.100.113:8080 | admin / password |

## Auteur

Wafa Chabbi — Projet de fin de formation — 2026
