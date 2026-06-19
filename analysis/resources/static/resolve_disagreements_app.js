let STATE = null;
let currentIndex = 0;
let selectedScore = null;
let activeTab = 'start';
let reviewMode = false;
let fullRubricRendered = false;
let saveInProgress = false;
let TASKS_BY_QUESTION = {};
let RUBRIC_ROWS_BY_KEY = {};
const COMPLETED_STATUSES = new Set(['resolved', 'flagged']);
const UI_STATE_STORAGE_KEY = 'resolveDisagreementsUiState:v1';
const $ = (id) => document.getElementById(id);

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'
  }[char]));
}

function lineBreaks(value) {
  return escapeHtml(value).replace(/\n/g, '<br>');
}

function clean(value) {
  return String(value ?? '').trim();
}

function requireElement(id) {
  const element = $(id);
  if (!element) throw new Error(`Expected element #${id} is missing.`);
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
  if (fillEl) fillEl.style.width = `${Math.max(8, Math.min(100, percent))}%`;
  if (detailEl) detailEl.textContent = detail;
}

function addLoadingLog(message) {
  const logEl = $('loading-log');
  if (!logEl) return;
  const item = document.createElement('li');
  item.textContent = message;
  logEl.appendChild(item);
}

function showLoadingError(error, stage) {
  const text = error && error.message ? error.message : String(error || 'Unknown error');
  setHidden('workspace', true);
  setHidden('done', true);
  setHidden('fatal', true);
  setHidden('loading', false);
  setLoading(`Could not load resolve-disagreements app at stage: ${stage}`, 100, text);
  addLoadingLog(`FAILED at ${stage}: ${text}`);
  const loading = $('loading');
  if (loading) loading.classList.add('loading-error');
}

function loadUiState() {
  try {
    const raw = window.localStorage.getItem(UI_STATE_STORAGE_KEY);
    return raw ? JSON.parse(raw) || {} : {};
  } catch (_error) {
    return {};
  }
}

function saveUiState(patch) {
  try {
    window.localStorage.setItem(UI_STATE_STORAGE_KEY, JSON.stringify({...loadUiState(), ...patch}));
  } catch (_error) {}
}

function normaliseImageUrl(rawPath) {
  const fileName = String(rawPath || '').split(/[\\/]/).pop();
  return fileName ? `/static/creatures/${encodeURIComponent(fileName)}` : '';
}

