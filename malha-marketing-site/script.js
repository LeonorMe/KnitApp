const notifyButton = document.querySelector("#notifyButton");
const emailInput = document.querySelector("#email");
const formMessage = document.querySelector("#formMessage");

notifyButton?.addEventListener("click", () => {
  const email = emailInput.value.trim();

  if (!email) {
    formMessage.textContent = "Add an email when the real waitlist is connected.";
    return;
  }

  formMessage.textContent =
    "Placeholder saved for now. Connect this form to your future launch list.";
});
