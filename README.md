# SpringEcom

API REST de e-commerce desenvolvida com Spring Boot. O projeto oferece autenticação de usuários, catálogo de produtos, imagens, carrinho de compras, criação de pedidos com controle de estoque e operações administrativas.

## Funcionalidades

- Cadastro e login de usuários e vendedores.
- Autenticação stateless com tokens JWT.
- Consulta, pesquisa paginada e gerenciamento de produtos.
- Associação de produtos a vendedores e consulta dos produtos do vendedor autenticado.
- Upload e consulta de imagens de produtos, armazenadas no banco de dados.
- Carrinho por usuário: adicionar itens, remover um item, consultar e limpar.
- Finalização de pedidos a partir do carrinho, com cálculo dos itens e baixa de estoque.
- Consulta paginada dos pedidos do usuário autenticado.
- Perfil do usuário: consulta, atualização, alteração de senha e exclusão.
- Administração de usuários: criação, pesquisa, alteração de role e exclusão, além da consulta de pedidos por usuário.
- Documentação OpenAPI/Swagger e endpoint de health check.

## Tecnologias utilizadas

- Java 21.
- Spring Boot 4.0.3.
- Spring MVC, Spring Data JPA e Hibernate.
- Spring Security com JWT (`jjwt`).
- MySQL 8.4.
- Flyway para versionamento do schema.
- Bean Validation.
- MapStruct e Lombok.
- Springdoc OpenAPI/Swagger UI.
- Spring Boot Actuator.
- Maven e Docker/Docker Compose.

## Arquitetura do projeto

O código principal está em `src/main/java/com/gabriel/SpringEcom` e segue uma organização em camadas:

- `controller`: endpoints HTTP agrupados por recurso.
- `service`: regras de negócio de autenticação, usuários, produtos, carrinho e pedidos.
- `model`: entidades JPA e enumerações de domínio.
- `dto`: objetos de entrada e saída da API.
- `repo`: repositórios Spring Data JPA.
- `security`: filtro JWT, geração/validação de tokens, carregamento de usuários e configuração de autorização.
- `mappers`: conversão entre entidades e DTOs usando MapStruct.
- `config`: configurações de CORS e OpenAPI.
- `exceptions`: exceções de domínio e tratamento global de erros.

As migrações ficam em `src/main/resources/db/migration`. Os exemplos de chamadas HTTP ficam em `api-tests`.

## Pré-requisitos

Para execução local:

- Java 21.
- MySQL compatível com o conector MySQL utilizado pelo projeto.
- Maven 3.9 ou o Maven Wrapper (`./mvnw`).

Para execução conteinerizada:

- Docker com suporte a BuildKit (o `Dockerfile` usa `RUN --mount=type=cache`).
- Docker Compose.

## Configuração e execução

### Execução local

1. Crie um banco MySQL e um usuário com permissão de leitura e escrita no schema.
2. Copie `.env.example` para `.env` apenas como referência de configuração e defina os valores no ambiente ou no arquivo de configuração local. O arquivo `.env` não deve ser versionado.
3. Configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `JWT_SECRET`. O `JWT_SECRET` deve ser uma chave Base64 forte, compatível com HS256.
4. Inicie a aplicação com o perfil de desenvolvimento:

   ```bash
   SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
   ```

   O perfil `dev` usa `spring.jpa.hibernate.ddl-auto=update` e habilita o log SQL. Sem informar o perfil, são usados os valores padrão de `application.properties`.

5. A API ficará disponível em `http://localhost:8080`.

Para gerar o artefato e executá-lo:

```bash
./mvnw clean package
java -jar target/SpringEcom.jar
```

### Docker Compose

O Compose inicia a aplicação e um MySQL 8.4, cria o volume persistente `mysql_data` e aguarda o health check do banco antes de iniciar a API.

1. Crie o arquivo de ambiente:

   ```bash
   cp .env.example .env
   ```

2. Ajuste no `.env` os valores de `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD` e `JWT_SECRET`.
3. Construa a imagem e inicie os serviços:

   ```bash
   docker compose up --build
   ```

   O `compose.override.yaml`, carregado automaticamente, seleciona o perfil `dev` e publica o MySQL em `127.0.0.1:3306`.

