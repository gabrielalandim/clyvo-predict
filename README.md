# 🔮 Clyvo Predict

**API de Saúde Animal — Challenge FIAP 2026 | Sprint 4**

![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-Database-red?style=for-the-badge&logo=oracle&logoColor=white)
![JWT](https://img.shields.io/badge/Spring_Security-JWT_RSA-blue?style=for-the-badge)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-red?style=for-the-badge)

O **Clyvo Predict** é uma solução desenvolvida para o **Challenge FIAP 2026**, em parceria com a startup Clyvo. A plataforma permite acompanhar a saúde de pets por meio de histórico clínico, eventos de saúde e um **Health Score dinâmico**, com integração a um serviço de inteligência artificial.

---

## 🎬 Vídeo de apresentação — Sprint 4

**Assista à demonstração do projeto:**

▶️ https://youtu.be/WREPnKKYxP

---

## 🌐 Aplicação online

| Recurso | Link |
|---|---|
| API Java | https://clyvo-api-kr5f.onrender.com/api |
| Swagger UI | https://clyvo-api-kr5f.onrender.com/swagger-ui/index.html |
| Serviço de IA | https://clyvo-ia.onrender.com |
| Aplicativo mobile | https://github.com/gabrielalandim/clyvo-predict-mobile-sprint3 |
| Vídeo da Sprint 4 | https://youtu.be/WREPnKKYxP |

> A API está hospedada no Render. A primeira requisição pode demorar alguns instantes caso o serviço esteja inativo.

---

## 🎯 Sobre o projeto

O Clyvo Predict é uma API RESTful desenvolvida com Java e Spring Boot para gerenciar tutores, veterinários, pets e eventos de saúde.

O sistema calcula automaticamente uma pontuação de saúde para cada pet, permitindo acompanhar sua evolução ao longo do tempo.

A solução integra:

- Backend Java com Spring Boot
- Banco de dados Oracle
- Autenticação e autorização com Spring Security e JWT RSA
- Controle de versões do banco com Flyway
- Aplicativo mobile React Native e Expo
- Integração com serviço de IA
- Deploy online no Render
- Documentação Swagger/OpenAPI

---

## ✨ Funcionalidades

- 🐾 **Gestão de Pets:** cadastro, consulta, atualização e exclusão
- 👤 **Gestão de Tutores:** cadastro, login e gerenciamento de conta
- 🩺 **Gestão de Veterinários:** cadastro, login e gerenciamento de conta
- 📋 **Eventos de Saúde:** registro, consulta, atualização e exclusão
- 💚 **Health Score:** pontuação dinâmica de 0 a 100
- 🔍 **Busca de Pets:** pesquisa por nome e listagem paginada
- 🔐 **Autenticação JWT:** tokens assinados com RSA
- 🛡️ **Controle de acesso:** permissões específicas para tutores e veterinários
- 🚫 **Revogação por exclusão:** tokens de contas excluídas deixam de ser aceitos
- 🤖 **Integração com IA:** análise de imagens no cadastro de pets
- 📖 **Swagger:** documentação interativa da API
- 🗄️ **Flyway:** gerenciamento de migrations do Oracle
- ⚡ **Spring Cache:** otimização de consultas

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Versão / finalidade |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.14 |
| Spring Web | API REST |
| Spring Data JPA | Persistência |
| Spring Security | Autenticação e autorização |
| OAuth2 Resource Server | Validação JWT |
| RSA | Assinatura dos tokens |
| BCrypt | Criptografia de senhas |
| Oracle Database | Banco relacional |
| Flyway | Migrations |
| Spring Cache | Cache de consultas |
| Bean Validation | Validação de dados |
| Springdoc OpenAPI | 2.8.16 |
| Maven | Build e dependências |
| React Native / Expo | Aplicativo mobile |
| Render | Hospedagem |

---

## 🏗️ Arquitetura


Diagrama de classe:
<img width="680" height="860" alt="diagrama_de_classes_clyvo" src="https://github.com/user-attachments/assets/18ee5b9f-30a0-4eb6-a846-b19dfdf6077c" />

A aplicação utiliza arquitetura em camadas:

```text
Aplicativo Mobile
       |
       v
API REST - Spring Boot
       |
Spring Security / JWT
       |
   Controllers
       |
    Services
       |
  Repositories
       |
  Oracle Database
```

A API Java também se comunica com um serviço externo de inteligência artificial.

### Camadas

- **Controller:** recebe as requisições HTTP
- **Service:** implementa regras de negócio
- **Repository:** realiza operações no banco
- **Model:** representa entidades JPA
- **DTO:** define os dados de entrada e saída
- **Security:** gerencia autenticação e autorização
- **Exception:** trata erros da aplicação

---

## 🔐 Segurança e autenticação

O sistema utiliza Spring Security com autenticação stateless baseada em JWT.

Os tokens são assinados e validados com chaves RSA.

As senhas são armazenadas com hash BCrypt.

### Perfis

**TUTOR**
- Gerencia sua própria conta
- Consulta seus pets
- Acessa informações autorizadas dos seus animais

**VETERINARIO**
- Gerencia sua própria conta
- Consulta a listagem geral de pets
- Visualiza os tutores relacionados aos pets, conforme as permissões da API

As permissões são validadas pelo backend.

### Autenticação

```http
Authorization: Bearer SEU_TOKEN_JWT
```

A API também valida a existência da conta associada ao token. Quando um tutor ou veterinário é excluído, seu token deixa de ser aceito.

---

## 🗂️ Modelo de domínio

```text
Tutor (1) -------- (N) Pet (1) -------- (N) EventoSaude
                       |
                  Health Score
```

### Entidades principais

**Tutor**
- ID
- Nome
- E-mail
- Telefone
- Senha

**Veterinário**
- ID
- Dados cadastrais
- Credenciais de autenticação

**Pet**
- ID
- Nome
- Espécie
- Raça
- Idade
- Peso
- Health Score
- Tutor responsável

**EventoSaude**
- ID
- Tipo do evento
- Descrição
- Data
- Pet relacionado

---

## 💚 Health Score

O Health Score é uma pontuação de 0 a 100 que representa indicadores da condição de saúde do pet.

Cada evento registrado pode alterar automaticamente a pontuação.

| Evento | Impacto |
|---|---:|
| VACINA | +10 |
| CONSULTA_ROTINA | +5 |
| EXAME | +5 |
| DOENCA_LEVE | -15 |
| CIRURGIA | -30 |
| DOENCA_GRAVE | -40 |
| ACIDENTE | -50 |
| RELATO_SINTOMA | -5 |
| ALTERACAO_COMPORTAMENTO | -5 |

O pet inicia com **100 pontos**.

O valor permanece sempre entre **0 e 100**.

---

## 🌐 Endpoints da API

**URL base:**

```text
https://clyvo-api-kr5f.onrender.com/api
```

### Tutores

| Método | Endpoint | Descrição |
|---|---|---|
| POST | /tutores | Cadastrar tutor |
| POST | /tutores/login | Login do tutor |
| GET | /tutores | Listar tutores |
| GET | /tutores/{id} | Consultar tutor |
| PUT | /tutores/{id} | Atualizar tutor |
| DELETE | /tutores/{id} | Excluir tutor |

### Veterinários

| Método | Endpoint | Descrição |
|---|---|---|
| POST | /veterinarios | Cadastrar veterinário |
| POST | /veterinarios/login | Login do veterinário |
| GET | /veterinarios | Listar veterinários |
| GET | /veterinarios/{id} | Consultar veterinário |
| PUT | /veterinarios/{id} | Atualizar veterinário |
| DELETE | /veterinarios/{id} | Excluir veterinário |

### Pets

| Método | Endpoint | Descrição |
|---|---|---|
| POST | /pets | Cadastrar pet |
| GET | /pets | Listar pets |
| GET | /pets/{id} | Consultar pet |
| PUT | /pets/{id} | Atualizar pet |
| DELETE | /pets/{id} | Excluir pet |
| POST | /pets/analisar-cadastro | Analisar imagem do pet |

### Eventos de saúde

| Método | Endpoint | Descrição |
|---|---|---|
| POST | /eventos | Registrar evento |
| GET | /eventos/pet/{petId} | Listar eventos do pet |
| GET | /eventos/{id} | Consultar evento |
| PUT | /eventos/{id} | Atualizar evento |
| DELETE | /eventos/{id} | Excluir evento |

**Observação:** a listagem de eventos é realizada por pet, utilizando `/eventos/pet/{petId}`.

---

## ⚙️ Pré-requisitos

- JDK 21
- Git
- Maven ou Maven Wrapper
- Acesso ao Oracle Database
- Credenciais de banco configuradas
- Chaves RSA para autenticação

---

## 🚀 Instalação e execução

### 1. Clonar o repositório

```bash
git clone https://github.com/gabrielalandim/clyvo-predict.git
cd clyvo-predict
```

### 2. Configurar o banco de dados

Defina as variáveis de ambiente:

```env
DB_USERNAME=SEU_USUARIO_ORACLE
DB_PASSWORD=SUA_SENHA_ORACLE
```

As credenciais devem ser configuradas no ambiente de execução e nunca publicadas no GitHub.

Para limitar o número de conexões simultâneas ao Oracle compartilhado:

```env
SPRING_DATASOURCE_HIKARI_MAXIMUMPOOLSIZE=3
```

### 3. Configurar JWT RSA

A aplicação utiliza arquivos de chaves RSA.

```text
src/main/resources/keys/private_key.pem
src/main/resources/keys/public_key.pem
```

A chave privada não deve ser publicada no repositório.

### 4. Executar a aplicação

Linux/macOS:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

API local:

```text
http://localhost:8080/api
```

Swagger local:

```text
http://localhost:8080/swagger-ui.html
```

---

## 🗄️ Flyway

O projeto utiliza Flyway para versionamento e gerenciamento das alterações do banco Oracle.

As migrations ficam em:

```text
src/main/resources/db/migration
```

O histórico de execução é armazenado na tabela:

```sql
FLYWAY_SCHEMA_HISTORY
```

A configuração JPA utiliza validação do esquema, evitando recriações automáticas das tabelas.

---

## 📱 Aplicativo mobile

Repositório:

https://github.com/gabrielalandim/clyvo-predict-mobile-sprint3

O aplicativo foi desenvolvido com React Native e Expo e oferece autenticação separada para tutores e veterinários.

### Instalação

```bash
git clone https://github.com/gabrielalandim/clyvo-predict-mobile-sprint3.git
cd clyvo-predict-mobile-sprint3
npm install
```

Na raiz do projeto mobile, crie o arquivo `.env.local`:

```env
EXPO_PUBLIC_API_URL=https://clyvo-api-kr5f.onrender.com/api
```

Inicie o Expo:

```bash
npx expo start -c
```

Abra o aplicativo no Expo Go ou em um emulador compatível.

---

## 🤖 Integração com inteligência artificial

O backend Java se comunica com um serviço externo de IA para auxiliar na análise de imagens relacionadas ao cadastro de pets.

**Serviço hospedado:**

https://clyvo-ia.onrender.com

A configuração da URL da IA é realizada no backend.

O aplicativo mobile envia as informações para a API Java, responsável por integrar os serviços.

---

## 📖 Documentação Swagger

**Swagger online:**

https://clyvo-api-kr5f.onrender.com/swagger-ui/index.html

**Swagger local:**

http://localhost:8080/swagger-ui.html

O Swagger permite consultar os endpoints, os modelos de requisição e resposta e testar as rotas da API.

---

## 🧩 Design Patterns

### Arquitetura em camadas

Separação entre controllers, services, repositories, entidades e DTOs.

### Strategy — TipoEvento

Os tipos de evento encapsulam impactos utilizados no cálculo do Health Score.

### Cache Pattern

O Spring Cache permite otimizar consultas de listagem, com invalidação nas operações de alteração dos dados.

### Repository Pattern

O Spring Data JPA abstrai as operações de persistência no Oracle Database.

---

## 🧪 Testes

Execute os testes automatizados com:

```bash
./mvnw test
```

Os relatórios são gerados em:

```text
target/surefire-reports/
```

Também foram realizados testes manuais no Postman para:

- Cadastro e autenticação de tutores
- Cadastro e autenticação de veterinários
- CRUD de pets
- CRUD de eventos de saúde
- Validação de permissões
- Rejeição de requisições não autorizadas
- Validação de tokens de contas excluídas

---

## 👥 Equipe

Desenvolvido por estudantes da **FIAP — Challenge 2026**.

| Nome | RM |
|---|---|
| Maria Gabriela Landim Severo | RM565146 |
| Eduarda Weiss Ventura | RM564434 |
| Samara Porto Souza | RM559072 |
| Lucas Nunes Soares | RM566503 |
| Camilly Vitoria Pereira Maciel | RM566520 |

---

## 🎥 Apresentação final

**Vídeo demonstrativo — Sprint 4:**

https://youtu.be/WREPnKKYxP

---

**Desenvolvido com ☕ e ❤️ pela equipe Clyvo Predict — FIAP 2026.**
