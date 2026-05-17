# Barra de Navegação Malha — Especificação Completa

## Princípios de Design da Navegação

Antes da definição dos ícones, três regras fundamentam as decisões:

1. **Máximo 5 itens** — limite cognitivo confortável
2. **Ordem por frequência de uso** — o mais usado fica em posição central ou de fácil acesso com o polegar
3. **Coerência visual** — todos os ícones partilham o mesmo estilo: linha suave, cantos arredondados, peso médio, opcional fill quando ativos

---

## Estrutura Final Recomendada

**5 itens, da esquerda para a direita:**

```
[ Casa ] [ Projetos ] [ Aidi ] [ Materiais ] [ Comunidade ]
```

O perfil pessoal **não** ocupa lugar na barra — fica acessível através de avatar no canto superior direito da Home (padrão estabelecido em Instagram, Spotify, etc.).

A loja **não** é um separador autónomo — está integrada em Materiais e Projetos onde faz sentido contextual.

A secção "Aprender" **não** é separador principal — vive dentro de Projetos e Aidi, surgindo quando relevante.

---

## 1. CASA (Home)

### Posição
**Primeira posição** — convenção universal, ponto de regresso.

### Ícone Visual
- **Inativo:** Casinha simples em linha, telhado triangular suave, sem porta detalhada
- **Ativo:** Mesma forma preenchida em verde sálvia
- **Alternativa temática:** Casinha onde o telhado é uma laçada de fio (sutil identidade Malha)
- **Tamanho:** 24dp standard, com touch target de 48dp

### Página para onde liga
**Dashboard principal — "A Tua Malha Hoje"**

### Função Principal
Servir como **ponto de entrada calmo** que mostra o estado atual da prática crafteira da utilizadora e oferece ações rápidas para o dia.

### Conteúdo da Página

**Topo:**
- Saudação contextual ("Boa noite, Ana")
- Saldo de moedas visível
- Avatar/perfil no canto

**Bloco "A continuar":**
- Cards horizontais dos 1-3 projetos em curso mais recentes
- Cada card mostra: foto, nome, % completa, tempo desde última sessão
- Tap → abre modo execução

**Bloco "Atividade diária":**
- Mini-jogo do novelo (abanar)
- Desafio do dia (quadrado 10x10 com ponto novo)
- Ambos com recompensa visível

**Bloco "A Aidi sugere":**
- 1 a 2 sugestões personalizadas
- Baseadas em materiais, estação, nível
- Botão claro: "Ver detalhes"

**Rodapé:**
- Resumo discreto: "Esta semana: 3h, 1 ponto novo aprendido"

### Necessidades do Utilizador que Resolve

| Necessidade identificada | Como resolve |
|--------------------------|--------------|
| "Quero retomar onde parei" | Cards de projetos ativos imediatamente visíveis |
| "Hoje não tenho tempo/vontade para projeto grande" | Mini-jogo e desafio rápidos |
| "Não sei o que fazer a seguir" | Sugestão da Aidi |
| "Quero sentir progresso" | Resumo semanal discreto |
| "Quero saldo de moedas para decidir compras" | Visível no topo |

---

## 2. PROJETOS

### Posição
**Segunda posição** — função core da app, alta frequência de uso.

### Ícone Visual
- **Inativo:** Duas agulhas de tricô cruzadas em diagonal, linhas finas
- **Ativo:** Mesmo desenho com fill suave ou linha mais espessa
- **Alternativa:** Novelo com fio a sair que forma uma laçada
- **Importante:** Evitar ícones genéricos de "lista" ou "tarefa" — deve sentir-se artesanal

### Página para onde liga
**Gestor de Projetos — "Os Meus Projetos"**

### Função Principal
Centro de **gestão completa do ciclo de vida de projetos**: planeados, em curso, concluídos e arquivados.

### Conteúdo da Página

**Cabeçalho com tabs ou filtros:**
- Em curso
- Planeados
- Concluídos
- Importados (da comunidade)

**Lista de projetos:**
- Vista em grelha (2 colunas) ou lista
- Cada card: foto, nome, % progresso, materiais alocados, custo/recompensa em moedas
- Indicador visual de "pausado há X dias" (sem julgamento)

**Botão flutuante:**
- "+ Novo Projeto"
- Abre fluxo: criar do zero / importar PDF / pedir sugestão à Aidi / repetir de amiga

**Acesso secundário:**
- Dicionário de pontos aprendidos
- Histórico de projetos arquivados

### Necessidades do Utilizador que Resolve

