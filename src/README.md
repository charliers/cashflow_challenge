#  CashFlow Services
Estas aplicações representam à solução apresentada para o desafio **CashFlow Challenge** conforme o desenho de arquitetura abaixo, porém alguns débitos técnicos serão criados:

- **Testes unitários**
- **Scripts Terraform** para criação do ambiente Google Cloud
- **Shell scripts** para provisionamento automatizado do ambiente local (emuladores Spanner e PubSub, criação do tópico e monitoramento de mensagens)
- **Idempotência** nas chamadas de serviços transacionais
- **Multi-tenant** (para permitir o reuso como SaaS, melhorando a rentabilidade através do rateio dos custos operacionais)
- **CQRS** projeto foi criado com base neste pattern, porém para facilitar o desenvolvimento, endpoits de consulta ainda necessitam ser movidos do projeto **command** para projeto **query**

Estes débitos serão sanados mesmo após o final do desafio para fins didáticos.

---
# Configuração e Setup

## Visão geral

Esta aplicação é um monorepo Java 21 utilizando Spring Boot 3 e Spring Cloud 2025.0.1, construída com Maven em modo multi‑módulo.  
Há um `pom.xml` raiz que concentra as configurações e dependências comuns, e cada serviço é um módulo Maven que herda essas configurações via `parent`. A arquitetura segue CQRS:

- `shared-library`: artefatos compartilhados (utilitários, domains, DTOs)
- `command-cashflow-service`: comandos de criação de lançamentos (crédito, débito, reembolso)
- `query-cashflow-service`: consultas de lançamentos e saldos consolidados
- `consolidator-cashflow-service`: consolidação assíncrona de saldos diários

Além disso, os serviços utilizam emuladores do Google Cloud Spanner e Pub/Sub em ambiente local para desenvolvimento.

---

## Pré‑requisitos

Certifique-se de ter instalado:

- **Java 21 JDK**
- **Maven 3.9+**
- **Docker** (recomendado para subir os emuladores do Google Cloud)
- Opcionalmente, **Docker Compose** para orquestrar todos os emuladores e serviços juntos

---

## Estrutura de diretórios (monorepo Maven)

Estrutura simplificada do repositório:
```text
cashflow-monorepo/
  pom.xml                 # POM pai (parent) com dependências e plugins comuns
  shared-library/
    pom.xml
  command-cashflow-service/
    pom.xml
  query-cashflow-service/
    pom.xml
  consolidator-cashflow-service/
    pom.xml
```
No `pom.xml` raiz:

- `packaging` do projeto pai: `pom`
- declaração dos módulos via `<modules>`
- gestão centralizada de:
    - versão do Java (21)
    - versão do Spring Boot (3.x)
    - versão do Spring Cloud (2025.0.1)
    - plugins de build (por exemplo, `spring-boot-maven-plugin`, plugins de teste/cobertura)
    - dependências comuns (logging, observabilidade, libs de teste, etc.)

Exemplo conceitual (trecho):
```xml
4<project>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.seuorg.cashflow</groupId>
    <artifactId>cashflow-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.x.x</version>
        <relativePath/>
    </parent>

    <modules>
        <module>shared-library</module>
        <module>command-cashflow-service</module>
        <module>query-cashflow-service</module>
        <module>consolidator-cashflow-service</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.0.1</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- Outras BOMs se necessário -->
        </dependencies>
    </dependencyManagement>

    <!-- dependências comuns e plugins -->
</project>
```

Cada submódulo declara esse POM como `parent`:

```xml
<parent>
    <groupId>com.ciandt.challenge</groupId>
    <artifactId>cashflow-service</artifactId>
    <version>1.0.0</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

## Instalação das dependências (monorepo Maven)

Com o monorepo, o ciclo de build é centralizado no `pom.xml` raiz. A partir da raiz do repositório:

### 1. Baixar e instalar todas as dependências (sem rodar testes)
```bash
mvn clean install -DskipTestsEste 
```

Comando:
- resolve e faz download de todas as dependências compartilhadas e dos módulos
- compila todos os módulos
- instala os artefatos no repositório Maven local

### 2. Rodar os testes de todos os módulos
```bash
mvn clean test
```
### 3. Build completo com testes
```bash
mvn clean verify
```
### 4. Build e empacotamento de um módulo específico

Por exemplo, apenas o `command-cashflow-service`:
```bash
mvn -pl command-cashflow-service -am clean package
```
- `-pl` limita o build a um módulo
- `-am` garante que dependências internas (por exemplo, `shared-library`) também sejam construídas quando necessário

---

## Relação entre os módulos (CQRS)

### `shared-library`

- Exporta DTOs de comandos, eventos, queries e objetos de domínio comuns.
- É consumida por todos os serviços, declarada como dependência Maven:
```xml
<dependency>
    <groupId>com.ciandt.challenge</groupId>
    <artifactId>shared-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### `command-cashflow-service`

- Exposto como API de comandos (por exemplo, REST) para criação de lançamentos de crédito, débito e reembolso.
- Publica mensagens em tópicos do Pub/Sub (Google Cloud Pub/Sub emulator em dev) para notificar outros serviços (especialmente o consolidator).

### `query-cashflow-service`

- API dedicada a consultas de lançamentos e saldos consolidados.
- Não realiza mutações; consome dados consolidados produzidos pelo consolidator (direto no Spanner emulado).

