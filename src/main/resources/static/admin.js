const notice = document.querySelector("#notice");
const dateInput = document.querySelector('#feedback-form [name="closingDate"]');
const localToday = new Date();
dateInput.min = `${localToday.getFullYear()}-${String(localToday.getMonth() + 1).padStart(2, "0")}-${String(localToday.getDate()).padStart(2, "0")}`;

let noticeTimer;
let state = { students: [], faculties: [], courses: [], forms: [] };
const departmentPrefixes = {
  "Computer Science and Engineering": "CS",
  "Mechanical Engineering": "ME",
  "Civil Engineering": "CE",
  "Electronics and communication engineering": "EC",
  "Electronics and Electrical Engineering": "EE"
};

function showNotice(message, isError = false) {
  clearTimeout(noticeTimer);
  notice.textContent = message;
  notice.classList.toggle("error", isError);
  notice.hidden = false;
  noticeTimer = setTimeout(() => { notice.hidden = true; }, 4200);
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

function emptyRow(columns, message) {
  return `<tr><td colspan="${columns}" class="empty-cell">${escapeHtml(message)}</td></tr>`;
}

function studentAssignmentControl(student) {
  const assignedCourseIds = student.courseIds || [];
  const availableCourses = state.courses.filter(course => !assignedCourseIds.includes(course.id));
  if (!availableCourses.length) return `<small class="field-help">All current courses assigned</small>`;
  const options = availableCourses.map(course =>
    `<option value="${course.id}">${escapeHtml(course.courseCode)} · ${escapeHtml(course.title)}</option>`).join("");
  return `<details class="student-assignment"><summary class="table-action">Assign course</summary>
    <form class="student-assignment-form" data-assign-student="${student.id}">
      <select name="courseId" required aria-label="Choose a course for ${escapeHtml(student.name)}"><option value="">Choose course</option>${options}</select>
      <button class="button button-quiet" type="submit">Assign</button>
    </form></details>`;
}

function renderPeople() {
  document.querySelector("#student-count").textContent = state.students.length;
  document.querySelector("#faculty-count").textContent = state.faculties.length;
  document.querySelector("#students-body").innerHTML = state.students.length ? state.students.map(student => `
    <tr><td><strong>${escapeHtml(student.name)}</strong><small>${escapeHtml(student.email)}</small>${studentAssignmentControl(student)}</td>
    <td>${escapeHtml(student.rollNumber)}</td><td>${escapeHtml(student.department)}</td>
    <td>${escapeHtml(student.assignedCourses.join(", "))}</td>
    <td><button class="delete-button" type="button" data-delete-student="${student.id}" aria-label="Delete ${escapeHtml(student.name)}">Delete</button></td></tr>`).join("") : emptyRow(5, "No students have been added yet.");
  document.querySelector("#faculties-body").innerHTML = state.faculties.length ? state.faculties.map(faculty => `
    <tr><td><strong>${escapeHtml(faculty.name)}</strong></td><td>${escapeHtml(faculty.email)}</td>
    <td>${escapeHtml(faculty.department)}</td><td>${escapeHtml(faculty.assignedCourses.join(", ") || "No courses assigned")}</td>
    <td><button class="delete-button" type="button" data-delete-faculty="${faculty.id}" aria-label="Delete ${escapeHtml(faculty.name)}">Delete</button></td></tr>`).join("") : emptyRow(5, "No faculty members have been added yet.");
}

function renderCourses() {
  document.querySelector("#course-count").textContent = state.courses.length;
  document.querySelector("#courses-body").innerHTML = state.courses.length ? state.courses.map(course => `
    <tr><td><strong>${escapeHtml(course.courseCode)}</strong></td><td>${escapeHtml(course.title)}</td><td>${escapeHtml(course.facultyName)}</td></tr>`).join("") : emptyRow(3, "No courses have been created yet.");

  const options = state.courses.map(course => `<option value="${course.id}">${escapeHtml(course.courseCode)} · ${escapeHtml(course.title)}</option>`).join("");
  const studentCourse = document.querySelector('#student-form [name="assignedCourseId"]');
  const selectedStudentCourse = studentCourse.value;
  studentCourse.innerHTML = `<option value="">Choose a course</option>${options}`;
  studentCourse.value = selectedStudentCourse;
  const facultyCourses = document.querySelector('#faculty-form [name="assignedCourseIds"]');
  const selectedFacultyCourses = [...facultyCourses.selectedOptions].map(option => option.value);
  facultyCourses.innerHTML = options;
  [...facultyCourses.options].forEach(option => { option.selected = selectedFacultyCourses.includes(option.value); });
  const formCourse = document.querySelector('#feedback-form [name="courseId"]');
  const selectedFormCourse = formCourse.value;
  formCourse.innerHTML = `<option value="">Choose course</option>${options}`;
  formCourse.value = selectedFormCourse;

  const facultySelect = document.querySelector('#course-form [name="facultyId"]');
  const selectedFaculty = facultySelect.value;
  const facultyOptions = state.faculties.map(faculty =>
    `<option value="${faculty.id}">${escapeHtml(faculty.name)} · ${escapeHtml(faculty.department)}</option>`).join("");
  facultySelect.innerHTML = `<option value="">Choose faculty</option>${facultyOptions}`;
  facultySelect.value = selectedFaculty;
  const createCourseButton = document.querySelector('#course-form button[type="submit"]');
  createCourseButton.disabled = state.faculties.length === 0;
  if (state.faculties.length === 0) showNotice("Add a faculty member before creating a course.", true);
}

function renderForms() {
  document.querySelector("#form-count").textContent = state.forms.length;
  document.querySelector("#forms-body").innerHTML = state.forms.length ? state.forms.map(form => {
    const isOpen = form.status === "OPEN";
    return `<tr><td><strong>${escapeHtml(form.courseCode)}</strong><small>${escapeHtml(form.courseTitle)}</small></td>
      <td>${escapeHtml(form.semester)}</td><td>${escapeHtml(form.closingDate)}</td>
      <td><span class="pill ${isOpen ? "pill-open" : "pill-closed"}">${isOpen ? "Open" : "Closed"}</span></td>
      <td>${form.submittedCount}</td><td>${isOpen ? `<button class="table-action" type="button" data-close-form="${form.id}">Close form</button>` : ""}</td></tr>`;
  }).join("") : emptyRow(6, "No feedback forms have been published yet.");
}

function renderDashboard(dashboard) {
  document.querySelector("#metric-students").textContent = dashboard.totalStudents;
  document.querySelector("#metric-faculties").textContent = dashboard.totalFaculties;
  document.querySelector("#metric-courses").textContent = dashboard.totalCourses;
  document.querySelector("#metric-forms").textContent = dashboard.totalFeedbackForms;
  document.querySelector("#metric-filled").textContent = dashboard.totalFeedbackFormsFilled;
}

async function refreshAll() {
  const refreshButton = document.querySelector("#refresh-all");
  refreshButton.disabled = true;
  try {
    const [dashboard, students, faculties, courses, forms] = await Promise.all([
      api("/api/admin/dashboard"), api("/api/admin/students"), api("/api/admin/faculties"),
      api("/api/admin/courses"), api("/api/admin/forms")
    ]);
    state = { students, faculties, courses, forms };
    renderDashboard(dashboard);
    renderPeople();
    renderCourses();
    renderForms();
  } catch (error) {
    showNotice(error.message, true);
  } finally {
    refreshButton.disabled = false;
  }
}

async function submitForm(form, path, successMessage, prepare = data => data) {
  const button = form.querySelector('button[type="submit"]');
  const originalText = button.innerHTML;
  button.disabled = true;
  button.textContent = "Saving…";
  try {
    await api(path, { method: "POST", body: JSON.stringify(prepare(Object.fromEntries(new FormData(form)))) });
    form.reset();
    showNotice(successMessage);
    await refreshAll();
  } catch (error) {
    showNotice(error.message, true);
  } finally {
    button.disabled = false;
    button.innerHTML = originalText;
  }
}

document.querySelector("#student-form").addEventListener("submit", event => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  data.rollNumber = data.rollNumber.toUpperCase();
  data.email = data.email.toLowerCase();
  const expectedPrefix = departmentPrefixes[data.department];
  if (!expectedPrefix || !new RegExp(`^${expectedPrefix}[0-9]{3}$`).test(data.rollNumber)) {
    showNotice(`Roll number must match ${expectedPrefix || "the selected department's prefix"}000.`, true);
    form.elements.rollNumber.focus();
    return;
  }
  if (!/^[A-Za-z0-9._%+-]+@stu\.edu\.in$/.test(data.email)) {
    showNotice("Student email must use the format name@stu.edu.in.", true);
    form.elements.email.focus();
    return;
  }
  if (state.students.some(student => student.rollNumber.toUpperCase() === data.rollNumber || student.email.toLowerCase() === data.email)) {
    showNotice("A student with this roll number or email already exists.", true);
    return;
  }
  if (!isValidPersonName(data.name)) {
    showNotice("Name may contain only letters, spaces, dots, and hyphens.", true);
    form.elements.name.focus();
    return;
  }
  if (!isValidPassword(data.password)) {
    showNotice("Password must have at least 6 characters and cannot start or end with a space.", true);
    form.elements.password.focus();
    return;
  }
  submitForm(form, "/api/admin/students", "Student added successfully.", () => ({ ...data, assignedCourseId: Number(data.assignedCourseId) }));
});

