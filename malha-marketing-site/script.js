const notifyButton = document.querySelector("#notifyButton");
const emailInput = document.querySelector("#email");
const formMessage = document.querySelector("#formMessage");
const revealItems = document.querySelectorAll(".reveal");
const voteButtons = document.querySelectorAll(".vote-options button");
const voteMessage = document.querySelector("#voteMessage");
const aidiQuote = document.querySelector("#aidiQuote");

const aidiQuotes = [
  "You already own yarn that matches this project. Want to plan it from your stash?",
  "You are 72% done. A short session could finish the sleeve.",
  "I can keep the row, the note, and the next step together.",
  "When AI helps, you stay in control of the final pattern."
];

const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

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

let quoteIndex = 0;

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
      voteMessage.textContent = `${button.textContent} marked as your priority. Connect this later to real voting.`;
    }
  });
});

document.querySelectorAll('a[aria-disabled="true"]').forEach((link) => {
  link.addEventListener("click", (event) => event.preventDefault());
});

notifyButton?.addEventListener("click", () => {
  const email = emailInput.value.trim();

  if (!email) {
    formMessage.textContent = "Add an email when the real waitlist is connected.";
    return;
  }

  formMessage.textContent =
    "Placeholder saved for now. Connect this form to your future launch list.";
});