### `consolidator-cashflow-service`

- Consumidor assíncrono de mensagens (Pub/Sub emulator) com lógica de consolidação diária.
- Persiste saldos consolidados no banco emulado do Spanner.

---

## Emuladores do Google Cloud (Spanner e Pub/Sub)

Para desenvolvimento local, os serviços utilizam emuladores do Google Cloud ao invés de recursos reais na nuvem. Isso evita custos, acelera o ciclo de feedback e permite trabalho offline.

A forma mais prática de subir os emuladores é via **Docker** manualmente ou de forma automatizada conforme descrito abaixo:
- https://docs.cloud.google.com/pubsub/docs/emulator?hl=pt-br
- https://docs.cloud.google.com/spanner/docs/emulator?hl=pt-br

---

### 1. Pub/Sub Emulator

#### 1.1. Subindo o emulador (Docker)
```bash
docker run --rm -it \
-p 8085:8085 \
gcr.io/google.com/cloudsdktool/cloud-sdk:latest \
gcloud beta emulators pubsub start --host-port=0.0.0.0:8085O
```
container ficará com o emulador rodando e logando no console.

#### 1.2. Configuração de ambiente para os serviços

Defina a variável de ambiente apontando para o host e porta do emulador:
```bash
export PUBSUBEMULATORHOST=localhost:8085
```
Em `application-local.yml` dos serviços que usam Pub/Sub (`command-cashflow-service` e `consolidator-cashflow-service`):
```yaml
spring:
  cloud:
    gcp:
      pubsub:
        emulator-host: ${PUBSUBEMULATORHOST:localhost:8085}
```
> Ajuste o prefixo de propriedade conforme a starter/biblioteca utilizada.

#### 1.3. Criação de tópicos e subscriptions

Após o emulador estar rodando, em outro terminal:
```bash
export PUBSUBEMULATORHOST=localhost:8085
gcloud pubsub topics create cashflow-transactions
gcloud pubsub subscriptions create cashflow-transactions-sub --topic=cashflow-transactions
```
Crie os tópicos e subscriptions necessários para o fluxo de comandos e eventos do domínio de cashflow.

---

### 2. Cloud Spanner Emulator

#### 2.1. Subindo o emulador (Docker)
```bash
docker run --rm -it \
-p 9010:9010 \
gcr.io/cloud-spanner-emulator/emulator
```

#### 2.2. Configuração de ambiente para os serviços
```bash
export SPANNEREMULATORHOST=localhost:9010
export GOOGLECLOUDPROJECT=local-project
export SPANNERINSTANCE=local-instance
export SPANNERDATABASE=cashflow
```

Em `application-local.yml` dos serviços que usam Spanner:
```yaml
spring:
  cloud:
    gcp:
      spanner:
        project-id: ${GOOGLECLOUDPROJECT:local-project}
        instance-id: ${SPANNERINSTANCE:local-instance}
        database: ${SPANNERDATABASE:cashflow}
        emulator-host: ${SPANNEREMULATORHOST:localhost:9010}
```

#### 2.3. Criação de instância e database no emulador

Com o emulador rodando:
```bash
export SPANNEREMULATORHOST=localhost:9010
export GOOGLECLOUDPROJECT=local-project
```
Configurar projeto local no gcloud

```bash
gcloud config set project ${GOOGLECLOUDPROJECT}Criar instância

gcloud spanner instances create local-instance \
--config=emulator-config \
--description="Local Spanner Instance" \
--nodes=1Criar database

gcloud spanner databases create cashflow \
--instance=local-instance
```

Depois, aplique o schema da aplicação (DDL) com o comando `gcloud spanner databases ddl update` com arquivo `schema.sql`

---

## Perfis de execução dos serviços

Os serviços utilizam o perfil `local` (ou `dev`) para apontar para os emuladores e desativar integrações com o GCP real.

### Executando com Maven
```bash
cd command-cashflow-service
mvn spring-boot:run -Dspring-boot.run.profiles=localquery-cashflow-service

cd query-cashflow-service
mvn spring-boot:run -Dspring-boot.run.profiles=localconsolidator-cashflow-service

cd consolidator-cashflow-service
mvn spring-boot:run -Dspring-boot.run.profiles=local### Executando via JAR
```

```bash
java -jar target/command-cashflow-service-.jar --spring.profiles.active=local
java -jar target/query-cashflow-service-.jar --spring.profiles.active=local
java -jar target/consolidator-cashflow-service-*.jar --spring.profiles.active=local---
```

## Fluxo de preparação de ambiente (resumo)

1. Clonar o repositório e entrar na pasta raiz do monorepo.
2. Garantir que Java 21, Maven e Docker estejam instalados.
3. Build inicial do monorepo:
```bash
   mvn clean install -DskipTests
```
4. Subir emulador **Pub/Sub** com Docker e exportar `PUBSUB_EMULATOR_HOST`.
   
5. Criar tópicos e subscriptions necessários no emulador.
6. Subir emulador **Spanner** com Docker e exportar `SPANNER_EMULATOR_HOST`, `GOOGLE_CLOUD_PROJECT`, `SPANNER_INSTANCE`, `SPANNER_DATABASE`.
7. Criar instância e database no Spanner emulator e aplicar DDL.
8. Executar cada serviço com o perfil `**local**`, garantindo que as configurações de Spanner e Pub/Sub apontem para os emuladores.

---
_Fim do documento._