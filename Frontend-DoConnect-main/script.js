console.log("Script is loaded");
/* =========================================
  BASE URL (UserService + QuestionService Gateway)
========================================= */
const inferredGatewayBase =
  window.__API_BASE__ ||
  (window.location.protocol.startsWith("http") && window.location.hostname
    ? `${window.location.protocol}//${window.location.hostname}:8080`
    : "http://localhost:8080");
const BASE_URL = inferredGatewayBase;
const AUTH_API = `${BASE_URL}/auth`;
const QUESTION_API = `${BASE_URL}/api/questions`;
const ANSWER_API = `${BASE_URL}/api/answers`;
const CHAT_API = `${BASE_URL}/api/chats`;
let chatPollingHandle = null;
let currentQuestionId = null;

function extractErrorMessage(payload, fallback = "Request failed") {
  if (!payload) return fallback;

  if (typeof payload === "string") {
    const trimmed = payload.trim();
    return trimmed || fallback;
  }

  if (Array.isArray(payload)) {
    const joined = payload
      .map((item) => extractErrorMessage(item, ""))
      .filter(Boolean)
      .join(". ");
    return joined || fallback;
  }

  if (typeof payload === "object") {
    if (typeof payload.message === "string" && payload.message.trim()) {
      return payload.message.trim();
    }

    const flattened = Object.values(payload)
      .flatMap((value) =>
        Array.isArray(value)
          ? value
          : typeof value === "object" && value !== null
          ? Object.values(value)
          : [value]
      )
      .map((value) => (value ?? "").toString().trim())
      .filter(Boolean);

    if (flattened.length) {
      return flattened.join(". ");
    }
  }

  return fallback;
}

function persistSessionTokens(payload) {
  if (!payload) return;
  if (payload.accessToken) localStorage.setItem("token", payload.accessToken);
  if (payload.refreshToken)
    localStorage.setItem("refreshToken", payload.refreshToken);
  if (payload.email) localStorage.setItem("email", payload.email);
  if (payload.role) localStorage.setItem("role", payload.role);
}

/* =========================================
   CHAT SYSTEM
========================================= */
function initChatPanel() {
  const chatPanel = document.getElementById("chatPanel");
  if (!chatPanel) return;

  const sendBtn = document.getElementById("sendChatBtn");
  const roomSelect = document.getElementById("chatRoom");
  const messageInput = document.getElementById("chatMessage");
  const nameInput = document.getElementById("chatSender");

  const storedName =
    localStorage.getItem("chatName") || localStorage.getItem("email") || "";
  if (nameInput && storedName) {
    nameInput.value = storedName;
  }

  if (sendBtn) {
    sendBtn.addEventListener("click", sendChatMessage);
  }

  if (messageInput) {
    messageInput.addEventListener("keyup", (event) => {
      if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        sendChatMessage();
      }
    });
  }

  if (roomSelect) {
    roomSelect.addEventListener("change", () => {
      loadChatMessages();
      restartChatPolling();
    });
  }

  loadChatMessages();
  restartChatPolling();
}

function restartChatPolling() {
  if (chatPollingHandle) {
    clearInterval(chatPollingHandle);
  }
  chatPollingHandle = setInterval(loadChatMessages, 5000);
}

async function loadChatMessages() {
  const roomSelect = document.getElementById("chatRoom");
  const status = document.getElementById("chatStatus");
  if (!roomSelect) return;

  try {
    const res = await fetch(
      `${CHAT_API}/rooms/${roomSelect.value}/messages?limit=50`,
      { headers: buildAuthHeaders() }
    );

    if (res.status === 401) {
      renderChatMessages([]);
      if (status) status.textContent = "Log in to join the chat";
      return;
    }

    if (!res.ok) {
      throw new Error(await res.text());
    }

    const data = await res.json();
    renderChatMessages(data);
    if (status) {
      status.textContent = `Updated at ${new Date().toLocaleTimeString()}`;
    }
  } catch (error) {
    console.error("Chat load error:", error);
    if (status) {
      status.textContent = "Unable to load chat";
    }
  }
}

