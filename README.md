# OpenStates App

Aplicação fullstack para consulta de representantes políticos dos Estados Unidos, com dados consumidos da [OpenStates API](https://v3.openstates.org/docs).

---

## Tecnologias

### Backend
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 3.3.5 |
| Maven Wrapper | 3.x |

### Frontend
| Tecnologia | Versão |
|---|---|
| React | 19.2.6 |
| TypeScript | 6.0.2 |
| Vite | 8.0.12 |
| Material UI (MUI) | 9.0.1 |

### Infraestrutura
| Tecnologia | Versão |
|---|---|
| PostgreSQL | 16 |
| Docker | 24+ |
| Docker Compose | 2.x |
| Nginx | Alpine |

---

## Obter a API Key do OpenStates

Antes de rodar o projeto, você precisará de um token de acesso à API:

1. Crie uma conta em [https://open.pluralpolicy.com/accounts/profile/](https://open.pluralpolicy.com/accounts/profile/)
2. Gere seu **API Token** na página de perfil
3. Guarde o token — ele será usado na variável `OPENSTATES_API_KEY`

---

## Variáveis de ambiente

Copie o arquivo de exemplo na raiz do projeto:

```bash
cp .env.example .env
```

Edite o `.env` com seus valores:

| Variável | Descrição | Valor padrão |
|---|---|---|
| `DB_NAME` | Nome do banco de dados | `openstates` |
| `DB_USERNAME` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | `postgres` |
| `DB_PORT` | Porta exposta do banco | `5432` |
| `OPENSTATES_API_KEY` | **Obrigatória.** API Key do OpenStates | — |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas pelo CORS | `http://localhost:5173,http://localhost` |
| `SYNC_CRON` | Expressão cron para sincronização automática | `0 0 2 * * *` (diariamente às 2h) |
| `VITE_API_BASE_URL` | URL base da API consumida pelo frontend | `http://localhost:8080/api` |

---

## Cenário 1 — Execução via Docker Compose (recomendado)

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução

### Passo a passo

**1. Clone o repositório**
```bash
git clone https://github.com/thiagoDimon/openstates-app.git
cd openstates-app
```

**2. Suba a stack completa**
```bash
docker compose up --build
```

Certifique-se de que o `.env` está configurado com o `OPENSTATES_API_KEY` antes de executar.

O Docker irá subir os três serviços em ordem:
1. Banco de dados PostgreSQL (aguarda health check)
2. Backend Spring Boot (aguarda health check)
3. Frontend React servido pelo Nginx

> A primeira execução pode levar alguns minutos devido ao build das imagens e à inicialização do Spring Boot.

**4. Acesse a aplicação**

| Serviço | URL |
|---|---|
| **Frontend** | http://localhost |
| **Backend (API)** | http://localhost:8080/api |
| **Health Check** | http://localhost:8080/actuator/health |

**5. Parar a stack**
```bash
docker compose down
```

Para remover também os dados persistidos do banco:
```bash
docker compose down -v
```

---

## Cenário 2 — Execução local

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução (necessário para subir o banco de dados)
- **Java 21** instalado com a variável `JAVA_HOME` configurada
  ```bash
  # Linux/macOS
  java -version   # deve exibir: openjdk 21...
  echo $JAVA_HOME # deve exibir o caminho do JDK

  # Windows (CMD)
  java -version
  echo %JAVA_HOME%

  # Windows (PowerShell)
  java -version
  $env:JAVA_HOME
  ```
- **Node.js 18+** com npm
  ```bash
  node -v   # deve exibir: v18.x.x ou superior
  ```

### Passo a passo

**1. Clone o repositório**
```bash
git clone https://github.com/thiagoDimon/openstates-app.git
cd openstates-app
```

**2. Suba apenas o banco de dados via Docker**
```bash
docker compose up db -d
```

---

#### Backend

**4. Acesse o diretório do backend**
```bash
cd backend
```

**5. Execute o backend com o Maven Wrapper**

Linux/macOS:
```bash
./mvnw spring-boot:run
```

Windows:
```cmd
mvnw.cmd spring-boot:run
```

O backend lê as variáveis diretamente do arquivo `.env` na raiz do projeto. Nenhuma configuração adicional é necessária.

O backend estará disponível em `http://localhost:8080`.

---

#### Frontend

**6. Em outro terminal, acesse o diretório do frontend**
```bash
cd frontend
```

**7. Instale as dependências**
```bash
npm install
```

**8. Execute o frontend**
```bash
npm run dev
```

O frontend estará disponível em `http://localhost:5173`.

---

## Endpoints da API

| Método | Endpoint | Descrição | Parâmetros |
|---|---|---|---|
| `GET` | `/api/politicians` | Lista políticos com paginação e filtros | `state` (sigla, ex: `CA`), `party` (ex: `Democratic`), `page` (default: `0`), `size` (default: `10`) |
| `POST` | `/api/politicians/sync/{stateCode}` | Sincroniza manualmente a próxima página de um estado | `stateCode` (path, ex: `ca`) |

Exemplo:
```
GET http://localhost:8080/api/politicians?state=CA&party=Democratic&page=0&size=10
```

---

## Documentação da API (Swagger)

Com a aplicação rodando, acesse a documentação interativa dos endpoints:

| Recurso | URL |
|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html |
| **OpenAPI JSON** | http://localhost:8080/api/v3/api-docs |

---

## Testes

Os testes unitários cobrem controller, service, repository e mapper. Para executá-los:

```bash
cd backend
```

Linux/macOS:
```bash
./mvnw test
```

Windows:
```cmd
mvnw.cmd test
```

Os testes utilizam banco H2 em memória — nenhuma conexão com PostgreSQL ou variável de ambiente é necessária.
