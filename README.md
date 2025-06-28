# Memorix API

<details>
<summary><strong>🇧🇷 Português</strong></summary>

## 📚 Sobre o Projeto

**Memorix** é uma API REST robusta e completa para um sistema de aprendizado com flashcards que implementa algoritmos de repetição espaçada. O sistema permite que usuários criem, gerenciem e estudem flashcards de forma eficiente, otimizando o processo de memorização através de técnicas cientificamente comprovadas.

### ✨ Principais Funcionalidades

- **🔐 Autenticação Completa**: Login local e OAuth2 com Google
- **👤 Gerenciamento de Usuários**: Registro, verificação de email, redefinição de senha
- **📦 Baralhos (Decks)**: Criação e organização de conjuntos de flashcards
- **🃏 Flashcards**: Sistema completo de CRUD para cartões de estudo
- **🧠 Repetição Espaçada**: Algoritmo inteligente baseado no método SuperMemo
- **📊 Estatísticas**: Análise detalhada do progresso de aprendizado
- **🖼️ Upload de Imagens**: Suporte para imagens nos flashcards
- **🔔 Notificações**: Sistema de email para ações importantes
- **📈 Monitoramento**: Métricas com Prometheus e dashboards Grafana
- **🔒 Segurança**: JWT tokens, validação de dados, rate limiting

### 🏗️ Arquitetura e Tecnologias

#### Stack Principal
- **Java 21** com Virtual Threads para alta performance
- **Spring Boot 3.5** - Framework principal
- **Spring Security** - Autenticação e autorização
- **PostgreSQL** - Banco de dados principal
- **MongoDB** - Armazenamento de flashcards e reviews
- **Redis** - Cache e sessões
- **RabbitMQ** - Mensageria assíncrona

#### Ferramentas de Desenvolvimento
- **Docker & Docker Compose** - Containerização
- **Flyway** - Migrations de banco de dados
- **MapStruct** - Mapeamento de objetos
- **OpenAPI/Swagger** - Documentação da API
- **TestContainers** - Testes de integração
- **MinIO** - Armazenamento de arquivos

#### Observabilidade
- **Prometheus** - Coleta de métricas
- **Grafana** - Visualização de dados
- **Spring Actuator** - Health checks e métricas

### 🚀 Como Executar

#### Pré-requisitos
- Java 21+
- Docker e Docker Compose
- Maven 3.9+

#### Executando com Docker (Recomendado)

1. **Clone o repositório:**
```bash
git clone <url-do-repositorio>
cd Memorix-API
```

2. **Inicie os serviços de infraestrutura:**
```bash
docker-compose -f docker-compose.dev.yml up -d
```

3. **Configure as variáveis de ambiente:**
```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
# Edite o arquivo com suas configurações
```

4. **Execute a aplicação:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Executando Localmente

1. **Instale as dependências:**
```bash
./mvnw clean install
```

2. **Configure o banco de dados PostgreSQL e outros serviços**

3. **Execute:**
```bash
./mvnw spring-boot:run
```

### 📁 Estrutura do Projeto

```
src/main/java/andrehsvictor/memorix/
├── auth/           # Autenticação e autorização
├── card/           # Gerenciamento de flashcards
├── deck/           # Gerenciamento de baralhos
├── user/           # Gerenciamento de usuários
├── review/         # Sistema de revisão
├── image/          # Upload e gerenciamento de imagens
├── common/         # Utilitários e configurações
└── MemorixApplication.java
```

### 🔌 API Endpoints

#### Autenticação
- `POST /api/v1/auth/token` - Login com credenciais
- `POST /api/v1/auth/google` - Login com Google OAuth
- `POST /api/v1/auth/refresh` - Renovar token
- `POST /api/v1/auth/revoke` - Revogar token

#### Usuários
- `POST /api/v1/users` - Registro de usuário
- `GET /api/v1/users/me` - Perfil do usuário
- `PUT /api/v1/users/me` - Atualizar perfil
- `POST /api/v1/users/verify-email` - Verificar email

#### Baralhos
- `GET /api/v1/decks` - Listar baralhos
- `POST /api/v1/decks` - Criar baralho
- `GET /api/v1/decks/{id}` - Obter baralho
- `PUT /api/v1/decks/{id}` - Atualizar baralho
- `DELETE /api/v1/decks/{id}` - Deletar baralho

#### Flashcards
- `GET /api/v1/cards` - Listar todos os cartões
- `GET /api/v1/decks/{deckId}/cards` - Cartões por baralho
- `POST /api/v1/decks/{deckId}/cards` - Criar cartão
- `PUT /api/v1/cards/{id}` - Atualizar cartão
- `DELETE /api/v1/cards/{id}` - Deletar cartão
- `GET /api/v1/cards/stats` - Estatísticas dos cartões

### 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Testes de integração
./mvnw test -Dspring.profiles.active=test

