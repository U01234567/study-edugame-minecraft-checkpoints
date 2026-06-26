const DEFAULT_CONDITION_COLOURS = {
  'Required continue': '#2563eb',
  'Required pauses': '#0f766e',
  'Optional pauses': '#d97706',
  'Overall': '#111827',
  'Missing / invalid': '#b42318',
};

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function reportData() {
  const node = document.getElementById('report-data');
  if (!node) return {};
  try {
    return JSON.parse(node.textContent || '{}');
  } catch (error) {
    return { renderError: String(error) };
  }
}

function table(headers, rows, options = {}) {
  const className = options.className ? ` class="${escapeHtml(options.className)}"` : '';
  const head = `<thead><tr>${headers.map(header => {
    const label = header.labelHtml || escapeHtml(header.label);
    return `<th${header.num ? ' class="num"' : ''}>${label}</th>`;
  }).join('')}</tr></thead>`;
  const bodyRows = (rows || []).map(row => {
    const rowClass = typeof options.rowClass === 'function' ? options.rowClass(row) : '';
    const rowClassAttr = rowClass ? ` class="${escapeHtml(rowClass)}"` : '';
    return `<tr${rowClassAttr}>${headers.map(header => {
      const rawValue = row[header.key] ?? '—';
      const value = typeof header.render === 'function'
        ? header.render(row)
        : header.html ? String(rawValue) : escapeHtml(rawValue);
      return `<td${header.num ? ' class="num"' : ''}>${value}</td>`;
    }).join('')}</tr>`;
  }).join('');
  const emptyRow = `<tr><td colspan="${headers.length}">No rows.</td></tr>`;
  return `<div class="table-wrap"><table${className}>${head}<tbody>${bodyRows || emptyRow}</tbody></table></div>`;
}

function renderMcidListCell(row) {
  const items = row.mcid_items || [];
  if (!items.length) return escapeHtml(row.mcids || '—');
  return `<ul class="mcid-list">${items.map(item => (
    `<li>${escapeHtml(item.mcid)} <span class="mcid-offset">(${escapeHtml(item.offset)})</span></li>`
  )).join('')}</ul>`;
}

function renderWarnings(warnings, options = {}) {
  if (!warnings || !warnings.length) return '';
  const className = options.danger ? 'notice notice-danger' : 'notice';
  const label = options.danger ? 'Retention scoring needs attention:' : 'Note:';
  return `<div class="${className}"><strong>${label}</strong><ul>${warnings.map(item => `<li>${escapeHtml(item)}</li>`).join('')}</ul></div>`;
}

function renderChecklistText(value) {
  const text = String(value ?? '').trim();
  if (!text || text === '—') return '—';
  const lines = text.split(/\r?\n/).map(line => line.trim()).filter(Boolean);
  if (lines.length <= 1) return escapeHtml(text);

  const nodes = [];
  let listItems = [];
  const flushList = () => {
    if (listItems.length) {
      nodes.push(`<ul>${listItems.map(item => `<li>${escapeHtml(item)}</li>`).join('')}</ul>`);
      listItems = [];
    }
  };

  for (const line of lines) {
    if (line.startsWith('- ')) {
      listItems.push(line.slice(2));
    } else {
      flushList();
      nodes.push(`<p>${escapeHtml(line)}</p>`);
    }
  }
  flushList();
  return nodes.join('');
}

function validRetentionScore(value) {
  const text = String(value ?? '').trim();
  if (!/^[0-2]$/.test(text)) return false;
  const number = Number(text);
  return Number.isInteger(number) && number >= 0 && number <= 2;
}

function retentionConflictRow(row) {
  return String(row.answer_std || '').trim() && !validRetentionScore(row.final_score);
}

function sourceLabelsFromRows(rows, prefix) {
  const labels = new Set();
  const labelKey = `${prefix}_labels`;
  for (const row of rows || []) {
    for (const label of row[labelKey] || []) labels.add(label);
    for (const key of Object.keys(row || {})) {
      const match = key.match(new RegExp(`^(${prefix}\\d+(?:_\\d+)?)_score$`));
      if (match) labels.add(match[1]);
    }
  }
  return [...labels].sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));
}

function displaySourceLabel(label) {
  return String(label || '')
    .replace(/^genai/i, 'GenAI ')
    .replace(/^grader/i, 'Grader ')
    .replaceAll('_', ' / ');
}

function conditionColours(data) {
  return data.condition_colours || DEFAULT_CONDITION_COLOURS;
}