document.querySelector("#faculty-form").addEventListener("submit", event => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  data.email = data.email.toLowerCase();
  if (!/^[A-Za-z0-9._%+-]+@prof\.edu\.in$/.test(data.email)) {
    showNotice("Faculty email must use the format name@prof.edu.in.", true);
    form.elements.email.focus();
    return;
  }
  if (state.faculties.some(faculty => faculty.email.toLowerCase() === data.email)) {
    showNotice("A faculty member with this email already exists.", true);
    return;
  }
  if (!isValidPersonName(data.name)) {
    showNotice("Name may contain only letters, spaces, dots, and hyphens.", true);
    form.elements.name.focus();
    return;
  }
  if (!isValidPassword(data.password)) {
    showNotice("Password must have at least 6 characters and cannot start or end with a space.", true);
    form.elements.password.focus();
    return;
  }
  submitForm(form, "/api/admin/faculties", "Faculty member added successfully.", () => ({
    ...data, assignedCourseIds: [...form.querySelector('[name="assignedCourseIds"]').selectedOptions].map(option => Number(option.value))
  }));
});

document.querySelector("#course-form").addEventListener("submit", event => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  if (!data.facultyId) {
    showNotice("Please select a faculty member for this course.", true);
    form.elements.facultyId.focus();
    return;
  }
  data.courseCode = data.courseCode.trim().toUpperCase();
  data.title = data.title.trim();
  if (state.courses.some(course => course.courseCode.toUpperCase() === data.courseCode)) {
    showNotice("A course with this code already exists.", true);
    form.elements.courseCode.focus();
    return;
  }
  if (!data.title) {
    showNotice("Course title is required.", true);
    form.elements.title.focus();
    return;
  }
  submitForm(form, "/api/admin/courses", "Course created and assigned successfully.", () => ({ ...data, facultyId: Number(data.facultyId) }));
});

