# Wallet Core (Educational Project)

A backend implementation for a secure password manager, built with **Java 21** and **SQLite**.

## 🎓 Project Goal
This project is designed for **educational purposes** to demonstrate core Software Engineering principles and architectural patterns in a real-world context.

Key architectural concepts explored:
* **Design Patterns:** Implementation of *Facade* (`WalletManager`), *Strategy* (`IEntryRepository`, `ICriptor`), and *Singleton* (`DbConnector`).
* **Composition over Inheritance:** Moving away from rigid class hierarchies to a flexible `Entry` + `Field` component model.
* **Security:** Manual implementation of AES-256 GCM encryption and key derivation.

## 🤖 AI Collaboration & Testing
The comprehensive **JUnit 5** test suite was generated in collaboration with an **AI Assistant**.
You will notice a distinct, descriptive style in the test comments (e.g., *"Scenario 1: Happy Path"*, *"Scenario: Bad Case"*). These are intentionally verbose to serve as a tutorial on how to write effective unit and integration tests covering edge cases and failure scenarios.

## 🛠 Tech Stack
* **Language:** Java 21
* **Build Tool:** Maven
* **Persistence:** SQLite (JDBC)
* **Testing:** JUnit 5
* **Logging:** SLF4J / Logback
