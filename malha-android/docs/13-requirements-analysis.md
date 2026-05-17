# Malha — Resumo do Projeto

**Malha** é uma aplicação Android pensada como um sistema operativo digital para a prática de tricô e crochet. Mais do que uma simples ferramenta de tracking, posiciona-se como um espaço calmo e estruturado onde quem cria pode acompanhar todo o ciclo de vida de um projeto manual: inspiração, planeamento, escolha de materiais, execução, conclusão e reflexão. A app é acompanhada por **Aidi**, um assistente de inteligência artificial integrado que funciona como companheiro discreto de planeamento e orientação técnica.

O conceito nasce de um problema real e amplamente reconhecido na comunidade de artesãos têxteis: a acumulação de fios, ferramentas e padrões cresce mais depressa do que a capacidade de terminar projetos. Malha responde a este desequilíbrio com uma filosofia clara — **recompensar a criação, não o consumo**. O utilizador é incentivado a finalizar trabalhos pendentes, aproveitar o stock existente e planear novas aquisições de forma consciente, em vez de ser empurrado para ciclos de compra impulsiva ou gamificação agressiva.

A arquitetura da aplicação assenta numa abordagem **offline-first**, garantindo que as funcionalidades essenciais — gestão de projetos, navegação por padrões passo-a-passo, registo de materiais, comandos de voz e acompanhamento de progresso — funcionam sem dependência de internet. O tricô acontece em qualquer lugar, e a fiabilidade do core não pode estar sujeita a conectividade. Tecnicamente, a app é construída em **Kotlin com Jetpack Compose**, segue uma arquitetura **MVVM com Clean Architecture**, utiliza **Room** para persistência local e **WorkManager** para sincronização em segundo plano. O backend, separado em microserviços, é responsável pela camada comunitária, sincronização e integração com modelos de IA externos.

O coração técnico da Malha é o seu **sistema de padrões estruturados**. Em vez de armazenar instruções como texto plano, a app transforma cada padrão num grafo executável em JSON, com três camadas: o documento original imutável, os metadados estruturados (materiais, tensão, dificuldade, tamanhos) e o grafo de execução com secções, passos, repetições, lógica condicional e bibliotecas de pontos reutilizáveis. Esta abordagem permite tracking preciso de progresso, comandos por voz, validação matemática, adaptação a diferentes fios e agulhas, e interpretação fiável por IA. Cada elemento extraído tem ainda uma pontuação de confiança, garantindo que zonas ambíguas são sinalizadas para revisão humana.

O **Aidi** atua sobre esta estrutura como camada de inteligência opcional. As suas funções incluem explicar passos técnicos, sugerir padrões com base no stock disponível, estimar consumo de fio, adaptar padrões a diferentes tensões, converter PDFs em padrões estruturados e responder a perguntas contextuais durante a execução. A sua personalidade é calma, minimalista e não invasiva — um presente silencioso, não um chatbot ruidoso. A IA é tratada como assistente, nunca como autoridade: o utilizador e a comunidade validam sempre o resultado final.

A componente comunitária introduz um sistema de **validação social** onde padrões podem ser avaliados, comentados, bifurcados em variantes e marcados como "aprovados pela comunidade". Esta camada de confiança compensa as limitações conhecidas da IA em domínios matematicamente precisos como o tricô, onde um único ponto errado pode comprometer um projeto inteiro.

A monetização segue um modelo sustentável e não predatório: tier gratuito completo para o workflow essencial, subscrição premium para funcionalidades avançadas de IA e um marketplace futuro de padrões verificados. Uma economia gentil de moedas virtuais recompensa consistência, finalização e uso inteligente do stock, sem nunca restringir o acesso ao core nem cair em mecânicas de FOMO.

Visualmente, a identidade de Malha aposta em geometria suave, texturas inspiradas em tecidos, paleta de cremes quentes, verde sálvia e índigo abafado, e tipografia legível para diferentes idades e contextos de uso. A interface é desenhada para situações reais de crafting: mãos ocupadas, atenção dividida, interrupções frequentes — daí a importância dos botões grandes, do controlo por voz e da retoma instantânea do progresso.

Em síntese, Malha é um ecossistema artesanal de confiança, com inteligência artificial integrada de forma responsável, focado em transformar a intenção criativa dispersa em trabalho manual concluído. Não é mais uma app de produtividade nem um tracker genérico: é um lar digital onde cada ponto encontra o seu lugar.

# Análise de Requisitos — Malha

## Extração de necessidades do utilizador para equipa de Design e Desenvolvimento

---

## 1. Visão Geral do Comportamento do Utilizador

O texto descreve uma utilizadora intermédia que:

- Tricota e faz crochet ocasionalmente
- Acumula materiais por impulso
- Tem dificuldade em terminar projetos
- Valoriza partilha social moderada (amigas próximas)
- Usa a app em casa, à noite, em momentos de relaxamento
- Combina compras digitais e físicas
- Quer aprender técnicas novas de forma gradual

Estas características devem orientar todas as decisões de design.

---

## 2. Funcionalidades Identificadas (Mapeamento Completo)

---

### 2.1 Onboarding Inicial

**Necessidades:**

- Importar projetos anteriores ao primeiro uso
- Adicionar inventário existente de materiais
- Suporte a estimativas imprecisas (utilizadora não sabe metragem exata)
- Upload de fotografias de projetos antigos para perfil
- Cálculo automático de moedas iniciais (50 base)

**Requisitos técnicos:**

- Fluxo de onboarding multi-step skipable
- Entrada flexível de materiais (gramas OU metros OU "aproximação")
- Upload e armazenamento de imagens
- Sistema de avaliação automática de valor em moedas para cada material
- Atribuição retroativa de moedas a projetos anteriores (valor reduzido — ex: 50%)

**Requisitos de design:**

- Tom acolhedor, não intimidante
- Possibilidade de adicionar materiais rapidamente em série
- Balança de cozinha como referência cultural (textos de ajuda)

---

### 2.2 Sistema de Materiais (Inventário/Stash)

**Necessidades:**

- Registo de agulhas (tamanho mm, tipo)
- Registo de fios (cor, marca, gramas/metros, preço, loja)
- Registo de acessórios (markers, agulhas auxiliares, etc.)
- Atualização de quantidades restantes após uso
- Histórico de origem (marca + loja) para recompras

**Requisitos técnicos:**

- Modelo de dados com campos flexíveis (alguns opcionais)
- Sistema de "restos" — fios parcialmente usados
- Rastreamento de stock por projeto
- Decremento automático ou manual após conclusão

**Requisitos de design:**

- Vista visual tipo "armário" do material disponível
- Filtros por tipo, cor, peso, disponibilidade
- Identificação clara de materiais "livres" vs "alocados a projeto"

---

### 2.3 Economia de Moedas Digitais

**Mecânicas centrais identificadas:**

| Ação | Resultado |
|------|-----------|
| Entrada na app | +50 moedas iniciais |
| Concluir projeto | +X moedas (base) |
| Usar apenas materiais existentes | +5 moedas bónus |
| Aprender ponto novo | +1 moeda + badge |
| Mini-jogo diário | Moedas extra |
| Desafio diário (quadrado 10x10) | +3 moedas |
| Comprar material | -X moedas |
| Projeto importado antigo | Recompensa parcial |

**Princípios fundamentais:**

- Moedas são **finitas** — criam escolhas reais
- Moedas **não correspondem** a valor monetário real
- Recompensa **sempre superior** ao custo do projeto (incentiva conclusão)
- Bónus por sustentabilidade (usar stash)
- Utilizadora decide como gastar — sem restrições rígidas

**Requisitos técnicos:**

- Motor de cálculo de custo/recompensa por projeto
- Algoritmo que considera: dificuldade, tempo estimado, materiais necessários vs disponíveis
- Histórico de transações de moedas
- Sistema anti-abuso (limites diários, validação de progresso real)

**Requisitos de design:**

- Visualização clara de saldo atual
- Preview de custo/recompensa antes de iniciar projeto
- Feedback positivo mas calmo ao ganhar moedas
- **Nunca punitivo** — não bloquear funcionalidades core

---

### 2.4 Sistema de Projetos

**Estados de projeto:**

- Sugerido (pela Aidi)
- Planeado/Guardado
- Em progresso (com %)
- Pausado
- Concluído
- Importado de comunidade

**Necessidades:**

- Múltiplos projetos simultâneos
- Progresso percentual visível
- Retoma instantânea de onde parou
- Histórico de tempo investido
- Materiais alocados visíveis

**Requisitos técnicos:**

- Persistência local (offline-first)
- Sincronização opcional
- Cálculo de % baseado em passos completados
- Cronómetro de sessão automático

---

### 2.5 Aidi — Assistente de IA

**Funções identificadas no texto:**

1. **Sugestão proativa de projetos**
   - Baseada em: materiais disponíveis, estação do ano, nível, histórico
   
2. **Adaptação de padrões**
   - Ajuste a medidas pessoais
   - Conversão de agulhas (quando utilizadora só tem outras)
   - Recálculo de pontos e linhas
   
3. **Verificação de gauge**
   - Validar quadrado 10x10cm fornecido pelo utilizador
   - Corrigir cálculos se necessário
   
