const notifyButton = document.querySelector("#notifyButton");
const emailInput = document.querySelector("#email");
const formMessage = document.querySelector("#formMessage");
const revealItems = document.querySelectorAll(".reveal");
const voteButtons = document.querySelectorAll(".vote-options button");
const voteMessage = document.querySelector("#voteMessage");
const aidiQuote = document.querySelector("#aidiQuote");
const languageButtons = document.querySelectorAll(".language-switcher button");
const metaDescription = document.querySelector("#metaDescription");
const ogTitle = document.querySelector("#ogTitle");
const ogDescription = document.querySelector("#ogDescription");
const twitterTitle = document.querySelector("#twitterTitle");
const twitterDescription = document.querySelector("#twitterDescription");

const copy = {
  en: {
    title: "Malha | Finish more handmade projects",
    description:
      "Malha is a calm crafting companion for knitting and crochet projects, materials, progress, and AI-assisted planning.",
    socialDescription:
      "A calm app for finishing knitting and crochet projects. Track rows, organize yarn, plan from your stash, and get ready for Aidi.",
    twitterDescription:
      "A calm app for finishing knitting and crochet projects, planned for a first version in June 2026.",
    quotes: [
      "You already own yarn that matches this project. Want to plan it from your stash?",
      "You are 72% done. A short session could finish the sleeve.",
      "I can keep the row, the note, and the next step together.",
      "When AI helps, you stay in control of the final pattern."
    ],
    text: {
      ".nav-links a:nth-child(1)": "For who",
      ".nav-links a:nth-child(2)": "Features",
      ".nav-links a:nth-child(3)": "Aidi",
      ".nav-links a:nth-child(4)": "FAQ",
      ".nav-links a:nth-child(5)": "Roadmap",
      ".nav-cta": "Join testers",
      ".hero .eyebrow": "A calm operating system for handmade creation",
      "#hero-title": "Turn scattered yarn plans into finished work.",
      ".hero-copy":
        "The calm app for finishing knitting and crochet projects. Plan your work, track every row, use your stash, and return to every stitch with confidence.",
      ".hero-actions .primary": "Join early testers",
      ".hero-actions .secondary": "Explore the idea",
      ".status-pill": "Offline ready",
      ".project-card > span": "Winter cardigan",
      ".current-step small": "Current row",
      ".current-step p": "Knit 24, increase 1, continue pattern repeat.",
      ".mini-grid div:nth-child(1) small": "Stash used",
      ".mini-grid div:nth-child(1) strong": "4 skeins",
      ".mini-grid div:nth-child(2) small": "Session",
      ".aidi-note p": "Aidi found enough sage wool for the sleeve.",
      ".intro-band p":
        "Built around a simple principle: reward creation, not consumption. Malha keeps the joy of craft while helping users finish more, buy smarter, and feel proud of the projects already in their hands.",
      "#problem-title": "Too many patterns, too much yarn, too many unfinished projects?",
      ".problem-section .eyebrow": "The problem",
      ".problem-grid article:nth-child(1) strong": "Lose the row",
      ".problem-grid article:nth-child(1) span:last-child":
        "Progress gets scattered across paper notes, PDFs, photos, and memory.",
      ".problem-grid article:nth-child(2) strong": "Buy twice",
      ".problem-grid article:nth-child(2) span:last-child":
        "Materials are easy to forget when the stash is not visible during planning.",
      ".problem-grid article:nth-child(3) strong": "Pause forever",
      ".problem-grid article:nth-child(3) span:last-child":
        "Projects become harder to resume when the next step is unclear.",
      ".audience-section .eyebrow": "Who it is for",
      "#audience-title": "Made for makers who want calm progress.",
      ".audience-grid article:nth-child(1) h3": "Beginners",
      ".audience-grid article:nth-child(1) p":
        "Simple steps, clearer instructions, and future Aidi explanations when a technique feels confusing.",
      ".audience-grid article:nth-child(2) h3": "Intermediate crafters",
      ".audience-grid article:nth-child(2) p":
        "Project state, materials, notes, and variants kept organized as work becomes more ambitious.",
      ".audience-grid article:nth-child(3) h3": "Stash owners",
      ".audience-grid article:nth-child(3) p":
        "A planning-first workflow that helps users see what they own before buying more.",
      ".screenshots-section .eyebrow": "Concept screens",
      "#screens-title": "A preview of the workflows Malha will connect.",
      ".mock-screen:nth-child(1) .mock-label": "Projects",
      ".mock-screen:nth-child(1) strong": "3 active projects",
      ".mock-screen:nth-child(2) .mock-label": "Stash",
      ".mock-screen:nth-child(2) strong": "Enough yarn found",
      ".mock-screen:nth-child(3) .mock-label": "Current row",
      ".mock-screen:nth-child(3) strong": "Knit 24, increase 1",
      "#features .eyebrow": "What Malha organizes",
      "#features-title": "One quiet place for the full project lifecycle.",
      ".feature-grid article:nth-child(1) h3": "Project tracking",
      ".feature-grid article:nth-child(1) p":
        "Resume the exact row, step, note, and progress state without searching through scattered notes.",
      ".feature-grid article:nth-child(2) h3": "Material awareness",
      ".feature-grid article:nth-child(2) p":
        "Catalog yarn, tools, purchases, and stash age so future projects begin with what users already own.",
      ".feature-grid article:nth-child(3) h3": "Hands-free flow",
      ".feature-grid article:nth-child(3) p":
        "Designed for large readable steps, quick actions, and future voice commands while both hands are busy.",
      ".feature-grid article:nth-child(4) h3": "Community trust",
      ".feature-grid article:nth-child(4) p":
        "Future pattern sharing will prioritize verified completions, variants, corrections, and real outcomes.",
      "#aidi .eyebrow": "Meet Aidi",
      "#aidi-title": "AI assistance that stays calm and useful.",
      "#aidi > div:first-child p:nth-of-type(2)":
        "Aidi is Malha's future AI craft companion. It will help explain steps, suggest projects from existing stash, estimate yarn, and adapt patterns with clear confidence levels.",
      "#aidi > div:first-child p:nth-of-type(3)":
        "AI is treated as an assistant, not an authority. The core workflow remains reliable offline, while advanced intelligence supports planning, learning, and discovery when users want it.",
      ".brand-note":
        'In Portuguese, "perdi o fio a meada" means losing the thread of the story. Malha is built to help makers keep that thread.',
      "#aidiQuote": "You already own yarn that matches this project. Want to plan it from your stash?",
      ".benefits-section .eyebrow": "Why people will return",
      "#benefits-title": "A calmer way to make progress visible.",
      ".benefit-list > div:nth-child(1) strong": "Finish more",
      ".benefit-list > div:nth-child(1) span:last-child": "See the next step without rebuilding context.",
      ".benefit-list > div:nth-child(2) strong": "Spend smarter",
      ".benefit-list > div:nth-child(2) span:last-child": "Plan with owned materials before buying new yarn.",
      ".benefit-list > div:nth-child(3) strong": "Never lose your row",
      ".benefit-list > div:nth-child(3) span:last-child": "Progress, notes, and current step stay together.",
      ".benefit-list > div:nth-child(4) strong": "Use AI carefully",
      ".benefit-list > div:nth-child(4) span:last-child": "Aidi explains and suggests, while users stay in control.",
      "#future .eyebrow": "Future app link coming later",
      "#future-title": "Built now as a marketing page, ready to connect later.",
      ".roadmap > div:nth-child(1) .status": "June 2026",
      ".roadmap > div:nth-child(1) strong": "Foundation",
      ".roadmap > div:nth-child(1) span:last-child": "Projects, patterns, materials, offline progress.",
      ".roadmap > div:nth-child(2) .status": "Future",
      ".roadmap > div:nth-child(2) strong": "Intelligence",
      ".roadmap > div:nth-child(2) span:last-child": "Aidi recommendations, yarn estimation, pattern help.",
      ".roadmap > div:nth-child(3) .status": "Future",
      ".roadmap > div:nth-child(3) strong": "Economy",
      ".roadmap > div:nth-child(3) span:last-child": "Gentle rewards for consistency, completion, and stash use.",
      ".roadmap > div:nth-child(4) .status": "Future",
      ".roadmap > div:nth-child(4) strong": "Ecosystem",
      ".roadmap > div:nth-child(4) span:last-child": "Community validation, pattern variants, marketplace.",
      ".trust-section .eyebrow": "Trust first",
      "#trust-title": "Offline when it matters. Transparent when AI helps.",
      ".trust-grid article:nth-child(1) h3": "Offline-first core",
      ".trust-grid article:nth-child(1) p":
        "Project execution should work even without internet, because crafting cannot depend on signal.",
      ".trust-grid article:nth-child(2) h3": "AI is labeled",
      ".trust-grid article:nth-child(2) p":
        "Aidi suggestions will be clearly marked, editable, and supported by confidence signals.",
      ".trust-grid article:nth-child(3) h3": "Users control data",
      ".trust-grid article:nth-child(3) p":
        "Material tracking, project history, and future AI context should be understandable and exportable.",
      ".vote-section .eyebrow": "Help shape Malha",
      "#vote-title": "Vote for what should come first.",
      ".vote-options button:nth-child(1)": "Pattern tracker",
      ".vote-options button:nth-child(2)": "Stash inventory",
      ".vote-options button:nth-child(3)": "Voice commands",
      ".vote-options button:nth-child(4)": "Aidi planning",
      "#voteMessage": "Voting is a placeholder for now.",
      ".faq-section .eyebrow": "FAQ",
      "#faq-title": "Questions early users may ask.",
      ".faq-list details:nth-child(1) summary span": "When will Malha be available?",
      ".faq-list details:nth-child(1) p":
        "The first version is planned for June 2026. This page will later link to the app, screenshots, and demo video.",
      ".faq-list details:nth-child(2) summary span": "Will it be Android or iOS?",
      ".faq-list details:nth-child(2) p":
        "The current technical direction starts with Android. Other platforms can come later if there is demand.",
      ".faq-list details:nth-child(3) summary span": "Will AI generate full patterns?",
      ".faq-list details:nth-child(3) p":
        "AI will be positioned as an assistant for explanations, estimates, and drafts. Verified human feedback stays central.",
      ".faq-list details:nth-child(4) summary span": "Will the app work offline?",
      ".faq-list details:nth-child(4) p":
        "The core project tracker, row progress, notes, and materials system are planned as offline-first features.",
      ".faq-list details:nth-child(5) summary span": "Will there be a free version?",
      ".faq-list details:nth-child(5) p":
        "The intended direction is a useful free core, with premium AI and marketplace features later.",
      "#waitlist .eyebrow": "Early testers",
      "#waitlist-title": "Malha is still being stitched together.",
      "#waitlist > div:first-child > p:not(.eyebrow)":
        "The first version is planned for June 2026. The app download link, screenshots, demo video, and mailing list can be added here when they are ready.",
      ".contact-links a:nth-child(1)": "Email placeholder",
      ".contact-links a:nth-child(2)": "GitHub coming soon",
      ".contact-links a:nth-child(3)": "Instagram coming soon",
      ".signup-form label": "Email for early tester updates",
      "#notifyButton": "Notify me",
      ".site-footer span:nth-child(2)": "Finish more. Buy smarter. Create better.",
      ".site-footer span:nth-child(3)": "(c) 2026 Malha. All rights reserved."
    },
    placeholders: {
      "#email": "you@example.com"
    },
    messages: {
      missingEmail: "Add an email when the real waitlist is connected.",
      saved: "Placeholder saved for now. Connect this form to your future launch list.",
      vote: "marked as your priority. Connect this later to real voting."
    }
  },
  "pt-PT": {
    title: "Malha | Termina mais projetos feitos a mao",
    description:
      "A Malha e uma app calma para tricô e crochet, com gestao de projetos, materiais, progresso e assistencia futura com IA.",
    socialDescription:
      "Uma app calma para terminar projetos de tricô e crochet. Regista carreiras, organiza fios, planeia com o teu stock e prepara-te para a Aidi.",
    twitterDescription:
      "Uma app calma para terminar projetos de tricô e crochet, com primeira versao prevista para junho de 2026.",
    quotes: [
      "Ja tens fio que combina com este projeto. Queres planear a partir do teu stock?",
      "Estas a 72%. Uma sessao curta pode terminar a manga.",
      "Eu posso manter a carreira, a nota e o proximo passo no mesmo sitio.",
      "Quando a IA ajuda, tu continuas no controlo do padrao final."
    ],
    text: {
      ".nav-links a:nth-child(1)": "Para quem",
      ".nav-links a:nth-child(2)": "Funcionalidades",
      ".nav-links a:nth-child(3)": "Aidi",
      ".nav-links a:nth-child(4)": "FAQ",
      ".nav-links a:nth-child(5)": "Roteiro",
      ".nav-cta": "Testar cedo",
      ".hero .eyebrow": "Um sistema calmo para criacao feita a mao",
      "#hero-title": "Transforma planos soltos em projetos terminados.",
      ".hero-copy":
        "A app calma para terminar projetos de tricô e crochet. Planeia o trabalho, acompanha cada carreira, usa o teu stock e volta a cada ponto com confianca.",
      ".hero-actions .primary": "Juntar-me aos testes",
      ".hero-actions .secondary": "Explorar a ideia",
      ".status-pill": "Pronta offline",
      ".project-card > span": "Casaco de inverno",
      ".current-step small": "Carreira atual",
      ".current-step p": "Tricotar 24, aumentar 1, continuar a repeticao.",
      ".mini-grid div:nth-child(1) small": "Stock usado",
      ".mini-grid div:nth-child(1) strong": "4 novelos",
      ".mini-grid div:nth-child(2) small": "Sessao",
      ".aidi-note p": "Aidi encontrou lã verde salva suficiente para a manga.",
      ".intro-band p":
        "Criada em torno de um principio simples: recompensar a criacao, nao o consumo. A Malha mantem a alegria do artesanato enquanto ajuda a terminar mais, comprar melhor e sentir orgulho nos projetos que ja tens nas maos.",
      ".problem-section .eyebrow": "O problema",
      "#problem-title": "Demasiados padroes, demasiado fio, demasiados projetos por acabar?",
      ".problem-grid article:nth-child(1) strong": "Perder a carreira",
      ".problem-grid article:nth-child(1) span:last-child":
        "O progresso fica espalhado por notas em papel, PDFs, fotografias e memoria.",
      ".problem-grid article:nth-child(2) strong": "Comprar a dobrar",
      ".problem-grid article:nth-child(2) span:last-child":
        "Os materiais esquecem-se facilmente quando o stock nao aparece durante o planeamento.",
      ".problem-grid article:nth-child(3) strong": "Pausar para sempre",
      ".problem-grid article:nth-child(3) span:last-child":
        "Os projetos tornam-se mais dificeis de retomar quando o proximo passo nao esta claro.",
      ".audience-section .eyebrow": "Para quem",
      "#audience-title": "Feita para quem quer progresso com calma.",
      ".audience-grid article:nth-child(1) h3": "Principiantes",
      ".audience-grid article:nth-child(1) p":
        "Passos simples, instrucoes mais claras e futuras explicacoes da Aidi quando uma tecnica parecer confusa.",
      ".audience-grid article:nth-child(2) h3": "Artesaos intermédios",
      ".audience-grid article:nth-child(2) p":
        "Estado do projeto, materiais, notas e variantes organizados quando o trabalho fica mais ambicioso.",
      ".audience-grid article:nth-child(3) h3": "Quem tem stock",
      ".audience-grid article:nth-child(3) p":
        "Um fluxo de planeamento que ajuda a ver o que ja existe antes de comprar mais.",
      ".screenshots-section .eyebrow": "Ecras conceito",
      "#screens-title": "Uma antevisao dos fluxos que a Malha vai ligar.",
      ".mock-screen:nth-child(1) .mock-label": "Projetos",
      ".mock-screen:nth-child(1) strong": "3 projetos ativos",
      ".mock-screen:nth-child(2) .mock-label": "Stock",
      ".mock-screen:nth-child(2) strong": "Fio suficiente encontrado",
      ".mock-screen:nth-child(3) .mock-label": "Carreira atual",
      ".mock-screen:nth-child(3) strong": "Tricotar 24, aumentar 1",
      "#features .eyebrow": "O que a Malha organiza",
      "#features-title": "Um espaco tranquilo para todo o ciclo do projeto.",
      ".feature-grid article:nth-child(1) h3": "Seguimento de projetos",
      ".feature-grid article:nth-child(1) p":
        "Retoma a carreira, o passo, a nota e o estado do progresso sem procurar em notas soltas.",
      ".feature-grid article:nth-child(2) h3": "Consciência dos materiais",
      ".feature-grid article:nth-child(2) p":
        "Cataloga fios, ferramentas, compras e idade do stock para comecar projetos com o que ja existe.",
      ".feature-grid article:nth-child(3) h3": "Fluxo sem maos",
      ".feature-grid article:nth-child(3) p":
        "Pensada para passos legiveis, acoes rapidas e comandos de voz futuros enquanto tens as maos ocupadas.",
      ".feature-grid article:nth-child(4) h3": "Confianca da comunidade",
      ".feature-grid article:nth-child(4) p":
        "A partilha futura de padroes vai dar prioridade a conclusoes verificadas, variantes, correcoes e resultados reais.",
      "#aidi .eyebrow": "Conhece a Aidi",
      "#aidi-title": "Assistencia por IA que se mantem calma e util.",
      "#aidi > div:first-child p:nth-of-type(2)":
        "Aidi e a futura companheira de IA da Malha. Vai explicar passos, sugerir projetos a partir do stock, estimar fio e adaptar padroes com niveis de confianca claros.",
      "#aidi > div:first-child p:nth-of-type(3)":
        "A IA e tratada como assistente, nao como autoridade. O fluxo principal continua fiavel offline, enquanto a inteligencia avancada apoia planeamento, aprendizagem e descoberta.",
      ".brand-note":
        'Em portugues, "perdi o fio a meada" significa perder o rumo da historia. A Malha foi criada para ajudar a manter esse fio.',
      "#aidiQuote": "Ja tens fio que combina com este projeto. Queres planear a partir do teu stock?",
      ".benefits-section .eyebrow": "Porque as pessoas voltam",
      "#benefits-title": "Uma forma mais calma de tornar o progresso visivel.",
      ".benefit-list > div:nth-child(1) strong": "Terminar mais",
      ".benefit-list > div:nth-child(1) span:last-child": "Ver o proximo passo sem reconstruir o contexto.",
      ".benefit-list > div:nth-child(2) strong": "Comprar melhor",
      ".benefit-list > div:nth-child(2) span:last-child": "Planear com materiais existentes antes de comprar novo fio.",
      ".benefit-list > div:nth-child(3) strong": "Nunca perder a carreira",
      ".benefit-list > div:nth-child(3) span:last-child": "Progresso, notas e passo atual ficam juntos.",
      ".benefit-list > div:nth-child(4) strong": "Usar IA com cuidado",
      ".benefit-list > div:nth-child(4) span:last-child": "Aidi explica e sugere, enquanto tu mantens o controlo.",
      "#future .eyebrow": "Ligacao para a app em breve",
      "#future-title": "Criada agora como pagina de apresentacao, pronta para ligar depois.",
      ".roadmap > div:nth-child(1) .status": "Junho 2026",
      ".roadmap > div:nth-child(1) strong": "Base",
      ".roadmap > div:nth-child(1) span:last-child": "Projetos, padroes, materiais, progresso offline.",
      ".roadmap > div:nth-child(2) .status": "Futuro",
      ".roadmap > div:nth-child(2) strong": "Inteligencia",
      ".roadmap > div:nth-child(2) span:last-child": "Recomendacoes da Aidi, estimativa de fio, ajuda com padroes.",
      ".roadmap > div:nth-child(3) .status": "Futuro",
      ".roadmap > div:nth-child(3) strong": "Economia",
      ".roadmap > div:nth-child(3) span:last-child": "Recompensas suaves por consistencia, conclusao e uso do stock.",
      ".roadmap > div:nth-child(4) .status": "Futuro",
      ".roadmap > div:nth-child(4) strong": "Ecossistema",
      ".roadmap > div:nth-child(4) span:last-child": "Validacao comunitaria, variantes de padroes, marketplace.",
      ".trust-section .eyebrow": "Confianca primeiro",
      "#trust-title": "Offline quando importa. Transparente quando a IA ajuda.",
      ".trust-grid article:nth-child(1) h3": "Base offline-first",
      ".trust-grid article:nth-child(1) p":
        "A execucao do projeto deve funcionar mesmo sem internet, porque criar nao pode depender de sinal.",
      ".trust-grid article:nth-child(2) h3": "IA identificada",
      ".trust-grid article:nth-child(2) p":
        "As sugestoes da Aidi serao claramente marcadas, editaveis e acompanhadas por sinais de confianca.",
      ".trust-grid article:nth-child(3) h3": "Controlo dos dados",
      ".trust-grid article:nth-child(3) p":
        "Materiais, historico de projetos e futuro contexto de IA devem ser compreensiveis e exportaveis.",
      ".vote-section .eyebrow": "Ajuda a moldar a Malha",
      "#vote-title": "Vota no que deve chegar primeiro.",
      ".vote-options button:nth-child(1)": "Tracker de padroes",
      ".vote-options button:nth-child(2)": "Inventario de stock",
      ".vote-options button:nth-child(3)": "Comandos de voz",
      ".vote-options button:nth-child(4)": "Planeamento Aidi",
      "#voteMessage": "A votacao e provisoria por agora.",
      ".faq-section .eyebrow": "FAQ",
      "#faq-title": "Perguntas que os primeiros utilizadores podem fazer.",
      ".faq-list details:nth-child(1) summary span": "Quando e que a Malha fica disponivel?",
      ".faq-list details:nth-child(1) p":
        "A primeira versao esta prevista para junho de 2026. Esta pagina vai depois ligar a app, screenshots e video demo.",
      ".faq-list details:nth-child(2) summary span": "Vai ser Android ou iOS?",
      ".faq-list details:nth-child(2) p":
        "A direcao tecnica atual comeca em Android. Outras plataformas podem chegar mais tarde se houver procura.",
      ".faq-list details:nth-child(3) summary span": "A IA vai gerar padroes completos?",
      ".faq-list details:nth-child(3) p":
        "A IA sera posicionada como assistente para explicacoes, estimativas e rascunhos. A validacao humana continua central.",
      ".faq-list details:nth-child(4) summary span": "A app vai funcionar offline?",
      ".faq-list details:nth-child(4) p":
        "O tracker de projetos, progresso de carreiras, notas e sistema de materiais estao planeados como funcionalidades offline-first.",
      ".faq-list details:nth-child(5) summary span": "Vai existir uma versao gratuita?",
      ".faq-list details:nth-child(5) p":
        "A intencao e ter uma base gratuita util, com funcionalidades premium de IA e marketplace mais tarde.",
      "#waitlist .eyebrow": "Primeiros testes",
      "#waitlist-title": "A Malha ainda esta a ser tecida.",
      "#waitlist > div:first-child > p:not(.eyebrow)":
        "A primeira versao esta prevista para junho de 2026. A ligacao para a app, screenshots, video demo e lista de emails podem ser adicionados aqui quando estiverem prontos.",
      ".contact-links a:nth-child(1)": "Email provisório",
      ".contact-links a:nth-child(2)": "GitHub em breve",
      ".contact-links a:nth-child(3)": "Instagram em breve",
      ".signup-form label": "Email para novidades dos testes",
      "#notifyButton": "Avisar-me",
      ".site-footer span:nth-child(2)": "Termina mais. Compra melhor. Cria melhor.",
      ".site-footer span:nth-child(3)": "(c) 2026 Malha. Todos os direitos reservados."
    },
    placeholders: {
      "#email": "tu@exemplo.pt"
    },
    messages: {
      missingEmail: "Adiciona um email quando a lista real estiver ligada.",
      saved: "Marcador guardado por agora. Liga este formulario a lista de lancamento futura.",
      vote: "marcado como prioridade. Liga isto mais tarde a votacao real."
    }
  }
};