function renderChatMessages(messages) {
  const container = document.getElementById("chatMessages");
  if (!container) return;

  container.innerHTML = "";

  if (!messages || messages.length === 0) {
    container.innerHTML =
      '<p class="chat-empty">No messages yet. Say hello!</p>';
    return;
  }

  messages.forEach((msg) => {
    const bubble = document.createElement("div");
    bubble.className = "chat-message";
    const timestamp = new Date(msg.sentAt).toLocaleTimeString();
    bubble.innerHTML = `
        <div class="chat-author">
          <strong>${msg.senderName}</strong>
          <span>${timestamp}</span>
        </div>
        <p>${msg.content}</p>
      `;
    container.appendChild(bubble);
  });

  container.scrollTop = container.scrollHeight;
}

window.addEventListener("beforeunload", () => {
  if (chatPollingHandle) {
    clearInterval(chatPollingHandle);
  }
});

async function sendChatMessage() {
  const messageInput = document.getElementById("chatMessage");
  const roomSelect = document.getElementById("chatRoom");
  const nameInput = document.getElementById("chatSender");
  const status = document.getElementById("chatStatus");

  if (!messageInput || !roomSelect || !nameInput) return;
  if (!ensureAuthenticated(status)) return;

  const content = messageInput.value.trim();
  const senderName = nameInput.value.trim();

  if (!senderName) {
    if (status) status.textContent = "Enter a display name";
    return;
  }

  if (!content) {
    if (status) status.textContent = "Type a message";
    return;
  }

  const payload = {
    senderId: localStorage.getItem("email") || senderName,
    senderName,
    content,
  };

  try {
    const res = await fetch(`${CHAT_API}/rooms/${roomSelect.value}/messages`, {
      method: "POST",
      headers: buildAuthHeaders(true),
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      throw new Error(await res.text());
    }

    localStorage.setItem("chatName", senderName);
    messageInput.value = "";
    await loadChatMessages();
  } catch (error) {
    console.error("Chat send error:", error);
    if (status) status.textContent = "Unable to send message";
  }
}

function buildAuthHeaders(asJson = false) {
  const headers = {};
  if (asJson) headers["Content-Type"] = "application/json";
  const token = localStorage.getItem("token");
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  return headers;
}

function ensureAuthenticated(targetMsg) {
  if (localStorage.getItem("token")) {
    return true;
  }
  if (targetMsg) {
    targetMsg.textContent = "Please log in to continue";
    targetMsg.style.color = "red";
  }
  return false;
}

/* =========================================
   LOGIN
========================================= */

async function loginUser() {
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();
  const msg = document.getElementById("msg");

  if (!email || !password) {
    msg.textContent = "Email & Password required";
    msg.style.color = "red";
    return;
  }

  try {
    const res = await fetch(`${AUTH_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const data = await res
      .json()
      .catch(async () => ({ message: await res.text() }));

    if (res.ok) {
      persistSessionTokens(data);
      msg.textContent = "Login successful";
      msg.style.color = "green";

      setTimeout(() => {
        window.location.href = "user-dashboard.html";
      }, 1000);
    } else {
      msg.textContent = data.message || "Invalid credentials";
      msg.style.color = "red";
    }
  } catch (err) {
    console.log(err);
    msg.textContent = "Server unreachable";
    msg.style.color = "red";
  }
}

const loginBtn = document.getElementById("loginBtn");
if (loginBtn) loginBtn.addEventListener("click", loginUser);

/* =========================================
   REGISTER USER
========================================= */
async function registerUser() {
  const name = document.getElementById("name").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();
  const msg = document.getElementById("msg");

  if (!name || !email || !password) {
    msg.textContent = "All fields are required!";
    msg.style.color = "red";
    return;
  }

  try {
    const res = await fetch(`${AUTH_API}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fullName: name,
        email,
        password,
        role: "ROLE_USER",
      }),
    });

    const data = await res
      .json()
      .catch(async () => ({ message: await res.text() }));

    if (res.status === 201) {
      persistSessionTokens(data);
      msg.textContent = "Registration successful! Redirecting...";
      msg.style.color = "green";

      setTimeout(() => {
        window.location.href = "user-dashboard.html";
      }, 1200);
    } else {
      msg.textContent = extractErrorMessage(data, "Registration failed");
      msg.style.color = "red";
    }
  } catch (err) {
    msg.textContent = "Server unreachable";
    msg.style.color = "red";
  }
}

