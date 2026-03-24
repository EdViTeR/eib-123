/* =========================
   FocusFlow — minimal JS
   ========================= */

const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => [...root.querySelectorAll(sel)];

const STORAGE_KEY = "focusflow.tasks.v1";
const THEME_KEY = "focusflow.theme.v1";

const state = {
  tasks: [],
  filter: "all"
};

function uid() {
  return Math.random().toString(16).slice(2) + Date.now().toString(16);
}

function load() {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (Array.isArray(saved)) state.tasks = saved;
  } catch { /* ignore */ }

  const savedTheme = localStorage.getItem(THEME_KEY);
  if (savedTheme === "light" || savedTheme === "dark") {
    document.documentElement.dataset.theme = savedTheme;
  }
}

function save() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state.tasks));
}

function setTheme(nextTheme) {
  document.documentElement.dataset.theme = nextTheme;
  localStorage.setItem(THEME_KEY, nextTheme);
}

function toggleTheme() {
  const cur = document.documentElement.dataset.theme || "dark";
  setTheme(cur === "dark" ? "light" : "dark");
}

function filteredTasks() {
  if (state.filter === "active") return state.tasks.filter(t => !t.done);
  if (state.filter === "done") return state.tasks.filter(t => t.done);
  return state.tasks;
}

function progressPercent() {
  const total = state.tasks.length;
  if (total === 0) return 0;
  const done = state.tasks.filter(t => t.done).length;
  return Math.round((done / total) * 100);
}

function metaText() {
  const total = state.tasks.length;
  const done = state.tasks.filter(t => t.done).length;
  return `${total} задач • выполнено ${done}`;
}

function render() {
  const list = $("#taskList");
  const bar = $("#progressBar");
  const meta = $("#taskMeta");

  const tasks = filteredTasks();
  list.innerHTML = "";

  for (const t of tasks) {
    const li = document.createElement("li");
    li.className = `task ${t.done ? "is-done" : ""}`;
    li.dataset.id = t.id;

    li.innerHTML = `
      <div class="task__left">
        <div class="checkbox" aria-hidden="true">${t.done ? "✓" : ""}</div>
        <div class="task__text" title="${escapeHtml(t.text)}">${escapeHtml(t.text)}</div>
      </div>
      <button class="icon-btn" type="button" aria-label="Удалить задачу">✕</button>
    `;

    li.querySelector(".task__left").addEventListener("click", () => toggleTask(t.id));
    li.querySelector(".icon-btn").addEventListener("click", () => removeTask(t.id));

    list.appendChild(li);
  }

  const pct = progressPercent();
  bar.style.width = `${pct}%`;
  meta.textContent = metaText();
}

function escapeHtml(str) {
  return str
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function addTask(text) {
  const trimmed = text.trim();
  if (!trimmed) return;

  state.tasks.unshift({ id: uid(), text: trimmed, done: false });
  save();
  render();
}

function toggleTask(id) {
  const t = state.tasks.find(x => x.id === id);
  if (!t) return;
  t.done = !t.done;
  save();
  render();
}

function removeTask(id) {
  state.tasks = state.tasks.filter(x => x.id !== id);
  save();
  render();
}

function clearDone() {
  state.tasks = state.tasks.filter(x => !x.done);
  save();
  render();
}

function resetAll() {
  state.tasks = [];
  save();
  render();
}

function init() {
  $("#year").textContent = new Date().getFullYear();

  if (!document.documentElement.dataset.theme) {
    document.documentElement.dataset.theme = "dark";
  }

  const toggle = $(".nav__toggle");
  const menu = $("#navMenu");

  toggle?.addEventListener("click", () => {
    const open = menu.classList.toggle("is-open");
    toggle.setAttribute("aria-expanded", String(open));
  });

  $$(".nav__link").forEach(a => {
    a.addEventListener("click", () => {
      menu.classList.remove("is-open");
      toggle?.setAttribute("aria-expanded", "false");
    });
  });

  $('[data-theme-toggle]')?.addEventListener("click", toggleTheme);

  // Fixed "to top" button
  $('[data-to-top]')?.addEventListener("click", () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  });

  $("#taskForm")?.addEventListener("submit", (e) => {
    e.preventDefault();
    const input = $("#taskInput");
    addTask(input.value);
    input.value = "";
    input.focus();
  });

  $$(".segmented__btn").forEach(btn => {
    btn.addEventListener("click", () => {
      $$(".segmented__btn").forEach(b => b.classList.remove("is-active"));
      btn.classList.add("is-active");
      state.filter = btn.dataset.filter || "all";
      render();
    });
  });

  $("#clearDone")?.addEventListener("click", clearDone);
  $("#resetAll")?.addEventListener("click", resetAll);

  load();

  if (state.tasks.length === 0) {
    state.tasks = [
      { id: uid(), text: "Сделать конспект по теме", done: true },
      { id: uid(), text: "Лабораторная №3 — черновик", done: false },
      { id: uid(), text: "Повторить вопросы к зачёту", done: false }
    ];
    save();
  }

  render();
}

init();
