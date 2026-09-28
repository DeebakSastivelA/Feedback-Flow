const notice = document.querySelector("#notice");
const loginView = document.querySelector("#login-view");
const studentView = document.querySelector("#student-view");
const facultyView = document.querySelector("#faculty-view");
const feedbackDialog = document.querySelector("#feedback-dialog");
const noticeHome = notice.parentElement;
let signedInUser;
let activeForm;
let noticeTimer;

function showNotice(message, isError = false) {
  clearTimeout(noticeTimer);
  if (feedbackDialog.open && notice.parentElement !== feedbackDialog) feedbackDialog.append(notice);
  notice.textContent = message;
  notice.classList.toggle("error", isError);
  notice.hidden = false;
  noticeTimer = setTimeout(() => { notice.hidden = true; }, 4500);
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { ...(options.body ? { "Content-Type": "application/json" } : {}), ...options.headers }
  });
  const data = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) throw new Error(data?.message || "The request could not be completed.");
  return data;
}

function escapeHtml(value = "") {
  return String(value).replace(/[&<>"']/g, character => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[character]);
}

function courseStrip(courses) {
  return courses.length ? courses.map(course => `<div class="course-chip"><strong>${escapeHtml(course.courseCode)}</strong><span>${escapeHtml(course.title)}</span></div>`).join("") : `<div class="empty-state">No courses are assigned yet.</div>`;
}

function statusPill(status) {
  const label = status === "SUBMITTED" ? "Submitted" : status === "CLOSED" ? "Closed" : "Open";
  return `<span class="pill pill-${status.toLowerCase()}">${label}</span>`;
}

async function loadStudent() {
  const [result, history] = await Promise.all([
    api(`/api/student/${signedInUser.id}/forms`), api(`/api/student/${signedInUser.id}/submissions`)
  ]);
  document.querySelector("#student-courses").innerHTML = courseStrip(signedInUser.courses);
  document.querySelector("#student-forms-body").innerHTML = result.forms.length ? result.forms.map(form => `
    <tr><td><strong>${escapeHtml(form.courseCode)}</strong><small>${escapeHtml(form.courseTitle)}</small></td>
    <td>${escapeHtml(form.semester)}</td><td>${escapeHtml(form.closingDate)}</td><td>${statusPill(form.status)}</td>
    <td>${form.status === "OPEN" ? `<button class="button button-primary" type="button" data-fill-form="${form.id}">Fill form <span aria-hidden="true">→</span></button>` : ""}</td></tr>`).join("") : `<tr><td colspan="5" class="empty-cell">There are no feedback forms for your assigned courses yet.</td></tr>`;
  document.querySelector("#submission-history").innerHTML = history.length ? history.map(submission => `
    <details class="history-item"><summary><strong>${escapeHtml(submission.courseCode)} · ${escapeHtml(submission.courseTitle)}</strong><span>${escapeHtml(submission.semester)} · ${new Date(submission.submittedAt).toLocaleDateString()}</span></summary>
      <div class="history-answers">${submission.answers.map(answer => `<div class="history-answer"><span>${escapeHtml(answer.questionText)}</span><strong>${answer.rating} / 5</strong></div>`).join("")}</div></details>`).join("") : `<div class="empty-state">Your submitted feedback and selected ratings will appear here.</div>`;
}

function renderFaculty(summary) {
  document.querySelector("#faculty-courses").innerHTML = summary.courses.length ? summary.courses.map(course => `
    <article class="faculty-card"><header class="faculty-card-head"><div><p class="eyebrow">${escapeHtml(course.courseCode)}</p><h3>${escapeHtml(course.courseTitle)}</h3></div>
      <div class="faculty-stat"><strong>${course.submissions}</strong><span>submissions</span></div></header><div class="summary-body">
      <p class="summary-highlight">Overall course rating: <strong>${course.overallAverage.toFixed(2)} / 5</strong>${course.lowestRatedQuestion === "No feedback yet" ? " · No feedback received yet" : ` · Lowest: ${escapeHtml(course.lowestRatedQuestion)} (${course.lowestQuestionAverage.toFixed(2)})`}</p>
      ${course.questions.length ? course.questions.map(question => `<div class="question-average"><span>${escapeHtml(question.question)}</span><span class="rating-track" aria-label="${question.averageRating.toFixed(2)} out of 5"><span style="width:${Math.max(0, Math.min(100, question.averageRating * 20))}%"></span></span><strong>${question.averageRating.toFixed(2)}</strong></div>`).join("") : `<div class="empty-state">No feedback forms have been published for this course.</div>`}
    </div></article>`).join("") : `<div class="empty-state">No courses are assigned to this faculty account yet.</div>`;
}

async function openForm(formId) {
  try {
    const result = await api(`/api/student/${signedInUser.id}/forms`);
    activeForm = result.forms.find(form => form.id === Number(formId));
    if (!activeForm || activeForm.status !== "OPEN") throw new Error("This feedback form is no longer open.");
    document.querySelector("#dialog-course").textContent = `${activeForm.courseCode} · ${activeForm.courseTitle} · ${activeForm.semester}`;
    const scale = [[5, "Strongly Agree"], [4, "Agree"], [3, "Somewhat Agree"], [2, "Disagree"], [1, "Strongly Disagree"]];
    document.querySelector("#question-list").innerHTML = activeForm.questions.map(question => `
      <fieldset class="question-card"><legend>${question.displayOrder}. ${escapeHtml(question.questionText)}</legend><div class="rating-options">
        ${scale.map(([rating, label]) => `<label class="rating-choice"><input type="radio" name="question-${question.id}" value="${rating}" required><strong>${rating}</strong><span>${label}</span></label>`).join("")}
      </div></fieldset>`).join("");
    feedbackDialog.showModal();
  } catch (error) {
    showNotice(error.message, true);
  }
}

function signOut() {
  signedInUser = undefined;
  studentView.hidden = true;
  facultyView.hidden = true;
  loginView.hidden = false;
  document.querySelector("#login-form").reset();
}

document.querySelector("#login-form").addEventListener("submit", async event => {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector('button[type="submit"]');
  button.disabled = true;
  button.textContent = "Signing in…";
  try {
    signedInUser = await api("/api/auth/login", { method: "POST", body: JSON.stringify(Object.fromEntries(new FormData(form))) });
    loginView.hidden = true;
    if (signedInUser.role === "STUDENT") {
      document.querySelector("#student-name").textContent = signedInUser.name;
      document.querySelector("#student-department").textContent = signedInUser.department;
      studentView.hidden = false;
      await loadStudent();
    } else {
      document.querySelector("#faculty-name").textContent = signedInUser.name;
      document.querySelector("#faculty-department").textContent = signedInUser.department;
      facultyView.hidden = false;
      renderFaculty(await api(`/api/faculty/${signedInUser.id}/feedback-summary`));
    }
  } catch (error) {
    signedInUser = undefined;
    studentView.hidden = true;
    facultyView.hidden = true;
    loginView.hidden = false;
    showNotice(error.message, true);
  } finally {
    button.disabled = false;
    button.innerHTML = 'Sign in <span aria-hidden="true">→</span>';
  }
});

document.body.addEventListener("click", event => {
  const fillButton = event.target.closest("[data-fill-form]");
  if (fillButton) openForm(fillButton.dataset.fillForm);
  if (event.target.closest("[data-logout]")) signOut();
});

document.querySelector("#student-refresh").addEventListener("click", async () => {
  try { await loadStudent(); showNotice("Your feedback status is up to date."); }
  catch (error) { showNotice(error.message, true); }
});

document.querySelector("#submission-form").addEventListener("submit", async event => {
  event.preventDefault();
  if (!event.currentTarget.reportValidity()) return;
  const answers = activeForm.questions.map(question => ({
    questionId: question.id,
    rating: Number(event.currentTarget.querySelector(`input[name="question-${question.id}"]:checked`).value)
  }));
  const button = event.currentTarget.querySelector('button[type="submit"]');
  button.disabled = true;
  button.textContent = "Submitting…";
  try {
    const result = await api(`/api/forms/${activeForm.id}/submissions`, {
      method: "POST", body: JSON.stringify({ studentId: signedInUser.id, answers })
    });
    feedbackDialog.close();
    event.currentTarget.reset();
    showNotice(result.message);
    await loadStudent();
  } catch (error) {
    showNotice(error.message, true);
  } finally {
    button.disabled = false;
    button.innerHTML = 'Submit feedback <span aria-hidden="true">→</span>';
  }
});

document.querySelector("#close-dialog").addEventListener("click", () => feedbackDialog.close());
document.querySelector("#cancel-dialog").addEventListener("click", () => feedbackDialog.close());
feedbackDialog.addEventListener("close", () => document.querySelector("#submission-form").reset());
feedbackDialog.addEventListener("close", () => noticeHome.insertBefore(notice, studentView));
document.querySelector("#submission-form").addEventListener("invalid", event => {
  if (event.target.type === "radio") showNotice("Please answer all six questions before submitting.", true);
}, true);