4. **Conversão de PDF → padrão estruturado**
   - Importar instrução externa
   - Transformar em formato app
   
5. **Recomendação de compras**
   - Sugerir materiais em falta na loja
   - Apresentar opções (não só uma)
   
6. **Ensino de pontos novos**
   - Mostrar animação quando utilizadora não sabe
   - Adicionar ao dicionário pessoal
   
7. **Resposta a perguntas durante o projeto**
   - Detalhes adicionais sob pedido
   
8. **Aviso de limitações**
   - Comunicar que pode errar
   - Encorajar verificação pelo utilizador

**Requisitos técnicos:**

- LLM com contexto do utilizador (inventário, nível, histórico)
- Sistema de prompts estruturados por função
- Integração com base de padrões
- Análise de imagem (foto de gauge)
- Confidence scores em sugestões

**Requisitos de design:**

- Aidi aparece **contextualmente**, não invasivamente
- Tom calmo, encorajador, não-paternalista
- Sempre permitir override pelo utilizador
- Marcação visual clara de conteúdo gerado por IA

---

### 2.6 Execução de Projeto (Modo Tricotar)

**Cenário de uso crítico:**

- Utilizadora sentada confortavelmente
- Telemóvel apoiado na vertical
- Mãos ocupadas com agulhas
- Ambiente: à noite, vê televisão, quer relaxar

**Necessidades:**

- Passo atual visível e grande
- Avanço por voz **ou** por toque
- Resposta vocal da Aidi (toggle on/off)
- Materiais do passo visíveis no topo
- Cronómetro discreto
- Configurações rápidas no modo execução:
  - Tamanho de letra
  - Tema (claro/escuro/sem luz azul)
  - Música on/off
  - Som da Aidi on/off

**Requisitos técnicos:**

- Reconhecimento de voz offline (Android Speech API)
- Comandos: "próximo", "anterior", "repetir", "já fiz", "parar"
- Modo "always on" do ecrã durante sessão
- Save automático contínuo
- Modo de baixa luz azul

**Requisitos de design:**

- Layout vertical otimizado para suporte
- Botões grandes para uso com uma mão
- Tipografia adaptável
- Animações de transição calmas
- Feedback vocal opcional positivo ("muito bem")

---

### 2.7 Sistema de Padrões Estruturados

**Casos de uso identificados:**

1. Padrão sugerido pela Aidi (pré-estruturado)
2. PDF importado pelo utilizador (convertido por IA)
3. Padrão importado da comunidade (já validado)

**Necessidades de granularidade dos passos:**

- "Com agulha 4 e linha castanha fazer 36 pontos iniciais"
- "Faz uma linha circular em ponto pearl"
- "Coloca 4 markers nas posições X, Y, Z, W"
- "Troca os pontos para torcer cabo"

**Requisitos técnicos:**

- JSON estruturado com tipos de passos (ver documento técnico)
- Referência a materiais específicos por passo
- Suporte a markers e instruções espaciais
- Ligação a biblioteca de pontos/técnicas
- Animações vinculadas a técnicas

---

### 2.8 Loja Digital + Lista de Compras

**Necessidades:**

- Recomendação automática quando faltam materiais
- Lista de compras gerada por projeto
- Registo de compras físicas reais (preço, loja, marca)
- Conversão para moedas digitais
- Histórico para recompras facilitadas

**Requisitos técnicos:**

- Base de dados de produtos (curada ou parceiros)
- Sistema de equivalências (substituições aceitáveis)
- Input manual de compras externas
- Possível integração futura com lojas parceiras

**Requisitos de design:**

- Distinção clara: loja digital vs registo de compra física
- Comparação de opções (3 sugestões, não apenas 1)
- Preço em moedas + preço estimado em euros

---

### 2.9 Dicionário de Pontos e Aprendizagem

**Necessidades:**

- Biblioteca pessoal de pontos aprendidos
- Animações ou vídeos demonstrativos
- Vinculação a projeto onde foi aprendido
- Sistema de badges por técnicas dominadas
- Secção dedicada "Aprender"

**Requisitos técnicos:**

- Repositório de pontos (vídeos curtos ou animações)
- Estado de aprendizagem por utilizador
- Sistema de gamificação ligado

**Requisitos de design:**

- Acesso rápido durante execução
- Modo de aprendizagem standalone
- Histórico contextualizado ("aprendido em: Luvas Castanhas")

---

### 2.10 Perfil e Componente Social

**Necessidades:**

- Perfil pessoal com projetos concluídos
- Publicação de projetos finalizados (foto + comentário)
- Partilha automática opcional de detalhes do padrão
- Importar projeto a partir de publicação alheia ("repetir")
- Funcionalidade explícita: enviar perfil a amigas