function conditionGroups(data) {
  return [...(data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses']), 'Overall'];
}

function groupedBarSvg(categories, groups, valuesByGroupAndCategory, colours) {
  const width = 820;
  const height = 430;
  const margin = { top: 68, right: 24, bottom: 104, left: 52 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const maxCount = Math.max(1, ...groups.flatMap(group => categories.map(category => valuesByGroupAndCategory[group]?.[category] || 0)));
  const categoryWidth = innerWidth / Math.max(1, categories.length);
  const barWidth = Math.max(2, (categoryWidth - 8) / Math.max(1, groups.length));

  const legend = groups.map((group, index) => {
    const x = margin.left + (index % 4) * 170;
    const y = 18 + Math.floor(index / 4) * 20;
    const colour = colours[group] || '#111827';
    return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="${escapeHtml(colour)}" stroke="rgba(0,0,0,.25)"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(group)}</text></g>`;
  }).join('');

  const bars = categories.map((category, categoryIndex) => groups.map((group, groupIndex) => {
    const count = valuesByGroupAndCategory[group]?.[category] || 0;
    const barHeight = innerHeight * count / maxCount;
    const x = margin.left + categoryIndex * categoryWidth + 4 + groupIndex * barWidth;
    const y = margin.top + innerHeight - barHeight;
    const colour = colours[group] || '#111827';
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

function ageYearBucket(row) {
  const age = Number(row.age);
  if (!Number.isFinite(age)) return 'NA';
  if (age >= 30) return '30+';
  if (age >= 16 && age <= 29) return String(age);
  return 'NA';
}

function valuesByGroup(rows, groups, categories, categoriseRow) {
  const output = {};
  for (const group of groups) {
    const scoped = group === 'Overall' ? rows : rows.filter(row => row.condition === group);
    output[group] = Object.fromEntries(categories.map(category => [category, 0]));
    for (const row of scoped) {
      const category = categoriseRow(row);
      if (category in output[group]) output[group][category] += 1;
    }
  }
  return output;
}

function retentionSourceColours(sourceLabels) {
  const palette = ['#2563eb', '#f97316', '#16a34a', '#9333ea', '#0f766e', '#d97706'];
  const colours = {};
  for (const [index, label] of (sourceLabels || []).entries()) {
    colours[displaySourceLabel(label)] = palette[index % palette.length];
  }
  return colours;
}


function hexToRgb(hex) {
  const value = String(hex || '').trim().replace(/^#/, '');
  const normalised = value.length === 3
    ? value.split('').map(char => char + char).join('')
    : value;
  if (!/^[0-9a-f]{6}$/i.test(normalised)) return null;
  const number = parseInt(normalised, 16);
  return {
    r: (number >> 16) & 255,
    g: (number >> 8) & 255,
    b: number & 255,
  };
}

function mixHexColour(hex, target, amount) {
  const rgb = hexToRgb(hex);
  if (!rgb) return hex || '#111827';
  const clamp = value => Math.max(0, Math.min(255, Math.round(value)));
  const mixed = {
    r: clamp(rgb.r + (target.r - rgb.r) * amount),
    g: clamp(rgb.g + (target.g - rgb.g) * amount),
    b: clamp(rgb.b + (target.b - rgb.b) * amount),
  };
  return `#${[mixed.r, mixed.g, mixed.b].map(value => value.toString(16).padStart(2, '0')).join('')}`;
}

function darkerSourceColour(hex) {
  return mixHexColour(hex, { r: 0, g: 0, b: 0 }, 0.22);
}

function lighterSourceColour(hex) {
  return mixHexColour(hex, { r: 255, g: 255, b: 255 }, 0.52);
}

function stackedGroupedBarSvg(categories, groups, segments, valuesByGroupCategorySegment, colours) {
  const width = 820;
  const height = 360;
  const margin = { top: 70, right: 24, bottom: 64, left: 52 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const totals = groups.flatMap(group => categories.map(category => (
    segments.reduce((sum, segment) => sum + Number(valuesByGroupCategorySegment[group]?.[category]?.[segment] || 0), 0)
  )));
  const maxCount = Math.max(1, ...totals);
  const categoryWidth = innerWidth / Math.max(1, categories.length);
  const barWidth = Math.max(5, (categoryWidth - 10) / Math.max(1, groups.length));

  const legend = groups.map((group, index) => {
    const x = margin.left + (index % 4) * 170;
    const y = 18 + Math.floor(index / 4) * 20;
    const colour = colours[group] || '#111827';
    return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="${escapeHtml(colour)}" stroke="rgba(0,0,0,.25)"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(group)}</text></g>`;
  }).join('');

  const segmentLegendX = width - 195;
  const segmentLegend = `<g>
    <rect x="${segmentLegendX}" y="40" width="12" height="12" rx="2" fill="#4b5563"></rect><text x="${segmentLegendX + 18}" y="50" font-size="11">Confident = darker</text>
    <rect x="${segmentLegendX}" y="58" width="12" height="12" rx="2" fill="#d1d5db"></rect><text x="${segmentLegendX + 18}" y="68" font-size="11">Unsure/unknown = lighter</text>
  </g>`;

  const bars = categories.map((category, categoryIndex) => groups.map((group, groupIndex) => {
    const baseColour = colours[group] || '#111827';
    const segmentColours = {
      Confident: darkerSourceColour(baseColour),
      Unsure: lighterSourceColour(baseColour),
    };
    const x = margin.left + categoryIndex * categoryWidth + 5 + groupIndex * barWidth;
    let yCursor = margin.top + innerHeight;
    const total = segments.reduce((sum, segment) => sum + Number(valuesByGroupCategorySegment[group]?.[category]?.[segment] || 0), 0);
    const rects = segments.map(segment => {
      const count = Number(valuesByGroupCategorySegment[group]?.[category]?.[segment] || 0);
      const barHeight = innerHeight * count / maxCount;
      yCursor -= barHeight;
      if (!count) return '';
      const fill = segmentColours[segment] || baseColour;
      return `<rect x="${x}" y="${yCursor}" width="${Math.max(1, barWidth - 1)}" height="${barHeight}" fill="${escapeHtml(fill)}" stroke="rgba(0,0,0,.12)">
        <title>${escapeHtml(group)} | ${escapeHtml(category)} | ${escapeHtml(segment)}: ${count}</title>
      </rect>`;
    }).join('');
    const labelY = Math.max(margin.top + 11, yCursor - 5);
    return `<g>${rects}<text x="${x + Math.max(1, barWidth - 1) / 2}" y="${labelY}" text-anchor="middle" font-size="10">${total || ''}</text></g>`;
  }).join('')).join('');

  const labels = categories.map((category, index) => {
    const x = margin.left + index * categoryWidth + categoryWidth / 2;
    return `<text x="${x}" y="${height - 28}" text-anchor="middle" font-size="12">${escapeHtml(category)}</text>`;
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
    ${segmentLegend}
    ${yTicks.join('')}
    <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    ${bars}
    ${labels}
  </svg>`;
}

function numericArray(values) {
  return (values || []).map(Number).filter(Number.isFinite);
}

function sampleSd(values) {
  if (values.length < 2) return 0;
  const mean = values.reduce((sum, value) => sum + value, 0) / values.length;
  const variance = values.reduce((sum, value) => sum + (value - mean) ** 2, 0) / (values.length - 1);
  return Math.sqrt(Math.max(0, variance));
}

function gaussianDensity(values, x, bandwidth) {
  if (!values.length || bandwidth <= 0) return 0;
  const normaliser = 1 / (values.length * bandwidth * Math.sqrt(2 * Math.PI));
  return normaliser * values.reduce((sum, value) => {
    const z = (x - value) / bandwidth;
    return sum + Math.exp(-0.5 * z * z);
  }, 0);
}

function ridgelineDensitySvg(valuesByGroup, groups, colours, options = {}) {
  const minValue = Number.isFinite(options.min) ? options.min : 0;
  const maxValue = Number.isFinite(options.max) ? options.max : 2;
  const width = 820;
  const rowHeight = 64;
  const height = 118 + rowHeight * Math.max(1, groups.length);
  const margin = { top: 36, right: 34, bottom: 58, left: 94 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const ridgeHeight = Math.min(44, rowHeight * 0.68);
  const rowGap = innerHeight / Math.max(1, groups.length);
  const xSteps = Array.from({ length: 121 }, (_item, index) => minValue + (index / 120) * (maxValue - minValue));
  const scaleX = value => margin.left + ((value - minValue) / Math.max(0.001, maxValue - minValue)) * innerWidth;

  const densitiesByGroup = {};
  let maxDensity = 0;
  for (const group of groups) {
    const values = numericArray(valuesByGroup[group]);
    const sd = sampleSd(values);
    const bandwidth = values.length > 1
      ? Math.max(0.08, 1.06 * (sd || 0.12) * values.length ** -0.2)
      : 0.12;
    const densities = xSteps.map(x => gaussianDensity(values, x, bandwidth));
    densitiesByGroup[group] = { values, densities };
    maxDensity = Math.max(maxDensity, ...densities);
  }
  maxDensity = Math.max(maxDensity, 0.001);

  const xTicks = [0, 0.5, 1, 1.5, 2].map(value => {
    const x = scaleX(value);
    return `<g><line x1="${x}" x2="${x}" y1="${margin.top}" y2="${margin.top + innerHeight}" stroke="#edf2f7"></line><text x="${x}" y="${height - 24}" text-anchor="middle" font-size="11">${value}</text></g>`;
  }).join('');

  const ridges = groups.map((group, index) => {
    const { values, densities } = densitiesByGroup[group];
    const baseY = margin.top + rowGap * index + rowGap * 0.76;
    const colour = colours[group] || '#111827';
    const points = xSteps.map((x, pointIndex) => [
      scaleX(x),
      baseY - (densities[pointIndex] / maxDensity) * ridgeHeight,
    ]);
    const linePath = points.map(([x, y], pointIndex) => `${pointIndex ? 'L' : 'M'}${x.toFixed(1)},${y.toFixed(1)}`).join(' ');
    const areaPath = `M${scaleX(minValue).toFixed(1)},${baseY.toFixed(1)} ${points.map(([x, y]) => `L${x.toFixed(1)},${y.toFixed(1)}`).join(' ')} L${scaleX(maxValue).toFixed(1)},${baseY.toFixed(1)} Z`;
    const rugs = values.map(value => {
      const x = scaleX(Math.max(minValue, Math.min(maxValue, value)));
      return `<line x1="${x}" x2="${x}" y1="${baseY + 3}" y2="${baseY + 12}" stroke="${escapeHtml(colour)}" stroke-width="1.2"><title>${escapeHtml(group)}: ${value.toFixed(3)}</title></line>`;
    }).join('');
    return `<g>
      <text x="${margin.left - 10}" y="${baseY + 4}" text-anchor="end" font-size="12">${escapeHtml(group)}</text>
      <path d="${areaPath}" fill="${escapeHtml(lighterSourceColour(colour))}" stroke="none" opacity="0.9"></path>
      <path d="${linePath}" fill="none" stroke="${escapeHtml(darkerSourceColour(colour))}" stroke-width="2"></path>
      <line x1="${margin.left}" x2="${margin.left + innerWidth}" y1="${baseY}" y2="${baseY}" stroke="#d9e0e4"></line>
      ${rugs}
      <text x="${width - margin.right}" y="${baseY + 4}" text-anchor="end" font-size="11">n=${values.length}</text>
    </g>`;
  }).join('');

  return `<svg class="standalone-figure retention-full-score-density" viewBox="0 0 ${width} ${height}" role="img">
    <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
    ${xTicks}
    <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    ${ridges}
    <text x="${margin.left + innerWidth / 2}" y="${height - 6}" text-anchor="middle" font-size="12">Full retention score (0-2)</text>
  </svg>`;
}

function retentionHistogramValues(chart) {
  const output = {};
  const segments = (chart.segments || [
    { key: 'confident', label: 'Confident' },
    { key: 'unsure', label: 'Unsure' },
  ]);
  for (const sourceLabel of chart.source_labels || []) {
    const displayLabel = displaySourceLabel(sourceLabel);
    output[displayLabel] = {};
    for (const category of chart.categories || []) {
      output[displayLabel][category.label] = {};
      const categoryCounts = chart.counts?.[sourceLabel]?.[category.key];
      if (typeof categoryCounts === 'number') {
        output[displayLabel][category.label].Confident = categoryCounts;
        output[displayLabel][category.label].Unsure = 0;
      } else {
        for (const segment of segments) {
          output[displayLabel][category.label][segment.label] = Number(categoryCounts?.[segment.key] || 0);
        }
      }
    }
  }
  return output;
}

function renderRetentionFullScoreDistributions(retention) {
  const distribution = retention.full_score_distributions || {};
  const sourceLabels = distribution.source_labels || [];
  const moments = distribution.moments || [];
  if (!sourceLabels.length || !moments.length) return '';
  const groups = sourceLabels.map(displaySourceLabel);
  const colours = retentionSourceColours(sourceLabels);

  return `<section class="card retention-full-score-distributions">
    <h2>Full retention score distributions by scoring file</h2>
    <p class="small">Each curve is one full retention score per participant per test occasion and scoring file, calculated on the original 0-2 rubric scale. Components are averaged first, then component means are averaged; missing/unadministered scores are omitted.</p>
    <div class="retention-full-score-grid">
      ${moments.map(moment => {
        const valuesByGroup = {};
        for (const sourceLabel of sourceLabels) {
          valuesByGroup[displaySourceLabel(sourceLabel)] = distribution.values?.[sourceLabel]?.[moment] || [];
        }
        return `<div class="chart-box retention-full-score-chart">
          <h3>${escapeHtml(moment)} full retention</h3>
          ${ridgelineDensitySvg(valuesByGroup, groups, colours, { min: 0, max: 2 })}
        </div>`;
      }).join('')}
    </div>
  </section>`;
}

function renderRetentionHumanGenaiComparison(retention) {
  const block = retention.human_genai_comparison || {};
  const summaryRows = block.summary_rows || [];
  const matrixSections = block.matrix_sections || [];
  if (!summaryRows.length && !matrixSections.length) return '';

  const matrixHtml = matrixSections.length ? `
    <h3>How scores differ</h3>
    <p class="small">Rows are human scores; columns are GenAI scores for the same exact <code>task_id</code> / standardised answer. Off-diagonal cells show where the two sources differ.</p>
    ${matrixSections.map(section => `
      <section class="retention-comparison-matrix">
        <h4>${escapeHtml(section.title || 'Human–GenAI comparison')}</h4>
        <p class="small">${escapeHtml(section.summary || '')}</p>
        ${table([
          { key: 'grader_score', label: 'Human score' },
          { key: 'source_score_0', label: 'GenAI score 0', num: true },
          { key: 'source_score_1', label: 'GenAI score 1', num: true },
          { key: 'source_score_2', label: 'GenAI score 2', num: true },
          { key: 'source_missing', label: 'GenAI missing/invalid', num: true },
          { key: 'total', label: 'Total', num: true },
        ], section.rows || [], { className: 'scale-table retention-comparison-table' })}
      </section>`).join('')}
  ` : '';

  return `<section class="card retention-human-genai-comparison">
    <h2>Human-vs-GenAI comparison for exact reviewed answers</h2>
    <p class="small">This uses the frozen human-review task IDs, so duplicate participant occurrences of the same standardised answer are counted once. The denominator for each progress row is that grader's configured review queue.</p>
    ${table([
      { key: 'grader', label: 'Human grader' },
      { key: 'comparison', label: 'Metric' },
      { key: 'value', label: 'Value' },
      { key: 'detail', label: 'Details' },
    ], summaryRows, { className: 'scale-table retention-human-genai-summary' })}
    ${matrixHtml}
  </section>`;
}

function renderRetentionScoreHistograms(retention) {
  const charts = retention.score_histograms || [];
  if (!charts.length) return '';
  return `<section class="card retention-score-histograms">
    <h2>Retention rubric-score distributions by scoring file</h2>
    <p class="small">Bars are grouped into 0, 1, 2, and Unknown. Within each bar, the darker segment is confident and the lighter segment is unsure. Confident = scored without a note and without confidence below the configured GenAI low-confidence threshold. Human grader files use notes only.</p>
    <div class="retention-score-histogram-grid">
      ${charts.map(chart => {
        const categories = (chart.categories || []).map(category => category.label);
        const groups = (chart.source_labels || []).map(displaySourceLabel);
        const segments = (chart.segments || [
          { key: 'confident', label: 'Confident' },
          { key: 'unsure', label: 'Unsure' },
        ]).map(segment => segment.label);
        if (!categories.length || !groups.length) {
          return `<div class="chart-box retention-score-histogram"><h3>${escapeHtml(chart.label)}</h3><p class="small">No scoring files available yet.</p></div>`;
        }
        return `<div class="chart-box retention-score-histogram">
          <h3>${escapeHtml(chart.label)}</h3>
          <p class="small">Rows represented: ${escapeHtml(chart.n_answer_rows ?? 0)}${(chart.q_elements || []).length ? ` · ${escapeHtml((chart.q_elements || []).join(', '))}` : ''}</p>
          ${stackedGroupedBarSvg(categories, groups, segments, retentionHistogramValues(chart), retentionSourceColours(chart.source_labels || []))}
        </div>`;
      }).join('')}
    </div>
  </section>`;
}

function renderDistributionCharts(data) {
  const participants = data.participants || [];
  const groups = conditionGroups(data);
  const colours = conditionColours(data);
  const conditionContainer = document.getElementById('condition-distribution-chart');
  const ageContainer = document.getElementById('age-distribution-chart');
  const genderContainer = document.getElementById('gender-distribution-chart');

  if (conditionContainer) {
    conditionContainer.innerHTML = groupedBarSvg(
      ['Participants'],
      groups,
      Object.fromEntries(groups.map(group => [
        group,
        { Participants: group === 'Overall' ? participants.length : participants.filter(row => row.condition === group).length },
      ])),
      colours,
    );
  }
  if (ageContainer) {
    const ageCategories = ['16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30+', 'NA'];
    ageContainer.innerHTML = groupedBarSvg(ageCategories, groups, valuesByGroup(participants, groups, ageCategories, ageYearBucket), colours);
  }
  if (genderContainer) {
    const genderCategories = ['Male', 'Female', 'Other', 'Unknown / missing'];
    genderContainer.innerHTML = groupedBarSvg(genderCategories, groups, valuesByGroup(participants, groups, genderCategories, row => row.gender || 'Unknown / missing'), colours);
  }
}

function openFigureModal(figure) {
  if (!figure) return;
  let modal = document.getElementById('figure-modal');
  if (!modal) {
    modal = document.createElement('div');
    modal.id = 'figure-modal';
    modal.hidden = true;
    modal.innerHTML = '<button id="figure-modal-close" type="button" aria-label="Close expanded figure">×</button><div id="figure-modal-content"></div>';
    document.body.appendChild(modal);
  }
  const content = document.getElementById('figure-modal-content');
  if (!content) return;
  content.innerHTML = figure.outerHTML;
  modal.hidden = false;
}

function closeFigureModal() {
  const modal = document.getElementById('figure-modal');
  const content = document.getElementById('figure-modal-content');
  if (!modal || !content) return;
  modal.hidden = true;
  content.innerHTML = '';
}

function bindFigureZoom() {
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

function labSlotStartMinutes(row) {
  const match = String(row?.time || '').match(/^\s*(\d{1,2}):(\d{2})\s*-/);
  if (!match) return Number.POSITIVE_INFINITY;
  return Number(match[1]) * 60 + Number(match[2]);
}

function sortedLabSlotRows(rows) {
  return [...(rows || [])].sort((a, b) => {
    const dateCompare = String(a.date || '').localeCompare(String(b.date || ''));
    if (dateCompare !== 0) return dateCompare;

    const timeCompare = labSlotStartMinutes(a) - labSlotStartMinutes(b);
    if (timeCompare !== 0) return timeCompare;

    return String(a.location || '').localeCompare(String(b.location || ''));
  });
}

function numericValues(rows, key) {
  return (rows || []).map(row => Number(row?.[key])).filter(value => Number.isFinite(value));
}

function quantile(sortedValues, probability) {
  if (!sortedValues.length) return null;
  const index = (sortedValues.length - 1) * probability;
  const lower = Math.floor(index);
  const upper = Math.ceil(index);
  if (lower === upper) return sortedValues[lower];
  return sortedValues[lower] + (sortedValues[upper] - sortedValues[lower]) * (index - lower);
}

function boxStats(values) {
  const sorted = values.filter(value => Number.isFinite(value)).sort((a, b) => a - b);
  if (!sorted.length) return null;
  return {
    min: sorted[0],
    q1: quantile(sorted, 0.25),
    median: quantile(sorted, 0.5),
    q3: quantile(sorted, 0.75),
    max: sorted[sorted.length - 1],
    n: sorted.length,
  };
}

function meanSdParts(value) {
  const text = String(value ?? '').trim();
  if (!text || text === '—') return { mean: '—', sd: '—' };
  const match = text.match(/^(.+?)\s*\((.+?)\)\s*$/);
  if (!match) return { mean: text, sd: '—' };
  return { mean: match[1], sd: match[2] };
}

function microStatHtmlFromSummary(summary) {
  if (!summary) return '—';
  const parts = meanSdParts(summary.mean_sd);
  return `<table class="micro-stat-table"><tbody>
    <tr><th>Mean</th><td>${escapeHtml(parts.mean)}</td></tr>
    <tr><th><em>SD</em></th><td>${escapeHtml(parts.sd)}</td></tr>
    <tr><th>Min</th><td>${escapeHtml(summary.min ?? '—')}</td></tr>
    <tr><th>Max</th><td>${escapeHtml(summary.max ?? '—')}</td></tr>
  </tbody></table>`;
}

function microStatHtmlFromRow(row, prefix) {
  return microStatHtmlFromSummary({
    mean_sd: row?.[`${prefix}_mean_sd`],
    min: row?.[`${prefix}_min`],
    max: row?.[`${prefix}_max`],
  });
}

function scalePerChapterRows(rows) {
  return (rows || []).map(row => ({
    item: row.item,
    n: row.n,
    ch1: microStatHtmlFromRow(row, 'ch1'),
    ch2: microStatHtmlFromRow(row, 'ch2'),
    ch3: microStatHtmlFromRow(row, 'ch3'),
  }));
}

function scaleOverallRows(rows) {
  return (rows || []).map(row => ({
    item: row.item,
    n: row.n,
    overall: microStatHtmlFromSummary(row),
  }));
}

function scaleMergedRows(rows, metrics) {
  return (rows || []).map(row => {
    const output = { condition: row.condition, n: row.n };
    for (const metric of metrics) output[metric.key] = microStatHtmlFromRow(row, metric.key);
    return output;
  });
}

function renderScaleFlags(flags) {
  return `<details class="details-block compact-details">
    <summary>Response-range checks</summary>
    ${table([
      { key: 'scale', label: 'Scale' },
      { key: 'flag', label: 'Flag' },
      { key: 'details', label: 'Details' },
    ], flags || [], { className: 'quality-flag-table' })}
  </details>`;
}

function boxGroup(labelPrimary, labelSecondary, x, colour, stats, scaleY) {
  const yMin = scaleY(stats.min);
  const yQ1 = scaleY(stats.q1);
  const yMedian = scaleY(stats.median);
  const yQ3 = scaleY(stats.q3);
  const yMax = scaleY(stats.max);
  const boxHeight = Math.max(1, Math.abs(yQ3 - yQ1));
  const boxTop = Math.min(yQ1, yQ3);
  const width = 34;
  return `<g>
    <line x1="${x}" x2="${x}" y1="${yMin}" y2="${yMax}" stroke="${escapeHtml(colour)}" stroke-width="2"></line>
    <line x1="${x - width / 3}" x2="${x + width / 3}" y1="${yMin}" y2="${yMin}" stroke="${escapeHtml(colour)}" stroke-width="2"></line>
    <line x1="${x - width / 3}" x2="${x + width / 3}" y1="${yMax}" y2="${yMax}" stroke="${escapeHtml(colour)}" stroke-width="2"></line>
    <rect x="${x - width / 2}" y="${boxTop}" width="${width}" height="${boxHeight}" fill="white" stroke="${escapeHtml(colour)}" stroke-width="2">
      <title>${escapeHtml(labelPrimary)} | ${escapeHtml(labelSecondary)} | n=${stats.n}, median=${stats.median.toFixed(2)}</title>
    </rect>
    <line x1="${x - width / 2}" x2="${x + width / 2}" y1="${yMedian}" y2="${yMedian}" stroke="${escapeHtml(colour)}" stroke-width="3"></line>
    <text x="${x}" y="${Math.max(16, yMax - 6)}" text-anchor="middle" font-size="10">n=${stats.n}</text>
    <text x="${x}" y="${Math.min(420, yMedian - 6)}" text-anchor="middle" font-size="10">${stats.median.toFixed(2)}</text>
  </g>`;
}

function renderBoxplot(containerId, data, metricKeys, options = {}) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const rows = data.participants || [];
  const conditionGroupsForPlot = data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses'];
  const colours = conditionColours(data);
  const width = 920;
  const height = 440;
  const margin = { top: 62, right: 24, bottom: 92, left: 58 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const minValue = options.minValue;
  const maxValue = options.maxValue;
  const scaleY = value => margin.top + innerHeight - ((value - minValue) / Math.max(0.001, maxValue - minValue)) * innerHeight;
  const slotWidth = innerWidth / Math.max(1, metricKeys.length);
  const boxWidthOffset = 44;

  const yTicks = [];
  for (let value = minValue; value <= maxValue; value += 1) {
    yTicks.push(`<g><line x1="${margin.left - 4}" x2="${margin.left + innerWidth}" y1="${scaleY(value)}" y2="${scaleY(value)}" stroke="#d9e0e4"></line><text x="${margin.left - 8}" y="${scaleY(value) + 4}" text-anchor="end" font-size="11">${value}</text></g>`);
  }

  const legend = conditionGroupsForPlot.map((condition, index) => {
    const x = margin.left + (index % 3) * 210;
    const y = 20 + Math.floor(index / 3) * 20;
    const colour = colours[condition] || '#111827';
    return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="white" stroke="${escapeHtml(colour)}" stroke-width="2"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(condition)}</text></g>`;
  }).join('');

  const boxes = [];
  metricKeys.forEach((metric, metricIndex) => {
    conditionGroupsForPlot.forEach((condition, conditionIndex) => {
      const scoped = rows.filter(row => row.condition === condition);
      const stats = boxStats(numericValues(scoped, metric.key));
      if (!stats) return;
      const centreOffset = (conditionIndex - (conditionGroupsForPlot.length - 1) / 2) * boxWidthOffset;
      const x = margin.left + metricIndex * slotWidth + slotWidth / 2 + centreOffset;
      boxes.push(boxGroup(metric.label, condition, x, colours[condition] || '#111827', stats, scaleY));
    });
  });

  const xLabels = metricKeys.map((metric, index) => {
    const x = margin.left + index * slotWidth + slotWidth / 2;
    return `<text x="${x}" y="${height - 46}" text-anchor="middle" font-size="12">${escapeHtml(metric.label)}</text>`;
  }).join('');

  container.innerHTML = `<p class="small">Standalone SVG: centre line = median; boxes = Q1–Q3; whiskers = min–max; labels show <em>n</em> and median.</p>
    <svg class="standalone-figure" viewBox="0 0 ${width} ${height}" role="img">
      <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
      ${legend}
      ${yTicks.join('')}
      <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
      <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
      ${boxes.join('')}
      ${xLabels}
    </svg>`;
}

function renderScaleTableBlocks(blocks, kind) {
  const headers = kind === 'chapter'
    ? [
        { key: 'item', label: 'Item' },
        { key: 'n', labelHtml: '<em>n</em>' },
        { key: 'ch1', label: 'Ch1', html: true },
        { key: 'ch2', label: 'Ch2', html: true },
        { key: 'ch3', label: 'Ch3', html: true },
      ]
    : [
        { key: 'item', label: 'Items' },
        { key: 'n', labelHtml: '<em>n</em>' },
        { key: 'overall', label: 'Overall', html: true },
      ];
  const className = kind === 'chapter' ? 'scale-table scale-per-chapter-table' : 'scale-table scale-overall-table';
  return (blocks || []).map(block => `<section class="subcard">
    <h3>${escapeHtml(block.title)}</h3>
    <p class="small">${escapeHtml(block.description || '')}</p>
    ${table(headers, kind === 'chapter' ? scalePerChapterRows(block.rows) : scaleOverallRows(block.rows), { className })}
  </section>`).join('');
}

function renderScaleTab(data, tabId, config) {
  const panel = document.getElementById(tabId);
  if (!panel) return;
  const scaleData = data.scale_tables?.[config.key] || {};
  panel.innerHTML = `<section class="card">
    <h1>${escapeHtml(config.title)}</h1>
    ${config.perChapter === false ? '' : `<h2>Per Chapter</h2>${renderScaleTableBlocks(scaleData.per_chapter || [], 'chapter')}`}
    <h2>Overall</h2>
    ${renderScaleTableBlocks(scaleData.overall || [], 'overall')}
    <h2>Merged Descriptives</h2>
    ${table([
      { key: 'condition', label: 'Condition' },
      { key: 'n', labelHtml: '<em>n</em>', num: true },
      ...config.metrics.map(metric => ({ key: metric.key, label: metric.label, html: true })),
    ], scaleMergedRows(scaleData.merged || [], config.metrics), { className: 'scale-table scale-merged-table' })}
    ${renderScaleFlags(scaleData.flags || [])}
    <h2>Boxplot</h2>
    <div id="${escapeHtml(config.boxplotId)}"></div>
  </section>`;
  renderBoxplot(config.boxplotId, data, config.metrics, { minValue: config.minValue, maxValue: config.maxValue });
}

function renderScaleTabs(data) {
  renderScaleTab(data, 'tab-cognitive-load', {
    key: 'cognitive_load',
    title: 'Cognitive Load',
    metrics: [
      { key: 'cl_intrinsic', label: 'Intrinsic load' },
      { key: 'cl_extraneous', label: 'Extraneous load' },
      { key: 'cl_germane', label: 'Germane load' },
    ],
    boxplotId: 'cl-boxplot',
    minValue: 0,
    maxValue: 10,
  });
  renderScaleTab(data, 'tab-engagement', {
    key: 'engagement',
    title: 'Engagement',
    metrics: [
      { key: 'eng_main', label: 'Engagement' },
    ],
    boxplotId: 'eng-boxplot',
    minValue: 1,
    maxValue: 7,
  });
  renderScaleTab(data, 'tab-control', {
    key: 'control',
    title: 'Perceived Control',
    perChapter: false,
    metrics: [
      { key: 'ctrl_perceived', label: 'Perceived control' },
    ],
    boxplotId: 'ctrl-boxplot',
    minValue: 1,
    maxValue: 7,
  });
}

function renderMain(data) {
  const rawBlock = data.raw_block || {};
  const delayedBlock = data.delayed_block || {};
  const rawInclusionHeaders = [
    { key: 'reason', label: 'Reason' },
    { key: 'n', labelHtml: '<em>n</em>', num: true },
    { key: 'mcids', label: 'MCIDs' },
  ];
  const delayedInclusionHeaders = [
    { key: 'reason', label: 'Reason' },
    { key: 'n', labelHtml: '<em>n</em>', num: true },
    { key: 'mcids', label: 'MCIDs', render: renderMcidListCell },
  ];
  const conditionRows = data.condition_summary || [];
  const controlling = data.controlling_variables || {};
  const main = document.getElementById('tab-main');
  if (!main) return;

  const labSlotRows = sortedLabSlotRows(controlling.lab_slot_summary || []);
  const conditionOrder = data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses'];
  const locationRows = controlling.location_condition_summary || [];
  const preferredLocations = ['Creative Space', 'Living Room', 'At home', 'Missing / not set'];
  const nonLocationKeys = new Set(['condition', 'n', 'same_room_participants']);
  const observedLocations = new Set();

  for (const row of locationRows) {
    for (const key of Object.keys(row || {})) {
      if (!nonLocationKeys.has(key)) observedLocations.add(key);
    }
  }

  const orderedLocations = [
    ...preferredLocations.filter(location => observedLocations.has(location)),
    ...[...observedLocations].filter(location => !preferredLocations.includes(location)).sort(),
  ];

  const locationHeaders = [
    { key: 'condition', label: 'Condition' },
    { key: 'n', labelHtml: '<em>n</em>', num: true },
    ...orderedLocations.map(location => ({
      key: location,
      label: location === 'At home' ? 'Remote' : location,
      num: true,
    })),
    { key: 'same_room_participants', label: 'Other same-room participants', html: true },
  ];
  main.innerHTML = `
    <section class="card report-header">
      <h1>${escapeHtml(data.meta?.title || 'Merged study summary')}</h1>
      <p>${escapeHtml(data.meta?.data_note || 'This statistics app uses /data/.')}</p>
      <p class="small">${escapeHtml(data.meta?.route_description || '')}</p>
      <p class="small">${escapeHtml(data.meta?.delayed_filter_note || '')}</p>
    </section>

    <section class="card raw-card">
      <h2>${escapeHtml(rawBlock.title || 'Exclusion / inclusion based on /raw/')}</h2>
      <p>${escapeHtml(rawBlock.description || '')}</p>
      ${table(rawInclusionHeaders, rawBlock.rows || [], { className: 'inclusion-checklist-table' })}
    </section>

    <section class="card delayed-card">
      <h2>${escapeHtml(delayedBlock.title || 'Exclusion / inclusion based on delayed response')}</h2>
      <p>${escapeHtml(delayedBlock.description || '')}</p>
      ${table(delayedInclusionHeaders, delayedBlock.rows || [], { className: 'inclusion-checklist-table' })}
    </section>

    <section class="card">
      <h2>Demographics &amp; Conditions</h2>
      <p class="small">Game duration is calculated from <code>/data/logs/</code> as the time from pressing <em>Agree and continue</em> to the first questionnaire-button press.</p>
      ${table([
        { key: 'condition', label: 'Condition' },
        { key: 'n', labelHtml: '<em>n</em>', num: true },
        { key: 'completed_delayed_retention_count', label: 'Delayed completed', num: true },
        { key: 'age', label: 'Age', html: true },
        { key: 'creature_score', label: 'Creature score', html: true },
        { key: 'game_duration', label: 'Game duration', html: true },
        { key: 'questionnaire_duration', label: 'Questionnaire duration', html: true },
        { key: 'delayed_duration', label: 'Delayed duration', html: true },
        { key: 'total_duration', label: 'Total duration', html: true },
      ], conditionRows, { className: 'condition-summary-table' })}
      <div class="chart-grid">
        <div class="chart-box"><h3>Distribution of conditions</h3><div id="condition-distribution-chart"></div></div>
        <div class="chart-box"><h3>Distribution of gender</h3><div id="gender-distribution-chart"></div></div>
        <div class="chart-box"><h3>Distribution of age</h3><div id="age-distribution-chart"></div></div>
      </div>
    </section>

    <section class="card">
      <h2>Collection Context</h2>
      <p class="small">Remote sessions are coded as At home. Lab sessions use <code>/data/config/collection_locations.json</code>. Lab slots are based on the parsed start of each participant’s <code>/data/logs/</code> file. Same-room participants are calculated over occupied lab slots only.</p>
      <h3>Collection context by condition</h3>
      ${table(locationHeaders, locationRows, { className: 'location-context-table' })}

      <details class="details-block">
        <summary>Lab Slots</summary>
        ${table([
          { key: 'date', label: 'Date' },
          { key: 'time', label: 'Time' },
          { key: 'location', label: 'Location' },
          { key: 'n', labelHtml: '<em>n</em>', num: true },
        ], labSlotRows, { className: 'lab-slot-table' })}
      </details>
    </section>
  `;
  renderDistributionCharts(data);
}

function finalRetentionSingleNHtml(cell) {
  if (!cell) return '—';

  const expected = Number(cell.n_expected || 0);
  const valid = Number(cell.n_valid || 0);
  if (!expected) return '—';

  const text = cell.complete ? String(valid) : `${valid}/${expected}`;
  return cell.complete
    ? escapeHtml(text)
    : `<span class="retention-final-n-incomplete">${escapeHtml(text)}</span>`;
}

function finalRetentionNCellHtml(immediateCell, delayedCell) {
  const items = [
    { label: 'Imm.', cell: immediateCell },
    { label: 'Del.', cell: delayedCell },
  ];

  const available = items.filter(item => item.cell);
  if (!available.length) return '—';

  const first = available[0].cell;
  const sameN = available.every(item => (
    Number(item.cell.n_expected || 0) === Number(first.n_expected || 0)
    && Number(item.cell.n_valid || 0) === Number(first.n_valid || 0)
    && Boolean(item.cell.complete) === Boolean(first.complete)
  ));

  if (sameN) return finalRetentionSingleNHtml(first);

  return `<span class="retention-final-n-stack">${
    items.map(item => (
      `<span><strong>${escapeHtml(item.label)}</strong> ${finalRetentionSingleNHtml(item.cell)}</span>`
    )).join('')
  }</span>`;
}

function finalRetentionCellHtml(cell) {
  if (!cell) return '—';

  const expected = Number(cell.n_expected || 0);
  const valid = Number(cell.n_valid || 0);
  if (!expected || !valid) {
    return `<p class="small retention-final-placeholder">${escapeHtml(cell.mean || 'x should appear here when scoring is finished')}</p>`;
  }

  return `<table class="micro-stat-table"><tbody>
    <tr><th>Mean</th><td>${escapeHtml(cell.mean)}</td></tr>
    <tr><th><em>SD</em></th><td>${escapeHtml(cell.sd)}</td></tr>
    <tr><th>Min</th><td>${escapeHtml(cell.min)}</td></tr>
    <tr><th>Max</th><td>${escapeHtml(cell.max)}</td></tr>
  </tbody></table>`;
}

function finalRetentionBoxplotSvg(data, finalBlock) {
  const rows = finalBlock.boxplot_rows || [];
  if (!rows.length) {
    return `<p class="small retention-final-placeholder">${escapeHtml(finalBlock.placeholder || 'x should appear here when scoring is finished')}</p>`;
  }

  const metricKeys = [
    { key: 'Immediate', label: 'Immediate retention' },
    { key: 'Delayed', label: 'Delayed retention' },
  ];
  const conditionGroupsForPlot = data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses'];
  const colours = conditionColours(data);
  const width = 920;
  const height = 440;
  const margin = { top: 62, right: 24, bottom: 92, left: 58 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const minValue = 0;
  const maxValue = 2;
  const scaleY = value => margin.top + innerHeight - ((value - minValue) / Math.max(0.001, maxValue - minValue)) * innerHeight;
  const slotWidth = innerWidth / Math.max(1, metricKeys.length);
  const boxWidthOffset = 44;

  const yTicks = [];
  for (let value = minValue; value <= maxValue; value += 1) {
    yTicks.push(`<g><line x1="${margin.left - 4}" x2="${margin.left + innerWidth}" y1="${scaleY(value)}" y2="${scaleY(value)}" stroke="#d9e0e4"></line><text x="${margin.left - 8}" y="${scaleY(value) + 4}" text-anchor="end" font-size="11">${value}</text></g>`);
  }

  const legend = conditionGroupsForPlot.map((condition, index) => {
    const x = margin.left + (index % 3) * 210;
    const y = 20 + Math.floor(index / 3) * 20;
    const colour = colours[condition] || '#111827';
    return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="white" stroke="${escapeHtml(colour)}" stroke-width="2"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(condition)}</text></g>`;
  }).join('');

  const boxes = [];
  metricKeys.forEach((metric, metricIndex) => {
    conditionGroupsForPlot.forEach((condition, conditionIndex) => {
      const values = rows
        .filter(row => row.condition === condition && row.moment === metric.key)
        .map(row => Number(row.score))
        .filter(value => Number.isFinite(value));
      const stats = boxStats(values);
      if (!stats) return;

      const centreOffset = (conditionIndex - (conditionGroupsForPlot.length - 1) / 2) * boxWidthOffset;
      const x = margin.left + metricIndex * slotWidth + slotWidth / 2 + centreOffset;
      boxes.push(boxGroup(metric.label, condition, x, colours[condition] || '#111827', stats, scaleY));
    });
  });

  const xLabels = metricKeys.map((metric, index) => {
    const x = margin.left + index * slotWidth + slotWidth / 2;
    return `<text x="${x}" y="${height - 46}" text-anchor="middle" font-size="12">${escapeHtml(metric.label)}</text>`;
  }).join('');

  return `<p class="small">Standalone SVG: centre line = median; boxes = Q1–Q3; whiskers = min–max; labels show <em>n</em> and median.</p>
    <svg class="standalone-figure" viewBox="0 0 ${width} ${height}" role="img">
      <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
      ${legend}
      ${yTicks.join('')}
      <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
      <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
      ${boxes.join('')}
      ${xLabels}
    </svg>`;
}

function renderFinalRetentionDescriptives(data, retention) {
  const finalBlock = retention.final_descriptives || {};
  const conditionRows = (finalBlock.condition_rows || []).map(row => ({
    condition: row.condition,
    n: finalRetentionNCellHtml(row.immediate, row.delayed),
    immediate: finalRetentionCellHtml(row.immediate),
    delayed: finalRetentionCellHtml(row.delayed),
  }));

  const questionTablesHtml = (finalBlock.question_tables || []).map(questionTable => {
    const rows = (questionTable.rows || []).map(row => ({
      item: row.item,
      n: finalRetentionNCellHtml(row.immediate, row.delayed),
      immediate: finalRetentionCellHtml(row.immediate),
      delayed: finalRetentionCellHtml(row.delayed),
    }));

    return `<section class="subcard">
      <h3>${escapeHtml(questionTable.title)}</h3>
      ${table([
        { key: 'item', label: 'Item' },
        { key: 'n', labelHtml: '<em>n</em>', html: true },
        { key: 'immediate', label: 'Immediate', html: true },
        { key: 'delayed', label: 'Delayed', html: true },
      ], rows, { className: 'scale-table scale-retention-final-table' })}
      <p class="small"><em>Note.</em> ${escapeHtml(questionTable.note || 'Retention scores range from 0 (= fully wrong) to 2 (= fully correct).')}</p>
    </section>`;
  }).join('');

  return `<section class="card retention-final-descriptives">
    <h2>Final retention descriptives</h2>
    <p class="small">These tables use only adjudicated <code>final_score</code> values from <code>retention_scores_merged.tsv</code>. Red <em>n</em> values indicate that not every expected final score is valid yet.</p>
    ${renderWarnings(finalBlock.warnings || [])}

    <h3>Final retention score by condition</h3>
    ${table([
      { key: 'condition', label: 'Condition' },
      { key: 'n', labelHtml: '<em>n</em>', html: true },
      { key: 'immediate', label: 'Immediate retention', html: true },
      { key: 'delayed', label: 'Delayed retention', html: true },
    ], conditionRows, { className: 'scale-table scale-merged-table' })}

    <h3>Final retention score by question element</h3>
    ${questionTablesHtml || `<p class="small retention-final-placeholder">${escapeHtml(finalBlock.placeholder || 'x should appear here when scoring is finished')}</p>`}

    <h3>Final retention score boxplot</h3>
    ${finalRetentionBoxplotSvg(data, finalBlock)}
  </section>`;
}

function renderRetention(data) {
  const panel = document.getElementById('tab-retention');
  if (!panel) return;

  const retention = data.retention || {};
  const reliability = retention.reliability || {};
  const reliabilityRows = reliability.rows || [];
  const rows = retention.answer_rows || [];
  const questions = retention.questions || [];
  const showGrades = Boolean(retention.show_grades);
  const unresolvedConflictCount = Number.isFinite(Number(retention.unresolved_conflict_count))
    ? Number(retention.unresolved_conflict_count)
    : rows.filter(retentionConflictRow).length;

  const summaryHeaders = [
    { key: 'condition', label: 'Condition' },
    { key: 'n', labelHtml: '<em>n</em>', num: true },
    { key: 'ret_delayed_wave_count', label: 'Completed delayed retention', num: true },
    { key: 'ret_immediate_answer_count', label: 'Immediate answer count', num: true },
    { key: 'ret_delayed_answer_count', label: 'Delayed answer count', num: true },
  ];

  if (showGrades) {
    summaryHeaders.push(
      { key: 'ret_immediate_scored_prompt_count', label: 'Immediate scored elements', num: true },
      { key: 'ret_delayed_scored_prompt_count', label: 'Delayed scored elements', num: true },
      { key: 'ret_immediate_scored_component_count', label: 'Immediate scored components', num: true },
      { key: 'ret_delayed_scored_component_count', label: 'Delayed scored components', num: true },
      { key: 'ret_immediate_mean_sd', label: 'Immediate score Mean (SD)' },
      { key: 'ret_delayed_mean_sd', label: 'Delayed score Mean (SD)' },
    );
  }

  const reliabilityHtml = showGrades ? `
    <section class="card">
      <h2>Interrater agreement</h2>
      <p>${escapeHtml(reliability.method || 'Reliability summary not available.')}</p>
      ${table([
        { key: 'group', label: 'Agreement group' },
        { key: 'n_units', label: 'Alpha units', num: true },
        { key: 'ordinal_krippendorff_alpha', label: 'Ordinal Krippendorff α', num: true },
        { key: 'n_double_scored', label: 'n used', num: true },
        { key: 'n_unique_double_scored', label: 'Unique reviewed n', num: true },
        { key: 'n_weighted_occurrences', label: 'Occurrence-weighted n', num: true },
        { key: 'exact_agreement_percent', label: 'Exact agreement %', num: true },
        { key: 'quadratic_weighted_kappa', label: 'Quadratic weighted κ', num: true },
      ], reliabilityRows)}
    </section>` : '';

  const conflictHtml = showGrades ? `
    <p class="retention-conflict-count ${unresolvedConflictCount ? 'has-conflicts' : ''}">
      Final-score conflicts still to resolve: <strong>${escapeHtml(unresolvedConflictCount)}</strong>
    </p>` : '';

  const checksHtml = (retention.checks || []).length ? `
    <section class="card">
      <h2>Retention scoring checks</h2>
      <p class="small">Fix the first row that is not ✅. Later rows may be waiting only because an earlier prerequisite is incomplete.</p>
      ${table([
        { key: 'step', label: 'Step' },
        { key: 'check', label: 'Requirement' },
        { key: 'status', label: 'Status' },
        { key: 'detail', label: 'Gathered', render: row => renderChecklistText(row.detail) },
        { key: 'action', label: 'What to do', render: row => renderChecklistText(row.action) },
      ], retention.checks)}
    </section>` : '';

  const finalRetentionHtml = renderFinalRetentionDescriptives(data, retention);
  const fullScoreDistributionsHtml = renderRetentionFullScoreDistributions(retention);
  const humanGenaiComparisonHtml = renderRetentionHumanGenaiComparison(retention);
  const scoreHistogramsHtml = renderRetentionScoreHistograms(retention);

  const summaryHtml = `
    <section class="card">
      <h1>Retention</h1>
      ${renderWarnings(retention.warnings || [], { danger: true })}
      ${conflictHtml}
      ${table(summaryHeaders, retention.condition_summary || [])}
    </section>
    ${checksHtml}
    ${humanGenaiComparisonHtml}
    ${finalRetentionHtml}
    ${fullScoreDistributionsHtml}
    ${scoreHistogramsHtml}
    ${reliabilityHtml}`;

  if (!rows.length) {
    panel.innerHTML = summaryHtml + '<section class="card"><h2>Retention answers by creature and question</h2><p>No retention answers found for included participants.</p></section>';
    return;
  }

  const creatures = [...new Map(rows.map(row => [row.creature_id, row])).values()]
    .sort((a, b) => String(a.creature_name).localeCompare(String(b.creature_name)));
  const answerHeadersBase = [
    { key: 'participant_id', label: 'MCID' },
    { key: 'moment', label: 'Moment' },
    { key: 'answer', label: 'Answer' },
  ];
  const genaiLabels = sourceLabelsFromRows(rows, 'genai');
  const graderLabels = sourceLabelsFromRows(rows, 'grader');
  const answerHeadersWithGrades = [
    { key: 'participant_id', label: 'MCID' },
    { key: 'moment', label: 'Moment' },
    { key: 'answer', label: 'Original answer' },
    { key: 'answer_std', label: 'Standardised answer' },
    ...genaiLabels.flatMap(label => [
      { key: `${label}_score`, label: `${displaySourceLabel(label)} score`, num: true },
      { key: `${label}_confidence`, label: `${displaySourceLabel(label)} confidence`, num: true },
    ]),
    ...graderLabels.map(label => ({ key: `${label}_score`, label: `${displaySourceLabel(label)} score`, num: true })),
    { key: 'final_score', label: 'Final score' },
    { key: 'final_note_auto', label: 'Auto final note' },
    { key: 'final_note_manual', label: 'Manual final note' },
  ];

  const creatureButtons = creatures.map((creature, index) =>
    `<button class="creature-tab-button ${index === 0 ? 'active' : ''}" data-group="creatures" data-target="creature-${escapeHtml(creature.creature_id)}">${escapeHtml(creature.creature_name)}</button>`
  ).join('');

  const creaturePanels = creatures.map((creature, creatureIndex) => {
    const questionButtons = questions.map((question, questionIndex) =>
      `<button class="question-tab-button ${questionIndex === 0 ? 'active' : ''}" data-group="question-${escapeHtml(creature.creature_id)}" data-target="question-${escapeHtml(creature.creature_id)}-${escapeHtml(question.key)}">${escapeHtml(question.key)}</button>`
    ).join('');
    const questionPanels = questions.map((question, questionIndex) => {
      const questionRows = rows
        .filter(row => row.creature_id === creature.creature_id && row.question === question.key)
        .map(row => {
          const output = { participant_id: row.participant_id, moment: row.moment, answer: row.answer };
          if (showGrades) {
            output.answer_std = row.answer_std;
            for (const label of genaiLabels) {
              output[`${label}_score`] = row[`${label}_score`];
              output[`${label}_confidence`] = row[`${label}_confidence`];
            }
            for (const label of graderLabels) {
              output[`${label}_score`] = row[`${label}_score`];
            }
            output.final_score = row.final_score;
            output.final_note_auto = row.final_note_auto;
            output.final_note_manual = row.final_note_manual;
          }
          return output;
        });
      return `<section id="question-${escapeHtml(creature.creature_id)}-${escapeHtml(question.key)}" class="question-panel ${questionIndex === 0 ? 'active' : ''}" data-group="question-${escapeHtml(creature.creature_id)}"><h3>${escapeHtml(question.label)}</h3>${table(showGrades ? answerHeadersWithGrades : answerHeadersBase, questionRows, {
          className: 'retention-answer-table',
          rowClass: showGrades ? row => (retentionConflictRow(row) ? 'retention-conflict-row' : '') : undefined,
        })}</section>`;
    }).join('');
    return `<section id="creature-${escapeHtml(creature.creature_id)}" class="creature-panel ${creatureIndex === 0 ? 'active' : ''}" data-group="creatures"><h2>${escapeHtml(creature.creature_name)}</h2><nav class="question-tabs">${questionButtons}</nav>${questionPanels}</section>`;
  }).join('');

  panel.innerHTML = summaryHtml + `<section class="card" id="retention-answer-browser"><h2>Retention answers by creature and question</h2><nav class="creature-tabs">${creatureButtons}</nav>${creaturePanels}</section>`;
  initialiseScopedTabs('retention-answer-browser', 'creature-tab-button', 'creature-panel');
  initialiseScopedTabs('retention-answer-browser', 'question-tab-button', 'question-panel');
}

function logThemeRows(rows, conditions) {
  return (rows || []).map(row => {
    const output = { metric: row.metric };
    for (const condition of conditions) output[condition] = microStatHtmlFromSummary(row[condition]);
    return output;
  });
}

function renderLogSummaryBlocks(blocks, conditions) {
  const headers = [
    { key: 'metric', label: 'Metric' },
    ...conditions.map(condition => ({ key: condition, label: condition, html: true })),
  ];
  return (blocks || []).map(block => `<section class="subcard">
    <h2>${escapeHtml(block.title)}</h2>
    <p class="small">${escapeHtml(block.description || '')}</p>
    ${table(headers, logThemeRows(block.rows || [], conditions), { className: 'log-summary-table compact-report-table' })}
  </section>`).join('');
}

function formatAxisValue(value, kind) {
  if (!Number.isFinite(value)) return '—';
  if (kind === 'percent') return `${value.toFixed(0)}%`;
  if (kind === 'duration') {
    const seconds = Math.round(value);
    const minutes = Math.floor(seconds / 60);
    const remainder = seconds % 60;
    if (minutes >= 60) return `${Math.floor(minutes / 60)}h ${minutes % 60}m`;
    if (minutes > 0) return `${minutes}m ${remainder}s`;
    return `${remainder}s`;
  }
  return Math.abs(value) >= 10 ? value.toFixed(0) : value.toFixed(2);
}

function renderAutoBoxplot(containerId, data, metric) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const rows = data.participants || [];
  const groups = data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses'];
  const colours = conditionColours(data);
  const width = 820;
  const height = 380;
  const margin = { top: 58, right: 24, bottom: 78, left: 72 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const allValues = groups.flatMap(group => numericValues(rows.filter(row => row.condition === group), metric.key));
  if (!allValues.length) {
    container.innerHTML = `<p class="small">No values available for ${escapeHtml(metric.label)}.</p>`;
    return;
  }
  const rawMin = Math.min(...allValues);
  const rawMax = Math.max(...allValues);
  const padding = Math.max((rawMax - rawMin) * 0.08, rawMax === rawMin ? 1 : 0);
  const minValue = Number.isFinite(metric.min) ? metric.min : Math.max(0, rawMin - padding);
  const maxValue = Number.isFinite(metric.max) ? metric.max : rawMax + padding;
  const scaleY = value => margin.top + innerHeight - ((value - minValue) / Math.max(0.001, maxValue - minValue)) * innerHeight;
  const slotWidth = innerWidth / Math.max(1, groups.length);
  const boxes = groups.map((group, index) => {
    const stats = boxStats(numericValues(rows.filter(row => row.condition === group), metric.key));
    if (!stats) return '';
    const x = margin.left + index * slotWidth + slotWidth / 2;
    return boxGroup(metric.label, group, x, colours[group] || '#111827', stats, scaleY);
  }).join('');
  const yTicks = [];
  for (let index = 0; index <= 4; index += 1) {
    const value = minValue + (index / 4) * (maxValue - minValue);
    const y = scaleY(value);
    yTicks.push(`<g><line x1="${margin.left - 4}" x2="${margin.left + innerWidth}" y1="${y}" y2="${y}" stroke="#d9e0e4"></line><text x="${margin.left - 8}" y="${y + 4}" text-anchor="end" font-size="11">${escapeHtml(formatAxisValue(value, metric.kind))}</text></g>`);
  }
  const xLabels = groups.map((group, index) => {
    const x = margin.left + index * slotWidth + slotWidth / 2;
    return `<text x="${x}" y="${height - 42}" text-anchor="middle" font-size="11">${escapeHtml(group)}</text>`;
  }).join('');
  container.innerHTML = `<h3>${escapeHtml(metric.label)}</h3><svg class="standalone-figure" viewBox="0 0 ${width} ${height}" role="img">
    <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
    ${yTicks.join('')}
    <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    ${boxes}
    ${xLabels}
  </svg>`;
}

function formatNWithTotalAndPercent(count, total) {
  if (!total) return `${count} / 0 (NA)`;
  return `${count} / ${total} (${(100 * count / total).toFixed(1)}%)`;
}

function sixthCreatureRowsForChapter(report, conditions, chapterLabel) {
  return conditions.map(condition => {
    const row = (report.time_to_sixth_creature || []).find(item => item.condition === condition && item.chapter === chapterLabel);
    const count = Number(row?.n ?? 0);
    const total = Number(row?.total ?? 0);
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

function sixthCreatureSummaryTable(report, conditions, chapterLabel) {
  return `
    <h3>${escapeHtml(chapterLabel)}</h3>
    ${table([
      {key:'condition', label:'Condition'},
      {key:'n_display', label:'n / total (%)'},
      {key:'mean_sd', label:'Mean (SD)'},
      {key:'min', label:'Min'},
      {key:'max', label:'Max'}
    ], sixthCreatureRowsForChapter(report, conditions, chapterLabel), { className: 'log-summary-table compact-report-table' })}
  `;
}

function renderTimeManagementBoxplot(containerId, data) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const rows = data.participants || [];
  const groups = data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses'];
  const colours = conditionColours(data);
  const metrics = [
    {key:'walking_sprinting_seconds_estimate', label:'Moving'},
    {key:'card_reading_seconds', label:'Reading cards'},
    {key:'other_seconds_estimate', label:'Still / other'}
  ];
  const allValues = metrics.flatMap(metric => numericValues(rows, metric.key));
  if (!allValues.length) {
    container.innerHTML = '<p class="small">No time-management values available.</p>';
    return;
  }

  const width = 920;
  const height = 440;
  const margin = { top: 72, right: 24, bottom: 84, left: 72 };
  const innerWidth = width - margin.left - margin.right;
  const innerHeight = height - margin.top - margin.bottom;
  const minValue = 0;
  const maxRaw = Math.max(...allValues);
  const maxValue = Math.max(60, Math.ceil(maxRaw / 60) * 60);
  const scaleY = value => margin.top + innerHeight - ((value - minValue) / Math.max(0.001, maxValue - minValue)) * innerHeight;
  const slotWidth = innerWidth / Math.max(1, metrics.length);
  const boxWidthOffset = 44;

  const legend = groups.map((condition, index) => {
    const x = margin.left + (index % 3) * 230;
    const y = 22 + Math.floor(index / 3) * 20;
    const colour = colours[condition] || '#111827';
    return `<g><rect x="${x}" y="${y - 10}" width="12" height="12" rx="2" fill="white" stroke="${escapeHtml(colour)}" stroke-width="2"></rect><text x="${x + 18}" y="${y}" font-size="12">${escapeHtml(condition)}</text></g>`;
  }).join('');

  const yTicks = [];
  for (let index = 0; index <= 4; index += 1) {
    const value = minValue + (index / 4) * (maxValue - minValue);
    const y = scaleY(value);
    yTicks.push(`<g><line x1="${margin.left - 4}" x2="${margin.left + innerWidth}" y1="${y}" y2="${y}" stroke="#d9e0e4"></line><text x="${margin.left - 8}" y="${y + 4}" text-anchor="end" font-size="11">${escapeHtml(formatAxisValue(value, 'duration'))}</text></g>`);
  }

  const boxes = [];
  metrics.forEach((metric, metricIndex) => {
    groups.forEach((condition, conditionIndex) => {
      const scoped = rows.filter(row => row.condition === condition);
      const stats = boxStats(numericValues(scoped, metric.key));
      if (!stats) return;
      const centreOffset = (conditionIndex - (groups.length - 1) / 2) * boxWidthOffset;
      const x = margin.left + metricIndex * slotWidth + slotWidth / 2 + centreOffset;
      boxes.push(boxGroup(metric.label, condition, x, colours[condition] || '#111827', stats, scaleY));
    });
  });

  const xLabels = metrics.map((metric, index) => {
    const x = margin.left + index * slotWidth + slotWidth / 2;
    return `<text x="${x}" y="${height - 42}" text-anchor="middle" font-size="12">${escapeHtml(metric.label)}</text>`;
  }).join('');

  container.innerHTML = `<h3>Time management</h3><p class="small">Moving combines walking and sprinting. Centre line = median; boxes = Q1–Q3; whiskers = min–max.</p>
  <svg class="standalone-figure" viewBox="0 0 ${width} ${height}" role="img">
    <rect x="0" y="0" width="${width}" height="${height}" fill="white"></rect>
    ${legend}
    ${yTicks.join('')}
    <line x1="${margin.left}" y1="${margin.top}" x2="${margin.left}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    <line x1="${margin.left}" y1="${margin.top + innerHeight}" x2="${margin.left + innerWidth}" y2="${margin.top + innerHeight}" stroke="#5f6c73"></line>
    ${boxes.join('')}
    ${xLabels}
  </svg>`;
}

function renderGameLogs(data) {
  const panel = document.getElementById('tab-logs');
  if (!panel) return;
  const report = data.game_logs || {};
  const conditions = [...(data.condition_order || ['Required continue', 'Required pauses', 'Optional pauses']), 'Overall'];
  const visualMetrics = report.visual_metrics || [];
  const visualHtml = [
    '<div class="chart-box"><div id="log-time-management-boxplot"></div></div>',
    ...visualMetrics.map((metric, index) => `<div class="chart-box"><div id="log-boxplot-${index}"></div></div>`)
  ].join('');

  panel.innerHTML = `<section class="card">
    <h1>Game Logs</h1>
    <p class="small">Focused per-condition summaries from <code>/data/logs/</code>. Tables are grouped by theme, with visuals placed at the end.</p>
    ${renderLogSummaryBlocks(report.theme_blocks || [], conditions)}
    <section class="subcard">
      <h2>Chapter Pacing</h2>
      <p class="small">Time from chapter start until the sixth unique species was first opened in that chapter. The <em>n / total (%)</em> column shows how many participants reached the sixth species within each condition.</p>
      ${sixthCreatureSummaryTable(report, conditions, 'Ch1')}
      ${sixthCreatureSummaryTable(report, conditions, 'Ch2')}
      ${sixthCreatureSummaryTable(report, conditions, 'Ch3')}
    </section>
    <section class="subcard">
      <h2>Checkpoint Decisions</h2>
      <p class="small">Included optional-pauses participants only. Only manipulated choices between Ch1 > Ch2 and Ch2 > Ch3 are counted.</p>
      <h3>Common manipulated-choice patterns</h3>
      ${table([
        {key:'pattern', label:'Pattern'},
        {key:'n', label:'n', num:true}
      ], report.optional_pause_choice_patterns || [], { className: 'log-summary-table compact-report-table' })}
    </section>
    <section class="subcard">
      <h2>Visuals</h2>
      <p class="small">Boxplots show the distribution within each condition. Centre line = median; boxes = Q1–Q3; whiskers = min–max.</p>
      <div class="chart-grid">${visualHtml}</div>
    </section>
  </section>`;

  renderTimeManagementBoxplot('log-time-management-boxplot', data);
  visualMetrics.forEach((metric, index) => renderAutoBoxplot(`log-boxplot-${index}`, data, metric));
}

function formatDuration(seconds){ if(!Number.isFinite(seconds)) return ''; const rounded=Math.round(seconds); const hours=Math.floor(rounded/3600); const minutes=Math.floor((rounded%3600)/60); const remainingSeconds=rounded%60; if(hours) return `${hours}h ${minutes}m ${remainingSeconds}s`; if(minutes) return `${minutes}m ${remainingSeconds}s`; return `${remainingSeconds}s`; }

function mean(values) {
  const cleanValues = (values || []).map(Number).filter(Number.isFinite);
  if (!cleanValues.length) return 0;
  return cleanValues.reduce((total, value) => total + value, 0) / cleanValues.length;
}

function sampleSd(values) {
  if (values.length <= 1) return 0;
  const average = mean(values);
  const variance = values.reduce((total, value) => total + Math.pow(value - average, 2), 0) / (values.length - 1);
  return Math.sqrt(variance);
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
  {label:'Immediate scored elements', source:'participant', key:'ret_immediate_scored_prompt_count', kind:'number0'},
  {label:'Delayed scored elements', source:'participant', key:'ret_delayed_scored_prompt_count', kind:'number0'},

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

  if (key === 'gender') {
    const genderOrder = ['Male', 'Female', 'Other', 'Unknown / missing'];
    const extraLabels = Object.keys(counts).filter(label => !genderOrder.includes(label)).sort();
    return [...genderOrder, ...extraLabels].map(label => `${label}: ${counts[label] || 0}`).join(', ');
  }

  return Object.entries(counts).map(([label, n]) => `${label}: ${n}`).join(', ') || '—';
}

function nestedChapterValue(log, chapter){
  return finiteNumber((log?.time_to_sixth_creature_by_chapter || {})[String(chapter)]);
}

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

function renderInterviews(data) {
  window.REPORT = data || window.REPORT || {};
  if (window.REPORT.game_logs && !window.REPORT.logs) {
    window.REPORT.logs = window.REPORT.game_logs;
  }
  renderInterviewsV2();
}




function formatValue(value, header={}){ if(value===null||value===undefined||value==='') return '—'; if(header.fixed&&typeof value==='number') return value.toFixed(2); if(header.html) return String(value??''); if(Array.isArray(value)) return escapeHtml(value.join(', ')); if(typeof value==='boolean') return value?'true':'false'; return escapeHtml(value); }

function statusText(ok, okText, badText){ return ok ? `<span class="status-ok">${escapeHtml(okText)}</span>` : `<span class="status-bad">${escapeHtml(badText)}</span>`; }

function showReportTab(tab, options = {}) {
  const navTab = options.navTab || tab;
  document.querySelectorAll('.tab-panel').forEach(panel => panel.classList.remove('active'));
  const panel = document.getElementById(`tab-${tab}`);
  if (panel) panel.classList.add('active');

  document.querySelectorAll('.tab-button').forEach(button => button.classList.remove('active'));
  const navButton = [...document.querySelectorAll('.tab-button')].find(button => button.dataset.tab === navTab);
  if (navButton) navButton.classList.add('active');

  window.scrollTo({ top: 0, left: 0, behavior: 'instant' });
}


function renderTbdTabs(data) {
  const tabs = data.tabs || {};
  Object.keys(tabs).forEach(tab => {
    if (tab === 'main') return;
    if (tab === 'retention' && data.retention) return;
    if (['cognitive-load', 'engagement', 'control'].includes(tab) && data.scale_tables) return;
    if (tab === 'logs' && data.game_logs) return;
    if (tab === 'interviews' && data.interviews) return;
    const panel = document.getElementById(`tab-${tab}`);
    if (!panel) return;
    panel.innerHTML = '<section class="card tbd">TBD</section>';
  });
}

function initialiseTabs() {
  document.querySelectorAll('.tab-button').forEach(button => {
    button.addEventListener('click', () => showReportTab(button.dataset.tab));
  });
}

function initialiseScopedTabs(containerId, buttonClass, panelClass) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.querySelectorAll(`.${buttonClass}`).forEach(button => {
    button.addEventListener('click', () => {
      const targetPanel = document.getElementById(button.dataset.target);
      const group = button.dataset.group || '';

      container.querySelectorAll(`.${buttonClass}`).forEach(item => {
        if (!group || item.dataset.group === group) item.classList.remove('active');
      });

      container.querySelectorAll(`.${panelClass}`).forEach(item => {
        if (!group || item.dataset.group === group) item.classList.remove('active');
      });

      button.classList.add('active');
      if (targetPanel) targetPanel.classList.add('active');
    });
  });
}

function render() {
  const data = reportData();
  window.REPORT = data;
  if (window.REPORT.game_logs && !window.REPORT.logs) {
    window.REPORT.logs = window.REPORT.game_logs;
  }
  if (data.renderError) {
    const main = document.getElementById('tab-main');
    if (main) main.innerHTML = `<section class="card"><h1>Render error</h1><p>${escapeHtml(data.renderError)}</p></section>`;
    return;
  }
  renderMain(data);
  renderRetention(data);
  renderScaleTabs(data);
  renderGameLogs(data);
  renderInterviews(data);
  renderTbdTabs(data);
  initialiseTabs();
  bindFigureZoom();
}

render();