Para executar somente a configuração base, sem o override:

```bash
docker compose -f docker-compose.yml up --build
```

Nesse caso, o perfil padrão é `prod`, que valida o schema existente (`ddl-auto=validate`) e desabilita Swagger UI e os documentos OpenAPI. A API continua disponível em `http://localhost:8080`.

## Configuração de ambiente

As propriedades da aplicação usam variáveis de ambiente com alguns valores padrão. Não use credenciais reais em arquivos versionados.

| Variável | Obrigatória | Finalidade | Exemplo seguro |
|---|---:|---|---|
| `SPRING_PROFILES_ACTIVE` | Não | Seleciona o perfil Spring (`dev` ou `prod`). | `dev` |
| `SPRING_DATASOURCE_URL` | Não | URL JDBC do MySQL. | `jdbc:mysql://localhost:3306/SpringEcom` |
| `SPRING_DATASOURCE_USERNAME` | Não | Usuário do banco. | `springecom_app` |
| `SPRING_DATASOURCE_PASSWORD` | Sim para ambiente sem valor padrão | Senha do banco. | `defina-fora-do-repositorio` |
| `JWT_SECRET` | Sim | Chave Base64 usada para assinar tokens JWT. | `gere-uma-chave-base64-com-32-bytes-ou-mais` |
| `JWT_EXPIRATION` | Não | Validade do JWT em milissegundos. | `86400000` |
| `MYSQL_DATABASE` | Compose | Nome do banco criado pelo container MySQL. | `SpringEcom` |
| `MYSQL_USER` | Compose | Usuário criado pelo container MySQL. | `springecom_app` |
| `MYSQL_PASSWORD` | Compose | Senha do usuário do MySQL. | `defina-fora-do-repositorio` |
| `MYSQL_ROOT_PASSWORD` | Compose | Senha do usuário root do MySQL. | `defina-fora-do-repositorio` |

O Compose monta a URL da aplicação usando o host `mysql`, nome do serviço da rede interna. Para execução local, o host normalmente é `localhost`.

## Endpoints da API

A API não usa o prefixo `/api`. A menos que indicado como público, as rotas exigem:

```http
Authorization: Bearer <token-jwt>
```

Paginação usa os parâmetros Spring Data `page`, `size` e `sort`. Os valores padrão definidos pelos controllers são `size=20` e ordenação por `name` para produtos ou `orderDate` para pedidos; o tamanho máximo global é 50.

### Autenticação

| Método | Rota | Acesso | Finalidade |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Cadastra um usuário comum. |
| `POST` | `/auth/seller/register` | Público | Cadastra um vendedor. |
| `POST` | `/auth/login` | Público | Autentica por e-mail e senha e retorna um JWT. |

Exemplo de login:

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "usuario@exemplo.com",
  "password": "sua-senha"
}
```

### Produtos e imagens

As consultas de produtos são públicas. Criação, atualização, exclusão e upload de imagens exigem `SELLER` (ou `ADMIN`, pela hierarquia de roles).

| Método | Rota | Finalidade |
|---|---|---|
| `GET` | `/products` | Lista produtos paginados. |
| `GET` | `/products/me` | Lista os produtos do vendedor autenticado. |
| `GET` | `/products/search?keyword=...` | Pesquisa por palavra-chave. |
| `GET` | `/products/{productId}` | Consulta um produto. |
| `POST` | `/products` | Cria um produto. |
| `PUT` | `/products/{productId}` | Atualiza um produto. |
| `DELETE` | `/products/{productId}` | Desativa um produto. |
| `POST` | `/products/{productId}/images` | Envia uma imagem como `multipart/form-data`, no campo `file`. |
| `GET` | `/products/images/{imageId}` | Consulta uma imagem. |
| `GET` | `/products/{productId}/images` | Lista as imagens de um produto. |

Exemplo de criação de produto:

```http
POST http://localhost:8080/products
Authorization: Bearer <token-do-vendedor>
Content-Type: application/json

