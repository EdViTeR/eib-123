const products = {
  classic: {
    title: "Лесная классика",
    price: "590 ₽",
    text: "Компактная шишка из древесины с мягким хвойным ароматом. Хорошо работает в небольших комнатах, на рабочем месте и возле входной зоны.",
    scent: "Ель, смола, сухая древесина",
    lifetime: "До 30 дней",
    format: "Дом и рабочее пространство"
  },
  car: {
    title: "Северный салон",
    price: "690 ₽",
    text: "Версия с более насыщенной ароматической пропиткой. Подходит для автомобиля, гардеробной или прихожей, где нужно быстрее нейтрализовать запахи.",
    scent: "Кедр, сосна, свежая хвоя",
    lifetime: "До 45 дней",
    format: "Автомобиль и входная зона"
  },
  gift: {
    title: "Таёжный подарок",
    price: "990 ₽",
    text: "Набор из двух ароматических шишек в подарочной упаковке. Хороший вариант для сувенира, небольшого корпоративного подарка или домашнего декора.",
    scent: "Сосновая смола, мох, кедровая кора",
    lifetime: "До 60 дней",
    format: "Подарок и интерьер"
  }
};

const header = document.querySelector(".site-header");
const menuToggle = document.querySelector(".menu-toggle");
const nav = document.querySelector(".nav");
const productChoice = document.querySelector("#productChoice");
const form = document.querySelector("#orderForm");
const formStatus = document.querySelector("#formStatus");

const detailTitle = document.querySelector("#detailTitle");
const detailPrice = document.querySelector("#detailPrice");
const detailText = document.querySelector("#detailText");
const detailScent = document.querySelector("#detailScent");
const detailLifetime = document.querySelector("#detailLifetime");
const detailFormat = document.querySelector("#detailFormat");

function setHeaderState() {
  header.classList.toggle("is-scrolled", window.scrollY > 12);
}

function setActiveCard(productKey) {
  document.querySelectorAll("[data-product-card]").forEach((card) => {
    card.classList.toggle("is-active", card.dataset.productCard === productKey);
  });
}

function renderProduct(productKey) {
  const product = products[productKey];

  if (!product) {
    return;
  }

  detailTitle.textContent = product.title;
  detailPrice.textContent = product.price;
  detailText.textContent = product.text;
  detailScent.textContent = product.scent;
  detailLifetime.textContent = product.lifetime;
  detailFormat.textContent = product.format;
  productChoice.value = productKey;
  setActiveCard(productKey);
}

function openOrder(productKey) {
  renderProduct(productKey);
  document.querySelector("#contact").scrollIntoView({ behavior: "smooth", block: "start" });
  productChoice.focus();
}

menuToggle?.addEventListener("click", () => {
  const isOpen = nav.classList.toggle("is-open");
  menuToggle.setAttribute("aria-expanded", String(isOpen));
});

document.querySelectorAll(".nav a").forEach((link) => {
  link.addEventListener("click", () => {
    nav.classList.remove("is-open");
    menuToggle?.setAttribute("aria-expanded", "false");
  });
});

document.querySelectorAll("[data-select-product]").forEach((button) => {
  button.addEventListener("click", () => {
    const productKey = button.dataset.selectProduct;
    renderProduct(productKey);
    document.querySelector("#detail").scrollIntoView({ behavior: "smooth", block: "start" });
  });
});

document.querySelectorAll("[data-order-product]").forEach((button) => {
  button.addEventListener("click", () => {
    const productKey = button.dataset.orderProduct;
    openOrder(productKey);
  });
});

productChoice?.addEventListener("change", () => {
  renderProduct(productChoice.value);
});

form?.addEventListener("submit", (event) => {
  event.preventDefault();

  const data = new FormData(form);
  const name = data.get("name");
  const product = products[data.get("product")]?.title || "товар";

  formStatus.textContent = `Спасибо, ${name}! Заявка на «${product}» сохранена. Это учебная форма, поэтому отправка показана только на странице.`;
  formStatus.classList.add("is-success");
  form.reset();
  renderProduct("classic");
});

window.addEventListener("scroll", setHeaderState);
setHeaderState();
renderProduct("classic");
