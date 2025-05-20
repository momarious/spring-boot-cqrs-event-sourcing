# 🏦 Gestion de Prêts - CQRS/Event Sourcing

## 📌 Description

Application de gestion de prêts bancaires utilisant l’architecture **CQRS + Event Sourcing**, découpée en deux microservices :

- **🟢 Command Service** (Spring Boot 3.3.8 + Java 21 + GraalVM)
  - Création de demande de prêt
  - Mise à jour du statut d’un prêt

- **🔵 Query Service** (Spring Boot 3.3.8 + Java 21 + GraalVM)
  - Consultation des prêts

## 🗃️ Event Sourcing

### 📦 Event Versioning

Utilisation de **Protobuf** pour la sérialisation binaire des événements, avec support du versioning grâce à la compatibilité ascendante.


#### 🆚 Protobuf 
Format de sérialisation binaire pour les événements et les APIs. Offre :
- **Performances** : Taille réduite vs JSON
- **Schema evolution** : Compatibilité ascendante/descendante
- **Codegen** : Génération automatique de classes en Java

#### ✅ Exemple de message Protobuf
```proto
syntax = "proto3";

message PretDemandeEvent {
    string aggregateId = 1;
    string clientId = 2;
    double montant = 3;
    string dateDemande = 4;
    int32 dureeEnMois = 5;
    string description = 6;
    StatutPret statut = 7;
}
```

### 📸 Snapshotting
Pour éviter de rejouer l’intégralité des événements :

- Reconstitution périodique des agrégats via des snapshots persistés.
- Améliore la performance de redémarrage et de lecture.

### 🔁 Patterns distribués
- Inbox Pattern : garantit une exécution "exactly-once" des commandes.
- Outbox Pattern : fiabilité des événements publiés via une table intermédiaire.

## 🧰 Technologies

| 🧩 Composant                   | 📝 Description                                      |
|-------------------------------|----------------------------------------------------|
| **JOOQ**                      | Génération de code SQL type-safe                   |
| **Apache Kafka + Zookeeper** | Bus d’événements distribué                         |
| **Kafka Connect + Debezium** | Capture des changements (CDC) depuis PostgreSQL    |
| **PostgreSQL**               | Base de données principale                         |
| **Flyway**                   | Migrations SQL versionnées                         |
| **Apicurio (Schema Registry)** | Gestion centralisée des schémas Avro/Protobuf   |
| **CloudEvents**              | Format standardisé pour la structuration d’événements |


## Exécution

### 🔧 Local (Docker Compose)

```bash
# Lancer l'infrastructure en local
docker-compose up -d
```

### ☸️ Kubernetes

```bash
# Appliquer les manifestes Kubernetes 
kubectl apply -f deployment/

# Vérifier les pods
kubectl get pods

# Vérifier les services exposés
kubectl get svc
```

## ℹ️ Remarques

- ❗ **Tests d'intégration manquants** : Les tests d’intégration ne sont **pas encore implémentés**, ce qui est un **gros manque** dans une architecture **event-driven**. Ils sont essentiels pour valider le bon enchaînement des événements entre microservices.
  
- ⚠️ **Manifestes Kubernetes incomplets** :
  - Les manifestes actuels ne prennent **pas en compte l'Inbox/Outbox Pattern**.
  - Ils ont été conçus pour un mode direct **publish/listen avec Kafka** via des dépendances Spring, sans inclure les jobs associés à ces patterns.

- 🔁 **Query Service & Base de Données** :
  - Initialement, le `query-service` devait utiliser **MongoDB** pour une meilleure séparation entre lecture et écriture.
  - En raison de **problèmes de performances**, la **même service PostgreSQL** est temporairement utilisée côté lecture.