# Coverage report
./mvnw jacoco:report
```

### 📊 Monitoramento

- **Aplicação**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

### 👤 Autor

**Andre Victor**
- Email: andrehsvictor@gmail.com
- GitHub: [@andrehsvictor](https://github.com/andrehsvictor)

---

</details>

<details>
<summary><strong>🇺🇸 English</strong></summary>

## 📚 About the Project

**Memorix** is a robust and comprehensive REST API for a flashcard learning system that implements spaced repetition algorithms. The system allows users to create, manage, and study flashcards efficiently, optimizing the memorization process through scientifically proven techniques.

### ✨ Key Features

- **🔐 Complete Authentication**: Local login and OAuth2 with Google
- **👤 User Management**: Registration, email verification, password reset
- **📦 Decks**: Creation and organization of flashcard sets
- **🃏 Flashcards**: Complete CRUD system for study cards
- **🧠 Spaced Repetition**: Intelligent algorithm based on SuperMemo method
- **📊 Statistics**: Detailed learning progress analysis
- **🖼️ Image Upload**: Support for images in flashcards
- **🔔 Notifications**: Email system for important actions
- **📈 Monitoring**: Prometheus metrics and Grafana dashboards
- **🔒 Security**: JWT tokens, data validation, rate limiting

### 🏗️ Architecture and Technologies

#### Main Stack
- **Java 21** with Virtual Threads for high performance
- **Spring Boot 3.5** - Main framework
- **Spring Security** - Authentication and authorization
- **PostgreSQL** - Main database
- **MongoDB** - Flashcards and reviews storage
- **Redis** - Cache and sessions
- **RabbitMQ** - Asynchronous messaging

#### Development Tools
- **Docker & Docker Compose** - Containerization
- **Flyway** - Database migrations
- **MapStruct** - Object mapping
- **OpenAPI/Swagger** - API documentation
- **TestContainers** - Integration testing
- **MinIO** - File storage

#### Observability
- **Prometheus** - Metrics collection
- **Grafana** - Data visualization
- **Spring Actuator** - Health checks and metrics

### 🚀 How to Run

#### Prerequisites
- Java 21+
- Docker and Docker Compose
- Maven 3.9+

#### Running with Docker (Recommended)

1. **Clone the repository:**
```bash
git clone <repository-url>
cd Memorix-API
```

2. **Start infrastructure services:**
```bash
docker-compose -f docker-compose.dev.yml up -d
```

3. **Configure environment variables:**
```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
# Edit the file with your configurations
```

4. **Run the application:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Running Locally

1. **Install dependencies:**
```bash
./mvnw clean install
```

2. **Configure PostgreSQL database and other services**

3. **Run:**
```bash
./mvnw spring-boot:run
```

### 📁 Project Structure

```
src/main/java/andrehsvictor/memorix/
├── auth/           # Authentication and authorization
├── card/           # Flashcard management
├── deck/           # Deck management
├── user/           # User management
├── review/         # Review system
├── image/          # Image upload and management
├── common/         # Utilities and configurations
└── MemorixApplication.java
```

### 🔌 API Endpoints

#### Authentication
- `POST /api/v1/auth/token` - Login with credentials
- `POST /api/v1/auth/google` - Login with Google OAuth
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/revoke` - Revoke token

#### Users
- `POST /api/v1/users` - User registration
- `GET /api/v1/users/me` - User profile
- `PUT /api/v1/users/me` - Update profile
- `POST /api/v1/users/verify-email` - Verify email

#### Decks
- `GET /api/v1/decks` - List decks
- `POST /api/v1/decks` - Create deck
- `GET /api/v1/decks/{id}` - Get deck
- `PUT /api/v1/decks/{id}` - Update deck
- `DELETE /api/v1/decks/{id}` - Delete deck

#### Flashcards
- `GET /api/v1/cards` - List all cards
- `GET /api/v1/decks/{deckId}/cards` - Cards by deck
- `POST /api/v1/decks/{deckId}/cards` - Create card
- `PUT /api/v1/cards/{id}` - Update card
- `DELETE /api/v1/cards/{id}` - Delete card
- `GET /api/v1/cards/stats` - Card statistics

### 🧪 Testing

```bash
# Run all tests
./mvnw test

# Integration tests
./mvnw test -Dspring.profiles.active=test

# Coverage report
./mvnw jacoco:report
```

### 📊 Monitoring

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 📄 License

This project is under the MIT license. See the `LICENSE` file for more details.

### 👤 Author

**Andre Victor**
- Email: andrehsvictor@gmail.com
- GitHub: [@andrehsvictor](https://github.com/andrehsvictor)

---

</details>

## 🌟 Quick Links

- 📖 [API Documentation (Swagger)](http://localhost:8080/swagger-ui.html)
- 📊 [Monitoring Dashboard](http://localhost:3000)
- 🐳 [Docker Hub](https://hub.docker.com)
- 🚀 [Live Demo](#) <!-- Add your demo URL here -->

## 📱 Tech Stack Summary

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)

## 🎯 Project Status

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-85%25-green)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)