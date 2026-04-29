const notifyButton = document.querySelector("#notifyButton");
const emailInput = document.querySelector("#email");
const formMessage = document.querySelector("#formMessage");
const revealItems = document.querySelectorAll(".reveal");

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

notifyButton?.addEventListener("click", () => {
  const email = emailInput.value.trim();

  if (!email) {
    formMessage.textContent = "Add an email when the real waitlist is connected.";
    return;
  }

  formMessage.textContent =
    "Placeholder saved for now. Connect this form to your future launch list.";
});