document.querySelector("#feedback-form").addEventListener("submit", event => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  data.semester = data.semester.trim();
  const closingDate = new Date(`${data.closingDate}T00:00:00`);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  if (!data.courseId || !data.semester || !data.closingDate || closingDate < today) {
    showNotice("Choose a course, enter a semester, and select a closing date that is today or later.", true);
    return;
  }
  const duplicateForm = state.forms.some(existing => existing.courseId === Number(data.courseId)
    && existing.semester.toLowerCase() === data.semester.toLowerCase() && existing.status === "OPEN");
  if (duplicateForm) {
    showNotice("An open feedback form already exists for this course and semester.", true);
    return;
  }
  submitForm(form, "/api/admin/forms", "Feedback form published with six standard questions.", () => ({ ...data, courseId: Number(data.courseId) }));
});

document.body.addEventListener("submit", async event => {
    const form = event.target.closest("form[data-assign-student]");
    if (!form) return;
    event.preventDefault();
    const button = form.querySelector('button[type="submit"]');
    button.disabled = true;
    try {
      const studentId = form.dataset.assignStudent;
      const courseId = form.elements.courseId.value;
      const student = await api(`/api/admin/students/${studentId}/courses/${courseId}`, { method: "POST" });
      showNotice(`${student.name} is now assigned to that course. Its feedback forms are available in the student portal.`);
      await refreshAll();
    } catch (error) {
      showNotice(error.message, true);
      button.disabled = false;
    }
  });