const registerBtn = document.getElementById("registerBtn");
if (registerBtn) registerBtn.addEventListener("click", registerUser);

/* =========================================
   ASK QUESTION (User) ✅ FIXED & CLEAN
========================================= */
async function addQuestion() {
  const titleInput = document.getElementById("q_title");
  const descInput = document.getElementById("q_desc");
  const topicInput = document.getElementById("q_topic");
  const msg = document.getElementById("q_msg");

  if (!titleInput || !descInput || !topicInput || !msg) {
    alert("❌ Input field ID mismatch in HTML");
    return;
  }

  if (!ensureAuthenticated(msg)) return;

  const title = titleInput.value.trim();
  const description = descInput.value.trim();
  const topic = topicInput.value.trim();

  if (!title || !description || !topic) {
    msg.textContent = "❌ Title, Topic & Description required";
    msg.style.color = "red";
    return;
  }

  const questionData = {
    title,
    description,
    topic,
    askedBy: localStorage.getItem("email") || "anonymous",
  };

  try {
    const res = await fetch(QUESTION_API, {
      method: "POST",
      headers: buildAuthHeaders(true),
      body: JSON.stringify(questionData),
    });

    if (res.ok) {
      msg.textContent = "✅ Question posted";
      msg.style.color = "green";

      titleInput.value = "";
      descInput.value = "";
      topicInput.value = "";
    } else if (res.status === 401) {
      msg.textContent = "Session expired. Please log in again.";
      msg.style.color = "red";
    } else {
      const errText = await res.text();
      console.error("Backend Error:", errText);
      msg.textContent = "❌ Failed to post question";
      msg.style.color = "red";
    }
  } catch (error) {
    console.error("Server Error:", error);
    msg.textContent = "❌ Server error";
    msg.style.color = "red";
  }
}

/* =========================================
   LOAD APPROVED QUESTIONS (User Dashboard) ✅ FIX
========================================= */
async function loadQuestions() {
  const box = document.getElementById("questionList");
  if (!box) return;

  try {
    const res = await fetch(QUESTION_API, { headers: buildAuthHeaders() });

    if (res.status === 401) {
      box.innerHTML = "<p>Please log in to see the community feed.</p>";
      return;
    }

    if (!res.ok) {
      throw new Error(await res.text());
    }

    const data = await res.json();

    renderQuestionCards(box, data);
  } catch (error) {
    console.error("Load Questions Error:", error);
    box.innerHTML = "<p style='color:red;'>Failed to load questions</p>";
  }
}

/* =========================================
   VIEW QUESTION PAGE REDIRECT
========================================= */
function viewQuestion(id) {
  window.location.href = `view-question.html?id=${id}`;
}

function renderQuestionCards(container, data) {
  container.innerHTML = "";

  if (!data || data.length === 0) {
    container.innerHTML = "<p>No questions available yet.</p>";
    return;
  }

  data.forEach((q) => {
    const card = document.createElement("div");
    card.className = "card";

    card.innerHTML = `
          <h3>${q.title}</h3>
          <p>${q.description}</p>
          <small>Topic: ${q.topic || "general"}</small>
          <div class="card-actions">
            <button class="outline-btn" data-question="${
              q.id
            }">View discussion</button>
          </div>
        `;

    const actionBtn = card.querySelector("button[data-question]");
    if (actionBtn) {
      actionBtn.addEventListener("click", () => viewQuestion(q.id));
    }

    container.appendChild(card);
  });
}