**Requisitos técnicos:**

- Sistema de perfis e privacidade
- Upload de imagens
- Sistema de "fork" de projetos da comunidade
- Validação automática pela Aidi ao importar

**Requisitos de design:**

- Feed social calmo (não scroll infinito agressivo)
- Foco em amigas próximas, não em métricas virais
- Partilha como subproduto natural, não objetivo principal

---

### 2.11 Engagement Diário

**Mecânicas identificadas:**

1. **Mini-jogo de abanar o ecrã**
   - Desenrolar novelo digital
   - Recompensa em moedas
   - Acessível na home
   
2. **Desafio diário**
   - Quadrado 10x10 com ponto novo
   - +3 moedas
   - Usa materiais existentes (reforça princípio)

**Princípios:**

- Atividades curtas para dias sem tempo
- Reforço de aprendizagem
- Recompensa proporcional e justa
- **Sem FOMO, sem streaks punitivos**

**Requisitos técnicos:**

- Detecção de movimento (acelerómetro)
- Rotação diária de desafios
- Pool de pontos para aprender

---

## 3. Princípios Transversais de Design

Extraídos do comportamento implícito:

### 3.1 Autonomia
A utilizadora decide tudo: o que tricotar, quando, como gastar moedas, se aceita sugestões da Aidi.

### 3.2 Não-linearidade
Vários projetos em paralelo são naturais. A app suporta múltiplos workflows simultâneos.

### 3.3 Tolerância a imprecisão
Quantidades aproximadas, gramas em vez de metros, preços lembrados. O sistema deve aceitar inputs incompletos.

### 3.4 Calma deliberada
Nenhum elemento empurra a utilizadora. A Aidi sugere mas não exige. Não há notificações agressivas.

### 3.5 Recompensa por consciência
Usar o que se tem é financeiramente e emocionalmente recompensado.

### 3.6 Aprendizagem orgânica
Técnicas novas surgem dentro de projetos reais, não em tutoriais isolados.

---

## 4. Estrutura de Ecrãs Sugerida

Com base nas interações descritas:

```
Home (Dashboard)
├── Mini-jogo diário (icon)
├── Desafio diário
├── Projetos ativos (cards com %)
├── Sugestão da Aidi
└── Saldo de moedas

Projetos
├── Em curso
├── Planeados
├── Concluídos
└── + Novo projeto

Materiais (Stash)
├── Fios
├── Agulhas
├── Acessórios
├── Importar PDF
└── Lista de compras

Aprender
├── Pontos aprendidos
├── Dicionário completo
└── Badges

Comunidade
├── Feed (amigas)
├── Publicações próprias
└── Perfil

Loja
├── Produtos recomendados
├── Lista de compras
└── Registar compra física

Modo Execução (fullscreen)
├── Passo atual
├── Controlos de voz/toque
├── Configurações rápidas
└── Cronómetro
```

---

## 5. Prioridades de Implementação Sugeridas

### MVP (Crítico para validar conceito)

1. Sistema de materiais (inventário)
2. Sistema de projetos com passos manuais
3. Modo execução com tracking de progresso
4. Sistema de moedas básico
5. Onboarding com importação retroativa
6. Comandos de voz essenciais

### V2 (Diferenciação)

7. Aidi com sugestões básicas
8. Conversão PDF → padrão estruturado
9. Adaptação de padrões (gauge, medidas)
10. Dicionário de pontos com animações
11. Loja digital + lista de compras

### V3 (Ecossistema)

12. Componente social/comunidade
13. Fork de projetos
14. Mini-jogos e desafios diários
15. Sistema completo de badges

---

## 6. Considerações Críticas para a Equipa

### Para Design
- O contexto de uso real é **mãos ocupadas e atenção dividida**. Cada decisão visual deve passar este teste.
- A app deve sentir-se como um caderno organizado, não como um jogo barulhento.
- Imprecisão é normal. Formulários rígidos quebram a experiência.

### Para Desenvolvimento
- Offline-first é não-negociável para o modo execução.
- O sistema de padrões estruturados é o coração técnico — investir tempo aqui.
- A Aidi deve ser **modular**: cada função (sugestão, adaptação, conversão) é independente.
- Confidence scores em IA são obrigatórios para preservar confiança.

### Para Produto
- A economia de moedas tem de ser **rigorosamente testada**. Se desequilibrada, quebra a motivação.
- Não copiar mecânicas de apps de produtividade agressivas. O público quer calma.
- A partilha social é importante mas secundária — não construir como rede social.

---
