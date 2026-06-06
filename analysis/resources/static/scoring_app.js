let STATE = null;
let currentIndex = 0;
let selectedScore = null;
let reviewMode = false;
let activeTab = 'instructions';
let fullRubricRendered = false;
let overviewRenderToken = 0;
let waitTimer = null;
let initStarted = false;
let saveInProgress = false;
const ACTION_FLASH_MS = 170;
let TASKS_BY_QUESTION = {};
let RUBRIC_ROWS_BY_KEY = {};
const UI_STATE_STORAGE_KEY = 'retentionScoringUiState:v1';
const SCROLL_STATE_SAVE_DELAY_MS = 120;
let scrollSaveTimer = null;
let allowScrollStateSaving = false;

const $ = (id) => document.getElementById(id);
const COMPLETED_STATUSES = new Set(['graded', 'skipped', 'flagged']);
const OVERVIEW_CHUNK_SIZE = 500;

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  }[char]));
}

function lineBreaks(value) {
  return escapeHtml(value).replace(/\n/g, '<br>');
}

function loadUiState() {
  try {
    const rawState = window.localStorage.getItem(UI_STATE_STORAGE_KEY);
    return rawState ? JSON.parse(rawState) || {} : {};
  } catch (_error) {
    return {};
  }
}

function saveUiState(patch) {
  try {
    const currentState = loadUiState();
    window.localStorage.setItem(UI_STATE_STORAGE_KEY, JSON.stringify({
      ...currentState,
      ...patch
    }));
  } catch (_error) {
    // Ignore storage errors: the scoring UI should still work if localStorage is unavailable.
  }
}

function saveScrollPosition() {
  saveUiState({
    scrollX: window.scrollX,
    scrollY: window.scrollY
  });
}

function scheduleSaveScrollPosition() {
  if (!allowScrollStateSaving) return;

  if (scrollSaveTimer) {
    window.clearTimeout(scrollSaveTimer);
  }

  scrollSaveTimer = window.setTimeout(() => {
    scrollSaveTimer = null;
    saveScrollPosition();
  }, SCROLL_STATE_SAVE_DELAY_MS);
}

function restoreScrollPosition(savedState) {
  const scrollX = Number(savedState && savedState.scrollX);
  const scrollY = Number(savedState && savedState.scrollY);
  const hasSavedScroll = Number.isFinite(scrollX) && Number.isFinite(scrollY);

  allowScrollStateSaving = false;

  window.requestAnimationFrame(() => {
    window.requestAnimationFrame(() => {
      if (hasSavedScroll) {
        window.scrollTo(scrollX, scrollY);
      }

      window.setTimeout(() => {
        allowScrollStateSaving = true;
        saveScrollPosition();
      }, 100);
    });
  });
}