/* =========================================
   ADMIN — LOAD PENDING QUESTIONS
========================================= */
async function loadAdminQuestions() {
  const pendingBox = document.getElementById("pendingQuestions");
  const approvedBox = document.getElementById("approvedQuestions");
  if (!pendingBox && !approvedBox) return;

  if (!localStorage.getItem("token")) {
    if (pendingBox) pendingBox.innerHTML = "<p>Please log in as admin.</p>";
    if (approvedBox) approvedBox.innerHTML = "<p>Please log in as admin.</p>";
    return;
  }

  try {
    const [pendingRes, allRes] = await Promise.all([
      fetch(`${QUESTION_API}/admin/pending`, { headers: buildAuthHeaders() }),
      fetch(`${QUESTION_API}/admin`, { headers: buildAuthHeaders() }),
    ]);

    if (pendingRes.status === 403 || allRes.status === 403) {
      const msg = "<p>Admin access required.</p>";
      if (pendingBox) pendingBox.innerHTML = msg;
      if (approvedBox) approvedBox.innerHTML = msg;
      return;
    }

    if (!pendingRes.ok || !allRes.ok) {
      throw new Error("Failed to load moderation data");
    }

    const [pendingQuestions, allQuestions] = await Promise.all([
      pendingRes.json(),
      allRes.json(),
    ]);

    renderPendingQuestions(pendingBox, pendingQuestions);
    renderApprovedQuestions(
      approvedBox,
      allQuestions.filter((q) => q.approved)
    );
  } catch (err) {
    console.error("Admin load error", err);
    if (pendingBox) {
      pendingBox.innerHTML =
        "<p style='color:red;'>Unable to load pending questions</p>";
    }
    if (approvedBox) {
      approvedBox.innerHTML =
        "<p style='color:red;'>Unable to load approved questions</p>";
    }
  }
}

function renderPendingQuestions(container, questions) {
  if (!container) return;

  if (!questions || questions.length === 0) {
    container.innerHTML = "<p>No pending questions.</p>";
    return;
  }

  container.innerHTML = "";

  questions.forEach((q) => {
    const card = document.createElement("div");
    card.className = "card";
    card.innerHTML = `
        <h3>${q.title}</h3>
        <p>${q.description}</p>
        <small>Topic: ${q.topic || "general"}</small>
      `;

    const actions = document.createElement("div");
    actions.className = "admin-actions";

    const approveBtn = document.createElement("button");
    approveBtn.className = "approveBtnBlue";
    approveBtn.textContent = "Approve";
    approveBtn.addEventListener("click", () => setQuestionApproval(q.id, true));

    const rejectBtn = document.createElement("button");
    rejectBtn.className = "rejectBtn";
    rejectBtn.textContent = "Reject";
    rejectBtn.addEventListener("click", () => setQuestionApproval(q.id, false));

    actions.appendChild(approveBtn);
    actions.appendChild(rejectBtn);
    card.appendChild(actions);

    container.appendChild(card);
  });
}

function renderApprovedQuestions(container, questions) {
  if (!container) return;

  if (!questions || questions.length === 0) {
    container.innerHTML = "<p>No approved questions yet.</p>";
    return;
  }

  container.innerHTML = "";

  questions.forEach((q) => {
    const card = document.createElement("div");
    card.className = "card";
    const status = q.resolved ? "Resolved" : "Open";
    const resolver = q.resolvedBy ? ` • ${q.resolvedBy}` : "";

    card.innerHTML = `
        <h3>${q.title}</h3>
        <p>${q.description}</p>
        <small>Topic: ${q.topic || "general"}</small>
        <div class="status-pill ${q.resolved ? "resolved" : "pending"}">
          ${status}${resolver}
        </div>
      `;

    const actions = document.createElement("div");
    actions.className = "admin-actions";

    const toggleBtn = document.createElement("button");
    toggleBtn.className = "approveBtnBlue";
    toggleBtn.textContent = q.resolved ? "Mark Unresolved" : "Mark Resolved";
    toggleBtn.addEventListener("click", () =>
      setQuestionResolution(q.id, !q.resolved)
    );

    actions.appendChild(toggleBtn);
    card.appendChild(actions);
    container.appendChild(card);
  });
}