function isPlainObject(value) {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function normaliseRubricTokenSpacing(value, options = {}) {
  const collapse = Boolean(options.collapse);
  let text = String(value ?? '').replace(/\u00a0/g, ' ').trim();
  if (!text) return '';
  text = text.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
  if (collapse) text = text.replace(/\s+/g, ' ').trim();
  return text.trim();
}

function cobaltTokenHtml(value, options = {}) {
  return lineBreaks(normaliseRubricTokenSpacing(value, options)).replace(/\[(SRC|FAN)\]/g, '<span class="rubric-token-cobalt">[$1]</span>');
}

function contentRowsForRubric(content) {
  if (isPlainObject(content)) {
    const rows = Object.entries(content).map(([label, examples]) => [String(label || '').trim(), String(examples || '').trim()]);
    return rows.length ? rows : [['', '—']];
  }
  if (Array.isArray(content)) {
    const rows = [];
    for (const item of content) {
      if (isPlainObject(item)) {
        for (const [label, examples] of Object.entries(item)) rows.push([String(label || '').trim(), String(examples || '').trim()]);
      } else if (String(item || '').trim()) {
        rows.push(['', String(item || '').trim()]);
      }
    }
    return rows.length ? rows : [['', '—']];
  }
  return [['', String(content || '').trim() || '—']];
}

function rubricContentHtml(row) {
  if (!row) return '';
  if (row.html) return row.html;
  const score = String(row.score || '');
  const rows = contentRowsForRubric(row.content);
  if (rows.length === 1 && !rows[0][0] && rows[0][1] === '—') return '<p class="rubric-content-text">—</p>';
  return `
    <table class="rubric-inner-table generated-rubric-inner-table">
      <tbody>
        ${rows.map(([label, examples]) => `
          <tr>
            <td class="inner-label-${escapeHtml(score)}">${cobaltTokenHtml(label || '—', {collapse: true})}</td>
            <td class="inner-example">${cobaltTokenHtml(examples || '—')}</td>
          </tr>
        `).join('')}
      </tbody>
    </table>
  `;
}

function expandedRubricRows(table) {
  const rows = [];
  const explicitScoreOrder = Array.isArray(table.score_order) ? table.score_order.map(score => String(score)) : null;
  const appendScoreRows = (scoresByValue, baseRow) => {
    const sourceScores = Object.keys(scoresByValue);
    const scores = explicitScoreOrder
      ? [...explicitScoreOrder.filter(score => Object.prototype.hasOwnProperty.call(scoresByValue, score)), ...sourceScores.filter(score => !explicitScoreOrder.includes(score))]
      : sourceScores;
    for (const score of scores) rows.push({...baseRow, score, content: scoresByValue[score]});
  };
  if (isPlainObject(table.scores)) appendScoreRows(table.scores, {creature_id: table.creature_id || '', creature: table.creature || 'All creatures'});
  for (const entry of table.rows || []) {
    if (isPlainObject(entry.scores)) appendScoreRows(entry.scores, {creature_id: entry.creature_id || '', creature: entry.creature || '', note: entry.note || ''});
    else rows.push(entry);
  }
  return rows;
}

function prepareClientIndexes() {
  TASKS_BY_QUESTION = {};
  for (const [index, task] of STATE.tasks.entries()) {
    const questionKey = task.q_element || task.question_key || '';
    if (!TASKS_BY_QUESTION[questionKey]) TASKS_BY_QUESTION[questionKey] = [];
    TASKS_BY_QUESTION[questionKey].push({task, index});
  }
  RUBRIC_ROWS_BY_KEY = {};
  const tables = STATE.rubric.question_rubric_tables || {};
  const scoreOrder = ((STATE.rubric && STATE.rubric.score_scale) || [0, 1, 2]).slice().reverse();
  for (const [questionKey, table] of Object.entries(tables)) {
    for (const row of expandedRubricRows({...table, score_order: scoreOrder})) {
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

function getCreature(task) {
  return (STATE.rubric.creatures || {})[task.creature_id] || {name: task.creature || task.creature_id, facts: [], image: ''};
}

function getStatus(task) {
  return task.status || 'todo';
}

function statusClass(task) {
  const status = getStatus(task);
  if (status === 'resolved') return 'scored';
  if (status === 'flagged') return 'flagged';
  return 'todo';
}

function firstOpenTaskIndex() {
  const index = STATE.tasks.findIndex(task => !COMPLETED_STATUSES.has(getStatus(task)));
  return index === -1 ? 0 : index;
}

function restoredTaskIndex(savedState) {
  const savedTaskId = savedState && savedState.taskId;
  if (!savedTaskId) return firstOpenTaskIndex();
  const index = STATE.tasks.findIndex(task => task.task_id === savedTaskId);
  return index === -1 ? firstOpenTaskIndex() : index;
}

function progress() {
  const total = STATE.tasks.length;
  let resolved = 0;
  let flagged = 0;
  for (const task of STATE.tasks) {
    if (getStatus(task) === 'resolved') resolved += 1;
    if (getStatus(task) === 'flagged') flagged += 1;
  }
  return {total, resolved, flagged, to_do: Math.max(0, total - resolved - flagged)};
}

function blockProgress(questionKey) {
  const items = TASKS_BY_QUESTION[questionKey] || [];
  let resolved = 0;
  let flagged = 0;
  for (const {task} of items) {
    if (getStatus(task) === 'resolved') resolved += 1;
    if (getStatus(task) === 'flagged') flagged += 1;
  }
  return {total: items.length, resolved, flagged, left: Math.max(0, items.length - resolved - flagged)};
}

function switchTab(tabName) {
  activeTab = tabName;
  saveUiState({activeTab: tabName});
  document.querySelectorAll('.tab-button').forEach(button => button.classList.toggle('active', button.dataset.tab === tabName));
  document.querySelectorAll('.tab-panel').forEach(panel => panel.classList.toggle('active', panel.dataset.panel === tabName));
  if (tabName === 'overview') renderOverview();
  if (tabName === 'full-rubric') renderFullRubric();
}

function getRubricRows(task) {
  const lookupKey = `${task.q_element || task.question_key || ''}\u0000${task.creature_id || ''}`;
  if (RUBRIC_ROWS_BY_KEY[lookupKey] && RUBRIC_ROWS_BY_KEY[lookupKey].length) return RUBRIC_ROWS_BY_KEY[lookupKey];
  const defaultKey = `${task.q_element || task.question_key || ''}\u0000`;
  if (RUBRIC_ROWS_BY_KEY[defaultKey] && RUBRIC_ROWS_BY_KEY[defaultKey].length) return RUBRIC_ROWS_BY_KEY[defaultKey];
  return [2, 1, 0].map(score => ({score, content: 'Rubric row unavailable.'}));
}

function isNaRubricRow(row) {
  return String(row && row.content !== undefined ? row.content : '').trim().toUpperCase() === 'NA';
}

function sourcePillsForScore(task, score) {
  const matches = (task.sources || []).filter(source => String(source.score || '') === String(score));
  return `<div class="source-pill-wrap">${matches.map(source => `
    <span class="source-pill ${escapeHtml(source.kind)}" title="${escapeHtml(source.note || '')}">
      ${escapeHtml(source.display_label)}
      ${source.kind === 'genai' && source.confidence ? `<small>${escapeHtml(source.confidence)}%</small>` : ''}
    </span>
  `).join('')}</div>`;
}

function renderScoreOptions(task) {
  const rows = getRubricRows(task);
  const selectableScores = new Set(rows.filter(row => !isNaRubricRow(row)).map(row => String(row.score)));
  if (selectedScore !== null && !selectableScores.has(String(selectedScore))) selectedScore = null;
  requireElement('score-options').innerHTML = `
    <div class="retention-rubric-appendix">
      <table class="appendix-rubric-table score-table">
        <colgroup><col class="appendix-score-col"><col></colgroup>
        <thead><tr><th>Score</th><th>Possible answers and current source choices</th></tr></thead>
        <tbody>
          ${rows.map(row => {
            const score = String(row.score);
            const isDisabled = isNaRubricRow(row);
            const isSelected = !isDisabled && score === selectedScore;
            const hasMatch = (task.sources || []).some(source => String(source.score || '') === score);
            return `
              <tr class="appendix-rubric-score-row score-row-${escapeHtml(score)} rubric-score-row ${isSelected ? 'selected' : ''} ${isDisabled ? 'disabled' : ''} ${hasMatch ? '' : 'no-source-match'}" data-score="${escapeHtml(score)}" tabindex="${isDisabled ? '-1' : '0'}" aria-disabled="${isDisabled ? 'true' : 'false'}">
                <td class="appendix-score-cell score-bg-${escapeHtml(score)}"><span class="score-number">${escapeHtml(score)}</span></td>
                <td class="appendix-content-cell content-bg-${escapeHtml(score)}">${rubricContentHtml(row)}${sourcePillsForScore(task, score)}</td>
              </tr>
            `;
          }).join('')}
        </tbody>
      </table>
    </div>
  `;
  document.querySelectorAll('.rubric-score-row:not(.disabled)').forEach(row => {
    const selectRow = () => {
      selectedScore = row.dataset.score;
      document.querySelectorAll('.rubric-score-row').forEach(item => item.classList.remove('selected'));
      row.classList.add('selected');
      updateFinaliseButton();
    };
    row.addEventListener('click', selectRow);
    row.addEventListener('keydown', event => {
      if (event.key === 'Enter') {
        event.preventDefault();
        if (selectedScore === row.dataset.score) runAction('finalise');
        else selectRow();
      }
      if (event.key === ' ') {
        event.preventDefault();
        selectRow();
      }
    });
  });
}

function renderSourceJudgements(task) {
  return `
    <section class="source-judgements">
      <h2>Source judgements</h2>
      <table class="source-judgement-table">
        <thead><tr><th>Source</th><th>Score</th><th>Confidence / status</th><th>Note</th></tr></thead>
        <tbody>
          ${(task.sources || []).map(source => `
            <tr>
              <td><span class="source-pill ${escapeHtml(source.kind)}">${escapeHtml(source.display_label)}</span></td>
              <td>${escapeHtml(source.score || 'missing/invalid')}</td>
              <td>${escapeHtml(source.kind === 'genai' ? (source.confidence ? `${source.confidence}%` : '—') : (source.status || '—'))}</td>
              <td>${lineBreaks(source.note || '—')}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </section>
  `;
}

function renderLocalStatusStrip() {
  const offsets = [-2, -1, 0, 1, 2];
  const strip = requireElement('local-status-strip');
  strip.innerHTML = offsets.map(offset => {
    const index = currentIndex + offset;
    if (index < 0 || index >= STATE.tasks.length) return '<span class="status-square local empty" aria-hidden="true"></span>';
    const task = STATE.tasks[index];
    const hasNote = Boolean(clean(task.final_note_manual));
    const label = `Task ${index + 1}: ${getStatus(task)}${hasNote ? '; note present' : ''}`;
    return `<button class="status-square local ${statusClass(task)} ${hasNote ? 'has-note' : ''} ${offset === 0 ? 'current' : ''}" type="button" data-index="${index}" title="${escapeHtml(label)}" aria-label="${escapeHtml(label)}"></button>`;
  }).join('');
  strip.querySelectorAll('button[data-index]').forEach(button => {
    button.addEventListener('click', () => {
      currentIndex = Number(button.dataset.index);
      renderCurrentTask();
    });
  });
}

function updateProgress() {
  const p = progress();
  const task = STATE.tasks[currentIndex];
  if (task) saveUiState({taskId: task.task_id});
  const block = task ? blockProgress(task.q_element || task.question_key) : {left: 0, total: 0};
  requireElement('progress').innerHTML = `
    <div><strong>${p.total}</strong> unresolved task group(s) (<strong>${block.total}</strong> in this block)</div>
    <div><strong>${p.resolved}</strong> resolved · <strong>${p.flagged}</strong> flagged · <strong>${p.to_do}</strong> to do</div>
    <div><strong>${block.left}</strong> left in this block</div>
  `;
}

function maybeShowDone() {
  const p = progress();
  if (p.total > 0 && p.to_do === 0 && !reviewMode) {
    setHidden('workspace', true);
    setHidden('done', false);
    return true;
  }
  return false;
}

function updateFinaliseButton() {
  const note = clean(requireElement('note').value);
  requireElement('finalise').disabled = selectedScore === null || !note;
}

function renderCurrentTask() {
  if (!STATE.tasks.length) {
    setHidden('workspace', true);
    setHidden('done', false);
    const done = requireElement('done');
    done.querySelector('h1').textContent = 'No remaining disagreements';
    done.querySelector('p').textContent = 'The startup pass left no unresolved conflict task groups.';
    return;
  }
  const task = STATE.tasks[currentIndex];
  const creature = getCreature(task);
  const rubric = (STATE.rubric.rubrics || {})[task.q_element || task.question_key] || {title: task.question_label};
  const taskFinalScore = String(task.final_score || '');
  selectedScore = /^[0-2]$/.test(taskFinalScore) ? taskFinalScore : null;
  
  setText('resolver-label', `Task group ${currentIndex + 1} of ${STATE.tasks.length} · ${task.row_count} TSV row(s)`);
  setText('creature-name', creature.name || task.creature || task.creature_id);
  const image = requireElement('creature-image');
  image.src = normaliseImageUrl(creature.image);
  image.alt = creature.name || task.creature || task.creature_id || 'Creature image';
  const modalImage = requireElement('image-modal-img');
  modalImage.src = image.src;
  modalImage.alt = `Enlarged ${image.alt}`;
  setText('creature-meta', [creature.chapter, creature.environment, creature.appearance].filter(Boolean).join(' · '));
  requireElement('creature-facts').innerHTML = (creature.facts || []).map(fact => `<li>${escapeHtml(fact)}</li>`).join('') || '<li>No creature facts configured.</li>';
  setText('rubric-key', getQuestionShortLabel(task.q_element || task.question_key));
  setText('rubric-title', rubric.title || task.question_label || getQuestionShortLabel(task.q_element || task.question_key));
  renderScoreOptions(task);

  requireElement('note').value = task.final_note_manual || '';
  requireElement('note').oninput = updateFinaliseButton;
  requireElement('previous').disabled = currentIndex === 0;
  requireElement('next').disabled = currentIndex === STATE.tasks.length - 1;
  requireElement('finalise').textContent = task.status === 'resolved' ? 'Update final score' : 'Finalise score';
  updateFinaliseButton();

  document.querySelector('.answer-card').innerHTML = `
    <h2>Question</h2>
    <p id="question">${escapeHtml(task.question_label || getQuestionShortLabel(task.q_element || task.question_key))}</p>
    <h2>Standardised answer to adjudicate</h2>
    <p id="answer">${escapeHtml(task.answer_std || task.answer || '')}</p>
    <div class="answer-meta-grid">
      <div><strong>TSV rows affected</strong><span class="row-list">${escapeHtml((task.row_numbers || []).join(', '))}</span></div>
      <div><strong>Moment / MCID example</strong>${escapeHtml([task.moment, task.MCID].filter(Boolean).join(' · ') || '—')}</div>
      <div><strong>Current final status</strong>${escapeHtml(task.final_status || task.status || 'todo')}</div>
    </div>
    <p class="final-note-auto"><strong>Current final_note_auto:</strong> ${escapeHtml(task.final_note_auto || '—')}</p>
    ${renderSourceJudgements(task)}
  `;
  renderLocalStatusStrip();
  updateProgress();
  if (activeTab === 'overview') renderOverview();
}

function nextIndexAfter(index) {
  if (!STATE.tasks.length) return 0;
  return Math.min(index + 1, STATE.tasks.length - 1);
}

async function saveAction(action) {
  const savedIndex = currentIndex;
  const task = STATE.tasks[savedIndex];
  const note = clean(requireElement('note').value);

  if (action === 'finalise' && !note) {
    alert('Write a final manual note before finalising the score.');
    return;
  }

  const response = await fetch('/api/resolve', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    cache: 'no-store',
    body: JSON.stringify({
      task_id: task.task_id,
      action,
      score: action === 'finalise' ? selectedScore : '',
      note,
    }),
  });

  const result = await response.json();
  if (!result.ok) throw new Error(result.error || 'Could not save resolution.');

  STATE.tasks[savedIndex] = result.task;

  // Rebuild question/block indexes because TASKS_BY_QUESTION stores task
  // object references. Without this, block progress and overview squares
  // keep reading the pre-save task object.
  prepareClientIndexes();

  currentIndex = nextIndexAfter(savedIndex);

  renderCurrentTask();
  renderOverview();
}

async function runAction(action) {
  if (saveInProgress) return;
  if (action === 'finalise' && selectedScore === null) return;
  saveInProgress = true;
  try {
    await saveAction(action);
  } catch (error) {
    alert(error.message);
  } finally {
    saveInProgress = false;
  }
}

function overviewCreatureOrder(items) {
  const seen = new Map();
  for (const {task} of items) {
    const id = task.creature_id || task.creature || '';
    if (!seen.has(id)) seen.set(id, task.creature || id);
  }
  return [...seen.entries()].sort((a, b) => String(a[1]).localeCompare(String(b[1]))).map(([id]) => id);
}

function overviewCreatureLabel(creatureId, items) {
  const task = (items || []).find(item => (item.task.creature_id || item.task.creature || '') === creatureId)?.task;
  const creature = task ? getCreature(task) : null;
  return (creature && creature.name) || (task && task.creature) || creatureId || 'Unknown creature';
}

function createOverviewButton(task, index) {
  const hasNote = Boolean(clean(task.final_note_manual));
  const label = `Task ${index + 1}: ${getStatus(task)}; ${task.row_count || 1} TSV row(s)${hasNote ? '; note present' : ''}`;
  return `<button class="status-square ${statusClass(task)} ${hasNote ? 'has-note' : ''}" type="button" data-index="${index}" title="${escapeHtml(label)}" aria-label="${escapeHtml(label)}"></button>`;
}

function renderOverview() {
  const container = requireElement('overview-grid');
  const questionOrder = STATE.questionOrder || Object.keys(TASKS_BY_QUESTION);
  container.innerHTML = questionOrder.filter(questionKey => (TASKS_BY_QUESTION[questionKey] || []).length).map(questionKey => {
    const items = TASKS_BY_QUESTION[questionKey] || [];
    const creatureOrder = overviewCreatureOrder(items);
    const block = blockProgress(questionKey);
    return `
      <section class="overview-question-block">
        <h3 class="overview-question-label">${escapeHtml(getQuestionShortLabel(questionKey))} · ${block.total.toLocaleString('en-GB')} task group(s) · ${block.left.toLocaleString('en-GB')} to do</h3>
        <div class="overview-creature-grid">
          ${creatureOrder.map(creatureId => {
            const creatureItems = items.filter(item => (item.task.creature_id || item.task.creature || '') === creatureId);
            return `
              <section class="overview-creature-block">
                <div class="overview-creature-header">${escapeHtml(overviewCreatureLabel(creatureId, items))} · ${creatureItems.length.toLocaleString('en-GB')}</div>
                <div class="overview-square-row">
                  ${creatureItems.map(({task, index}) => createOverviewButton(task, index)).join('')}
                </div>
              </section>
            `;
          }).join('')}
        </div>
      </section>
    `;
  }).join('') || '<p class="small">No task groups are available.</p>';
}

function groupRowsByCreature(rows, table) {
  const sourceRows = {};
  for (const row of (table.rows || [])) {
    const key = row.creature_id || row.creature || '';
    if (key) sourceRows[key] = row;
  }
  const groups = [];
  for (const row of rows || []) {
    const creatureKey = row.creature_id || row.creature || '';
    let group = groups[groups.length - 1];
    if (!group || group.key !== creatureKey) {
      const source = sourceRows[creatureKey] || {};
      group = {key: creatureKey, creature: row.creature || '', note: source.note || source.rubric_note || '—', rows: []};
      groups.push(group);
    }
    group.rows.push(row);
  }
  return groups;
}

function groupedFullRubricRowsHtml(rows) {
  return (rows || []).map(row => {
    const score = String(row.score || '');
    return `
      <tr class="appendix-rubric-score-row score-row-${escapeHtml(score)}">
        <td class="appendix-score-cell score-bg-${escapeHtml(score)}"><span class="score-number">${escapeHtml(score || '—')}</span></td>
        <td class="appendix-content-cell content-bg-${escapeHtml(score)}">${rubricContentHtml(row)}</td>
      </tr>
    `;
  }).join('');
}

function renderRubricTableHtml(rows) {
  return `
    <table class="appendix-rubric-table">
      <colgroup><col class="appendix-score-col"><col></colgroup>
      <thead><tr><th>Score</th><th>Possible answers</th></tr></thead>
      <tbody>${groupedFullRubricRowsHtml(rows)}</tbody>
    </table>
  `;
}

function renderCreatureRubricBlock(group) {
  return `
    <section class="appendix-rubric-block">
      <h3>${escapeHtml(group.creature || 'Creature')}</h3>
      <p class="rubric-note-line">Note: ${cobaltTokenHtml(group.note || '—')}</p>
      ${renderRubricTableHtml(group.rows || [])}
    </section>
  `;
}

function renderQuestionRubricSection(questionKey, table) {
  const scoreScale = ((STATE.rubric && STATE.rubric.score_scale) || [0, 1, 2]).slice().reverse();
  const rows = expandedRubricRows({...table, score_order: scoreScale});
  const groups = groupRowsByCreature(rows, table);
  return `
    <section class="appendix-question-section" data-question="${escapeHtml(questionKey)}">
      <h2>${escapeHtml(table.title || table.short_title || getQuestionShortLabel(questionKey))}</h2>
      ${table.intro ? `<p>${cobaltTokenHtml(table.intro)}</p>` : ''}
      ${groups.length ? groups.map(renderCreatureRubricBlock).join('') : '<p class="small">No creature-specific rubric rows are configured for this question element.</p>'}
    </section>
  `;
}

function activateRubricSubtab(container, questionKey) {
  container.querySelectorAll('.rubric-subtab-button').forEach(item => item.classList.toggle('active', item.dataset.rubricQuestion === questionKey));
  container.querySelectorAll('.rubric-subtab-panel').forEach(panel => panel.classList.toggle('active', panel.dataset.rubricQuestionPanel === questionKey));
  saveUiState({rubricQuestion: questionKey});
}

function renderFullRubric() {
  if (fullRubricRendered) return;
  const container = requireElement('full-rubric-content');
  const tables = STATE.rubric.question_rubric_tables || {};
  const questionOrder = STATE.questionOrder || Object.keys(tables);
  const availableQuestions = questionOrder.filter(questionKey => tables[questionKey]);
  const saved = loadUiState().rubricQuestion;
  const firstQuestion = availableQuestions.includes(saved) ? saved : availableQuestions[0];
  if (!firstQuestion) {
    container.innerHTML = '<p class="small">No rubric tables are configured.</p>';
    fullRubricRendered = true;
    return;
  }
  container.innerHTML = `
    <article class="retention-rubric-appendix">
      <h1>Materials: Retention Scoring Rubrics</h1>
      <hr class="appendix-title-rule">
      <div class="rubric-subtabs" role="tablist" aria-label="Rubric question tabs">
        ${availableQuestions.map(questionKey => `<button class="rubric-subtab-button ${questionKey === firstQuestion ? 'active' : ''}" type="button" data-rubric-question="${escapeHtml(questionKey)}">${escapeHtml(getQuestionShortLabel(questionKey))}</button>`).join('')}
      </div>
      <div class="rubric-subtab-panels">
        ${availableQuestions.map(questionKey => `<section class="rubric-subtab-panel ${questionKey === firstQuestion ? 'active' : ''}" data-rubric-question-panel="${escapeHtml(questionKey)}">${renderQuestionRubricSection(questionKey, tables[questionKey])}</section>`).join('')}
      </div>
    </article>
  `;
  container.querySelectorAll('.rubric-subtab-button').forEach(button => button.addEventListener('click', () => activateRubricSubtab(container, button.dataset.rubricQuestion)));
  fullRubricRendered = true;
}

function bindEvents() {
  document.querySelectorAll('.tab-button').forEach(button => button.addEventListener('click', () => switchTab(button.dataset.tab)));
  requireElement('resolve-rest').addEventListener('click', () => {
    reviewMode = false;
    switchTab('score');
    currentIndex = firstOpenTaskIndex();
    renderCurrentTask();
  });
  requireElement('previous').addEventListener('click', () => { if (currentIndex > 0) { currentIndex -= 1; renderCurrentTask(); } });
  requireElement('next').addEventListener('click', () => { if (currentIndex < STATE.tasks.length - 1) { currentIndex += 1; renderCurrentTask(); } });
  requireElement('finalise').addEventListener('click', () => runAction('finalise'));
  requireElement('flag').addEventListener('click', () => runAction('flag'));
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
  requireElement('creature-image-button').addEventListener('click', () => {
    requireElement('image-modal-img').src = requireElement('creature-image').src;
    requireElement('image-modal-img').alt = requireElement('creature-image').alt;
    requireElement('image-modal').hidden = false;
  });
  requireElement('image-modal-close').addEventListener('click', () => { requireElement('image-modal').hidden = true; });
  requireElement('image-modal').addEventListener('click', event => { if (event.target === requireElement('image-modal')) requireElement('image-modal').hidden = true; });
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && $('image-modal')) $('image-modal').hidden = true;
    if (event.key === 'Enter' && activeTab === 'score' && selectedScore !== null && event.target.tagName.toLowerCase() !== 'textarea') {
      event.preventDefault();
      runAction('finalise');
    }
  });
}

async function init() {
  let stage = 'binding controls';
  try {
    setLoading('Preparing resolve controls.', 16, 'Checking that the HTML and JavaScript match.');
    addLoadingLog('Preparing controls.');
    bindEvents();

    stage = 'fetching /api/tasks';
    setLoading('Waiting for task payload from server.', 38, 'The server may still be applying the startup auto-resolution pass.');
    addLoadingLog('Requesting /api/tasks.');
    const response = await fetch(`/api/tasks?ts=${Date.now()}`, {cache: 'no-store'});
    if (!response.ok) throw new Error(`Could not load tasks: HTTP ${response.status}`);

    stage = 'parsing /api/tasks JSON';
    const rawPayload = await response.text();
    setLoading('Parsing task payload.', 70, `${rawPayload.length.toLocaleString('en-GB')} byte(s) received.`);
    STATE = JSON.parse(rawPayload);

    if (!STATE.ok) {
      setHidden('loading', true);
      setHidden('fatal', false);
      setText('fatal-message', STATE.error || 'Unknown startup error.');
      return;
    }

    stage = 'indexing task payload';
    STATE.tasks = STATE.tasks || [];
    STATE.rubric = STATE.rubric || {};
    prepareClientIndexes();
    const saved = loadUiState();
    currentIndex = restoredTaskIndex(saved);

    setText('input-path-label', STATE.input_path || './data/retention_scores_merged.tsv');
    setText('auto-majority-rows', (STATE.autoSummary.majority_rows || 0).toLocaleString('en-GB'));
    setText('auto-human-rows', (STATE.autoSummary.human_agreement_rows || 0).toLocaleString('en-GB'));
    setText('remaining-task-groups', (STATE.tasks.length || 0).toLocaleString('en-GB'));
    requireElement('instructions-content').innerHTML = STATE.rubric.instructions_html || '';

    setLoading('Ready.', 100, `${STATE.tasks.length.toLocaleString('en-GB')} task group(s) loaded.`);
    setHidden('loading', true);
    setHidden('workspace', false);
    setHidden('done', true);
    switchTab(saved.activeTab && document.querySelector(`.tab-button[data-tab="${saved.activeTab}"]`) ? saved.activeTab : 'start');
    if (STATE.tasks.length) renderCurrentTask();
    else maybeShowDone();
  } catch (error) {
    showLoadingError(error, stage);
  }
}

window.addEventListener('error', event => { if (!STATE) showLoadingError(event.error || event.message, 'browser runtime error'); });
window.addEventListener('unhandledrejection', event => { if (!STATE) showLoadingError(event.reason || 'Unhandled promise rejection', 'browser promise rejection'); });

if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init, {once: true});
else init();