function isPlainObject(value) {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function cobaltTokenHtml(value) {
  return lineBreaks(value).replace(/\[(SRC|FAN)\]/g, '<span class="rubric-token-cobalt">[$1]</span>');
}

function rubricContentHtml(row) {
  if (!row) return '';

  // Backward-compatible fallback for old local rubric files. New bundled rubrics
  // use structured `content` rather than pre-rendered mini-table HTML.
  if (row.html) return row.html;

  const note = row.note ? `<p class="rubric-content-title">${escapeHtml(row.note)}</p>` : '';
  const content = row.content;
  let contentHtml = '';

  if (isPlainObject(content)) {
    const entries = Object.entries(content);
    if (entries.length) {
      contentHtml = `
        <table class="rubric-inner-table generated-rubric-inner-table">
          <tbody>
            ${entries.map(([left, right]) => `
              <tr>
                <td>${lineBreaks(left)}</td>
                <td>${cobaltTokenHtml(right)}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      `;
    }
  } else if (Array.isArray(content)) {
    const items = content
      .map(item => {
        if (isPlainObject(item)) {
          return Object.entries(item).map(([left, right]) => `${left}: ${right}`).join('\n');
        }
        return item;
      })
      .map(item => String(item ?? '').trim())
      .filter(Boolean);

    if (items.length) {
      contentHtml = `
        <ul class="rubric-content-list">
          ${items.map(item => `<li>${cobaltTokenHtml(item)}</li>`).join('')}
        </ul>
      `;
    }
  } else if (content) {
    contentHtml = `<p class="rubric-content-text">${lineBreaks(content)}</p>`;
  }

  return note || contentHtml ? `${note}${contentHtml}` : lineBreaks(row.note || '');
}


function decorateRubricScoreBadges(root) {
  if (!root) return;

  root.querySelectorAll('.rubric-base-table td.c12').forEach(cell => {
    const scoreText = cell.textContent.trim();
    if (!/^[0-4]$/.test(scoreText)) return;
    if (cell.querySelector('.score-number')) return;

    cell.innerHTML = `<span class="score-number">${escapeHtml(scoreText)}</span>`;
  });
}

function scoreShortcutAllowed(target) {
  if (!target) return true;
  const tagName = target.tagName ? target.tagName.toLowerCase() : '';
  if (tagName === 'textarea' || tagName === 'input' || tagName === 'select') return false;
  if (target.isContentEditable) return false;
  return true;
}

function requireElement(id) {
  const element = $(id);
  if (!element) throw new Error(`Expected element #${id} is missing. The HTML and JavaScript are out of sync; restart the scoring server and hard-refresh the browser.`);
  return element;
}

function setHidden(id, hidden) {
  const element = $(id);
  if (element) element.hidden = hidden;
}

function setText(id, value) {
  const element = $(id);
  if (element) element.textContent = value;
}

function setLoading(message, percent, detail = '') {
  const messageEl = $('loading-message');
  const fillEl = $('loading-bar-fill');
  const detailEl = $('loading-detail');
  if (messageEl) messageEl.textContent = message;
  if (!messageEl && $('loading')) $('loading').textContent = detail ? `${message} ${detail}` : message;
  if (fillEl) fillEl.style.width = `${Math.max(8, Math.min(100, percent))}%`;
  if (detailEl && detail) detailEl.textContent = detail;
}

function addLoadingLog(message) {
  const logEl = $('loading-log');
  if (!logEl) return;
  const item = document.createElement('li');
  item.textContent = message;
  logEl.appendChild(item);
}

function startWaitingDetail(stage, detail) {
  stopWaitingDetail();
  const started = Date.now();
  waitTimer = window.setInterval(() => {
    const elapsed = Math.round((Date.now() - started) / 1000);
    setLoading(stage, 45, `${detail} Waiting ${elapsed}s. If this keeps increasing, check the terminal: the server now prints every 100 tasks while it works.`);
  }, 1000);
}

function stopWaitingDetail() {
  if (waitTimer) {
    window.clearInterval(waitTimer);
    waitTimer = null;
  }
}

function showLoadingError(error, stage) {
  stopWaitingDetail();
  const text = error && error.message ? error.message : String(error || 'Unknown error');
  setHidden('workspace', true);
  setHidden('done', true);
  setHidden('loading', false);
  setLoading(`Could not load scoring app at stage: ${stage}`, 100, text);
  addLoadingLog(`FAILED at ${stage}: ${text}`);
  const loading = $('loading');
  if (loading) loading.classList.add('loading-error');
}

function normaliseImageUrl(rawPath) {
  const fileName = String(rawPath || '').split(/[\\/]/).pop();
  return fileName ? `/static/creatures/${encodeURIComponent(fileName)}` : '';
}

function prepareClientIndexes() {
  TASKS_BY_QUESTION = {};
  for (const [index, task] of STATE.tasks.entries()) {
    const questionKey = task.question_key || '';
    if (!TASKS_BY_QUESTION[questionKey]) TASKS_BY_QUESTION[questionKey] = [];
    TASKS_BY_QUESTION[questionKey].push({task, index});
  }

  RUBRIC_ROWS_BY_KEY = {};
  const tables = STATE.rubric.question_rubric_tables || {};
  for (const [questionKey, table] of Object.entries(tables)) {
    for (const row of expandedRubricRows(table)) {
      const lookupKey = `${questionKey}\u0000${row.creature_id || ''}`;
      if (!RUBRIC_ROWS_BY_KEY[lookupKey]) RUBRIC_ROWS_BY_KEY[lookupKey] = [];
      RUBRIC_ROWS_BY_KEY[lookupKey].push(row);
    }
  }
}

function getQuestionShortLabel(questionKey) {
  return (STATE.rubric.question_short_labels || {})[questionKey]
    || (STATE.questionLabels || {})[questionKey]
    || questionKey;
}

function expandedRubricRows(table) {
  const rows = [];
  const scoreOrder = ((STATE && STATE.rubric && STATE.rubric.score_scale) || [0, 1, 2, 3, 4]).map(score => String(score));

  const appendScoreRows = (scoresByValue, baseRow) => {
    const remainingScores = Object.keys(scoresByValue).filter(score => !scoreOrder.includes(score));
    const scores = [
      ...scoreOrder.filter(score => Object.prototype.hasOwnProperty.call(scoresByValue, score)),
      ...remainingScores
    ];

    for (const score of scores) {
      rows.push({
        ...baseRow,
        score,
        content: scoresByValue[score]
      });
    }
  };

  if (isPlainObject(table.scores)) {
    appendScoreRows(table.scores, {
      creature_id: table.creature_id || '',
      creature: table.creature || 'All creatures'
    });
  }

  for (const entry of table.rows || []) {
    if (isPlainObject(entry.scores)) {
      appendScoreRows(entry.scores, {
        creature_id: entry.creature_id || '',
        creature: entry.creature || ''
      });
    } else {
      rows.push(entry);
    }
  }

  return rows;
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

function statusForTask(task) {
  const row = getScore(task);
  if (row.status === 'graded') return 'scored';
  if (row.status === 'flagged') return 'flagged';
  return 'todo';
}

function progress() {
  const total = STATE.tasks.length;
  let graded = 0;
  let skipped = 0;
  let flagged = 0;

  for (const task of STATE.tasks) {
    const row = getScore(task);
    if (row.status === 'graded') graded += 1;
    if (row.status === 'skipped') skipped += 1;
    if (row.status === 'flagged') flagged += 1;
  }

  return {
    total,
    graded,
    skipped,
    flagged,
    to_be_graded: Math.max(0, total - graded - skipped - flagged)
  };
}

function blockProgress(questionKey) {
  const items = TASKS_BY_QUESTION[questionKey] || [];
  let graded = 0;
  let skipped = 0;
  let flagged = 0;

  for (const {task} of items) {
    const row = getScore(task);
    if (row.status === 'graded') graded += 1;
    if (row.status === 'skipped') skipped += 1;
    if (row.status === 'flagged') flagged += 1;
  }

  return {
    total: items.length,
    graded,
    skipped,
    flagged,
    left: Math.max(0, items.length - graded - skipped - flagged)
  };
}

function firstOpenTaskIndex() {
  const index = STATE.tasks.findIndex(task => !COMPLETED_STATUSES.has(getScore(task).status));
  return index === -1 ? 0 : index;
}

function restoredTaskIndex(savedState) {
  const savedTaskId = savedState && savedState.taskId;
  if (!savedTaskId) return firstOpenTaskIndex();

  const index = STATE.tasks.findIndex(task => task.task_id === savedTaskId);
  return index === -1 ? firstOpenTaskIndex() : index;
}

function restoredTab(savedState) {
  const candidate = savedState && savedState.activeTab;
  return document.querySelector(`.tab-button[data-tab="${candidate}"]`) ? candidate : 'instructions';
}

function restoredRubricQuestion(questionOrder, savedState) {
  const candidate = savedState && savedState.rubricQuestion;
  return questionOrder.includes(candidate) ? candidate : questionOrder[0];
}

function switchTab(tabName) {
  activeTab = tabName;
  saveUiState({activeTab: tabName});

  document.querySelectorAll('.tab-button').forEach(button => {
    button.classList.toggle('active', button.dataset.tab === tabName);
  });
  document.querySelectorAll('.tab-panel').forEach(panel => {
    panel.classList.toggle('active', panel.dataset.panel === tabName);
  });

  if (tabName === 'overview') renderOverview();
  if (tabName === 'full-rubric') renderFullRubric();
}

function updateProgress() {
  const p = progress();
  const task = STATE.tasks[currentIndex];
  saveUiState({taskId: task.task_id});

  const block = task ? blockProgress(task.question_key) : {left: 0, total: 0};

  requireElement('progress').innerHTML = `
    <div><strong>${p.total}</strong> review tasks (<strong>${block.total}</strong> in this block)</div>
    <div><strong>${p.graded}</strong> graded · <strong>${p.skipped}</strong> skipped · <strong>${p.flagged}</strong> flagged · <strong>${p.to_be_graded}</strong> to do</div>
    <div><strong>${block.left}</strong> left in this block</div>
  `;
}

function maybeShowDone() {
  const p = progress();
  if (p.total > 0 && p.to_be_graded === 0 && !reviewMode) {
    setHidden('workspace', true);
    setHidden('done', false);
    return true;
  }
  return false;
}

function getRubricRows(task) {
  const lookupKey = `${task.question_key || ''}\u0000${task.creature_id || ''}`;
  const rows = RUBRIC_ROWS_BY_KEY[lookupKey];
  if (rows && rows.length) return rows;

  const questionDefaultKey = `${task.question_key || ''}\u0000`;
  const questionDefaultRows = RUBRIC_ROWS_BY_KEY[questionDefaultKey];
  if (questionDefaultRows && questionDefaultRows.length) return questionDefaultRows;

  return (STATE.rubric.score_scale || [0, 1, 2, 3, 4]).map(score => ({
    score,
    note: ''
  }));
}

function isNaRubricRow(row) {
  return String(row && row.content !== undefined ? row.content : '').trim().toUpperCase() === 'NA';
}

function renderScoreOptions(task) {
  const rows = getRubricRows(task);
  const selectableScores = new Set(
    rows
      .filter(row => !isNaRubricRow(row))
      .map(row => String(row.score))
  );

  if (selectedScore !== null && !selectableScores.has(String(selectedScore))) {
    selectedScore = null;
  }

  requireElement('score-options').innerHTML = `
    <table class="score-table">
      <thead>
        <tr><th>Score</th><th>Label / Note</th></tr>
      </thead>
      <tbody>
        ${rows.map(row => {
          const score = String(row.score);
          const isDisabled = isNaRubricRow(row);
          const isSelected = !isDisabled && score === selectedScore;
          return `
            <tr class="rubric-score-row ${isSelected ? 'selected' : ''} ${isDisabled ? 'disabled' : ''}" data-score="${escapeHtml(score)}" tabindex="${isDisabled ? '-1' : '0'}" aria-disabled="${isDisabled ? 'true' : 'false'}">
              <td><span class="score-number">${escapeHtml(score)}</span></td>
              <td class="rubric-note">${rubricContentHtml(row)}</td>
            </tr>
          `;
        }).join('')}
      </tbody>
    </table>
  `;

  document.querySelectorAll('.rubric-score-row:not(.disabled)').forEach(row => {
    const selectRow = () => {
      selectedScore = row.dataset.score;
      document.querySelectorAll('.rubric-score-row').forEach(item => item.classList.remove('selected'));
      row.classList.add('selected');
      requireElement('grade').disabled = false;
    };
    row.addEventListener('click', selectRow);
    row.addEventListener('keydown', event => {
      if (event.key === 'Enter') {
        event.preventDefault();
        event.stopPropagation();
        if (selectedScore === row.dataset.score) {
          runScoreAction('grade');
        } else {
          selectRow();
        }
      }
      if (event.key === ' ') {
        event.preventDefault();
        selectRow();
      }
    });
  });
}

function renderLocalStatusStrip() {
  const offsets = [-2, -1, 0, 1];
  const strip = requireElement('local-status-strip');
  strip.innerHTML = offsets.map(offset => {
    const index = currentIndex + offset;
    if (index < 0 || index >= STATE.tasks.length) {
      return '<span class="status-square local empty" aria-hidden="true"></span>';
    }
    const task = STATE.tasks[index];
    const saved = getScore(task);
    const status = statusForTask(task);
    const hasNote = Boolean((saved.note || '').trim());
    const labelBase = saved.status === 'graded'
      ? `Answer ${index + 1}: scored ${saved.score}`
      : `Answer ${index + 1}: ${saved.status || 'todo'}`;
    const label = hasNote ? `${labelBase}; note present` : labelBase;
    return `<button class="status-square local ${status} ${hasNote ? 'has-note' : ''} ${offset === 0 ? 'current' : ''}" type="button" data-index="${index}" title="${escapeHtml(label)}" aria-label="${escapeHtml(label)}"></button>`;
  }).join('');

  strip.querySelectorAll('button[data-index]').forEach(button => {
    button.addEventListener('click', () => {
      currentIndex = Number(button.dataset.index);
      renderCurrentTask();
    });
  });
}

function renderCurrentTask() {
  if (!STATE.tasks.length) {
    setHidden('loading', true);
    setHidden('workspace', true);
    setHidden('done', false);
    const done = requireElement('done');
    done.querySelector('h1').textContent = 'No review tasks to grade';
    done.querySelector('p').textContent = 'No eligible retention review tasks were found for this grader.';
    return;
  }

  const task = STATE.tasks[currentIndex];
  const creature = getCreature(task);
  const saved = getScore(task);
  const rubric = (STATE.rubric.rubrics || {})[task.question_key] || {
    title: task.question_label
  };

  selectedScore = saved.status === 'graded' ? String(saved.score) : null;

  setText('grader-label', `Grader ${STATE.grader} · answer ${currentIndex + 1} of ${STATE.tasks.length}`);
  setText('creature-name', creature.name || task.creature_name);

  const image = requireElement('creature-image');
  image.src = normaliseImageUrl(creature.image);
  image.alt = creature.name || task.creature_name;
  const modalImage = requireElement('image-modal-img');
  modalImage.src = image.src;
  modalImage.alt = `Enlarged ${creature.name || task.creature_name}`;

  setText('creature-meta', [creature.chapter, creature.environment, creature.appearance].filter(Boolean).join(' · '));
  requireElement('creature-facts').innerHTML = (creature.facts || []).map(fact => `<li>${escapeHtml(fact)}</li>`).join('')
    || '<li>Placeholder: add creature facts in resources/retention_rubrics.json.</li>';

  setText('rubric-key', getQuestionShortLabel(task.question_key));
  setText('rubric-title', rubric.title || task.question_label);
  renderScoreOptions(task);

  const answerCard = document.querySelector('.answer-card');
  const comparisonHtml = saved.status ? `
    <section class="post-save-comparison">
      <h2>GenAI comparison</h2>
      <p class="small">Shown only after this item has been saved by you.</p>
      <dl class="task-meta-list">
        <dt>GenAI score</dt><dd>${escapeHtml(task.genai_score || '')}</dd>
        <dt>GenAI confidence</dt><dd>${escapeHtml(task.genai_confidence ? `${task.genai_confidence}%` : '')}</dd>
        <dt>GenAI note</dt><dd>${escapeHtml(task.genai_note || '') || '—'}</dd>
      </dl>
    </section>
  ` : '';
  answerCard.innerHTML = `
    <h2>Question</h2>
    <p id="question">${escapeHtml(task.question_label || '')}</p>
    <h2>Standardised answer to score</h2>
    <p id="answer">${escapeHtml(task.answer_std || task.answer || '')}</p>
    ${comparisonHtml}
  `;
  requireElement('note').value = saved.note || '';
  requireElement('grade').disabled = selectedScore === null;
  requireElement('previous').disabled = currentIndex === 0;
  requireElement('next').disabled = currentIndex === STATE.tasks.length - 1;

  renderLocalStatusStrip();
  updateProgress();
  if (activeTab === 'overview') renderOverview();
}

function createOverviewButton(task, index, questionKey) {
  const saved = getScore(task);
  const status = statusForTask(task);
  const hasNote = Boolean((saved.note || '').trim());
  const labelBase = saved.status === 'graded'
    ? `${getQuestionShortLabel(questionKey)} answer ${index + 1}: scored ${saved.score}`
    : `${getQuestionShortLabel(questionKey)} answer ${index + 1}: ${saved.status || 'todo'}`;
  const label = hasNote ? `${labelBase}; note present` : labelBase;

  const button = document.createElement('button');
  button.type = 'button';
  button.className = `status-square ${status} ${hasNote ? 'has-note' : ''} ${index === currentIndex ? 'current' : ''}`;
  button.dataset.index = String(index);
  button.title = label;
  button.setAttribute('aria-label', label);
  return button;
}

function overviewCreatureOrder(items) {
  const seen = new Set();
  const ordered = [];

  for (const creatureId of Object.keys(STATE.rubric.creatures || {})) {
    seen.add(creatureId);
    ordered.push(creatureId);
  }

  for (const {task} of items) {
    const creatureId = task.creature_id || '';
    if (!seen.has(creatureId)) {
      seen.add(creatureId);
      ordered.push(creatureId);
    }
  }

  return ordered;
}

function groupOverviewItemsByCreature(items) {
  const groups = {};
  for (const item of items) {
    const creatureId = item.task.creature_id || '';
    if (!groups[creatureId]) groups[creatureId] = [];
    groups[creatureId].push(item);
  }
  return groups;
}

function overviewCreatureLabel(creatureId, items) {
  const creature = (STATE.rubric.creatures || {})[creatureId];
  if (creature && creature.name) return creature.name;
  const firstTask = items && items[0] && items[0].task;
  return firstTask ? (firstTask.creature_name || creatureId || 'Unknown creature') : (creatureId || 'Unknown creature');
}

function renderOverview() {
  if (!STATE) return;
  const token = ++overviewRenderToken;
  const questionOrder = STATE.questionOrder || ['img1', 'img2', 'name1', 'name2'];
  const container = requireElement('overview-grid');
  container.innerHTML = '<p id="overview-build-status" class="small">Preparing overview squares…</p><div class="overview-table" id="overview-table"></div>';
  const table = requireElement('overview-table');
  const status = requireElement('overview-build-status');

  let questionPosition = 0;
  let rendered = 0;
  const total = questionOrder.reduce((sum, questionKey) => sum + (TASKS_BY_QUESTION[questionKey] || []).length, 0);

  function renderNextQuestion() {
    if (token !== overviewRenderToken) return;
    if (questionPosition >= questionOrder.length) {
      status.textContent = `Overview ready: ${rendered.toLocaleString('en-GB')} square(s).`;
      return;
    }

    const questionKey = questionOrder[questionPosition];
    questionPosition += 1;
    const items = TASKS_BY_QUESTION[questionKey] || [];
    const block = blockProgress(questionKey);
    const groupsByCreature = groupOverviewItemsByCreature(items);
    const creatureOrder = overviewCreatureOrder(items).filter(creatureId => (groupsByCreature[creatureId] || []).length);

    const section = document.createElement('section');
    section.className = 'overview-question-block';
    table.appendChild(section);

    const label = document.createElement('div');
    label.className = 'overview-question-label';
    label.textContent = `${getQuestionShortLabel(questionKey)} · ${block.total.toLocaleString('en-GB')} review tasks · ${creatureOrder.length.toLocaleString('en-GB')} creatures`;
    section.appendChild(label);

    const creatureGrid = document.createElement('div');
    creatureGrid.className = 'overview-creature-grid';
    section.appendChild(creatureGrid);

    if (questionPosition < questionOrder.length) {
      const divider = document.createElement('hr');
      divider.className = 'overview-block-divider';
      table.appendChild(divider);
    }

    let creaturePosition = 0;

    function renderNextCreature() {
      if (token !== overviewRenderToken) return;
      if (creaturePosition >= creatureOrder.length) {
        window.requestAnimationFrame(renderNextQuestion);
        return;
      }

      const creatureId = creatureOrder[creaturePosition];
      creaturePosition += 1;
      const creatureItems = groupsByCreature[creatureId] || [];

      const creatureBlock = document.createElement('section');
      creatureBlock.className = 'overview-creature-block';
      creatureGrid.appendChild(creatureBlock);

      const creatureHeader = document.createElement('div');
      creatureHeader.className = 'overview-creature-header';
      creatureHeader.textContent = `${overviewCreatureLabel(creatureId, creatureItems)} · ${creatureItems.length.toLocaleString('en-GB')}`;
      creatureBlock.appendChild(creatureHeader);

      const row = document.createElement('div');
      row.className = 'overview-square-row overview-creature-square-row';
      row.setAttribute('aria-label', `${getQuestionShortLabel(questionKey)} · ${overviewCreatureLabel(creatureId, creatureItems)}`);
      creatureBlock.appendChild(row);

      let offset = 0;

      function renderChunk() {
        if (token !== overviewRenderToken) return;
        const fragment = document.createDocumentFragment();
        const startOffset = offset;
        const end = Math.min(offset + OVERVIEW_CHUNK_SIZE, creatureItems.length);

        for (; offset < end; offset += 1) {
          const {task, index} = creatureItems[offset];
          fragment.appendChild(createOverviewButton(task, index, questionKey));
        }

        row.appendChild(fragment);
        rendered += end - startOffset;
        status.textContent = `Preparing overview squares: ${Math.min(rendered, total).toLocaleString('en-GB')} / ${total.toLocaleString('en-GB')}.`;

        if (offset < creatureItems.length) {
          window.requestAnimationFrame(renderChunk);
        } else {
          window.requestAnimationFrame(renderNextCreature);
        }
      }

      renderChunk();
    }

    renderNextCreature();
  }

  renderNextQuestion();
}

function bindRubricSubtabs(container) {
  container.querySelectorAll('.rubric-subtab-button').forEach(button => {
    button.addEventListener('click', () => {
      activateRubricSubtab(container, button.dataset.rubricQuestion);
    });
  });
}

function activateRubricSubtab(container, questionKey, {persist = true} = {}) {
  if (!questionKey) return;

  container.querySelectorAll('.rubric-subtab-button').forEach(item => {
    const isActive = item.dataset.rubricQuestion === questionKey;
    item.classList.toggle('active', isActive);
    item.setAttribute('aria-selected', isActive ? 'true' : 'false');
  });

  container.querySelectorAll('.rubric-subtab-panel').forEach(panel => {
    panel.classList.toggle('active', panel.dataset.rubricQuestionPanel === questionKey);
  });

  if (persist) {
    saveUiState({rubricQuestion: questionKey});
  }
}

function groupedFullRubricRowsHtml(rows) {
  const groups = [];

  for (const row of rows || []) {
    const creatureKey = row.creature_id || row.creature || '';
    let group = groups[groups.length - 1];

    if (!group || group.key !== creatureKey) {
      group = {
        key: creatureKey,
        creature: row.creature || '',
        rows: []
      };
      groups.push(group);
    }

    group.rows.push(row);
  }

  return groups.map(group => {
    const rowSpan = Math.max(1, group.rows.length);
    return group.rows.map((row, rowIndex) => `
      <tr>
        ${rowIndex === 0 ? `<td rowspan="${rowSpan}">${escapeHtml(group.creature)}</td>` : ''}
        <td><span class="score-number">${escapeHtml(row.score)}</span></td>
        <td class="rubric-note">${rubricContentHtml(row)}</td>
      </tr>
    `).join('');
  }).join('');
}

function renderFullRubric() {
  if (fullRubricRendered) return;

  const container = requireElement('full-rubric-content');
  const tables = STATE.rubric.question_rubric_tables || {};
  const questionOrder = STATE.questionOrder || Object.keys(Object.keys(tables).length ? tables : (STATE.rubric.question_rubric_html || {}));

  if (Object.keys(tables).length) {
    const availableQuestions = questionOrder.filter(questionKey => tables[questionKey]);
    const firstQuestion = restoredRubricQuestion(availableQuestions, loadUiState());

    if (!firstQuestion) {
      container.innerHTML = '<p class="small">No rubric tables are configured yet.</p>';
      fullRubricRendered = true;
      return;
    }

    container.innerHTML = `
      <div class="rubric-subtabs" role="tablist" aria-label="Rubric question tabs">
        ${availableQuestions.map(questionKey => `
          <button class="rubric-subtab-button ${questionKey === firstQuestion ? 'active' : ''}" type="button" role="tab" data-rubric-question="${escapeHtml(questionKey)}" aria-selected="${questionKey === firstQuestion ? 'true' : 'false'}">
            ${escapeHtml(getQuestionShortLabel(questionKey))}
          </button>
        `).join('')}
      </div>
      <div class="rubric-subtab-panels">
        ${availableQuestions.map(questionKey => {
          const table = tables[questionKey];
          return `
            <section class="rubric-subtab-panel ${questionKey === firstQuestion ? 'active' : ''}" role="tabpanel" data-rubric-question-panel="${escapeHtml(questionKey)}">
              <section class="full-rubric-section">
                <h3>${escapeHtml(table.short_title || getQuestionShortLabel(questionKey))}</h3>
                <p class="small">${escapeHtml(table.title || '')}</p>
                ${table.intro ? `<p>${escapeHtml(table.intro)}</p>` : ''}
                <table class="full-rubric-table">
                  <colgroup>
                    <col class="full-rubric-creature-col">
                    <col class="full-rubric-score-col">
                    <col>
                  </colgroup>
                  <thead>
                    <tr><th>Creature</th><th>Score</th><th>Label / Content</th></tr>
                  </thead>
                  <tbody>
                    ${groupedFullRubricRowsHtml(expandedRubricRows(table))}
                  </tbody>
                </table>
              </section>
            </section>
          `;
        }).join('')}
      </div>
    `;

    bindRubricSubtabs(container);
    activateRubricSubtab(container, firstQuestion, {persist: false});
    fullRubricRendered = true;
    return;
  }

  const questionHtml = STATE.rubric.question_rubric_html || {};

  if (questionOrder.length && Object.keys(questionHtml).length) {
    const firstQuestion = restoredRubricQuestion(questionOrder, loadUiState());
    container.innerHTML = `
      <div class="rubric-subtabs" role="tablist" aria-label="Rubric question tabs">
        ${questionOrder.map(questionKey => `
          <button class="rubric-subtab-button ${questionKey === firstQuestion ? 'active' : ''}" type="button" role="tab" data-rubric-question="${escapeHtml(questionKey)}" aria-selected="${questionKey === firstQuestion ? 'true' : 'false'}">
            ${escapeHtml(getQuestionShortLabel(questionKey))}
          </button>
        `).join('')}
      </div>
      <div class="rubric-subtab-panels">
        ${questionOrder.map(questionKey => `
          <section class="rubric-subtab-panel ${questionKey === firstQuestion ? 'active' : ''}" role="tabpanel" data-rubric-question-panel="${escapeHtml(questionKey)}">
            ${questionHtml[questionKey] || '<p class="small">No rubric is configured for this question yet.</p>'}
          </section>
        `).join('')}
      </div>
    `;

    decorateRubricScoreBadges(container);

    bindRubricSubtabs(container);
    activateRubricSubtab(container, firstQuestion, {persist: false});

    fullRubricRendered = true;
    return;
  }

  if (STATE.rubric.full_rubric_html) {
    container.innerHTML = STATE.rubric.full_rubric_html;
    decorateRubricScoreBadges(container);
    fullRubricRendered = true;
    return;
  }

  container.innerHTML = '<p class="small">No rubric tables are configured yet.</p>';
  fullRubricRendered = true;
}


function flashAnswerCard(action) {
  const card = document.querySelector('.answer-card');
  if (!card) return Promise.resolve();

  card.classList.remove('action-feedback-grade', 'action-feedback-skip', 'action-feedback-flag');
  card.classList.add('action-feedback', `action-feedback-${action}`);

  return new Promise(resolve => {
    window.setTimeout(() => {
      card.classList.remove('action-feedback', `action-feedback-${action}`);
      resolve();
    }, ACTION_FLASH_MS);
  });
}

async function saveAction(action) {
  const task = STATE.tasks[currentIndex];

  const response = await fetch('/api/score', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    cache: 'no-store',
    body: JSON.stringify({
      task_id: task.task_id,
      action,
      score: action === 'grade' ? selectedScore : '',
      note: requireElement('note').value
    })
  });

  const result = await response.json();
  if (!result.ok) throw new Error(result.error || 'Could not save score.');

  STATE.scores[task.task_id] = result.score;
  await flashAnswerCard(action);

  const nextOpen = STATE.tasks.findIndex((candidate, index) => {
    return index > currentIndex && !COMPLETED_STATUSES.has(getScore(candidate).status);
  });

  currentIndex = nextOpen !== -1 ? nextOpen : firstOpenTaskIndex();

  if (!maybeShowDone()) renderCurrentTask();
}

async function runScoreAction(action) {
  if (saveInProgress) return;
  if (action === 'grade' && selectedScore === null) return;

  saveInProgress = true;
  try {
    await saveAction(action);
  } catch (error) {
    alert(error.message);
  } finally {
    saveInProgress = false;
  }
}

function bindEvents() {
  document.querySelectorAll('.tab-button').forEach(button => {
    button.addEventListener('click', () => switchTab(button.dataset.tab));
  });

  window.addEventListener('scroll', scheduleSaveScrollPosition, {passive: true});

  requireElement('creature-image-button').addEventListener('click', () => {
    requireElement('image-modal-img').src = requireElement('creature-image').src;
    requireElement('image-modal-img').alt = requireElement('creature-image').alt;
    requireElement('image-modal').hidden = false;
  });

  requireElement('image-modal-close').addEventListener('click', () => {
    requireElement('image-modal').hidden = true;
  });

  requireElement('image-modal').addEventListener('click', event => {
    if (event.target === requireElement('image-modal')) {
      requireElement('image-modal').hidden = true;
    }
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && $('image-modal')) {
      $('image-modal').hidden = true;
    }

    if (event.key === 'Enter'
      && activeTab === 'score'
      && selectedScore !== null
      && scoreShortcutAllowed(event.target)
      && !event.ctrlKey
      && !event.metaKey
      && !event.altKey
      && !event.shiftKey) {
      event.preventDefault();
      runScoreAction('grade');
    }
  });

  requireElement('previous').addEventListener('click', () => {
    if (currentIndex > 0) {
      currentIndex -= 1;
      renderCurrentTask();
    }
  });

  requireElement('next').addEventListener('click', () => {
    if (currentIndex < STATE.tasks.length - 1) {
      currentIndex += 1;
      renderCurrentTask();
    }
  });

  requireElement('grade').addEventListener('click', () => runScoreAction('grade'));
  requireElement('skip').addEventListener('click', () => runScoreAction('skip'));
  requireElement('flag').addEventListener('click', () => runScoreAction('flag'));

  requireElement('review').addEventListener('click', () => {
    reviewMode = true;
    setHidden('done', true);
    setHidden('workspace', false);
    currentIndex = 0;
    switchTab('score');
    renderCurrentTask();
  });

  requireElement('overview-grid').addEventListener('click', event => {
    const button = event.target.closest('button[data-index]');
    if (!button) return;
    currentIndex = Number(button.dataset.index);
    reviewMode = true;
    switchTab('score');
    renderCurrentTask();
  });
}

async function init() {
  if (initStarted) return;
  initStarted = true;
  let stage = 'binding interface controls';

  try {
    setLoading('Preparing scoring controls.', 14, 'Checking that the HTML and JavaScript match.');
    addLoadingLog('Preparing scoring controls.');
    bindEvents();

    stage = 'fetching /api/tasks';
    setLoading('Waiting for task payload from server.', 34, 'The server may still be building or sending tasks. Watch the terminal for task batch messages.');
    addLoadingLog('Requesting /api/tasks.');
    startWaitingDetail('Waiting for task payload from server.', 'The browser is still waiting for /api/tasks.');
    const response = await fetch(`/api/tasks?ts=${Date.now()}`, {cache: 'no-store'});
    stopWaitingDetail();

    if (!response.ok) {
      throw new Error(`Could not load scoring tasks: HTTP ${response.status}`);
    }

    stage = 'reading /api/tasks response';
    setLoading('Task payload arrived.', 58, 'Reading response body.');
    addLoadingLog('Task payload arrived; reading body.');
    const rawPayload = await response.text();

    stage = 'parsing /api/tasks JSON';
    setLoading('Parsing answers and rubric data.', 70, `${rawPayload.length.toLocaleString('en-GB')} byte(s) received.`);
    addLoadingLog(`Parsing JSON payload (${rawPayload.length.toLocaleString('en-GB')} bytes).`);
    STATE = JSON.parse(rawPayload);

    stage = 'validating task payload';
    if (!STATE || !Array.isArray(STATE.tasks)) {
      throw new Error('The /api/tasks response did not contain a tasks array.');
    }
    STATE.scores = STATE.scores || {};
    STATE.rubric = STATE.rubric || {};

    stage = 'indexing task payload';
    setLoading('Indexing tasks for fast scoring.', 82, `${STATE.tasks.length.toLocaleString('en-GB')} task(s) loaded.`);
    addLoadingLog(`Indexing ${STATE.tasks.length.toLocaleString('en-GB')} task(s).`);
    prepareClientIndexes();
    const savedUiState = loadUiState();
    currentIndex = restoredTaskIndex(savedUiState);

    stage = 'rendering first scoring screen';
    setLoading('Preparing first screen.', 92, 'Instructions open first; scoring, overview, and full rubric are ready from the tabs.')
    requireElement('instructions-content').innerHTML = STATE.rubric.instructions_html || '<h1>Instructions</h1><p>No instruction text is configured yet.</p>';

    setLoading('Ready.', 100, `${STATE.tasks.length.toLocaleString('en-GB')} task(s) loaded.`);
    addLoadingLog('Scoring interface ready.');
    setHidden('loading', true);
    setHidden('done', true);
    setHidden('workspace', false);

    switchTab(restoredTab(savedUiState));
    renderCurrentTask();
    restoreScrollPosition(savedUiState);
  } catch (error) {
    showLoadingError(error, stage);
  }
}

window.addEventListener('error', event => {
  if (!STATE) {
    showLoadingError(event.error || event.message, 'browser runtime error');
  }
});

window.addEventListener('unhandledrejection', event => {
  if (!STATE) {
    showLoadingError(event.reason || 'Unhandled promise rejection', 'browser promise rejection');
  }
});

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init, {once: true});
} else {
  init();
}