document.body.addEventListener("click", async event => {
  const studentId = event.target.closest("[data-delete-student]")?.dataset.deleteStudent;
  const facultyId = event.target.closest("[data-delete-faculty]")?.dataset.deleteFaculty;
  const formId = event.target.closest("[data-close-form]")?.dataset.closeForm;
  if (studentId && confirm("Delete this student? This cannot be undone.")) {
    try { const result = await api(`/api/admin/students/${studentId}`, { method: "DELETE" }); showNotice(result.message); await refreshAll(); }
    catch (error) { showNotice(error.message, true); }
  }
  if (facultyId && confirm("Delete this faculty member? This cannot be undone.")) {
    try { const result = await api(`/api/admin/faculties/${facultyId}`, { method: "DELETE" }); showNotice(result.message); await refreshAll(); }
    catch (error) { showNotice(error.message, true); }
  }
  if (formId && confirm("Close this feedback form now? Students will no longer be able to submit.")) {
    try { await api(`/api/forms/${formId}/close`, { method: "PUT" }); showNotice("Feedback form closed."); await refreshAll(); }
    catch (error) { showNotice(error.message, true); }
  }
});

document.querySelector("#refresh-all").addEventListener("click", refreshAll);

function isValidPersonName(name) {
  return /^[\p{L} .-]+$/u.test(name.trim()) && /\p{L}/u.test(name);
}

function isValidPassword(password) {
  return password.length >= 6 && password === password.trim();
}

const studentDepartment = document.querySelector('#student-form [name="department"]');
const studentRollNumber = document.querySelector('#student-form [name="rollNumber"]');
studentDepartment.addEventListener("change", () => {
  const prefix = departmentPrefixes[studentDepartment.value];
  document.querySelector("#student-roll-help").textContent = prefix ? `Format: ${prefix}000` : "Choose a department to see the required format.";
  studentRollNumber.pattern = prefix ? `${prefix}[0-9]{3}` : "(CS|ME|CE|EC|EE)[0-9]{3}";
  studentRollNumber.value = studentRollNumber.value.toUpperCase();
});

document.querySelectorAll('#student-form [name="rollNumber"]').forEach(input => input.addEventListener("input", () => {
  input.value = input.value.toUpperCase();
}));
document.querySelectorAll('#student-form [name="email"], #faculty-form [name="email"]').forEach(input => input.addEventListener("input", () => {
  input.value = input.value.toLowerCase();
}));

document.querySelectorAll("#student-form, #faculty-form, #course-form, #feedback-form").forEach(form => {
  form.addEventListener("invalid", event => {
    if (event.target.name === "facultyId") showNotice("Please select a faculty member for this course.", true);
    else showNotice(event.target.validationMessage, true);
  }, true);
});

refreshAll();