| Necessidade identificada | Como resolve |
|--------------------------|--------------|
| "Tenho vários projetos em paralelo" | Vista clara de todos os estados |
| "Esqueci-me daquela luva sem par" | Visualização de projetos pausados |
| "Quero importar PDF da internet" | Fluxo de criação acessível |
| "Quero ver os projetos que já fiz" | Tab de concluídos com fotos |
| "Quero repetir o gorro da amiga" | Tab de importados visível |

---

## 3. AIDI (Centro)

### Posição
**Posição central (terceira)** — destaque visual estratégico, polegar acessível.

### Ícone Visual
- **Tratamento especial:** Botão ligeiramente elevado ou com fundo circular contínuo
- **Símbolo:** Pequeno avatar abstrato da Aidi (versão simplificada da mascote — novelinho com olhos ou estilizado)
- **Estado ativo:** Animação subtil ocasional (piscar, balançar muito ligeiramente) quando tem nova sugestão
- **Cor:** Indigo abafado (cor identitária da Aidi), distinta dos outros ícones em sálvia

### Página para onde liga
**Assistente Aidi — Conversação e Planeamento**

### Função Principal
Espaço dedicado à **interação com a inteligência artificial**, tanto para conversação aberta como para fluxos guiados.

### Conteúdo da Página

**Modo conversação:**
- Histórico de interações com a Aidi
- Input de texto e voz
- Aidi responde com sugestões, explicações, adaptações

**Atalhos rápidos (cards):**
- "Ajuda-me a escolher próximo projeto"
- "Adapta um padrão à minha medida"
- "Importa este PDF"
- "Estima quanto fio preciso"
- "Como faço este ponto?"

**Histórico de sugestões:**
- Sugestões anteriores arquivadas
- Possibilidade de retomar conversas

**Indicador de uso:**
- Limites de IA se aplicável (tier premium vs free)

### Necessidades do Utilizador que Resolve

| Necessidade identificada | Como resolve |
|--------------------------|--------------|
| "Quero sugestão personalizada" | Conversa direta com IA |
| "Tenho PDF para converter" | Fluxo dedicado |
| "Quero adaptar padrão à minha medida" | Atalho rápido |
| "Não conheço este ponto" | Pergunta direta à Aidi |
| "Quero estimar materiais antes de comprar" | Função explícita |

### Nota crítica de design
A Aidi também aparece **contextualmente** noutras páginas (durante execução, ao adicionar material, etc.). Esta página é o local onde a utilizadora **a procura ativamente** — não onde ela aparece sozinha.

---

## 4. MATERIAIS (Stash)

### Posição
**Quarta posição** — uso frequente mas não-execução, dimensão de gestão.

### Ícone Visual
- **Inativo:** Novelo de fio simples com pequeno fio a sair na ponta
- **Ativo:** Versão preenchida com fio em destaque
- **Alternativa:** Caixa/cesto com fio a transbordar (metáfora do stash)
- **Importante:** Distinto do ícone de projetos — o stash é matéria-prima, não trabalho

### Página para onde liga
**Inventário e Loja — "A Minha Caixa"**

### Função Principal
Gestão do **inventário físico e digital de materiais**, com integração à loja e lista de compras.

### Conteúdo da Página

**Tabs principais:**
- Fios
- Agulhas
- Acessórios (markers, agulhas auxiliares, tesouras, etc.)
- Lista de compras
- Loja

**Vista de inventário:**
- Cards visuais com foto/cor
- Quantidade restante (com indicador visual: cheio, parcial, vazio)
- Estado: livre / alocado a projeto X
- Marca, loja, preço pago (se registado)

**Botão "+ Adicionar":**
- Manual
- Por foto (visão computacional opcional)
- Por código de barras (futuro)

**Lista de compras:**
- Items pendentes agrupados por projeto
- Sugestões da Aidi para alternativas
- Botão "Registar compra física feita"

**Loja:**
- Produtos recomendados pela Aidi
- Filtros por preço em moedas / euros
- Não obrigatório — meramente sugestivo

### Necessidades do Utilizador que Resolve

| Necessidade identificada | Como resolve |
|--------------------------|--------------|
| "Tenho fios em casa e não sei o que tenho" | Inventário visual completo |
| "Preciso de comprar mais do azul daquele projeto" | Histórico com marca e loja |
| "Quanto me sobra deste novelo?" | Quantidade restante visível |
| "O que posso fazer com o que já tenho?" | Aidi sugere a partir daqui |
| "Preciso de uma agulha 5mm para este projeto" | Lista de compras automática |