async function setQuestionApproval(id, approved) {
  const reviewerId = localStorage.getItem("email") || "admin";

  try {
    const res = await fetch(`${QUESTION_API}/${id}/approval`, {
      method: "PUT",
      headers: buildAuthHeaders(true),
      body: JSON.stringify({ approved, reviewerId }),
    });

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || "Approval update failed");
    }

    loadAdminQuestions();
  } catch (error) {
    console.error("Approval error", error);
    alert("Unable to update approval status");
  }
}

async function setQuestionResolution(id, resolved) {
  const resolverId = localStorage.getItem("email") || "admin";

  try {
    const res = await fetch(`${QUESTION_API}/${id}/resolution`, {
      method: "PUT",
      headers: buildAuthHeaders(true),
      body: JSON.stringify({ resolved, resolverId }),
    });

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(errText || "Resolution update failed");
    }

    loadAdminQuestions();
  } catch (error) {
    console.error("Resolution error", error);
    alert("Unable to update resolution status");
  }
}

/* =========================================
   ADMIN — LOAD UNAPPROVED ANSWERS
========================================= */
async function loadUnapprovedAnswers() {
  const box = document.getElementById("adminAnswerList");
  if (!box) return;

  try {
    const res = await fetch(`${BASE_URL}/answers/unapproved`);
    const data = await res.json();

    box.innerHTML = "";

    if (data.length === 0) {
      box.innerHTML = "<p>No pending answers.</p>";
      return;
    }

    data.forEach((ans) => {
      const card = document.createElement("div");
      card.className = "card";

      card.innerHTML = `
                <p><b>Answer:</b> ${ans.content}</p>
                <p><b>Likes:</b> ${ans.likes}</p>
                <button class="approve-btn" onclick="approveAnswer(${ans.id})">Approve</button>
            `;

      box.appendChild(card);
    });
  } catch (err) {
    box.innerHTML = `<p style='color:red;'>Failed to load answers</p>`;
  }
}

/* =========================================
   APPROVE ANSWER
========================================= */
async function approveAnswer(id) {
  try {
    const res = await fetch(`${BASE_URL}/answers/approve/${id}`, {
      method: "PUT",
    });

    if (res.ok) {
      alert("Answer approved!");
      loadUnapprovedAnswers();
    } else {
      alert("Failed to approve answer");
    }
  } catch (err) {
    alert("Server unreachable");
  }
}

/* =========================================
   LOAD PARTIALS (SHARED HEADER/FOOTER)
========================================= */
async function loadPartials() {
  const partials = document.querySelectorAll("[data-partial]");
  for (const node of partials) {
    const partialName = node.getAttribute("data-partial");
    try {
      const res = await fetch(`partials/${partialName}.html`);
      if (res.ok) {
        node.innerHTML = await res.text();
      }
    } catch (err) {
      console.error(`Failed to load partial: ${partialName}`, err);
    }
  }
}

/* =========================================
   AUTO PAGE DETECTION (Runs correct functions)
========================================= */
document.addEventListener("DOMContentLoaded", async function () {
  await loadPartials();

  // ✅ User Dashboard
  if (document.getElementById("questionList")) {
    loadQuestions();
  }

  // ✅ Admin — Approve Questions Page
  if (document.getElementById("adminList")) {
    loadAdminQuestions();
  }

  // ✅ Admin — Approve Answers Page
  if (document.getElementById("adminAnswerList")) {
    loadUnapprovedAnswers();
  }

  // ✅ Resolved questions page
  if (document.getElementById("resolvedList")) {
    loadResolvedQuestions();
  }

  // ✅ View question detail page
  if (
    document.getElementById("q_title") &&
    document.getElementById("answerList")
  ) {
    loadQuestionDetail();
  }

  const currentUserEmailEl = document.getElementById("currentUserEmail");
  if (currentUserEmailEl) {
    currentUserEmailEl.textContent = localStorage.getItem("email") || "guest";
  }

  // ✅ Logout buttons (any element with data-logout attribute)
  const logoutTriggers = document.querySelectorAll("[data-logout]");
  logoutTriggers.forEach((trigger) => {
    trigger.addEventListener("click", (event) => {
      event.preventDefault();
      const allDevices = trigger.dataset.logout === "all";
      logout(allDevices);
    });
  });

  initChatPanel();
});

