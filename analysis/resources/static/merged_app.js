const REPORT = JSON.parse(document.getElementById('report-data').textContent);
function escapeHtml(value){return String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[c]));}
function formatValue(value, header={}){ if(value===null||value===undefined||value==='') return '—'; if(header.fixed&&typeof value==='number') return value.toFixed(2); if(header.html) return String(value??''); if(Array.isArray(value)) return escapeHtml(value.join(', ')); if(typeof value==='boolean') return value?'true':'false'; return escapeHtml(value); }
function statusText(ok, okText, badText){ return ok ? `<span class="status-ok">${escapeHtml(okText)}</span>` : `<span class="status-bad">${escapeHtml(badText)}</span>`; }
function table(headers, rows, options={}){
  const extraClass = options.className ? ` ${escapeHtml(options.className)}` : '';
  const stickyClass = options.stickyHeader ? ' sticky-header' : '';
  const wrapperClass = `table-wrap${extraClass}${stickyClass}`;
  const head = `<tr>${headers.map(h => `<th${h.num ? ' class="num"' : ''}>${escapeHtml(h.label)}</th>`).join('')}</tr>`;
  const body = rows.map(row => {
    const rowClass = row.__summary ? ' class="summary-row"' : '';
    return `<tr${rowClass}>${headers.map(h => `<td${h.num ? ' class="num"' : ''}>${formatValue(row[h.key], h)}</td>`).join('')}</tr>`;
  }).join('');
  return `<div class="${wrapperClass}"><table><thead>${head}</thead><tbody>${body || `<tr><td colspan="${headers.length}">No rows.</td></tr>`}</tbody></table></div>`;
}
function metric(label,value,note=''){ return `<div class="metric"><div class="label">${escapeHtml(label)}</div><div class="value">${formatValue(value)}</div><div class="small">${escapeHtml(note)}</div></div>`; }
function numericValues(rows,key){ return rows.map(row=>Number(row[key])).filter(value=>Number.isFinite(value)); }
function mean(values){ return values.length ? values.reduce((total,value)=>total+value,0)/values.length : null; }
function formatDuration(seconds){ if(!Number.isFinite(seconds)) return ''; const rounded=Math.round(seconds); const hours=Math.floor(rounded/3600); const minutes=Math.floor((rounded%3600)/60); const remainingSeconds=rounded%60; if(hours) return `${hours}h ${minutes}m ${remainingSeconds}s`; if(minutes) return `${minutes}m ${remainingSeconds}s`; return `${remainingSeconds}s`; }
function renderLegend(){ document.getElementById('condition-legend').innerHTML=Object.entries(REPORT.condition_colours).map(([label,colour])=>`<span class="legend-item"><span class="swatch" style="background:${escapeHtml(colour)}"></span>${escapeHtml(label)}</span>`).join(''); }

function detailsBlock(title, innerHtml, open=false){
  return `<details class="summary-details"${open ? ' open' : ''}><summary>${escapeHtml(title)}</summary><div class="details-body">${innerHtml}</div></details>`;
}

function statsRowsByModel(rows, text){
  return (rows || []).filter(row => String(row.model || '').includes(text));
}

function statsRowsByHypothesis(rows, hypothesis){
  return (rows || []).filter(row => String(row.hypothesis || '') === hypothesis);
}

function renderStatsResultTable(rows){
  return table([
    {key:'hypothesis', label:'Hypothesis'},
    {key:'model', label:'Model'},
    {key:'outcome', label:'Outcome'},
    {key:'n', label:'n', num:true},
    {key:'term', label:'Term'},
    {key:'b_display', label:'b', num:true},
    {key:'se_hc3_display', label:'HC3 SE', num:true},
    {key:'ci_95', label:'95% CI'},
    {key:'planned_contrast_estimate_display', label:'Planned contrast', num:true},
    {key:'planned_contrast_ci_95', label:'Contrast 95% CI'},
    {key:'p_display', label:'p'},
    {key:'p_holm_display', label:'Holm p'},
    {key:'std_beta_display', label:'std. beta', num:true},
    {key:'partial_r2_display', label:'partial r²', num:true},
    {key:'note', label:'Interpretation note'}
  ], rows || [], {className:'stats-table'});
}

function renderModelAudit(models){
  return table([
    {key:'model', label:'Model'},
    {key:'formula', label:'Formula'},
    {key:'n', label:'n', num:true},
    {key:'df_residual', label:'df residual', num:true},
    {key:'r2', label:'R²', num:true},
    {key:'status', label:'Status'},
    {key:'omitted_covariates', label:'Omitted covariates'}
  ], models || [], {className:'stats-model-table'});
}

function renderIndirectTable(result){
  if (!result) return '<p class="small">No model result.</p>';
  const note = `<p class="small">Status: ${escapeHtml(result.status || 'OK')} · complete-case n = ${escapeHtml(result.n ?? '0')}</p>`;
  return note + table([
    {key:'contrast', label:'Contrast'},
    {key:'mediator', label:'Mediator'},
    {key:'effect', label:'Indirect effect', num:true},
    {key:'boot_se', label:'Boot SE', num:true},
    {key:'boot_ci_95', label:'Bootstrap 95% CI'},
    {key:'bootstrap_samples', label:'Bootstrap samples', num:true},
    {key:'focal', label:'Focal'}
  ], result.indirect_rows || [], {className:'stats-table'});
}

function renderDirectMediationTable(result){
  if (!result) return '';
  return table([
    {key:'contrast', label:'Contrast'},
    {key:'direct_b', label:'Direct b', num:true}
  ], result.direct_rows || [], {className:'stats-table'});
}

function flattenSerialRows(results){
  return (results || []).flatMap(result => (result.rows || []).map(row => ({...row, n: result.n, status: result.status || 'OK'})));
}

function renderSerialTable(results){
  return table([
    {key:'contrast', label:'Contrast'},
    {key:'path', label:'Path'},
    {key:'n', label:'n', num:true},
    {key:'effect', label:'Serial indirect effect', num:true},
    {key:'boot_se', label:'Boot SE', num:true},
    {key:'boot_ci_95', label:'Bootstrap 95% CI'},
    {key:'bootstrap_samples', label:'Bootstrap samples', num:true},
    {key:'status', label:'Status'}
  ], flattenSerialRows(results), {className:'stats-table'});
}

function renderFactorAnalyses(prefix){
  const rows = (REPORT.statistics.factor_analyses || []).filter(row => String(row.title || '').startsWith(prefix));
  const summary = table([
    {key:'title', label:'Scale check'},
    {key:'n_complete', label:'complete n', num:true},
    {key:'items', label:'items', num:true},
    {key:'alpha', label:'Cronbach α', num:true},
    {key:'first_eigenvalue', label:'first eigenvalue', num:true},
    {key:'first_factor_variance_percent', label:'first-factor %', num:true},
    {key:'loading_range', label:'loading range'},
    {key:'status', label:'Status'}
  ], rows, {className:'stats-table'});
  const loadingDetails = rows.map(row => detailsBlock(
    `${row.title}: item loadings`,
    table([{key:'item', label:'Item'}, {key:'loading', label:'Loading', num:true}], row.loadings || [], {className:'stats-table'})
  )).join('');
  return detailsBlock(`${prefix} factor-analysis / scale checks`, summary + loadingDetails);
}

function renderPowerRows(power){
  const rows = REPORT.condition_order.map(condition => ({
    condition,
    planned: power.planned_per_condition,
    current: (power.current_by_condition || {})[condition] || 0,
    delayed: (power.current_delayed_by_condition || {})[condition] || 0
  }));
  rows.push({condition:'Total', planned:power.planned_total, current:power.current_total, delayed:power.current_delayed_total});
  return rows;
}

function openFigureModal(figure){
  const modal = document.getElementById('figure-modal');
  const content = document.getElementById('figure-modal-content');
  if (!modal || !content || !figure) return;

  content.innerHTML = figure.outerHTML;
  modal.hidden = false;
}

function closeFigureModal(){
  const modal = document.getElementById('figure-modal');
  const content = document.getElementById('figure-modal-content');
  if (!modal || !content) return;

  modal.hidden = true;
  content.innerHTML = '';
}

function bindFigureZoom(){
  document.addEventListener('click', event => {
    const modal = document.getElementById('figure-modal');
    const closeButton = event.target.closest('#figure-modal-close');
    const figure = event.target.closest('.standalone-figure');

    if (closeButton) {
      closeFigureModal();
      return;
    }

    if (modal && !modal.hidden && event.target === modal) {
      closeFigureModal();
      return;
    }

    if (figure && !event.target.closest('#figure-modal')) {
      openFigureModal(figure);
    }
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') closeFigureModal();
  });
}