{
  "name": "Produto exemplo",
  "description": "Descrição",
  "brand": "Marca",
  "price": 199.90,
  "category": "Categoria",
  "stockQuantity": 10
}
```

### Carrinho e pedidos

As rotas de carrinho exigem autenticação. Pela configuração de segurança, o acesso é destinado à role `USER` e também é alcançado pelas roles superiores na hierarquia.

| Método | Rota | Finalidade |
|---|---|---|
| `GET` | `/cart` | Consulta o carrinho do usuário. |
| `POST` | `/cart/add` | Adiciona ou atualiza um item (`productId`, `quantity`). |
| `DELETE` | `/cart/item/{productId}` | Remove um produto do carrinho. |
| `DELETE` | `/cart/items` | Limpa o carrinho. |
| `POST` | `/orders/place` | Finaliza o carrinho e cria um pedido. |
| `GET` | `/orders/all` | Lista os pedidos do usuário autenticado. |

Exemplo de item de carrinho:

```json
{
  "productId": 1,
  "quantity": 2
}
```

### Perfil e administração

| Método | Rota | Acesso | Finalidade |
|---|---|---|---|
| `GET` | `/user/me` | Autenticado | Consulta o próprio perfil. |
| `PUT` | `/user/me` | Autenticado | Atualiza perfil e retorna um novo JWT. |
| `PATCH` | `/user/me/password` | Autenticado | Altera a senha. |
| `DELETE` | `/user/me` | Autenticado | Exclui/desativa o próprio usuário. |
| `PATCH` | `/admin/users/{externalId}/role` | `ADMIN` | Altera a role de um usuário. |
| `GET` | `/admin/users/search?keyword=...` | `ADMIN` | Pesquisa usuários. |
| `GET` | `/admin/users/orders/{externalId}` | `ADMIN` | Lista pedidos de um usuário. |
| `POST` | `/admin/users` | `ADMIN` | Cria um usuário. |
| `DELETE` | `/admin/users/{externalId}` | `ADMIN` | Exclui/desativa um usuário. |

### Operação

| Método | Rota | Acesso | Finalidade |
|---|---|---|---|
| `GET` | `/actuator/health` | Público | Verifica a saúde da aplicação. |
| `GET` | `/swagger-ui.html` | Público em `dev` | Abre a interface Swagger. |
| `GET` | `/v3/api-docs` | Público em `dev` | Retorna a especificação OpenAPI. |

## Autenticação e autorização

O login valida as credenciais com BCrypt e retorna um token JWT. O `JwtAuthenticationFilter` valida o token enviado no header `Authorization` em cada requisição protegida; a aplicação não mantém sessão HTTP (`STATELESS`).

As roles disponíveis são `USER`, `SELLER` e `ADMIN`. A hierarquia configurada é `ADMIN > SELLER > USER`: administradores herdam permissões de vendedores e usuários, e vendedores herdam as permissões de usuários. O registro público comum cria `USER`, enquanto `/auth/seller/register` cria `SELLER`; operações sob `/admin/**` exigem `ADMIN`.

## Banco de dados

O projeto usa MySQL com Spring Data JPA/Hibernate. As entidades principais são:

- `User`: conta, e-mail, role, status e relacionamentos com produtos, carrinho e pedidos.
- `Product`: catálogo, preço, categoria, disponibilidade, estoque e vendedor.
- `ProductImage`: metadados e conteúdo binário das imagens.
- `Cart` e `CartItem`: carrinho e seus itens.
- `Order` e `OrderItem`: pedidos, itens, quantidades e totais.

O schema inicial está em `src/main/resources/db/migration/V1__baseline.sql` e é gerenciado pelo Flyway. No perfil `dev`, o Hibernate usa `ddl-auto=update`; no perfil `prod`, usa `ddl-auto=validate`, exigindo que o schema esteja compatível com as migrações.

## Testes

Existe um teste automatizado de contexto em `src/test/java/com/gabriel/SpringEcom/SpringEcomApplicationTests.java`. Execute-o com:

```bash
./mvnw test
```

As chamadas manuais estão organizadas em `api-tests/*.http`. Elas podem ser executadas pelo HTTP Client do IntelliJ IDEA ou de outra IDE compatível. O arquivo `api-tests/http-client.env.json` define o ambiente `dev`; substitua os valores de autenticação por dados locais e não inclua credenciais reais no repositório. O script de login salva o JWT nas requisições subsequentes.