/* =========================================
   SEARCH QUESTIONS ✅ FINAL FIX
========================================= */
async function searchQuestions() {
  const keyword = document.getElementById("searchBox").value.trim();
  const box = document.getElementById("questionList");

  if (!box) return;

  // If search box empty → load all approved questions
  if (keyword === "") {
    loadQuestions();
    return;
  }

  try {
    const res = await fetch(
      `${QUESTION_API}/search?keyword=${encodeURIComponent(keyword)}`,
      { headers: buildAuthHeaders() }
    );

    if (res.status === 401) {
      box.innerHTML = "<p>Please log in to search.</p>";
      return;
    }

    if (!res.ok) {
      throw new Error("Search API failed");
    }

    const data = await res.json();

    renderQuestionCards(box, data);
  } catch (error) {
    console.error("Search error:", error);
    box.innerHTML = "<p style='color:red;'>Search failed</p>";
  }
}

/* =========================================
   LOGOUT HANDLER
========================================= */
async function logout(allDevices = false) {
  const accessToken = localStorage.getItem("token");
  const refreshToken = localStorage.getItem("refreshToken");

  // If we do not have an access token, just clear what we can and exit.
  if (!accessToken) {
    clearSessionAndRedirect();
    return;
  }

  const payload = {
    allDevices: allDevices || !refreshToken,
    refreshToken: refreshToken || null,
  };

  try {
    const res = await fetch(`${AUTH_API}/logout`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const errText = await res.text();
      console.error("Logout failed:", errText || res.statusText);
    }
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    if (chatPollingHandle) {
      clearInterval(chatPollingHandle);
      chatPollingHandle = null;
    }
    clearSessionAndRedirect();
  }
}

function clearSessionAndRedirect() {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("role");
  localStorage.removeItem("email");
  localStorage.removeItem("chatName");
  window.location.href = "index.html";
}

/* =========================================
   LOAD RESOLVED QUESTIONS
========================================= */
async function loadResolvedQuestions() {
  const list = document.getElementById("resolvedList");
  if (!list) return;

  try {
    const res = await fetch(`${QUESTION_API}/resolved`, {
      headers: buildAuthHeaders(),
    });

    if (res.status === 401) {
      list.innerHTML = "<p>Please log in to view resolved questions.</p>";
      return;
    }

    if (!res.ok) {
      throw new Error(await res.text());
    }

    const data = await res.json();

    if (!data || data.length === 0) {
      list.innerHTML = "<p>No resolved questions yet.</p>";
      return;
    }

    list.innerHTML = "";

    data.forEach((q) => {
      const card = document.createElement("div");
      card.className = "card resolved-card";
      const resolver = q.resolvedBy || "Unknown";
      const timestamp = new Date(q.updatedAt).toLocaleDateString();

      card.innerHTML = `
        <h4>${q.title}</h4>
        <p>${q.description}</p>
        <footer>
          <span>Resolved by ${resolver}</span>
          <span>${timestamp}</span>
        </footer>
      `;

      list.appendChild(card);
    });
  } catch (error) {
    console.error("Resolved questions load error:", error);
    list.innerHTML =
      "<p style='color:red;'>Failed to load resolved questions</p>";
  }
}