function filteredRows(rows,filters){ return rows.filter(row=>Object.entries(filters).every(([key,value])=>!value||String(row[key]??'')===value)); }
function filterControlsHtml(rows,filterKeys){ return `<div class="filter-row">${filterKeys.map(filter=>{ const values=[...new Set(rows.map(row=>String(row[filter.key]??'')).filter(Boolean))].sort(); return `<label>${escapeHtml(filter.label)}<select data-filter-key="${escapeHtml(filter.key)}"><option value="">All</option>${values.map(value=>`<option value="${escapeHtml(value)}">${escapeHtml(value)}</option>`).join('')}</select></label>`; }).join('')}</div>`; }
function renderFilterableTable(containerId,headers,rows,filterKeys,options={}){ const container=document.getElementById(containerId); const filters={}; container.innerHTML=`${filterControlsHtml(rows,filterKeys)}<div id="${containerId}-table"></div>`; function update(){ const scoped=filteredRows(rows,filters); const displayedRows=options.summaryRow?[...scoped,options.summaryRow(scoped)]:scoped; document.getElementById(`${containerId}-table`).innerHTML=table(headers,displayedRows,options); } container.querySelectorAll('select[data-filter-key]').forEach(select=>{ select.addEventListener('change',()=>{ filters[select.dataset.filterKey]=select.value; update(); }); }); update(); }
function participantSummaryRow(rows){ const ages=numericValues(rows,'age'); const experimentDurations=numericValues(rows,'experiment_duration_seconds'); const delayedDurations=numericValues(rows,'delayed_duration_seconds'); const scores=numericValues(rows,'logs_creature_score_of_18'); return {__summary:true,participant_id:'Mean / total',condition:`${rows.length} participant(s)`,age:mean(ages)===null?'':mean(ages).toFixed(2),gender:'',started:'',finished:'',experiment_duration:formatDuration(mean(experimentDurations)),completed_delayed_retention_tick:rows.filter(row=>row.completed_delayed_retention_tick).length,delayed_duration:formatDuration(mean(delayedDurations)),logs_creature_score_label:mean(scores)===null?'':`${mean(scores).toFixed(2)}/18`}; }
function countBy(rows,key){ const counts={}; for(const row of rows){ const value=row[key]||'Unknown / missing'; counts[value]=(counts[value]||0)+1; } return counts; }
function groupedBarSvg(categories, groups, valuesByGroupAndCategory){
  const width = 820;
  const height = 430;
  const margin = {top: 68, right: 24, bottom: 104, left: 52};
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const maxCount = Math.max(1, ...groups.flatMap(group => categories.map(category => valuesByGroupAndCategory[group]?.[category] || 0)));
  const categoryWidth = innerWidth / Math.max(1, categories.length);
  const barWidth = Math.max(2, (categoryWidth - 8) / Math.max(1, groups.length));

  const legend = groups.map((group, index) => {
    const x = margin.left + (index % 4) * 170;
    const y = 18 + Math.floor(index / 4) * 20;
    const colour = REPORT.condition_colours[group] || '#111827';
    return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="${escapeHtml(colour)}" stroke="rgba(0,0,0,.25)"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(group)}</text></g>`;
  }).join('');

  const bars = categories.map((category, categoryIndex) => groups.map((group, groupIndex) => {
    const count = valuesByGroupAndCategory[group]?.[category] || 0;
    const barHeight = innerHeight * count / maxCount;
    const x = margin.left + categoryIndex * categoryWidth + 4 + groupIndex * barWidth;
    const y = margin.top + innerHeight - barHeight;
    const colour = REPORT.condition_colours[group] || '#111827';
    const labelY = Math.max(margin.top + 11, y - 5);
    return `<g>
      <rect x="${x}" y="${y}" width="${Math.max(1, barWidth - 1)}" height="${barHeight}" fill="${escapeHtml(colour)}">
        <title>${escapeHtml(group)} | ${escapeHtml(category)}: ${count}</title>
      </rect>
      <text x="${x + Math.max(1, barWidth - 1) / 2}" y="${labelY}" text-anchor="middle" font-size="10">${count}</text>
    </g>`;
  }).join('')).join('');

  const labels = categories.map((category, index) => {
    const x = margin.left + index * categoryWidth + categoryWidth / 2;
    return `<text x="${x}" y="${height - 48}" text-anchor="end" transform="rotate(-35 ${x} ${height - 48})" font-size="11">${escapeHtml(category)}</text>`;
  }).join('');

  const yTicks = [];
  const tickStep = Math.max(1, Math.ceil(maxCount / 5));
  for (let value = 0; value <= maxCount; value += tickStep) {
    const y = margin.top + innerHeight - (innerHeight * value / maxCount);
    yTicks.push(`<g><line x1="${margin.left - 4}" x2="${margin.left + innerWidth}" y1="${y}" y2="${y}" stroke="#d9e0e4"></line><text x="${margin.left - 8}" y="${y + 4}" text-anchor="end" font-size="11">${value}</text></g>`);
  }

  return `<svg class="standalone-figure" viewBox="0 0 ${width} ${height}" role="img">
    <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
    ${legend}
    ${yTicks.join('')}
    <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    ${bars}
    ${labels}
  </svg>`;
}
function ageYearBucket(row){ const age=Number(row.age); if(!Number.isFinite(age)) return 'NA'; if(age>=30) return '30+'; if(age>=16&&age<=29) return String(age); return 'NA'; }
function valuesByGroup(rows,groups,categories,categoriseRow){ const output={}; for(const group of groups){ const scoped=group==='Overall'?rows:rows.filter(row=>row.condition===group); output[group]=Object.fromEntries(categories.map(category=>[category,0])); for(const row of scoped){ const category=categoriseRow(row); if(category in output[group]) output[group][category]+=1; } } return output; }
function renderDistributionCharts(){ const groups=[...REPORT.condition_order,'Overall']; document.getElementById('condition-distribution-chart').innerHTML=groupedBarSvg(['Participants'],groups,Object.fromEntries(groups.map(group=>[group,{Participants:group==='Overall'?REPORT.participants.length:REPORT.participants.filter(row=>row.condition===group).length}]))); const ageCategories=['16','17','18','19','20','21','22','23','24','25','26','27','28','29','30+','NA']; document.getElementById('age-distribution-chart').innerHTML=groupedBarSvg(ageCategories,groups,valuesByGroup(REPORT.participants,groups,ageCategories,ageYearBucket)); const genderCategories=['Male','Female','Other','Unknown / missing']; document.getElementById('gender-distribution-chart').innerHTML=groupedBarSvg(genderCategories,groups,valuesByGroup(REPORT.participants,groups,genderCategories,row=>row.gender||'Unknown / missing')); }
function quantile(sortedValues,probability){ if(!sortedValues.length) return null; const index=(sortedValues.length-1)*probability; const lower=Math.floor(index); const upper=Math.ceil(index); if(lower===upper) return sortedValues[lower]; return sortedValues[lower]+(sortedValues[upper]-sortedValues[lower])*(index-lower); }
function boxStats(values){ const sorted=values.filter(value=>Number.isFinite(value)).sort((a,b)=>a-b); if(!sorted.length) return null; return {min:sorted[0],q1:quantile(sorted,0.25),median:quantile(sorted,0.5),q3:quantile(sorted,0.75),max:sorted[sorted.length-1],n:sorted.length}; }
function renderBoxplot(containerId, metricKeys, options = {}) {
  const container = document.getElementById(containerId);
  const rows = options.rows || REPORT.participants;
  const conditionGroups = options.includeOverall === false
    ? [...REPORT.condition_order]
    : [...REPORT.condition_order, 'Overall'];

  const groupByMetric = options.groupBy === 'metric';

  const width = 920;
  const height = 440;
  const margin = {top: 74, right: 24, bottom: 92, left: 58};
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;

  const allValues = metricKeys.flatMap(metric => numericValues(rows, metric.key));
  const observedMax = Math.max(...allValues);
  const isEngagementPlot = metricKeys.length === 1 && metricKeys[0].key === 'eng_main';

  const minValue = options.minValue ?? (isEngagementPlot ? 1 : 0);
  const maxValue = options.maxValue ?? (
    Number.isFinite(observedMax)
      ? Math.ceil(Math.max(isEngagementPlot ? 7 : 10, observedMax))
      : (isEngagementPlot ? 7 : 10)
  );

  const scaleY = value =>
    margin.top + innerHeight - ((value - minValue) / Math.max(0.001, maxValue - minValue)) * innerHeight;

  const xCategories = groupByMetric ? metricKeys : conditionGroups;
  const nestedGroups = groupByMetric ? conditionGroups : metricKeys;

  const slotWidth = innerWidth / Math.max(1, xCategories.length);
  const boxWidth = Math.min(46, Math.max(18, slotWidth / (nestedGroups.length + 1)));
  const tickStep = maxValue > 20 ? Math.max(1, Math.ceil((maxValue - minValue) / 8 / 10) * 10) : 1;

  const yTicks = [];
  for (let value = minValue; value <= maxValue; value += tickStep) {
    yTicks.push(`<g>
      <line x1="${margin.left - 4}" x2="${margin.left + innerWidth}" y1="${scaleY(value)}" y2="${scaleY(value)}" stroke="#d9e0e4"></line>
      <text x="${margin.left - 8}" y="${scaleY(value) + 4}" text-anchor="end" font-size="11">${value}</text>
    </g>`);
  }

  const legendItems = groupByMetric
    ? conditionGroups.map((condition, index) => {
        const colour = REPORT.condition_colours[condition] || '#111827';
        const x = margin.left + (index % 4) * 170;
        const y = 20 + Math.floor(index / 4) * 20;
        return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="white" stroke="${escapeHtml(colour)}" stroke-width="2"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(condition)}</text></g>`;
      })
    : metricKeys.map((metric, index) => {
        const x = margin.left + (index % 4) * 170;
        const y = 20 + Math.floor(index / 4) * 20;
        return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="white" stroke="#111827" stroke-width="2"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(metric.label)}</text></g>`;
      });

  const boxes = [];

  function boxGroup(labelA, labelB, x, colour, s) {
    const q1Y = scaleY(s.q1);
    const q3Y = scaleY(s.q3);
    const medianY = scaleY(s.median);
    const nY = Math.max(margin.top + 11, q3Y - 7);
    const medianLabelY = Math.min(margin.top + innerHeight - 4, medianY + 13);
    return `<g>
      <line x1="${x}" x2="${x}" y1="${scaleY(s.min)}" y2="${scaleY(s.max)}" stroke="${escapeHtml(colour)}" stroke-width="1.5"></line>
      <rect x="${x - boxWidth / 2}" y="${q3Y}" width="${boxWidth}" height="${Math.max(2, q1Y - q3Y)}" fill="white" stroke="${escapeHtml(colour)}" stroke-width="2">
        <title>${escapeHtml(labelA)} | ${escapeHtml(labelB)} | n=${s.n}, min=${s.min.toFixed(2)}, Q1=${s.q1.toFixed(2)}, median=${s.median.toFixed(2)}, Q3=${s.q3.toFixed(2)}, max=${s.max.toFixed(2)}</title>
      </rect>
      <line x1="${x - boxWidth / 2}" x2="${x + boxWidth / 2}" y1="${medianY}" y2="${medianY}" stroke="${escapeHtml(colour)}" stroke-width="2.5"></line>
      <line x1="${x - boxWidth / 3}" x2="${x + boxWidth / 3}" y1="${scaleY(s.min)}" y2="${scaleY(s.min)}" stroke="${escapeHtml(colour)}" stroke-width="1.5"></line>
      <line x1="${x - boxWidth / 3}" x2="${x + boxWidth / 3}" y1="${scaleY(s.max)}" y2="${scaleY(s.max)}" stroke="${escapeHtml(colour)}" stroke-width="1.5"></line>
      <text x="${x}" y="${nY}" text-anchor="middle" font-size="9">n=${s.n}</text>
      <text x="${x}" y="${medianLabelY}" text-anchor="middle" font-size="8">Mdn ${s.median.toFixed(1)}</text>
    </g>`;
  }

  if (groupByMetric) {
    metricKeys.forEach((metric, metricIndex) => {
      conditionGroups.forEach((condition, conditionIndex) => {
        const scoped = condition === 'Overall' ? rows : rows.filter(row => row.condition === condition);
        const s = boxStats(numericValues(scoped, metric.key));
        if (!s) return;
        const centreOffset = (conditionIndex - (conditionGroups.length - 1) / 2) * (boxWidth + 6);
        const x = margin.left + metricIndex * slotWidth + slotWidth / 2 + centreOffset;
        const colour = REPORT.condition_colours[condition] || '#111827';
        boxes.push(boxGroup(metric.label, condition, x, colour, s));
      });
    });
  } else {
    conditionGroups.forEach((condition, conditionIndex) => {
      metricKeys.forEach((metric, metricIndex) => {
        const scoped = condition === 'Overall' ? rows : rows.filter(row => row.condition === condition);
        const s = boxStats(numericValues(scoped, metric.key));
        if (!s) return;
        const centreOffset = (metricIndex - (metricKeys.length - 1) / 2) * (boxWidth + 6);
        const x = margin.left + conditionIndex * slotWidth + slotWidth / 2 + centreOffset;
        const colour = REPORT.condition_colours[condition] || '#111827';
        boxes.push(boxGroup(condition, metric.label, x, colour, s));
      });
    });
  }

  const xLabels = xCategories.map((category, index) => {
    const x = margin.left + index * slotWidth + slotWidth / 2;
    const label = groupByMetric ? category.label : category;
    const rotate = groupByMetric ? '' : ` transform="rotate(-28 ${x} ${height - 44})"`;
    const anchor = groupByMetric ? 'middle' : 'end';
    return `<text x="${x}" y="${height - 44}" text-anchor="${anchor}"${rotate} font-size="12">${escapeHtml(label)}</text>`;
  }).join('');

  container.innerHTML = `
    <p class="small">Standalone SVG: centre line = median; boxes = Q1–Q3; whiskers = min–max; labels show n and median.</p>
    <svg class="standalone-figure" viewBox="0 0 ${width} ${height}" role="img">
      <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
      ${legendItems.join('')}
      ${yTicks.join('')}
      <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
      <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
      ${boxes.join('')}
      ${xLabels}
    </svg>`;
}
function renderMain(){ const audit=REPORT.audit; const participantHeaders=[{key:'participant_id',label:'MCID'},{key:'included',label:'Included'},{key:'condition',label:'Condition'},{key:'age',label:'Age',num:true},{key:'gender',label:'Gender'},{key:'started',label:'Started'},{key:'finished',label:'Finished'},{key:'experiment_duration',label:'Experiment duration'},{key:'completed_delayed_retention_tick',label:'Completed delayed retention'},{key:'delayed_duration',label:'Delayed duration'},{key:'logs_creature_score_label',label:'Score'}]; const conditionHeaders=[{key:'condition',label:'Condition'},{key:'n',label:'n',num:true},{key:'completed_delayed_retention_count',label:'Completed delayed retention',num:true},{key:'age_mean_sd',label:'Age Mean (SD)'},{key:'experiment_duration_mean_sd',label:'Duration Mean (SD)'},{key:'experiment_duration_min',label:'Duration min'},{key:'experiment_duration_max',label:'Duration max'},{key:'creature_score_mean_sd',label:'Score Mean (SD)'},{key:'creature_score_min',label:'Score min',num:true,fixed:true},{key:'creature_score_max',label:'Score max',num:true,fixed:true},{key:'gender_Female',label:'Female',num:true},{key:'gender_Male',label:'Male',num:true},{key:'gender_Other',label:'Other',num:true},{key:'gender_Unknown / missing',label:'Gender missing',num:true}]; const auditHeaders=[{key:'participant_id',label:'MCID'},{key:'included',label:'Included'},{key:'survey_present',label:'Survey'},{key:'survey_start',label:'Survey start'},{key:'survey_duration',label:'Survey duration'},{key:'survey_progress',label:'Progress',num:true},{key:'age',label:'Age',num:true},{key:'gender',label:'Gender'},{key:'delayed_present',label:'Delayed row'},{key:'delayed_completed',label:'Delayed complete'},{key:'delayed_duration',label:'Delayed duration'},{key:'log_present',label:'Log'},{key:'log_start',label:'Log start'},{key:'log_consent_time',label:'Agree time'},{key:'log_duration',label:'Log duration'},{key:'condition',label:'Condition'},{key:'checkpoint_decisions',label:'Checkpoint decisions'},{key:'creature_score',label:'Score'},{key:'exclusion_reasons',label:'Exclusion reasons'}]; const sourceRows=Object.entries(REPORT.sources).map(([label,path])=>({label,path})); const questionRows=REPORT.study_questions.map(item=>({id:item.id,text:item.text})); const exclusionRows=REPORT.exclusion_summary.map(item=>({reason:item.reason,n:item.n,ids:item.ids.join(', ')})); const modelHtml=REPORT.conceptual_model_data_uri?`<img class="conceptual-model" src="${REPORT.conceptual_model_data_uri}" alt="Conceptual model">`:`<p class="small status-bad">Conceptual model image was not found at generation time, so it could not be embedded.</p>`; document.getElementById('tab-main').innerHTML=`<section class="card"><h2>Overview</h2><div class="metric-grid">${metric('Included participants',audit.included_count,'Unique MCIDs after exclusions')}${metric('Excluded participants',audit.excluded_count,'Unique MCIDs with any exclusion reason')}${metric('Immediate responses',audit.included_immediate_response_count,'Included non-delayed responses')}${metric('Delayed responses',audit.included_delayed_response_count,'Included completed delayed retention responses')}</div><p class="small">Survey/log ID match: ${statusText(audit.ids_in_survey_not_logs.length===0&&audit.ids_in_logs_not_survey.length===0,'OK','Mismatch found')}.</p></section><section class="card"><h2>Distributions</h2><div class="chart-grid"><div class="chart-box"><h3>Distribution of conditions</h3><div id="condition-distribution-chart"></div></div><div class="chart-box"><h3>Distribution of age</h3><div id="age-distribution-chart"></div></div><div class="chart-box"><h3>Distribution of gender</h3><div id="gender-distribution-chart"></div></div></div></section><section class="card"><h2>Condition summary</h2>${table(conditionHeaders,REPORT.summaries.condition)}</section><section class="card"><h2>Used files</h2>${table([{key:'label',label:'Input'},{key:'path',label:'Relative path'}],sourceRows)}</section><section class="card"><h2>Conceptual model</h2>${modelHtml}</section><section class="card"><h2>Research questions, hypotheses, and exploratory questions</h2>${table([{key:'id',label:'ID'},{key:'text',label:'Content'}],questionRows)}</section><section class="card"><h2>Participants included in the current merged table</h2><div id="participant-filter-table"></div></section><section class="card"><h2>Excluded participant summary</h2>${table([{key:'reason',label:'Reason'},{key:'n',label:'n',num:true},{key:'ids',label:'MCIDs'}],exclusionRows)}</section><section class="card"><h2>Own logs and merge audit</h2><div id="audit-filter-table"></div></section><section class="card"><h2>Additional ID checks</h2><p>Survey MCIDs not found in logs: ${formatValue(audit.ids_in_survey_not_logs.join(', '))}</p><p>Log MCIDs not found in survey: ${formatValue(audit.ids_in_logs_not_survey.join(', '))}</p><p>Duplicate immediate survey MCIDs: ${formatValue(audit.survey_duplicate_immediate_ids.join(', '))}</p><p>Duplicate delayed survey MCIDs: ${formatValue(audit.survey_duplicate_delayed_ids.join(', '))}</p><p>Delayed rows without immediate row: ${formatValue(audit.delayed_without_immediate_ids.join(', '))}</p><p>Survey rows without MCID: ${formatValue(audit.survey_missing_mcid_rows.join(', '))}</p></section><section class="card"><h2>Lab collection slots</h2><p class="small">Only non-remote participants are included here. Participants with REMOTE = 1 are coded as At home, so lab and shared-slot counts are irrelevant for them.</p>${table([{key:'date',label:'Date'},{key:'time',label:'Time'},{key:'lab',label:'Lab'},{key:'n_participants',label:'n participants',num:true}],((REPORT.statistics||{}).lab_slot_summary||[]),{className:'stats-table'})}</section>`; renderFilterableTable('participant-filter-table',participantHeaders,REPORT.participants,[{key:'condition',label:'Condition'},{key:'included',label:'Included'},{key:'gender',label:'Gender'}],{summaryRow:participantSummaryRow}); renderFilterableTable('audit-filter-table',auditHeaders,REPORT.audit_rows,[{key:'condition',label:'Condition'},{key:'included',label:'Included'},{key:'survey_present',label:'Survey'},{key:'log_present',label:'Log'}]); renderDistributionCharts(); }
function renderSummaryTab(tabId,title,description,headers,rows){ document.getElementById(tabId).innerHTML=`<section class="card"><h2>${escapeHtml(title)}</h2><p>${escapeHtml(description)}</p>${table(headers,rows)}</section>`; }
function initialiseScopedTabs(rootId,buttonClass,panelClass){ const root=document.getElementById(rootId); if(!root) return; root.querySelectorAll(`.${buttonClass}`).forEach(button=>{ button.addEventListener('click',()=>{ const group=button.dataset.group; root.querySelectorAll(`.${buttonClass}[data-group="${group}"]`).forEach(item=>item.classList.remove('active')); root.querySelectorAll(`.${panelClass}[data-group="${group}"]`).forEach(item=>item.classList.remove('active')); button.classList.add('active'); root.querySelector(`#${button.dataset.target}`).classList.add('active'); }); }); }
function renderScaleTables(tables){ return tables.map(block=>`<section class="card"><h3>${escapeHtml(block.title)}</h3><p class="small">${escapeHtml(block.description)}</p>${table([{key:'item',label:'Item'},{key:'n',label:'n'},{key:'ch1_mean_sd',label:'Ch1 Mean (SD)'},{key:'ch1_min',label:'Ch1 Min',num:true},{key:'ch1_max',label:'Ch1 Max',num:true},{key:'ch2_mean_sd',label:'Ch2 Mean (SD)'},{key:'ch2_min',label:'Ch2 Min',num:true},{key:'ch2_max',label:'Ch2 Max',num:true},{key:'ch3_mean_sd',label:'Ch3 Mean (SD)'},{key:'ch3_min',label:'Ch3 Min',num:true},{key:'ch3_max',label:'Ch3 Max',num:true}],block.rows,{className:'scale-table'})}</section>`).join(''); }
function renderOverallScaleTables(tables){ return tables.map(block=>`<section class="card"><h3>${escapeHtml(block.title)}</h3><p class="small">${escapeHtml(block.description)}</p>${table([{key:'item',label:'Item'},{key:'n',label:'n',num:true},{key:'mean_sd',label:'Mean (SD)'},{key:'min',label:'Min',num:true},{key:'max',label:'Max',num:true}],block.rows,{className:'scale-table'})}</section>`).join(''); }
function renderCognitiveLoadTab(){ document.getElementById('tab-cognitive-load').innerHTML=`<div id="cl-subtabs"><nav class="subtabs"><button class="subtab-button active" data-group="cl" data-target="cl-per-chapter">Per chapter</button><button class="subtab-button" data-group="cl" data-target="cl-overall">Overall</button><button class="subtab-button" data-group="cl" data-target="cl-merged">Merged</button></nav><section id="cl-per-chapter" class="subtab-panel active" data-group="cl">${renderScaleTables(REPORT.scale_tables.cognitive_load.per_chapter)}</section><section id="cl-overall" class="subtab-panel" data-group="cl">${renderOverallScaleTables(REPORT.scale_tables.cognitive_load.overall)}</section><section id="cl-merged" class="subtab-panel" data-group="cl"><section class="card"><h2>Boxplot by cognitive-load type and condition</h2><div id="cl-boxplot"></div></section><section class="card"><h2>Merged cognitive-load constructs</h2><p>Intrinsic load is the mean of the per-chapter intrinsic-load items. Extraneous load is the equal-source-weighted mean of environment-related, instruction-related, and interaction-related extraneous load. Germane load is the mean of the overall germane-load items.</p>${table([{key:'condition',label:'Condition'},{key:'n',label:'n',num:true},{key:'cl_intrinsic_mean_sd',label:'IL Mean (SD)'},{key:'cl_intrinsic_min',label:'IL min',num:true,fixed:true},{key:'cl_intrinsic_max',label:'IL max',num:true,fixed:true},{key:'cl_extraneous_mean_sd',label:'EL Mean (SD)'},{key:'cl_extraneous_min',label:'EL min',num:true,fixed:true},{key:'cl_extraneous_max',label:'EL max',num:true,fixed:true},{key:'cl_germane_mean_sd',label:'GL Mean (SD)'},{key:'cl_germane_min',label:'GL min',num:true,fixed:true},{key:'cl_germane_max',label:'GL max',num:true,fixed:true}],REPORT.scale_tables.cognitive_load.merged)}</section><section class="card"><h2>Scale response flags</h2>${table([{key:'scale',label:'Scale'},{key:'flag',label:'Flag'},{key:'details',label:'Details'}],REPORT.scale_tables.cognitive_load.flags)}</section></section></div>`; initialiseScopedTabs('cl-subtabs','subtab-button','subtab-panel'); renderBoxplot(
  'cl-boxplot',
  [
    {key:'cl_intrinsic', label:'IL'},
    {key:'cl_extraneous', label:'EL'},
    {key:'cl_germane', label:'GL'}
  ],
  {
    groupBy: 'metric',
    minValue: 0,
    maxValue: 10
  }
); }
function renderEngagementTab(){ document.getElementById('tab-engagement').innerHTML=`<div id="eng-subtabs"><nav class="subtabs"><button class="subtab-button active" data-group="eng" data-target="eng-per-chapter">Per chapter</button><button class="subtab-button" data-group="eng" data-target="eng-overall">Overall</button><button class="subtab-button" data-group="eng" data-target="eng-merged">Merged</button></nav><section id="eng-per-chapter" class="subtab-panel active" data-group="eng">${renderScaleTables(REPORT.scale_tables.engagement.per_chapter)}</section><section id="eng-overall" class="subtab-panel" data-group="eng">${renderOverallScaleTables(REPORT.scale_tables.engagement.overall)}</section><section id="eng-merged" class="subtab-panel" data-group="eng"><section class="card"><h2>Boxplot by engagement score and condition</h2><div id="eng-boxplot"></div></section><section class="card"><h2>Merged engagement construct</h2><p>The primary engagement score is the mean of the chapter-specific engagement score and the overall engagement score, after reverse-coding frustration and confusion.</p>${table([{key:'condition',label:'Condition'},{key:'n',label:'n',num:true},{key:'eng_main_mean_sd',label:'Engagement Mean (SD)'},{key:'eng_main_min',label:'Engagement min',num:true,fixed:true},{key:'eng_main_max',label:'Engagement max',num:true,fixed:true}],REPORT.scale_tables.engagement.merged)}</section><section class="card"><h2>Scale response flags</h2>${table([{key:'scale',label:'Scale'},{key:'flag',label:'Flag'},{key:'details',label:'Details'}],REPORT.scale_tables.engagement.flags)}</section></section></div>`; initialiseScopedTabs('eng-subtabs','subtab-button','subtab-panel'); renderBoxplot(
  'eng-boxplot',
  [
    {key:'eng_main', label:'Engagement'}
  ],
  {
    groupBy: 'metric',
    minValue: 1,
    maxValue: 7
  }
); }
function renderRetentionTab(){
  const rows=REPORT.retention_answer_rows;
  const showGrades=Boolean(REPORT.show_retention_grades);
  const creatures=[...new Map(rows.map(row=>[row.creature_id,row])).values()].sort((a,b)=>a.creature_name.localeCompare(b.creature_name));
  const scoreExplanation = showGrades
    ? 'Prompt-level scores are visible because SHOW_RETENTION_GRADES is True. Participant-level retention scores are Grader 1 proportions: Grader 1 points divided by the maximum possible points on prompts scored for that participant and wave.'
    : 'Retention grades are hidden because SHOW_RETENTION_GRADES is False in apps/summarise_merged.py. Flip that switch only after grading is complete.';

  const summaryHeaders=[
    {key:'condition',label:'Condition'},
    {key:'n',label:'n',num:true},
    {key:'ret_delayed_wave_count',label:'Completed delayed retention',num:true},
    {key:'ret_immediate_answer_count',label:'Immediate answer count',num:true},
    {key:'ret_delayed_answer_count',label:'Delayed answer count',num:true}
  ];

  if(showGrades){
    summaryHeaders.push(
      {key:'ret_immediate_scored_prompt_count',label:'Immediate scored prompts',num:true},
      {key:'ret_delayed_scored_prompt_count',label:'Delayed scored prompts',num:true},
      {key:'ret_immediate_mean_sd',label:'Immediate score Mean (SD)'},
      {key:'ret_delayed_mean_sd',label:'Delayed score Mean (SD)'}
    );
  }

  const reliabilityHtml = showGrades ? `
    <section class="card">
      <h2>Interrater agreement</h2>
      <p>${escapeHtml(REPORT.retention_reliability.method)}</p>
      ${table([
        {key:'group',label:'Question group'},
        {key:'n_double_scored',label:'Double-scored prompts',num:true},
        {key:'exact_agreement_percent',label:'Exact agreement %',num:true},
        {key:'quadratic_weighted_kappa',label:'Quadratic weighted κ',num:true}
      ], REPORT.retention_reliability.rows || [])}
    </section>` : '';

  const summaryHtml=`<section class="card"><h2>Retention</h2><p>${escapeHtml(scoreExplanation)}</p>${table(summaryHeaders,REPORT.summaries.retention)}</section>${reliabilityHtml}`;

  if(!creatures.length){
    document.getElementById('tab-retention').innerHTML=summaryHtml+'<section class="card"><h2>Retention answers</h2><p>No retention answers found for included participants.</p></section>';
    return;
  }

  const answerHeadersBase=[
    {key:'participant_id',label:'MCID'},
    {key:'moment',label:'Moment'},
    {key:'answer',label:'Answer'}
  ];

  const answerHeadersWithGrades=[
    {key:'participant_id',label:'MCID'},
    {key:'moment',label:'Moment'},
    {key:'answer',label:'Answer'},
    {key:'grader1_score',label:'Grader 1 score',num:true},
    {key:'grader2_score',label:'Grader 2 score',num:true},
    {key:'grader_notes_html',label:'Grader notes',html:true}
  ];

  const creatureButtons=creatures.map((creature,index)=>`<button class="creature-tab-button ${index===0?'active':''}" data-group="creatures" data-target="creature-${escapeHtml(creature.creature_id)}">${escapeHtml(creature.creature_name)}</button>`).join('');

  const creaturePanels=creatures.map((creature,creatureIndex)=>{
    const questionButtons=REPORT.retention_questions.map((question,questionIndex)=>`<button class="question-tab-button ${questionIndex===0?'active':''}" data-group="question-${escapeHtml(creature.creature_id)}" data-target="question-${escapeHtml(creature.creature_id)}-${escapeHtml(question.key)}">${escapeHtml(question.key)}</button>`).join('');
    const questionPanels=REPORT.retention_questions.map((question,questionIndex)=>{
      const questionRows=rows.filter(row=>row.creature_id===creature.creature_id&&row.question===question.key).map(row=>{
        const output={participant_id:row.participant_id,moment:row.moment,answer:row.answer};
        if(showGrades){
          output.grader1_score=row.grader1_score;
          output.grader2_score=row.grader2_score;
          output.grader_notes_html=row.grader_notes_html;
        }
        return output;
      });
      return `<section id="question-${escapeHtml(creature.creature_id)}-${escapeHtml(question.key)}" class="question-panel ${questionIndex===0?'active':''}" data-group="question-${escapeHtml(creature.creature_id)}"><h3>${escapeHtml(question.label)}</h3>${table(showGrades?answerHeadersWithGrades:answerHeadersBase,questionRows,{className:'retention-answer-table'})}</section>`;
    }).join('');
    return `<section id="creature-${escapeHtml(creature.creature_id)}" class="creature-panel ${creatureIndex===0?'active':''}" data-group="creatures"><h2>${escapeHtml(creature.creature_name)}</h2><nav class="question-tabs">${questionButtons}</nav>${questionPanels}</section>`;
  }).join('');

  document.getElementById('tab-retention').innerHTML=summaryHtml+`<section class="card" id="retention-answer-browser"><h2>Retention answers by creature and question</h2><nav class="creature-tabs">${creatureButtons}</nav>${creaturePanels}</section>`;
  initialiseScopedTabs('retention-answer-browser','creature-tab-button','creature-panel');
  initialiseScopedTabs('retention-answer-browser','question-tab-button','question-panel');
}
function sampleSd(values) {
  if (values.length <= 1) return 0;
  const average = mean(values);
  const variance = values.reduce((total, value) => total + Math.pow(value - average, 2), 0) / (values.length - 1);
  return Math.sqrt(variance);
}

function timeUseSummaryRows(metricKey) {
  return REPORT.condition_order.map(condition => {
    const scoped = REPORT.logs.logs.filter(row => row.condition === condition);
    const values = numericValues(scoped, metricKey);
    const average = mean(values);
    const sd = sampleSd(values);

    return {
      condition,
      n: values.length,
      mean_sd: average === null ? '' : `${average.toFixed(2)} (${sd.toFixed(2)})`,
      min: values.length ? Math.min(...values).toFixed(2) : '',
      max: values.length ? Math.max(...values).toFixed(2) : '',
    };
  });
}

function timeUseSummaryTable(title, metricKey) {
  return `
    <h3>${escapeHtml(title)}</h3>
    ${table([
      {key:'condition', label:'Condition'},
      {key:'n', label:'n', num:true},
      {key:'mean_sd', label:'Mean (SD), seconds'},
      {key:'min', label:'Min, seconds', num:true},
      {key:'max', label:'Max, seconds', num:true}
    ], timeUseSummaryRows(metricKey))}
  `;
}

function meanOrBlank(values, digits=2) {
  const value = mean(values);
  return value === null ? '' : value.toFixed(digits);
}

function sdOrBlank(values, digits=2) {
  return values.length ? sampleSd(values).toFixed(digits) : '';
}

function logDetailSummaryRows(rows) {
  const numericKeys = [
    'interacted_species_count',
    'interacted_creature_instance_count',
    'species_revisited_count',
    'creatures_revisited_count',
    'ch0_duration_seconds',
    'card_reading_seconds',
    'walking_sprinting_seconds_estimate',
    'other_seconds_estimate',
    'movement_total_distance',
    'movement_total_sprint_distance'
  ];

  const makeRow = (label, formatter) => {
    const row = {__summary: true, participant_id: label, condition: 'All included'};
    for (const key of numericKeys) {
      const values = numericValues(rows, key);
      row[key] = formatter(values);
    }
    const ch0Values = numericValues(rows, 'ch0_duration_seconds');
    row.ch0_duration = ch0Values.length ? formatDuration(Number(row.ch0_duration_seconds)) : '';
    row.game_duration = '';
    row.creature_score_label = '';
    return row;
  };

  return [
    makeRow('Mean', values => meanOrBlank(values)),
    makeRow('SD', values => sdOrBlank(values))
  ];
}
function sixthCreatureTotalForCondition(condition) {
  return REPORT.logs.logs.filter(row => row.condition === condition).length;
}

function formatNWithTotalAndPercent(count, total) {
  if (!total) return `${count} / 0 (NA)`;
  return `${count} / ${total} (${(100 * count / total).toFixed(1)}%)`;
}

function sixthCreatureRowsForChapter(chapterLabel) {
  return REPORT.condition_order.map(condition => {
    const row = REPORT.logs.time_to_sixth_creature.find(item =>
      item.condition === condition && item.chapter === chapterLabel
    );

    const count = Number(row?.n ?? 0);
    const total = sixthCreatureTotalForCondition(condition);

    return {
      condition,
      chapter: chapterLabel,
      n_display: formatNWithTotalAndPercent(count, total),
      mean_sd: row?.mean_sd || '',
      min: row?.min || '',
      max: row?.max || ''
    };
  });
}

function sixthCreatureSummaryTable(chapterLabel) {
  return `
    <h3>${escapeHtml(chapterLabel)}</h3>
    ${table([
      {key:'condition', label:'Condition'},
      {key:'n_display', label:'n / total (%)'},
      {key:'mean_sd', label:'Mean (SD)'},
      {key:'min', label:'Min'},
      {key:'max', label:'Max'}
    ], sixthCreatureRowsForChapter(chapterLabel))}
  `;
}

function renderOtherTabs() {
  renderRetentionTab();
  renderCognitiveLoadTab();
  renderEngagementTab();

  document.getElementById('tab-control').innerHTML = `
    <section class="card">
      <h2>Perceived control at checkpoints</h2>
      <p>Manipulation-check summary from ctrl_scores_1 and ctrl_scores_2.</p>
      <p class="small"><strong>Scale:</strong> 1 = strongly disagree, 7 = strongly agree.</p>
      <p class="small"><strong>Interpretation:</strong> Higher scores indicate higher perceived control at the checkpoints.</p>
    </section>

    <section class="card">
      <h2>Boxplot by perceived-control score and condition</h2>
      <div id="ctrl-boxplot"></div>
    </section>

    ${renderOverallScaleTables(REPORT.scale_tables.control.overall)}

    <section class="card">
      <h2>Merged perceived-control score</h2>
      <p>The merged perceived-control score is the mean of the two checkpoint-control items.</p>
      ${table([
        {key:'condition', label:'Condition'},
        {key:'n', label:'n', num:true},
        {key:'ctrl_mean_sd', label:'Control Mean (SD)'},
        {key:'ctrl_min', label:'Control min', num:true, fixed:true},
        {key:'ctrl_max', label:'Control max', num:true, fixed:true}
      ], REPORT.scale_tables.control.merged)}
    </section>

    <section class="card">
      <h2>Scale response flags</h2>
      ${table([
        {key:'scale', label:'Scale'},
        {key:'flag', label:'Flag'},
        {key:'details', label:'Details'}
      ], REPORT.scale_tables.control.flags)}
    </section>`;

  renderBoxplot(
    'ctrl-boxplot',
    [
      {key:'ctrl_perceived', label:'Perceived control'}
    ],
    {
      groupBy: 'metric',
      minValue: 1,
      maxValue: 7
    }
  );

  document.getElementById('tab-logs').innerHTML = `
    <section class="card">
      <h2>Time-use boxplots by condition</h2>
      <div id="logs-time-boxplot"></div>
    </section>

    <section class="card">
      <h2>Time-use values behind the boxplots</h2>
      ${timeUseSummaryTable('Chapter 0', 'ch0_duration_seconds')}
      ${timeUseSummaryTable('Card reading', 'card_reading_seconds')}
      ${timeUseSummaryTable('Walking + sprinting', 'walking_sprinting_seconds_estimate')}
      ${timeUseSummaryTable('Standing still / other time', 'other_seconds_estimate')}
    </section>

    <section class="card">
      <h2>Time to sixth creature by chapter and condition</h2>
      <p class="small">Time from chapter start until the sixth unique species was first opened in that chapter.</p>
      ${sixthCreatureSummaryTable('Ch1')}
      ${sixthCreatureSummaryTable('Ch2')}
      ${sixthCreatureSummaryTable('Ch3')}
    </section>

    <section class="card">
      <h2>Optional pauses: common manipulated-choice patterns</h2>
      <p class="small">Included optional-pauses participants only. Only the manipulated choices between Ch1 > Ch2 and Ch2 > Ch3 are counted.</p>
      ${table([
        {key:'pattern', label:'Pattern'},
        {key:'n', label:'n', num:true}
      ], REPORT.logs.optional_pause_choice_patterns)}
    </section>

    <section class="card">
      <h2>Optional pauses: checkpoint choices</h2>
      ${table([
        {key:'participant_id', label:'MCID'},
        {key:'moment', label:'Moment'},
        {key:'choice', label:'Choice'},
        {key:'choice_time_label', label:'Thinking / choice time'},
        {key:'choice_time_ms', label:'Thinking / choice ms', num:true}
      ], REPORT.logs.checkpoint_choices)}
    </section>

    <section class="card">
      <h2>Game logs</h2>
      <p class="small">Detailed included-participant log table. Kept below the summary figures because it is primarily for inspection and debugging.</p>
      ${table([
        {key:'participant_id', label:'MCID'},
        {key:'condition', label:'Condition'},
        {key:'source_log', label:'Log'},
        {key:'consent_agreed_at', label:'Accepted study'},
        {key:'survey_opened_at', label:'Opened survey'},
        {key:'game_duration', label:'Game duration'},
        {key:'ch0_duration', label:'Ch0 duration'},
        {key:'completed_chapters', label:'Completed chapters'},
        {key:'creature_score_label', label:'Score'},
        {key:'interacted_species_count', label:'Unique species', num:true},
        {key:'interacted_creature_instance_count', label:'Unique creatures', num:true},
        {key:'species_revisited_count', label:'Species revisited', num:true},
        {key:'creatures_revisited_count', label:'Creatures revisited', num:true},
        {key:'ch0_duration_seconds', label:'Ch0 sec', num:true, fixed:true},
        {key:'card_reading_seconds', label:'Card reading sec', num:true, fixed:true},
        {key:'walking_sprinting_seconds_estimate', label:'Walking + sprinting sec', num:true, fixed:true},
        {key:'other_seconds_estimate', label:'Other sec', num:true, fixed:true},
        {key:'movement_total_distance', label:'Total distance', num:true, fixed:true},
        {key:'movement_total_sprint_distance', label:'Sprint distance', num:true, fixed:true},
        {key:'game_end_reason', label:'End reason'}
      ], [...REPORT.logs.logs, ...logDetailSummaryRows(REPORT.logs.logs)], {className:'log-table', stickyHeader:true})}
    </section>`;

  renderBoxplot(
    'logs-time-boxplot',
    [
      {key:'ch0_duration_seconds', label:'Ch0'},
      {key:'card_reading_seconds', label:'Card reading'},
      {key:'walking_sprinting_seconds_estimate', label:'Walking + sprinting'},
      {key:'other_seconds_estimate', label:'Standing still / other time'}
    ],
    {
      rows: REPORT.logs.logs,
      minValue: 0,
      groupBy: 'metric'
    }
  );

  renderInterviewsV2();

  const stats = REPORT.statistics || {};
  const power = stats.power || {};
  const h1Immediate = statsRowsByModel(stats.focal_rows, 'H1 immediate');
  const h1Delayed = statsRowsByModel(stats.focal_rows, 'H1 delayed');
  const h2Rows = [...statsRowsByHypothesis(stats.focal_rows, 'H2a'), ...statsRowsByHypothesis(stats.focal_rows, 'H2b')];
  const h3Rows = [...statsRowsByHypothesis(stats.focal_rows, 'H3a'), ...statsRowsByHypothesis(stats.focal_rows, 'H3b')];
  const h4Rows = statsRowsByHypothesis(stats.focal_rows, 'H4');
  const manipulationRows = statsRowsByHypothesis(stats.focal_rows, 'Manipulation check');
  const warningHtml = (stats.warnings || []).length ? `<section class="card"><h2>Warnings / missing inputs</h2><ul>${stats.warnings.map(item=>`<li>${escapeHtml(item)}</li>`).join('')}</ul></section>` : '';

  document.getElementById('tab-statistics').innerHTML = `
    <section class="card">
      <h2>Inferential statistics</h2>
      <p>${escapeHtml(stats.intro || '')}</p>
      <p class="small"><strong>Status:</strong> ${escapeHtml(stats.status || '')}</p>
    </section>

    ${warningHtml}

    <section class="card">
      <h2>Power analysis and current analysis n</h2>
      <p>The planned sample size was ${escapeHtml(power.planned_total || '')} participants (${escapeHtml(power.planned_per_condition || '')} per condition), anchored to ${escapeHtml(power.planning_effect || '')} for ${escapeHtml(power.planning_test || '')}.</p>
      <p class="small">${escapeHtml(power.note || '')}</p>
      ${table([{key:'condition',label:'Condition'},{key:'planned',label:'Planned n',num:true},{key:'current',label:'Current included n',num:true},{key:'delayed',label:'Current delayed scored n',num:true}], renderPowerRows(power), {className:'stats-table'})}
    </section>

    <section class="card">
      <h2>Collection location and lab-slot context</h2>
      <p class="small">Remote participants are coded as At home. For lab participants, the app reads resources/collection_locations.json by collection date, then counts how many included participants were in the same date, time slot, and lab location.</p>
      ${table([
        {key:'location',label:'Location'},
        {key:'n',label:'Included n',num:true},
        {key:'slot_n_mean',label:'Mean lab-slot n'},
        {key:'slot_n_min',label:'Min lab-slot n'},
        {key:'slot_n_max',label:'Max lab-slot n'}
      ], stats.location_summary || [], {className:'stats-table'})}
    </section>

    <section class="card">
      <h2>Calculation audit trail</h2>
      <p class="small">This is intentionally placed before the results so a reviewer can verify the model coding and the reported effect estimates first.</p>
      ${table([{key:'item',label:'Calculation'},{key:'calculation',label:'What the code does'}], stats.calculation_notes || [], {className:'stats-table'})}
      ${detailsBlock('All model formulas and complete-case n', renderModelAudit(stats.model_rows || []))}
    </section>

    <section class="card">
      <h2>Checkpoint Design → Retention, immediate (H1, primary)</h2>
      <p>H1 hypothesises that Checkpoint Design affects immediate retention, with required pauses expected to outperform required continue. Inspect the planned contrast estimate, its uncertainty, and partial r²; the p-value is only one part of that judgement.</p>
      ${renderStatsResultTable(h1Immediate)}
    </section>

    <section class="card">
      <h2>Checkpoint Design → Retention, delayed (H1, secondary)</h2>
      <p>Delayed retention tests the same planned contrasts, but follow-up attrition lowers the effective n and therefore the precision of the estimates.</p>
      ${renderStatsResultTable(h1Delayed)}
    </section>

    <section class="card">
      <h2>Checkpoint Design → Cognitive Load → Retention (H2a/H2b/H2)</h2>
      <p>H2 is evaluated in steps: Checkpoint Design should affect the three cognitive-load dimensions (H2a), and those dimensions should relate to retention in the predicted directions (H2b). The indirect effect is then judged from the bootstrap CI, especially the required-pause paths through extraneous and germane load.</p>
      ${renderStatsResultTable(h2Rows)}
      ${detailsBlock('Parallel mediation, immediate retention (H2 primary)', renderIndirectTable(stats.mediation?.h2_parallel_immediate) + renderDirectMediationTable(stats.mediation?.h2_parallel_immediate), true)}
      ${detailsBlock('Parallel mediation, delayed retention (H2 secondary)', renderIndirectTable(stats.mediation?.h2_parallel_delayed) + renderDirectMediationTable(stats.mediation?.h2_parallel_delayed))}
      ${renderFactorAnalyses('Cognitive load')}
    </section>

    <section class="card">
      <h2>Checkpoint Design → Engagement → Retention (H3a/H3b/H3)</h2>
      <p>H3 tests whether optional pauses improve engagement compared with the two system-controlled checkpoint designs, and whether engagement is positively associated with retention. The indirect effect is again interpreted through the bootstrap CI rather than by requiring a significant total effect first.</p>
      ${renderStatsResultTable(h3Rows)}
      ${detailsBlock('Simple mediation, immediate retention (H3 primary)', renderIndirectTable(stats.mediation?.h3_simple_immediate) + renderDirectMediationTable(stats.mediation?.h3_simple_immediate), true)}
      ${detailsBlock('Simple mediation, delayed retention (H3 secondary)', renderIndirectTable(stats.mediation?.h3_simple_delayed) + renderDirectMediationTable(stats.mediation?.h3_simple_delayed))}
      ${renderFactorAnalyses('Engagement')}
    </section>

    <section class="card">
      <h2>H4: Engagement → cognitive-load dimensions</h2>
      <p>H4 tests whether higher engagement is associated with lower extraneous cognitive load and higher germane cognitive load; the intrinsic-load association is checked without a directional prediction. Interpret the sign and size of each association together with its uncertainty.</p>
      ${renderStatsResultTable(h4Rows)}
    </section>

    <section class="card">
      <h2>Checkpoint Design → Engagement → Cognitive Load → Retention (EQ1, exploratory serial effect)</h2>
      <p>This exploratory check asks whether the pattern is consistent with Checkpoint Design affecting engagement, engagement relating to one cognitive-load dimension, and that load dimension relating to retention. Because engagement and cognitive load are measured in the same post-game block, this is a theory-consistency check, not strong evidence for temporal ordering.</p>
      ${detailsBlock('Serial mediation, immediate retention', renderSerialTable(stats.mediation?.serial_immediate), true)}
      ${detailsBlock('Serial mediation, delayed retention', renderSerialTable(stats.mediation?.serial_delayed))}
    </section>

    <section class="card">
      <h2>Other preregistered checks and robustness</h2>
      <p class="small">These checks help assess stability and measurement quality, but they should not replace the preregistered primary models.</p>
      ${detailsBlock('Perceived-control manipulation check', renderStatsResultTable(manipulationRows))}
      ${detailsBlock('Robustness: age and gender covariates', renderModelAudit(stats.robustness_age_gender || []))}
      ${detailsBlock('Robustness: room type and shared-slot n', renderModelAudit(stats.robustness_context || []))}
      ${renderFactorAnalyses('Perceived control')}
    </section>`;
}

function initialiseTabs(){
  document.querySelectorAll('.tab-button').forEach(button => {
    button.addEventListener('click', () => {
      const targetPanel = document.getElementById(`tab-${button.dataset.tab}`);

      document.querySelectorAll('.tab-button').forEach(item => item.classList.remove('active'));
      document.querySelectorAll('.tab-panel').forEach(item => item.classList.remove('active'));

      button.classList.add('active');

      if (targetPanel) {
        targetPanel.classList.add('active');
      }
    });
  });
}

function speakerColour(speaker){
  const text = String(speaker || 'Participant');
  let hash = 0;
  for (let index = 0; index < text.length; index += 1) {
    hash = ((hash << 5) - hash + text.charCodeAt(index)) | 0;
  }
  const hue = Math.abs(hash) % 360;
  return {
    stroke: `hsl(${hue}, 64%, 38%)`,
    background: `hsl(${hue}, 78%, 96%)`
  };
}

function uniqueTranscriptSpeakers(transcript){
  const speakers = [];
  for (const turn of transcript.turns || []) {
    const speaker = String(turn.speaker || '').trim();
    if (speaker && !speakers.includes(speaker)) speakers.push(speaker);
  }
  return speakers;
}

function participantById(participantId){
  return (REPORT.participants || []).find(row => row.participant_id === participantId) || null;
}

function logById(participantId){
  return ((REPORT.logs || {}).logs || []).find(row => row.participant_id === participantId) || null;
}

function auditRowById(participantId){
  return (REPORT.audit_rows || []).find(row => row.participant_id === participantId) || null;
}

function mergedStatusForParticipant(participantId){
  if (participantById(participantId)) {
    return {inMergedData: true, label: 'Included in merged dataset'};
  }

  const auditRow = auditRowById(participantId);
  const reasons = String(auditRow?.exclusion_reasons || '').trim();

  if (reasons) {
    return {
      inMergedData: false,
      label: `Excluded from merged dataset: ${reasons}`
    };
  }

  return {
    inMergedData: false,
    label: 'Not found among included merged participants'
  };
}

function missingMergedDataIds(participantIds){
  return (participantIds || []).filter(participantId => participantId && !mergedStatusForParticipant(participantId).inMergedData);
}

function participantIdsFromRows(rows){
  return new Set((rows || []).map(row => row.participant_id).filter(Boolean));
}

function logsForParticipantRows(rows){
  const ids = participantIdsFromRows(rows);
  return ((REPORT.logs || {}).logs || []).filter(log => ids.has(log.participant_id));
}

function finiteNumber(value){
  if (value === null || value === undefined) return null;

  if (typeof value === 'string' && value.trim() === '') {
    return null;
  }

  const number = Number(value);
  return Number.isFinite(number) ? number : null;
}

function formatPlainNumber(value, digits=2){
  const number = finiteNumber(value);
  return number === null ? '—' : number.toFixed(digits);
}

function formatProportion(value){
  const number = finiteNumber(value);
  return number === null ? '—' : number.toFixed(2);
}

function formatMaybeDuration(value){
  const number = finiteNumber(value);
  return number === null ? '—' : formatDuration(number);
}

function formatMeanSd(values, formatter=(value) => value.toFixed(2)){
  const cleanValues = values.map(Number).filter(Number.isFinite);
  if (!cleanValues.length) return '—';
  const average = mean(cleanValues);
  const sd = sampleSd(cleanValues);
  return `${formatter(average)} (${formatter(sd)})`;
}

function textDistribution(rows, key){
  const counts = {};
  for (const row of rows || []) {
    const value = String(row?.[key] || 'Unknown / missing');
    counts[value] = (counts[value] || 0) + 1;
  }
  return Object.entries(counts).map(([label, n]) => `${label}: ${n}`).join(', ') || '—';
}

function nestedChapterValue(log, chapter){
  return finiteNumber((log?.time_to_sixth_creature_by_chapter || {})[String(chapter)]);
}

const INTERVIEW_METRICS = [
  {label:'Condition', source:'participant', key:'condition', kind:'text'},
  {label:'Age', source:'participant', key:'age', kind:'number'},
  {label:'Gender', source:'participant', key:'gender', kind:'text'},
  {label:'Completed delayed retention', source:'participant', key:'completed_delayed_retention_tick', kind:'text'},
  {label:'Experiment duration', source:'participant', key:'experiment_duration_seconds', kind:'duration'},
  {label:'Delayed survey duration', source:'participant', key:'delayed_duration_seconds', kind:'duration'},

  {label:'Creature score', source:'participant', key:'logs_creature_score_of_18', kind:'score18'},
  {label:'Immediate retention score', source:'participant', key:'ret_immediate_score', kind:'proportion'},
  {label:'Delayed retention score', source:'participant', key:'ret_delayed_score', kind:'proportion'},
  {label:'Immediate scored prompts', source:'participant', key:'ret_immediate_scored_prompt_count', kind:'number0'},
  {label:'Delayed scored prompts', source:'participant', key:'ret_delayed_scored_prompt_count', kind:'number0'},

  {label:'Intrinsic cognitive load', source:'participant', key:'cl_intrinsic', kind:'number'},
  {label:'Extraneous cognitive load', source:'participant', key:'cl_extraneous', kind:'number'},
  {label:'Germane cognitive load', source:'participant', key:'cl_germane', kind:'number'},
  {label:'Engagement', source:'participant', key:'eng_main', kind:'number'},
  {label:'Perceived control', source:'participant', key:'ctrl_perceived', kind:'number'},

  {label:'Ch0 time', source:'log', key:'ch0_duration_seconds', kind:'duration'},
  {label:'Card reading time', source:'log', key:'card_reading_seconds', kind:'duration'},
  {label:'Walking + sprinting time', source:'log', key:'walking_sprinting_seconds_estimate', kind:'duration'},
  {label:'Standing still / other time', source:'log', key:'other_seconds_estimate', kind:'duration'},
  {label:'Total movement distance', source:'log', key:'movement_total_distance', kind:'number'},
  {label:'Sprint distance', source:'log', key:'movement_total_sprint_distance', kind:'number'},
  {label:'Unique species found', source:'log', key:'interacted_species_count', kind:'number0'},
  {label:'Unique creature instances found', source:'log', key:'interacted_creature_instance_count', kind:'number0'},
  {label:'Species revisited', source:'log', key:'species_revisited_count', kind:'number0'},
  {label:'Creatures revisited', source:'log', key:'creatures_revisited_count', kind:'number0'},
  {label:'Checkpoint decisions', source:'participant', key:'logs_checkpoint_decisions', kind:'text'},

  {label:'Ch1 sixth creature found after', source:'log_nested_chapter', chapter:'1', kind:'duration'},
  {label:'Ch2 sixth creature found after', source:'log_nested_chapter', chapter:'2', kind:'duration'},
  {label:'Ch3 sixth creature found after', source:'log_nested_chapter', chapter:'3', kind:'duration'}
];

function rawMetricValue(metric, participant, log){
  if (metric.source === 'participant') return participant?.[metric.key];
  if (metric.source === 'log') return log?.[metric.key];
  if (metric.source === 'log_nested_chapter') return nestedChapterValue(log, metric.chapter);
  return null;
}

function formatSingleMetricValue(metric, participant, log){
  const value = rawMetricValue(metric, participant, log);

  if (metric.kind === 'text') return formatValue(value);
  if (metric.kind === 'duration') return formatMaybeDuration(value);
  if (metric.kind === 'score18') {
    const number = finiteNumber(value);
    return number === null ? '—' : `${number.toFixed(0)}/18`;
  }
  if (metric.kind === 'proportion') return formatProportion(value);
  if (metric.kind === 'number0') {
    const number = finiteNumber(value);
    return number === null ? '—' : String(Math.round(number));
  }

  return formatPlainNumber(value);
}

function metricValues(metric, participants, logs){
  if (metric.source === 'participant') {
    return (participants || []).map(row => finiteNumber(row?.[metric.key])).filter(value => value !== null);
  }
  if (metric.source === 'log') {
    return (logs || []).map(row => finiteNumber(row?.[metric.key])).filter(value => value !== null);
  }
  if (metric.source === 'log_nested_chapter') {
    return (logs || []).map(row => nestedChapterValue(row, metric.chapter)).filter(value => value !== null);
  }
  return [];
}

function formatAggregateMetricValue(metric, participants, logs){
  if (metric.kind === 'text') {
    if (metric.source !== 'participant') return '—';
    return textDistribution(participants, metric.key);
  }

  const values = metricValues(metric, participants, logs);

  if (metric.kind === 'duration') return formatMeanSd(values, formatDuration);
  if (metric.kind === 'score18') return formatMeanSd(values, value => `${value.toFixed(1)}/18`);
  if (metric.kind === 'proportion') return formatMeanSd(values, value => value.toFixed(2));
  if (metric.kind === 'number0') return formatMeanSd(values, value => value.toFixed(0));

  return formatMeanSd(values, value => value.toFixed(2));
}

function compareCell(mainValue, fullValue){
  return `<span class="compare-cell-main">${escapeHtml(mainValue)}</span><span class="compare-cell-full">Full: ${escapeHtml(fullValue)}</span>`;
}

function compareCellHtml(mainHtml, fullValue){
  return `<span class="compare-cell-main">${mainHtml}</span><span class="compare-cell-full">Full: ${escapeHtml(fullValue)}</span>`;
}

function participantValueLine(participantId, value, useParticipantColour=true){
  if (!useParticipantColour) {
    return `<span class="compare-cell-main">${escapeHtml(value)}</span>`;
  }

  const colours = speakerColour(participantId);
  return `<span class="participant-value-line" style="--speaker-color:${escapeHtml(colours.stroke)};">${escapeHtml(value)}</span>`;
}

function emptyParticipantValueLine(){
  return `<span class="participant-value-empty">—</span>`;
}

function interviewComparisonHeaders(){
  return [
    {key:'condition', label:'Condition'},
    {key:'n', label:'n', html:true},
    {key:'age', label:'Age', html:true},
    {key:'gender', label:'Gender', html:true},
    {key:'experiment_duration', label:'Experiment duration', html:true},
    {key:'creature_score', label:'Creature score', html:true},
    {key:'ret_immediate', label:'Immediate retention', html:true},
    {key:'ret_delayed', label:'Delayed retention', html:true},
    {key:'cl_intrinsic', label:'IL', html:true},
    {key:'cl_extraneous', label:'EL', html:true},
    {key:'cl_germane', label:'GL', html:true},
    {key:'engagement', label:'Engagement', html:true},
    {key:'control', label:'Control', html:true},
    {key:'ch0', label:'Ch0', html:true},
    {key:'ch1_sixth', label:'Ch1 sixth', html:true},
    {key:'ch2_sixth', label:'Ch2 sixth', html:true},
    {key:'ch3_sixth', label:'Ch3 sixth', html:true}
  ];
}

function compactInterviewMetricMap(){
  return {
    age: INTERVIEW_METRICS.find(metric => metric.key === 'age'),
    gender: INTERVIEW_METRICS.find(metric => metric.key === 'gender'),
    experiment_duration: INTERVIEW_METRICS.find(metric => metric.key === 'experiment_duration_seconds'),
    creature_score: INTERVIEW_METRICS.find(metric => metric.key === 'logs_creature_score_of_18'),
    ret_immediate: INTERVIEW_METRICS.find(metric => metric.key === 'ret_immediate_score'),
    ret_delayed: INTERVIEW_METRICS.find(metric => metric.key === 'ret_delayed_score'),
    cl_intrinsic: INTERVIEW_METRICS.find(metric => metric.key === 'cl_intrinsic'),
    cl_extraneous: INTERVIEW_METRICS.find(metric => metric.key === 'cl_extraneous'),
    cl_germane: INTERVIEW_METRICS.find(metric => metric.key === 'cl_germane'),
    engagement: INTERVIEW_METRICS.find(metric => metric.key === 'eng_main'),
    control: INTERVIEW_METRICS.find(metric => metric.key === 'ctrl_perceived'),
    ch0: INTERVIEW_METRICS.find(metric => metric.key === 'ch0_duration_seconds'),
    ch1_sixth: INTERVIEW_METRICS.find(metric => metric.chapter === '1'),
    ch2_sixth: INTERVIEW_METRICS.find(metric => metric.chapter === '2'),
    ch3_sixth: INTERVIEW_METRICS.find(metric => metric.chapter === '3')
  };
}

function compactInterviewMetricKeys(){
  return new Set(Object.values(compactInterviewMetricMap()).filter(Boolean).map(metric => `${metric.source || ''}|${metric.key || ''}|${metric.chapter || ''}`));
}

function aggregateComparisonRows(sampleRows, fullRows){
  const sampleLogs = logsForParticipantRows(sampleRows);
  const fullLogs = logsForParticipantRows(fullRows);
  const groups = [...REPORT.condition_order, 'Overall'];
  const compactMetricMap = compactInterviewMetricMap();

  return groups.map(group => {
    const sampleScoped = group === 'Overall' ? sampleRows : sampleRows.filter(row => row.condition === group);
    const fullScoped = group === 'Overall' ? fullRows : fullRows.filter(row => row.condition === group);
    const sampleScopedIds = participantIdsFromRows(sampleScoped);
    const fullScopedIds = participantIdsFromRows(fullScoped);
    const sampleScopedLogs = sampleLogs.filter(log => sampleScopedIds.has(log.participant_id));
    const fullScopedLogs = fullLogs.filter(log => fullScopedIds.has(log.participant_id));

    const row = {
      condition: group,
      n: compareCell(String(sampleScoped.length), String(fullScoped.length))
    };

    for (const [outputKey, metric] of Object.entries(compactMetricMap)) {
      row[outputKey] = compareCell(
        formatAggregateMetricValue(metric, sampleScoped, sampleScopedLogs),
        formatAggregateMetricValue(metric, fullScoped, fullScopedLogs)
      );
    }

    return row;
  });
}

function selectedParticipantComparisonRows(selectedIds, fullRows){
  const selectedParticipants = selectedIds
    .map(participantId => participantById(participantId))
    .filter(Boolean);

  const fullLogs = logsForParticipantRows(fullRows);
  const groups = [...REPORT.condition_order, 'Overall'];
  const compactMetricMap = compactInterviewMetricMap();

  return groups.map(group => {
    const selectedScoped = group === 'Overall'
      ? selectedParticipants
      : selectedParticipants.filter(row => row.condition === group);

    const fullScoped = group === 'Overall'
      ? fullRows
      : fullRows.filter(row => row.condition === group);

    const selectedScopedIds = participantIdsFromRows(selectedScoped);
    const selectedScopedLogs = ((REPORT.logs || {}).logs || [])
      .filter(log => selectedScopedIds.has(log.participant_id));

    const fullScopedIds = participantIdsFromRows(fullScoped);
    const fullScopedLogs = fullLogs.filter(log => fullScopedIds.has(log.participant_id));

    const row = {
      condition: group,
      n: compareCell(String(selectedScoped.length), String(fullScoped.length))
    };

    for (const [outputKey, metric] of Object.entries(compactMetricMap)) {
      let mainHtml = emptyParticipantValueLine();

      if (selectedScoped.length && group === 'Overall') {
        mainHtml = `<span class="compare-cell-main">${escapeHtml(
          formatAggregateMetricValue(metric, selectedScoped, selectedScopedLogs)
        )}</span>`;
      } else if (selectedScoped.length) {
        mainHtml = selectedScoped.map(participant => {
          const log = logById(participant.participant_id);
          return participantValueLine(
            participant.participant_id,
            formatSingleMetricValue(metric, participant, log),
            true
          );
        }).join('');
      }

      row[outputKey] = compareCellHtml(
        mainHtml,
        formatAggregateMetricValue(metric, fullScoped, fullScopedLogs)
      );
    }

    return row;
  });
}

function renderBaselineInterviewDataPanel(){
  const data = REPORT.interviews || {};
  const ids = new Set(data.unique_participant_ids || []);
  const interviewRows = (REPORT.participants || []).filter(row => ids.has(row.participant_id));

  document.getElementById('interview-data-section').innerHTML = `
    <h2>Interview subsample compared with full sample</h2>
    <p class="small">No transcript is selected. Each cell shows the interview-subsample value first, followed by the full included-sample value on the second line.</p>
    ${table(interviewComparisonHeaders(), aggregateComparisonRows(interviewRows, REPORT.participants || []), {className:'interview-summary-table'})}
  `;
}

function extraInterviewMetrics(){
  const compactKeys = compactInterviewMetricKeys();
  return INTERVIEW_METRICS.filter(metric => !compactKeys.has(`${metric.source || ''}|${metric.key || ''}|${metric.chapter || ''}`));
}

function renderSelectedExtraPills(selectedIds){
  const metrics = extraInterviewMetrics();

  const groups = selectedIds.map(participantId => {
    const participant = participantById(participantId);
    const colours = speakerColour(participantId);

    if (!participant) {
      const status = mergedStatusForParticipant(participantId);
      return `
        <div class="selected-extra-pill-group missing-merged-data" style="--speaker-color:${escapeHtml(colours.stroke)}; --speaker-bg:${escapeHtml(colours.background)};">
          <div class="selected-extra-pill-heading">${escapeHtml(participantId)}</div>
          <span class="selected-extra-pill"><strong>Status:</strong> ${escapeHtml(status.label)}</span>
        </div>
      `;
    }

    const log = logById(participantId);
    const pills = metrics.map(metric => `
      <span class="selected-extra-pill"><strong>${escapeHtml(metric.label)}:</strong> ${escapeHtml(formatSingleMetricValue(metric, participant, log))}</span>
    `).join('');

    return `
      <div class="selected-extra-pill-group" style="--speaker-color:${escapeHtml(colours.stroke)}; --speaker-bg:${escapeHtml(colours.background)};">
        <div class="selected-extra-pill-heading">${escapeHtml(participantId)} · additional details</div>
        ${pills || '<span class="selected-extra-pill">No extra metrics.</span>'}
      </div>
    `;
  }).join('');

  return `<div class="selected-extra-pills">${groups}</div>`;
}

function renderSelectedInterviewDataPanel(transcript){
  const speakerIds = (transcript?.speaker_ids || []).filter(Boolean);

  if (!speakerIds.length) {
    renderBaselineInterviewDataPanel();
    return;
  }

  document.getElementById('interview-data-section').innerHTML = `
    <h2>Interview subsample compared with full sample</h2>
    <p class="small">A transcript is selected. The first line in each cell now shows the selected MCID value(s), using the same participant colours as the transcript. The second line remains the full included-sample reference.</p>
    ${table(interviewComparisonHeaders(), selectedParticipantComparisonRows(speakerIds, REPORT.participants || []), {className:'interview-summary-table'})}
    ${renderSelectedExtraPills(speakerIds)}
  `;
}

function renderCategorySlot(slot, slotLabel){
  if (!slot || !slot.transcript_id) {
    return `<span class="category-slot-empty">${escapeHtml(slotLabel)}:<br>—</span>`;
  }

  const missingIds = missingMergedDataIds(slot.speaker_ids || []);
  const missingClass = missingIds.length ? ' category-slot-button-missing-data' : '';
  const missingTitle = missingIds
    .map(participantId => `${participantId}: ${mergedStatusForParticipant(participantId).label}`)
    .join('; ');
  const missingWarning = missingIds.length ? `<span class="category-slot-warning" title="${escapeHtml(missingTitle)}">Not in merged data: ${escapeHtml(missingIds.join(', '))}</span>` : '';

  return `
    <button class="category-slot-button${missingClass}" type="button" data-transcript-id="${escapeHtml(slot.transcript_id)}">
      <strong>${escapeHtml(slotLabel)}: ${escapeHtml(slot.label || slot.filename)}</strong>
      <span class="meta">${escapeHtml(slot.filename)} · ${escapeHtml(slot.n_turns)} turns</span>
      ${missingWarning}
    </button>
  `;
}

function categorySlotsForRow(row){
  const slots = Array.isArray(row?.slots)
    ? row.slots
    : [row?.slot_1, row?.slot_2, row?.slot_3];

  return slots.filter(slot => slot && (slot.transcript_id || slot.filename || slot.label));
}

function renderSelectionCategoryTable(rows){
  const categoryRows = rows || [];

  const idColumnCount = Math.max(
    3,
    ...categoryRows.map(row => categorySlotsForRow(row).length)
  );

  // Keep the whole table inside the visible page.
  // Three slots get a wider criterion column; extra slots progressively shrink it.
  const criterionColumnWidth = idColumnCount <= 3
    ? 34
    : Math.max(18, 34 - ((idColumnCount - 3) * 4));

  const idColumnWidth = (100 - criterionColumnWidth) / idColumnCount;

  const idHeaders = Array.from(
    {length: idColumnCount},
    (_, index) => `<th>ID ${index + 1}</th>`
  ).join('');

  const colgroup = `
    <colgroup>
      <col class="selection-criterion-col" style="width:${criterionColumnWidth.toFixed(4)}%">
      ${Array.from(
        {length: idColumnCount},
        () => `<col class="selection-id-col" style="width:${idColumnWidth.toFixed(4)}%">`
      ).join('')}
    </colgroup>
  `;

  const body = categoryRows.map(row => {
    const slots = categorySlotsForRow(row);

    const slotCells = Array.from({length: idColumnCount}, (_, index) => `
      <td>${renderCategorySlot(slots[index], `ID ${index + 1}`)}</td>
    `).join('');

    return `
      <tr>
        <td>
          <strong>${escapeHtml(row.definition || row.category || '')}</strong>
          ${slots.length > 3 ? `<div class="category-overflow">Expanded from 3 to ${escapeHtml(slots.length)} assigned transcript slots.</div>` : ''}
        </td>
        ${slotCells}
      </tr>
    `;
  }).join('');

  return `
    <div class="table-wrap selection-category-wrap">
      <table
        class="selection-category-table"
        style="--selection-id-count:${escapeHtml(idColumnCount)}; --selection-criterion-width:${criterionColumnWidth.toFixed(4)}%; --selection-id-width:${idColumnWidth.toFixed(4)}%;"
      >
        ${colgroup}
        <thead>
          <tr>
            <th>Selection criterion</th>
            ${idHeaders}
          </tr>
        </thead>
        <tbody>${body || `<tr><td colspan="${idColumnCount + 1}">No selection categories found.</td></tr>`}</tbody>
      </table>
    </div>
  `;
}

function renderInterviewsV2(){
  const data = REPORT.interviews || {};

  document.getElementById('tab-interviews').innerHTML = `
    <section class="card">
      <h2>Prospect interviewees / selected interviews</h2>
      <p class="small">Each selection criterion shows three ID slots by default. If a criterion has more than three assigned interviews, the table expands with equal-width ID columns. Click a filled slot to open the linked transcript below. Click the selected slot again to deselect it. Red slots are interview transcripts whose speaker MCID is not in the included merged dataset.</p>
      ${renderSelectionCategoryTable(data.category_rows || [])}
    </section>

    <section class="card interview-data-panel" id="interview-data-section"></section>

    <section class="card transcript-viewer">
      <h2 id="transcript-title">Transcript</h2>
      <div id="transcript-meta" class="small"></div>
      <div id="transcript-speaker-legend" class="transcript-legend"></div>
      <div id="transcript-turns"></div>
    </section>
  `;

  document.querySelectorAll('.category-slot-button').forEach(button => {
    button.addEventListener('click', () => {
      const wasActive = button.classList.contains('active');
      document.querySelectorAll('.category-slot-button').forEach(item => item.classList.remove('active'));

      if (wasActive) {
        renderBaselineInterviewDataPanel();
        renderTranscriptV2(null);
        return;
      }

      button.classList.add('active');
      const transcript = (data.transcripts || []).find(item => item.transcript_id === button.dataset.transcriptId) || null;
      renderSelectedInterviewDataPanel(transcript);
      renderTranscriptV2(button.dataset.transcriptId);
    });
  });

  renderBaselineInterviewDataPanel();
  renderTranscriptV2(null);
}

function renderTranscriptV2(transcriptId){
  const data = REPORT.interviews || {};
  const transcript = (data.transcripts || []).find(item => item.transcript_id === transcriptId);

  if (!transcript) {
    document.getElementById('transcript-title').textContent = 'Transcript';
    document.getElementById('transcript-meta').textContent = '';
    document.getElementById('transcript-speaker-legend').innerHTML = '';
    document.getElementById('transcript-turns').innerHTML = '<p class="small">Click a filled ID slot above to show a transcript.</p>';
    return;
  }

  document.getElementById('transcript-title').textContent = `Transcript: ${transcript.title}`;
  document.getElementById('transcript-meta').innerHTML =
    `${escapeHtml(transcript.filename)} · speakers: ${escapeHtml((transcript.speaker_ids || []).join(', ') || 'none detected')} · categories: ${escapeHtml((transcript.selection_categories || []).join(', ') || 'none')}`;

  const speakers = uniqueTranscriptSpeakers(transcript);
  document.getElementById('transcript-speaker-legend').innerHTML = speakers.map(speaker => {
    const isResearcher = speaker.toLowerCase() === 'researcher';
    const missingStatus = !isResearcher && !mergedStatusForParticipant(speaker).inMergedData;
    const colours = isResearcher ? {stroke:'#667085', background:'#f2f4f7'} : speakerColour(speaker);
    const title = missingStatus ? ` title="${escapeHtml(mergedStatusForParticipant(speaker).label)}"` : '';
    return `<span class="speaker-chip${missingStatus ? ' missing-merged-data' : ''}" style="--speaker-color:${escapeHtml(colours.stroke)};"${title}>${escapeHtml(speaker)}</span>`;
  }).join('');

  document.getElementById('transcript-turns').innerHTML = (transcript.turns || []).map(turn => {
    const speaker = String(turn.speaker || '').trim();
    const isResearcher = speaker.toLowerCase() === 'researcher';
    const colours = isResearcher ? {stroke:'#667085', background:'#f2f4f7'} : speakerColour(speaker);
    return `
      <div class="turn ${isResearcher ? 'researcher-turn' : 'participant-turn'}" style="--speaker-color:${escapeHtml(colours.stroke)}; --speaker-bg:${escapeHtml(colours.background)};">
        <div class="turn-speaker">${escapeHtml(speaker)}</div>
        <div class="turn-text">${escapeHtml(turn.transcript)}</div>
      </div>
    `;
  }).join('') || '<p class="small">No transcript rows.</p>';
}

function showStartupError(label, error){
  console.error(label, error);

  const mainPanel = document.getElementById('tab-main');
  if (!mainPanel) return;

  const message = error && error.message ? error.message : String(error);

  mainPanel.insertAdjacentHTML('afterbegin', `
    <section class="card">
      <h2 class="status-bad">Render error: ${escapeHtml(label)}</h2>
      <p>${escapeHtml(message)}</p>
      <p class="small">The tab buttons are still initialised so you can inspect other sections.</p>
    </section>
  `);
}

function safeRender(label, fn){
  try {
    fn();
  } catch (error) {
    showStartupError(label, error);
  }
}

function initialiseApp(){
  initialiseTabs();
  bindFigureZoom();

  safeRender('legend', renderLegend);
  safeRender('main tab', renderMain);
  safeRender('other tabs', renderOtherTabs);
}

initialiseApp();