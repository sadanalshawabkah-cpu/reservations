


const screens = ["landing", "booking", "details", "done", "manage"];

function show(name) {
  for (const id of screens) {
    document.getElementById(id).hidden = (id !== name);
  }
  window.scrollTo(0, 0);
}


const state = {
  party: 2,
  tables: [],
  selected: null,   // the table object the user tapped
};
const ZONES = [
  { name: "Window",  x1: 0, x2: 3, y1: 0, y2: 4, label: true  },
  { name: "Terrace", x1: 4, x2: 9, y1: 0, y2: 1, label: true  },
  { name: "Terrace", x1: 7, x2: 9, y1: 2, y2: 7, label: false },
  { name: "Indoor",  x1: 4, x2: 6, y1: 2, y2: 4, label: false },
  { name: "Indoor",  x1: 0, x2: 6, y1: 5, y2: 7, label: true  },
];

const $ = (id) => document.getElementById(id);
const fromInput = $("from");
const toInput = $("to");

// ---------- 3. Times ----------
function toLocalInput(date) {            // Date -> "2026-11-22T19:00"
  const pad = (n) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function shortLabel(value) {             // "2026-11-22T19:00" -> "Sun 22 · 19:00"
  const d = new Date(value);
  const day = d.toLocaleDateString("en-GB", { weekday: "short", day: "numeric" });
  return `· ${day} · ${value.slice(11, 16)}`;
}

function setDefaultTimes() {
  const start = new Date();
  start.setHours(19, 0, 0, 0);
  const end = new Date(start);
  end.setHours(21);
  fromInput.value = toLocalInput(start);
  toInput.value = toLocalInput(end);
  updateTimeLabels();
}

function updateTimeLabels() {
  $("from-label").textContent = shortLabel(fromInput.value);
  $("to-label").textContent = shortLabel(toInput.value);
}

fromInput.addEventListener("change", () => {
  // keep "to" after "from": if it isn't, push it 2 hours after the new start
  if (toInput.value <= fromInput.value) {
    const end = new Date(fromInput.value);
    end.setHours(end.getHours() + 2);
    toInput.value = toLocalInput(end);
  }
  updateTimeLabels();
  loadFloor();
});

toInput.addEventListener("change", () => {
  if (toInput.value <= fromInput.value) {
    showToast("The end time must be after the start time");
    return;
  }
  updateTimeLabels();
  loadFloor();
});

// From / To segmented control: shows one picker at a time
for (const segment of document.querySelectorAll(".segment")) {
  segment.addEventListener("click", () => {
    const which = segment.dataset.seg;
    document.querySelectorAll(".segment").forEach((s) => s.classList.toggle("is-on", s === segment));
    fromInput.hidden = which !== "from";
    toInput.hidden = which !== "to";
  });
}

// ---------- 4. Floor map: ask Java, then draw ----------
async function loadFloor() {
  const params = new URLSearchParams({ from: fromInput.value, to: toInput.value, party: state.party });
  try {
    const response = await fetch("/api/availability?" + params);
    state.tables = await response.json();
  } catch {
    showToast("Can't reach the server. Is App running?");
    return;
  }
  // if the chosen table stopped being free (new time or party size), drop it
  if (state.selected && !state.tables.some((t) => t.label === state.selected.label && t.status === "FREE")) {
    state.selected = null;
  }
  drawFloor();
}

function drawFloor() {
  const floor = $("floor");
  const filter = $("location").value.trim().toLowerCase();
  floor.innerHTML = "";
    for (const z of ZONES) {
    const area = document.createElement("div");
    area.className = `zone zone--${z.name.toLowerCase()}`;
    area.style.gridColumn = `${z.x1 + 1}/${z.x2 + 2}`;
    area.style.gridRow = `${z.y1 + 1}/${z.y2 + 2}`;
    if (z.label) area.textContent = z.name;
    floor.appendChild(area);
  }

  for (const t of state.tables) {
    const button = document.createElement("button");
    button.className = `table ${t.kind} ${t.status}`;
    button.style.gridColumn = t.x + 1;      // CSS grid counts from 1, the floor plan from 0
    button.style.gridRow = t.y + 1;
    button.textContent = t.label;
    button.title = `${t.label} · ${t.seats} seat${t.seats === 1 ? "" : "s"} · ${t.zone.toLowerCase()}`;

    if (state.selected && state.selected.label === t.label) button.classList.add("is-selected");
    if (filter && !`${t.label} ${t.zone}`.toLowerCase().includes(filter)) button.classList.add("is-dimmed");

    if (t.status === "FREE") {
      button.addEventListener("click", () => selectTable(t));
    } else {
      button.disabled = true;
    }
    floor.appendChild(button);
  }
  updateSelectionText();
}

function selectTable(table) {
  state.selected = table;
  drawFloor();
}

function updateSelectionText() {
  const t = state.selected;
  $("selection").textContent = t
      ? `Table ${t.label} · ${t.seats} seat${t.seats === 1 ? "" : "s"} · ${t.zone.toLowerCase()}`
      : "Pick a table on the map";
  $("reserve").disabled = !t;
}

$("location").addEventListener("input", drawFloor);
$("clear").addEventListener("click", () => { state.selected = null; $("location").value = ""; drawFloor(); });

// ---------- 5. Seats stepper ----------
function setParty(n) {
  state.party = Math.min(6, Math.max(1, n));
  $("party").textContent = state.party;
  loadFloor();
}
$("minus").addEventListener("click", () => setParty(state.party - 1));
$("plus").addEventListener("click", () => setParty(state.party + 1));

// ---------- 6. Booking ----------
$("reserve").addEventListener("click", () => {
  const t = state.selected;
  $("details-meta").textContent =
      `Table ${t.label} · ${state.party} guest${state.party === 1 ? "" : "s"} ${shortLabel(fromInput.value)}–${toInput.value.slice(11, 16)}`;
  $("details-error").textContent = "";
  show("details");
});

$("details-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.target);
  const body = new URLSearchParams({
    table: state.selected.label,
    from: fromInput.value,
    to: toInput.value,
    party: state.party,
    name: form.get("name"),
    phone: form.get("phone"),
    email: form.get("email"),
  });

  const response = await fetch("/api/reservations", { method: "POST", body });
  const result = await response.json();

  if (!response.ok) {
    $("details-error").textContent = result.error;   // the message from your Java exception
    return;
  }
  event.target.reset();
  state.selected = null;
  $("booking-id").textContent = result.id;
  show("done");
});

$("details-back").addEventListener("click", () => show("booking"));

// ---------- 7. Cancelling ----------
$("manage-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const id = $("cancel-id").value.trim();
  const response = await fetch(`/api/reservations/${encodeURIComponent(id)}/cancel`, { method: "POST" });
  const result = await response.json();

  if (!response.ok) {
    $("manage-error").textContent = result.error;
    return;
  }
  $("cancel-id").value = "";
  $("manage-error").textContent = "";
  show("landing");
  showToast(`Booking ${id} cancelled`);
});

// ---------- 8. Moving between screens ----------
$("start").addEventListener("click", () => { show("booking"); loadFloor(); });
$("close").addEventListener("click", () => show("landing"));
$("open-manage").addEventListener("click", () => show("manage"));
$("manage-back").addEventListener("click", () => show("landing"));
$("done-button").addEventListener("click", () => { show("booking"); loadFloor(); });

function showToast(message) {
  const toast = $("toast");
  toast.textContent = message;
  toast.classList.add("show");
  setTimeout(() => toast.classList.remove("show"), 2600);
}

setDefaultTimes();
show("landing");