/* =========================================
   LOAD QUESTION DETAIL PAGE
========================================= */
function loadQuestionDetail() {
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");

  if (!id) {
    document.body.innerHTML =
      "<main class='center-stage'><h2>Invalid question ID</h2></main>";
    return;
  }

  currentQuestionId = id;

  loadQuestionInfo(id);
  loadAnswersList(id);

  // Wire up submit button
  const submitBtn = document.getElementById("submitAnswerBtn");
  if (submitBtn) {
    submitBtn.addEventListener("click", submitAnswer);
  }

  // Wire up refresh button
  const refreshBtn = document.getElementById("refreshAnswers");
  if (refreshBtn) {
    refreshBtn.addEventListener("click", () =>
      loadAnswersList(currentQuestionId)
    );
  }
}

async function loadQuestionInfo(id) {
  try {
    const res = await fetch(`${QUESTION_API}/${id}`, {
      headers: buildAuthHeaders(),
    });

    if (!res.ok) {
      throw new Error("Question not found");
    }

    const q = await res.json();

    document.getElementById("q_title").textContent = q.title;
    document.getElementById("q_desc").textContent = q.description;
    document.getElementById("detailTopic").textContent = q.topic || "General";

    const statusEl = document.getElementById("detailStatus");
    if (statusEl) {
      statusEl.textContent = q.resolved
        ? `Resolved by ${q.resolvedBy || "Unknown"}`
        : "Open discussion";
    }
  } catch (err) {
    console.error("Question load error:", err);
    document.getElementById("q_title").textContent = "Failed to load question";
  }
}

async function loadAnswersList(qid) {
  try {
    const res = await fetch(`${ANSWER_API}/question/${qid}`, {
      headers: buildAuthHeaders(),
    });

    if (!res.ok) {
      throw new Error(await res.text());
    }

    const list = await res.json();
    const box = document.getElementById("answerList");
    box.innerHTML = "";

    if (!list || list.length === 0) {
      box.innerHTML = "<p>No answers yet. Be the first to respond!</p>";
      return;
    }

    list.forEach((ans) => {
      const div = document.createElement("div");
      div.className = "card";
      div.innerHTML = `
        <p>${ans.content}</p>
        <small>Likes: ${ans.likes || 0}</small>
        <button class="like-btn" data-answer-id="${
          ans.id
        }" style="margin-top:8px;">❤ Like</button>
      `;

      const likeBtn = div.querySelector(".like-btn");
      if (likeBtn) {
        likeBtn.addEventListener("click", () => likeAnswer(ans.id));
      }

      box.appendChild(div);
    });
  } catch (err) {
    console.error("Answers load error:", err);
    document.getElementById("answerList").innerHTML =
      "<p style='color:red;'>Failed to load answers</p>";
  }
}

async function submitAnswer() {
  const textbox = document.getElementById("answerBox");
  const msgEl = document.getElementById("answerMsg");

  if (!ensureAuthenticated(msgEl)) return;

  const content = textbox.value.trim();

  if (!content) {
    msgEl.textContent = "Please enter an answer";
    msgEl.style.color = "red";
    return;
  }

  const payload = {
    questionId: currentQuestionId,
    content,
  };

  try {
    const res = await fetch(ANSWER_API, {
      method: "POST",
      headers: buildAuthHeaders(true),
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      msgEl.textContent = "Answer posted!";
      msgEl.style.color = "green";
      textbox.value = "";
      await loadAnswersList(currentQuestionId);
    } else {
      throw new Error(await res.text());
    }
  } catch (err) {
    console.error("Answer submit error:", err);
    msgEl.textContent = "Failed to post answer";
    msgEl.style.color = "red";
  }
}

async function likeAnswer(id) {
  try {
    const res = await fetch(`${ANSWER_API}/${id}/likes`, {
      method: "PATCH",
      headers: buildAuthHeaders(true),
      body: JSON.stringify({ increment: 1 }),
    });

    if (res.ok) {
      await loadAnswersList(currentQuestionId);
    } else {
      alert("Failed to like answer");
    }
  } catch (err) {
    console.error("Like error:", err);
    alert("Server unreachable");
  }
}
