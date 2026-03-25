# Decisões Arquiteturais do Projeto

Este documento consolida as principais Architectural Decision Records (ADRs) do projeto, com seus contextos, decisões, justificativas e trade-offs.

---

## ADR-001 — Uso de Google Cloud Spanner como Banco de Dados

**Contexto**  
Há necessidade de alta disponibilidade e consistência global para o banco de dados, com suporte a transações distribuídas e forte consistência temporal (especialmente relevante para cálculo de saldos diários).

**Decisão**  
Adotar **Google Cloud Spanner** como banco de dados principal do sistema.

**Justificativa**

- ✓ Fornece **ACID distribuído horizontalmente**, um diferencial relevante em comparação a soluções como Cassandra.  
- ✓ Oferece **SLA de 99,999%** em configuração multi-região de forma nativa.  
- ✓ Disponibiliza a **TrueTime API**, que garante consistência temporal, fundamental para cálculo confiável de **saldo diário** e outras operações sensíveis ao tempo.  
- ✓ Suporta **SQL padrão ANSI** com **transações distribuídas**, facilitando adoção pela equipe e integrando-se bem com padrões existentes de desenvolvimento.  
- ✓ Minimiza o trade-off clássico do **Teorema CAP**, oferecendo comportamento **CP** (consistência e tolerância a partições) com **disponibilidade próxima a AP**.  
- ✓ Apresenta **custo inicial reduzido** em relação a PostgreSQL gerenciado para a **carga prevista** nesse projeto.  
- ✓ Permite **escalabilidade automática** via **Processing Units**, simplificando o ajuste de capacidade para variações de carga.
- ✓ Menor unidade de capacidade, 100 processing units, proverá **2.250 reads/s** e **350 writes/s** de capacidade de processamento

**Trade-offs**

- ✗ Introduz **vendor lock-in** com a **Google Cloud**, tornando a portabilidade para outros provedores mais complexa.

---

## ADR-002 — Uso de Google Cloud Pub/Sub como Mensageria

**Contexto**  
É necessário realizar o desacoplamento entre os componentes de **Lançamentos** e **Consolidado**, garantindo comunicação assíncrona, resiliência e escalabilidade.

**Decisão**  
Utilizar **Google Cloud Pub/Sub** como serviço de mensageria entre os domínios de Lançamentos e Consolidado.

**Justificativa**

- ✓ Serviço **gerenciado nativamente no GCP**, resultando em **zero ops** para a equipe em termos de manutenção da infraestrutura de mensageria.  
- ✓ **Escala automaticamente** para picos de carga (por exemplo, de 50 req/s ou mais) sem necessidade de configuração manual de capacidade.  
- ✓ Oferece **garantia de entrega at-least-once**, com mecanismos de **deduplicação**, aumentando a confiabilidade das mensagens processadas.  
- ✓ Possui **Dead Letter Topics nativos**, facilitando **auditoria de falhas**, reprocessamento e análise de erros.  
- ✓ Permite tanto **push** quanto **pull subscriptions**, suportando diferentes padrões de consumo e topologias de integração.

**Trade-offs**

- ✗ Proporciona **menor controle sobre particionamento** quando comparado a soluções como **Apache Kafka**, o que pode limitar algumas otimizações avançadas de throughput.  
- ✗ A **ordenação de mensagens** é garantida apenas quando se utiliza **message ordering key**, exigindo cuidado no design de tópicos e chaves.

---

## ADR-003 — Committed Use Discount (CUD) de 3 Anos para Spanner

**Contexto**  
Existe a necessidade de **reduzir os custos operacionais** do uso do Cloud Spanner, considerando que a carga de trabalho é previsível e contínua.

**Decisão**  
Firmar um **Committed Use Discount (CUD) de 3 anos** especificamente para o uso do **Cloud Spanner**.

**Justificativa**

- ✓ Possibilidade de obter **desconto de até 65%** sobre o preço on-demand do Spanner, otimizando significativamente o custo recorrente.  
- ✓ O workload é **previsível** (controle de fluxo de caixa e operações contínuas), tornando viável o compromisso de uso a longo prazo.  
- ✓ Há **ROI positivo já no 1º mês de uso**, dado o volume esperado de consumo.  
- ✓ O **commitment** é aplicado no **nível de projeto GCP**, simplificando o gerenciamento do benefício em múltiplos recursos dentro do mesmo projeto (quando aplicável).

**Trade-offs**

- ✗ **Redução de flexibilidade**: não é possível reduzir o compromisso antes do término do contrato de 3 anos.  
- ✗ Envolve **pagamento antecipado** ou **comprometimento de faturamento mensal mínimo**, o que requer planejamento financeiro.  
- ✗ Depende de **aprovação financeira/contratual** com a Google Cloud, o que pode alongar o processo de contratação.

---

## ADR-004 — Uso de GKE Standard com CUD de 3 Anos

**Contexto**  
Há necessidade de **controle fino sobre os nodes do cluster Kubernetes**, inclusive tipos de máquina e configurações de disco, o que impacta performance e custo.

**Decisão**  
Adotar **GKE Standard** no lugar do **GKE Autopilot**, utilizando **Committed Use Discount de 3 anos** para os nodes do cluster.

**Justificativa**

- ✓ A opção de Autopilot seria baseada em um **machine type específico (c2-standard-4)**, mas com menos flexibilidade de configuração detalhada.  
- ✓ O **GKE Standard** permite configurar explicitamente o **boot disk SSD**, o que é importante para desempenho de IO.  
- ✓ O modelo de **Resource-based CUD** exige **node pools com machine type fixo**, o que se alinha ao uso de **c2-standard-4** para otimizar custo.  
- ✓ **c2-standard-4** é otimizada para workloads **compute-intensive**, adequando-se às características do processamento do projeto.  
- ✓ Uso de **Container-Optimized OS (cos_containerd)**, sem custo de licença, reduz custos e melhora segurança e desempenho para containers.
- ✓ Versatilidade no controle de replicaSet e autoscalling das aplicações com base em métricas colhidas diretamente destas

**Trade-offs**

- ✗ A **gestão dos node pools** (escalonamento, updates, tuning de recursos) passa a ser responsabilidade da equipe, aumentando a complexidade operacional.  
- ✗ O **Autopilot** seria mais simples do ponto de vista operacional, abrindo mão de parte dessa simplicidade pelo controle fino.  
- ✗ O **CUD de 3 anos** exige **compromisso com o machine type** definido, reduzindo a flexibilidade para trocar de tipo de máquina durante a vigência.

---

_Fim do documento._