const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
let currentLanguage = "en";
let aidiQuotes = copy.en.quotes;
let quoteIndex = 0;

const setText = (selector, value) => {
  const element = document.querySelector(selector);

  if (element) {
    element.textContent = value;
  }
};

const setPlaceholder = (selector, value) => {
  const element = document.querySelector(selector);

  if (element) {
    element.setAttribute("placeholder", value);
  }
};

const getRequestedLanguage = () => {
  const params = new URLSearchParams(window.location.search);
  const queryLanguage = params.get("lang");
  const storedLanguage = window.localStorage.getItem("malha-language");

  if (queryLanguage && copy[queryLanguage]) {
    return queryLanguage;
  }

  if (storedLanguage && copy[storedLanguage]) {
    return storedLanguage;
  }

  return "en";
};

const applyLanguage = (language) => {
  const selectedCopy = copy[language] || copy.en;

  currentLanguage = language;
  aidiQuotes = selectedCopy.quotes;
  quoteIndex = 0;

  document.documentElement.lang = language === "pt-PT" ? "pt-PT" : "en";
  document.title = selectedCopy.title;
  metaDescription?.setAttribute("content", selectedCopy.description);
  ogTitle?.setAttribute("content", selectedCopy.title);
  ogDescription?.setAttribute("content", selectedCopy.socialDescription);
  twitterTitle?.setAttribute("content", selectedCopy.title);
  twitterDescription?.setAttribute("content", selectedCopy.twitterDescription);

  Object.entries(selectedCopy.text).forEach(([selector, value]) => setText(selector, value));
  Object.entries(selectedCopy.placeholders).forEach(([selector, value]) => setPlaceholder(selector, value));

  languageButtons.forEach((button) => {
    const isActive = button.dataset.lang === language;
    button.classList.toggle("is-active", isActive);
    button.setAttribute("aria-pressed", String(isActive));
  });
};

languageButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const language = button.dataset.lang || "en";
    window.localStorage.setItem("malha-language", language);
    applyLanguage(language);
  });
});

applyLanguage(getRequestedLanguage());

if (reducedMotion) {
  revealItems.forEach((item) => item.classList.add("is-visible"));
} else {
  const revealObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          revealObserver.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.14 }
  );

  revealItems.forEach((item) => revealObserver.observe(item));
}

if (aidiQuote && !reducedMotion) {
  window.setInterval(() => {
    quoteIndex = (quoteIndex + 1) % aidiQuotes.length;
    aidiQuote.style.opacity = "0";

    window.setTimeout(() => {
      aidiQuote.textContent = aidiQuotes[quoteIndex];
      aidiQuote.style.opacity = "1";
    }, 220);
  }, 4600);
}

voteButtons.forEach((button) => {
  button.addEventListener("click", () => {
    voteButtons.forEach((item) => item.classList.remove("is-selected"));
    button.classList.add("is-selected");

    if (voteMessage) {
      voteMessage.textContent = `${button.textContent} ${copy[currentLanguage].messages.vote}`;
    }
  });
});

document.querySelectorAll('a[aria-disabled="true"]').forEach((link) => {
  link.addEventListener("click", (event) => event.preventDefault());
});

notifyButton?.addEventListener("click", () => {
  const email = emailInput.value.trim();

  if (!email) {
    formMessage.textContent = copy[currentLanguage].messages.missingEmail;
    return;
  }

  formMessage.textContent = copy[currentLanguage].messages.saved;
});
