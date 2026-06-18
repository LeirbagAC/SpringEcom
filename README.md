# Spring Boot E-Commerce API (SpringEcom)

Este é um projeto de uma API RESTful desenvolvida com **Java 21** e **Spring Boot**, que simula o ecossistema básico do backend de um e-commerce. A aplicação gerencia um catálogo completo de produtos com suporte a upload de imagens e processamento de pedidos com cálculo dinâmico de valores e controle de inventário.

---

## 🚀 Funcionalidades Principais

### 📦 Catálogo de Produtos & Upload de Arquivos
* **Gerenciamento de Produtos (CRUD):** Cadastro, edição, listagem e exclusão de produtos contendo nome, marca, descrição, preço, categoria e quantidade em estoque.
* **Upload de Imagens integrado:** Suporte ao recebimento de mídias via requisições `Multipart/Form-Data`, armazenando o nome, tipo e os bytes binários da imagem diretamente no banco de dados através da anotação `@Lob`.
* **Busca Textual Inteligente:** Endpoint de pesquisa que realiza uma busca customizada (utilizando cláusulas `LIKE` e `LOWER` em JPQL) para filtrar produtos por correspondências parciais no nome, descrição, marca ou categoria, de forma totalmente *case-insensitive*.

### 🛒 Fluxo de Checkout e Pedidos (Orders)
* **Fechamento de Pedido:** Processa listas de itens de compra vinculando múltiplos produtos em uma única transação.
* **Baixa Automatizada de Estoque:** Ao criar um pedido, o sistema valida a existência do produto, calcula o valor total (`quantidade * preço`) e subtrai a quantidade comprada do estoque atual de forma automática.
* **Identificadores Únicos Universais (UUID):** Geração segura e dinâmica de códigos alfanuméricos externos para identificação dos pedidos comerciais (ex: `ORD-A1B2C3D4`), isolando os IDs sequenciais nativos do banco de dados.

---

## 🛠️ Tecnologias e Ferramentas Utilizadas

* **Linguagem:** Java 21
* **Framework Principal:** Spring Boot (v4.0.3)
* **Persistência de Dados:** Spring Data JPA / Hibernate
* **Banco de Dados:** MySQL
* **Mapeamento e Utilitários:** Project Lombok & Java Records (DTOs)
* **Gerenciador de Dependências:** Maven

---

## 🧠 Padrões de Projeto e Decisões de Arquitetura

1. **Uso de Java Records para DTOs:** Para transferir dados entre as camadas da API sem expor as entidades diretas do banco de dados, foram implementados **Records** (`OrderRequest`, `OrderResponse`, etc.). Por serem imutáveis e concisos por natureza, reduzem o acoplamento e eliminam códigos repetitivos (*boilerplate*).
2. **Relacionamentos Complexos Baseados em Performance:** Mapeamento bidirecional e unidirecional utilizando JPA:
   * Um Pedido (`Order`) possui uma lista de Itens (`OrderItem`) com propagação de ciclo de vida configurada via `cascade = CascadeType.ALL`.
   * Carregamento sob demanda (`FetchType.LAZY`) configurado nas relações para prevenir problemas de performance e evitar o disparo de consultas desnecessárias ao banco de dados.
3. **Consistência de Dados com `@Transactional`:** O método de busca geral de ordens utiliza transações em modo somente leitura (`@Transactional(readOnly = true)`), otimizando a velocidade de resposta do Hibernate ao desativar o gerenciamento de modificações em memória.
4. **CORS Habilitado:** Configuração do mecanismo de Cross-Origin Resource Sharing habilitada nos controllers apontando para o servidor local do ecossistema frontend (`http://localhost:5173/`).

### ⚠️ Lição Aprendida: O Perigo de `@Data` com `@Entity`
Uma excelente decisão documentada no código (Classe Product em model) foi o entendimento do impacto negativo do uso indiscriminado da anotação `@Data` do Lombok em classes anotadas com `@Entity`. Como o `@Data` gera implementações automáticas de `equals()`, `hashCode()` e `toString()`, ele tenta varrer recursivamente todas as propriedades da classe. Em relacionamentos bidirecionais ou preguiçosos (*Lazy*), isso acarreta em erros graves de **StackOverflow** (loops infinitos) ou na inicialização forçada de proxies, degradando a performance. *(Nota: Erro cometido durante o projeto, mas que serviu de lição e mantido de forma consciente no escopo deste laboratório simples, mas mapeado como ponto de melhoria para projetos futuros).*

---

## 📋 Endpoints da API (Prefixo: `/api`)

### 📦 Produtos
| Método | Endpoint | Parâmetros / Body | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Nenhum | Retorna todos os produtos cadastrados |
| `GET` | `/api/product/{id}` | `id` (Path) | Busca os detalhes de um produto específico |
| `GET` | `/api/product/{id}/image` | `id` (Path) | Retorna exclusivamente os bytes binários da imagem |
| `GET` | `/api/products/search` | `keyword` (Query) | Filtra produtos por palavra-chave (*case-insensitive*) |
| `POST` | `/api/product` | `Multipart/Form-Data` | Cria um produto enviando dados JSON + arquivo de imagem |
| `PUT` | `/api/product/{id}` | `Multipart/Form-Data` | Atualiza os dados e a imagem de um produto |
| `DELETE`| `/api/product/{id}` | `id` (Path) | Remove um produto do catálogo |

### 🛒 Pedidos (Orders)
| Método | Endpoint | Request Body | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders/place` | `OrderRequest` (JSON) | Cria e finaliza um pedido, dando baixa no estoque |
| `GET` | `/api/orders` | Nenhum | Lista o histórico detalhado de todos os pedidos efetuados |

---

## 📦 Como Executar o Projeto

### 1. Configuração do Banco de Dados
Crie um schema no MySQL com o nome `spring_ecom`. No arquivo `src/main/resources/application.properties`, configure as credenciais da sua instância de banco local:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring_ecom
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
