# CashFlow Challenge
Um comerciante precisa controlar o seu fluxo de caixa diário com os lançamentos(débitos e créditos), também precisa de um relatório que disponibilize o saldo diário consolidado.

## 1. Requisitos Funcionais

- Registrar lançamentos de fluxo de caixa diário (créditos e débitos).

- Gerar e disponibilizar relatório com o saldo diário consolidado.

- Identificar e documentar domínios, subdomínios e capacidades associadas.

---

## 2. Requisitos Não Funcionais

- O serviço de controle de lançamento **não deve ficar indisponível** se o sistema de consolidado diário cair.

- O serviço de consolidado diário deve suportar **picos de 50 requisições por segundo**,  
    com **no máximo 5% de perda de requisições**, traduzidos em aproximadamente 3000 operações por minuto.

- **Escalabilidade**  
  - Solução deve ser escalável incluindo:
    - escalabilidade horizontal automática;  
    - balanceamento de carga;  
  - Arquiteturas desenhadas para serem escaláveis, reutilizáveis e flexíveis.

- **Resiliência**  
  - Projeto voltado à recuperação de falhas (redundância, failover, monitoramento proativo, self-healing).

- **Segurança**  
  - Proteção de dados e sistemas com:
    - autenticação;  
    - autorização;  
    - criptografia;  
    - mecanismos de proteção contra ataques.  

- **Disponibilidade e confiabilidade**  
  - Otimização para alta disponibilidade e confiabilidade.  
  - Definição de métricas e metas claras para NFRs (SLA/SLO).

- **Monitoramento e observabilidade (diferencial)**  
  - Definição de métricas, logs, traces e alertas para acompanhar saúde e desempenho dos serviços.

- **Manutenibilidade e flexibilidade**  
  - Soluções desenhadas para:
    - fácil evolução;  
    - reutilização;  
    - adaptação à estratégia de negócios e à arquitetura de referência.

- **Documentação como ativo de qualidade**  
  - Documentação completa das decisões e da arquitetura para facilitar entendimento, operação e manutenção.

---

## 3. Capacidades de Negócio

- **Controle do fluxo de caixa diário**  
  - Registrar e acompanhar entradas (créditos) e saídas (débitos) do caixa diariamente.

- **Consolidação diária de informações financeiras**  
  - Agregar e processar os lançamentos do dia para obter o saldo consolidado.

- **Geração de relatórios financeiros diários**  
  - Disponibilizar relatórios de saldo diário para acompanhamento do negócio.

- **Suporte à tomada de decisão financeira**  
  - Fornecer informações consolidadas e confiáveis para decisões rápidas sobre caixa, investimentos, pagamentos, etc.

- **Integração de processos e sistemas**  
  - Capacidade de integrar áreas, atividades, serviços e sistemas de forma coordenada para entrega de valor alinhada ao planejamento estratégico.

- **Evolução alinhada à estratégia de negócio**  
  - Capacidade de adaptar a solução e a arquitetura para suportar crescimento, novos produtos/serviços e mudanças estratégicas.

- **Reuso e flexibilidade de soluções**  
  - Capacidade organizacional de criar soluções reutilizáveis e flexíveis, reduzindo tempo e custo de novas iniciativas.

---

## 4. Arquitetura de Soluções

- [Blueprint](/doc/CashFlow_Challenge_Archi_v1-Blueprint.drawio.png)
- [Diagrama C4 Model - System Context](/doc/CashFlow_Challenge_Archi_v1-C4_SystemContext.drawio.png)
- [Diagrama C4 Model - Containeres](/doc/CashFlow_Challenge_Archi_v1-C4_Containeres.drawio.png)
- [Descritivo de Custos Google Cloud](/doc/Google%20Cloud%20Estimate%20Summary.pdf)
- [ADRs](/doc/ADR.md)
- [API Design](/src/openapi_3_cashflow.yml)

---
_Fim do documento._