---

## 5. COMUNIDADE

### Posição
**Quinta posição** — funcionalidade social, importante mas não diária.

### Ícone Visual
- **Inativo:** Duas figuras estilizadas muito simples lado a lado, ou um pequeno coração feito de fio
- **Ativo:** Versão preenchida
- **Alternativa identitária:** Dois novelinhos pequenos sobrepostos (mais coerente com a linguagem visual)
- **Evitar:** Ícone genérico de pessoas tipo Instagram/Facebook

### Página para onde liga
**Feed da Comunidade — "Inspiração e Partilha"**

### Função Principal
Espaço de **partilha calma entre utilizadoras**, descoberta de padrões testados e inspiração de amigas próximas.

### Conteúdo da Página

**Feed principal:**
- Publicações de amigas e contas seguidas
- Cada post: foto do projeto, padrão usado, comentário pessoal, opção "repetir/importar"
- Não há scroll infinito agressivo — paginação ou limite suave

**Tabs ou filtros:**
- Amigas (próximas)
- Descobrir (curado)
- Tendências da estação
- Padrões mais testados

**Interações:**
- Guardar projeto para mais tarde
- Importar diretamente para os meus projetos
- Comentar (moderado)
- Apoiar (em vez de "like" — termo mais calmo)

**Perfil próprio:**
- Acessível por avatar no canto superior
- Mostra projetos concluídos publicados
- Estatísticas pessoais (opcional)

### Necessidades do Utilizador que Resolve

| Necessidade identificada | Como resolve |
|--------------------------|--------------|
| "Quero ver o que as minhas amigas fizeram" | Feed de amigas |
| "Quero partilhar o meu colete terminado" | Publicação direta |
| "Vi um gorro giro e quero fazer igual" | Botão "Repetir/Importar" |
| "Quero recomendar a app a alguém" | Partilha de perfil |
| "Procuro inspiração para a próxima estação" | Tendências sazonais |

---

## Resumo Visual da Barra

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│   🏠         🧶          ✨          🧵         👥    │
│  Casa    Projetos      Aidi      Materiais   Comunidade│
│                                                     │
└─────────────────────────────────────────────────────┘
```

*Nota: emojis usados apenas para esquematização. Os ícones finais são desenhados em estilo coerente, linha contínua, peso médio, cantos arredondados.*

---

## Estados Visuais Consistentes

Para todos os ícones:

| Estado | Tratamento |
|--------|-----------|
| Inativo | Linha em cor neutra escura (sobre fundo claro) ou cor neutra clara (sobre fundo escuro) |
| Ativo | Preenchimento em verde sálvia (cor primária) ou indigo no caso da Aidi |
| Com notificação | Pequeno ponto colorido no canto superior direito (max 1, sem números) |
| Animação | Apenas no momento de tap (escala 0.95 → 1.0), sem animações constantes |

---

## Justificação da Ordem

A ordem **Casa → Projetos → Aidi → Materiais → Comunidade** segue:

1. **Casa primeiro** — convenção universal e ponto de regresso
2. **Projetos segundo** — o "porquê" da app, função core
3. **Aidi central** — destaque estratégico para a diferenciação da app
4. **Materiais quarto** — suporte aos projetos, lógico vir depois
5. **Comunidade último** — periférico mas presente, não obriga a usar

A posição central da Aidi é deliberada: comunica imediatamente que a IA é parte da identidade, sem ser o único protagonista. A utilizadora vê-a sempre, mas decide quando interagir.

---

## Decisões Excluídas (e Porquê)

| Item considerado | Decisão | Razão |
|------------------|---------|-------|
| Perfil como separador | **Excluído** | Acessível por avatar — não justifica espaço dedicado |
| Loja como separador | **Excluído** | Integrada em Materiais — evita sensação comercial |
| Aprender como separador | **Excluído** | Vive dentro de Projetos e Aidi contextualmente |
| Configurações como separador | **Excluído** | Acessível por perfil, baixa frequência |
| Mini-jogos como separador | **Excluído** | Vivem na Home — não merecem destaque autónomo |

---

## Considerações Finais para Implementação

- **Touch target mínimo:** 48dp por ícone, espaçamento adequado
- **Acessibilidade:** Todos os ícones têm `contentDescription` clara para screen readers
- **Localização:** Labels em português, inglês e espanhol, com text expansion considerado
- **Haptic feedback:** Vibração subtil ao trocar de tab
- **Sem badges numéricos agressivos:** Apenas ponto discreto quando absolutamente necessário
