let STATE = null;
let currentIndex = 0;
let selectedScore = null;
let reviewMode = false;

const $ = (id) => document.getElementById(id);

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  }[char]));
}

function normaliseImageUrl(rawPath) {
  const fileName = String(rawPath || '').split(/[\\/]/).pop();
  return fileName ? `/static/creatures/${encodeURIComponent(fileName)}` : '';
}

function getCreature(task) {
  return (STATE.rubric.creatures || {})[task.creature_id] || {
    name: task.creature_name,
    facts: [],
    image: ''
  };
}

function getScore(task) {
  return STATE.scores[task.task_id] || {};
}

function progress() {
  const total = STATE.tasks.length;
  let graded = 0;
  let skipped = 0;

  for (const task of STATE.tasks) {
    const row = getScore(task);
    if (row.status === 'graded') graded += 1;
    if (row.status === 'skipped') skipped += 1;
  }

  return {
    total,
    graded,
    skipped,
    to_be_graded: Math.max(0, total - graded - skipped)
  };
}

function firstOpenTaskIndex() {
  const index = STATE.tasks.findIndex(task => !['graded', 'skipped'].includes(getScore(task).status));
  return index === -1 ? 0 : index;
}

function updateTaskSelect() {
  $('task-select').innerHTML = STATE.tasks.map((task, index) => {
    const row = getScore(task);
    const status = row.status === 'graded' ? `graded ${row.score}` : (row.status || 'open');
    return `<option value="${index}">${index + 1}. ${escapeHtml(task.moment)} · ${escapeHtml(task.question_key)} · ${escapeHtml(status)}</option>`;
  }).join('');

  $('task-select').value = String(currentIndex);
}

function updateProgress() {
  const p = progress();
  $('progress').innerHTML = `
    <div><strong>${p.graded}</strong> graded · <strong>${p.skipped}</strong> skipped</div>
    <div><strong>${p.to_be_graded}</strong> to be graded · <strong>${p.total}</strong> total</div>
  `;
}

function maybeShowDone() {
  const p = progress();
  if (p.total > 0 && p.to_be_graded === 0 && !reviewMode) {
    $('screen').hidden = true;
    $('done').hidden = false;
    return true;
  }
  return false;
}

function renderCurrentTask() {
  if (!STATE.tasks.length) {
    $('loading').hidden = true;
    $('done').hidden = false;
    $('done').querySelector('h1').textContent = 'No answers to grade';
    $('done').querySelector('p').textContent = 'No eligible retention answers were found for this grader.';
    return;
  }

  const task = STATE.tasks[currentIndex];
  const creature = getCreature(task);
  const saved = getScore(task);
  const rubric = (STATE.rubric.rubrics || {})[task.question_key] || {
    title: task.question_label,
    score_labels: {}
  };

  selectedScore = saved.status === 'graded' ? String(saved.score) : null;

  $('grader-label').textContent = `Grader ${STATE.grader} · answer ${currentIndex + 1} of ${STATE.tasks.length}`;
  $('creature-name').textContent = creature.name || task.creature_name;
  $('creature-image').src = normaliseImageUrl(creature.image);
  $('creature-image').alt = creature.name || task.creature_name;
  $('image-modal-img').src = $('creature-image').src;
  $('image-modal-img').alt = `Enlarged ${creature.name || task.creature_name}`;
  $('creature-meta').textContent = [creature.chapter, creature.environment, creature.appearance_placeholder].filter(Boolean).join(' · ');
  $('creature-facts').innerHTML = (creature.facts || []).map(fact => `<li>${escapeHtml(fact)}</li>`).join('')
    || '<li>Placeholder: add creature facts in resources/retention_rubrics.json.</li>';

  $('rubric-key').textContent = task.question_key;
  $('rubric-title').textContent = rubric.title || task.question_label;

  $('score-options').innerHTML = (STATE.rubric.score_scale || [0, 1, 2, 3, 4]).map(score => {
    const label = (rubric.score_labels || {})[String(score)] || '';
    return `
      <button class="score-option ${String(score) === selectedScore ? 'selected' : ''}" type="button" data-score="${score}">
        <span class="score-number">${score}</span><strong>Score ${score}</strong>
        <span class="score-label">${escapeHtml(label)}</span>
      </button>
    `;
  }).join('');

  document.querySelectorAll('.score-option').forEach(button => {
    button.addEventListener('click', () => {
      selectedScore = button.dataset.score;
      document.querySelectorAll('.score-option').forEach(item => item.classList.remove('selected'));
      button.classList.add('selected');
      $('grade').disabled = false;
    });
  });

  $('question').textContent = task.question_label;
  $('answer').textContent = task.answer;
  $('note').value = saved.note || '';
  $('grade').disabled = selectedScore === null;
  $('previous').disabled = currentIndex === 0;
  $('next').disabled = currentIndex === STATE.tasks.length - 1;

  updateTaskSelect();
  updateProgress();
}

async function saveAction(action) {
  const task = STATE.tasks[currentIndex];

  const response = await fetch('/api/score', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      task_id: task.task_id,
      action,
      score: action === 'grade' ? selectedScore : '',
      note: $('note').value
    })
  });

  const result = await response.json();
  if (!result.ok) throw new Error(result.error || 'Could not save score.');

  STATE.scores[task.task_id] = result.score;

  const nextOpen = STATE.tasks.findIndex((candidate, index) => {
    return index > currentIndex && !['graded', 'skipped'].includes(getScore(candidate).status);
  });

  currentIndex = nextOpen !== -1 ? nextOpen : firstOpenTaskIndex();

  if (!maybeShowDone()) renderCurrentTask();
}

function bindEvents() {
  $('creature-image-button').addEventListener('click', () => {
    $('image-modal-img').src = $('creature-image').src;
    $('image-modal-img').alt = $('creature-image').alt;
    $('image-modal').hidden = false;
  });

  $('image-modal-close').addEventListener('click', () => {
    $('image-modal').hidden = true;
  });

  $('image-modal').addEventListener('click', event => {
    if (event.target === $('image-modal')) {
      $('image-modal').hidden = true;
    }
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
      $('image-modal').hidden = true;
    }
  });

  $('previous').addEventListener('click', () => {
    if (currentIndex > 0) {
      currentIndex -= 1;
      renderCurrentTask();
    }
  });

  $('next').addEventListener('click', () => {
    if (currentIndex < STATE.tasks.length - 1) {
      currentIndex += 1;
      renderCurrentTask();
    }
  });

  $('task-select').addEventListener('change', event => {
    currentIndex = Number(event.target.value);
    renderCurrentTask();
  });

  $('grade').addEventListener('click', async () => {
    try {
      await saveAction('grade');
    } catch (error) {
      alert(error.message);
    }
  });

  $('skip').addEventListener('click', async () => {
    try {
      await saveAction('skip');
    } catch (error) {
      alert(error.message);
    }
  });

  $('review').addEventListener('click', () => {
    reviewMode = true;
    $('done').hidden = true;
    $('screen').hidden = false;
    currentIndex = 0;
    renderCurrentTask();
  });
}

async function init() {
  bindEvents();

  try {
    const response = await fetch('/api/tasks');

    if (!response.ok) {
      throw new Error(`Could not load scoring tasks: HTTP ${response.status}`);
    }

    STATE = await response.json();

    currentIndex = firstOpenTaskIndex();

    $('loading').hidden = true;
    $('done').hidden = true;
    $('screen').hidden = false;

    if (!maybeShowDone()) renderCurrentTask();
  } catch (error) {
    $('screen').hidden = true;
    $('done').hidden = true;
    $('loading').hidden = false;
    $('loading').innerHTML = `<p style="color:#b42318;font-weight:800">${escapeHtml(error.message)}</p>`;
  }
}

init();