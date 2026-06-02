const API_BASE = '/api/admin/risk';

const SCOPE_OPTIONS = [
    { code: 'GLOBAL', label: '全平台' },
    { code: 'USER', label: '指定用户' },
    { code: 'SYMBOL', label: '指定交易对' },
    { code: 'USER_SYMBOL', label: '用户+交易对' }
];

const ACTION_OPTIONS = [
    { code: 'REJECT', label: '直接拒绝' },
    { code: 'REVIEW', label: '人工审核' },
    { code: 'ALERT', label: '告警放行' }
];

const STATUS_OPTIONS = [
    { code: 'ENABLED', label: '启用' },
    { code: 'DISABLED', label: '停用' }
];

const state = {
    page: 0,
    size: 10,
    total: 0,
    editingId: null,
    ruleTypes: []
};

const els = {
    filterKeyword: document.getElementById('filterKeyword'),
    filterRuleType: document.getElementById('filterRuleType'),
    filterScope: document.getElementById('filterScope'),
    filterStatus: document.getElementById('filterStatus'),
    ruleTableBody: document.getElementById('ruleTableBody'),
    emptyState: document.getElementById('emptyState'),
    paginationInfo: document.getElementById('paginationInfo'),
    ruleModal: document.getElementById('ruleModal'),
    logModal: document.getElementById('logModal'),
    ruleForm: document.getElementById('ruleForm'),
    modalTitle: document.getElementById('modalTitle'),
    ruleCodeGroup: document.getElementById('ruleCodeGroup'),
    ruleCode: document.getElementById('ruleCode'),
    ruleType: document.getElementById('ruleType'),
    ruleTypeHint: document.getElementById('ruleTypeHint'),
    scope: document.getElementById('scope'),
    action: document.getElementById('action'),
    targetUserGroup: document.getElementById('targetUserGroup'),
    targetSymbolGroup: document.getElementById('targetSymbolGroup'),
    logList: document.getElementById('logList'),
    logEmpty: document.getElementById('logEmpty'),
    logModalTitle: document.getElementById('logModalTitle'),
    loadingOverlay: document.getElementById('loadingOverlay'),
    toastContainer: document.getElementById('toastContainer'),
    redisStatus: document.getElementById('redisStatus')
};

function showLoading(show) {
    els.loadingOverlay.classList.toggle('open', show);
}

function toast(message, type = 'success') {
    const node = document.createElement('div');
    node.className = `toast ${type}`;
    node.textContent = message;
    els.toastContainer.appendChild(node);
    setTimeout(() => node.remove(), 3200);
}

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        },
        ...options
    });
    const payload = await response.json();
    if (!response.ok || payload.code !== 200) {
        throw new Error(payload.message || `请求失败 (${response.status})`);
    }
    return payload.data;
}

function fillSelect(select, options, includeEmpty = false, emptyLabel = '请选择') {
    select.innerHTML = '';
    if (includeEmpty) {
        const opt = document.createElement('option');
        opt.value = '';
        opt.textContent = emptyLabel;
        select.appendChild(opt);
    }
    options.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item.code;
        opt.textContent = item.label || item.code;
        select.appendChild(opt);
    });
}

function formatDateTime(value) {
    if (!value) {
        return '-';
    }
    return value.replace('T', ' ').slice(0, 19);
}

function toInputDateTime(value) {
    if (!value) {
        return '';
    }
    return value.slice(0, 16);
}

function fromInputDateTime(value) {
    if (!value) {
        return null;
    }
    return value.length === 16 ? `${value}:00` : value;
}

function updateScopeFields() {
    const scope = els.scope.value;
    els.targetUserGroup.classList.toggle('hidden', scope !== 'USER' && scope !== 'USER_SYMBOL');
    els.targetSymbolGroup.classList.toggle('hidden', scope !== 'SYMBOL' && scope !== 'USER_SYMBOL');
}

function updateRuleTypeHint() {
    const selected = state.ruleTypes.find(item => item.code === els.ruleType.value);
    els.ruleTypeHint.textContent = selected?.description || '';
}

function buildPayload() {
    const thresholdValueRaw = document.getElementById('thresholdValue').value;
    const thresholdConfigRaw = document.getElementById('thresholdConfig').value.trim();
    const payload = {
        ruleName: document.getElementById('ruleName').value.trim(),
        ruleType: els.ruleType.value,
        scope: els.scope.value,
        targetUserId: document.getElementById('targetUserId').value.trim() || null,
        targetSymbol: document.getElementById('targetSymbol').value.trim() || null,
        thresholdValue: thresholdValueRaw === '' ? null : Number(thresholdValueRaw),
        thresholdConfig: thresholdConfigRaw === '' ? null : thresholdConfigRaw,
        action: els.action.value,
        priority: Number(document.getElementById('priority').value),
        description: document.getElementById('description').value.trim() || null,
        effectiveFrom: fromInputDateTime(document.getElementById('effectiveFrom').value),
        effectiveTo: fromInputDateTime(document.getElementById('effectiveTo').value),
        operator: document.getElementById('operator').value.trim()
    };

    if (!state.editingId) {
        payload.ruleCode = els.ruleCode.value.trim();
    }

    if (payload.thresholdConfig) {
        try {
            JSON.parse(payload.thresholdConfig);
        } catch (error) {
            throw new Error('阈值配置 JSON 格式不正确');
        }
    }

    return payload;
}

function resetForm() {
    state.editingId = null;
    els.ruleForm.reset();
    document.getElementById('operator').value = 'admin';
    document.getElementById('priority').value = '10';
    els.ruleCode.disabled = false;
    els.ruleCodeGroup.classList.remove('hidden');
    updateScopeFields();
    updateRuleTypeHint();
}

function openCreateModal() {
    resetForm();
    els.modalTitle.textContent = '新建规则';
    els.ruleModal.classList.add('open');
}

async function openEditModal(id) {
    showLoading(true);
    try {
        const rule = await request(`${API_BASE}/rules/${id}`);
        resetForm();
        state.editingId = id;
        els.modalTitle.textContent = '编辑规则';
        els.ruleCodeGroup.classList.add('hidden');
        els.ruleCode.value = rule.ruleCode;
        document.getElementById('ruleName').value = rule.ruleName || '';
        els.ruleType.value = rule.ruleType;
        els.scope.value = rule.scope;
        document.getElementById('targetUserId').value = rule.targetUserId || '';
        document.getElementById('targetSymbol').value = rule.targetSymbol || '';
        document.getElementById('thresholdValue').value = rule.thresholdValue ?? '';
        document.getElementById('thresholdConfig').value = rule.thresholdConfig || '';
        els.action.value = rule.action;
        document.getElementById('priority').value = rule.priority ?? 10;
        document.getElementById('description').value = rule.description || '';
        document.getElementById('effectiveFrom').value = toInputDateTime(rule.effectiveFrom);
        document.getElementById('effectiveTo').value = toInputDateTime(rule.effectiveTo);
        document.getElementById('operator').value = 'admin';
        updateScopeFields();
        updateRuleTypeHint();
        els.ruleModal.classList.add('open');
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

function closeRuleModal() {
    els.ruleModal.classList.remove('open');
}

function closeLogModal() {
    els.logModal.classList.remove('open');
}

function renderRules(records) {
    els.ruleTableBody.innerHTML = '';
    const hasData = records && records.length > 0;
    els.emptyState.classList.toggle('hidden', hasData);

    if (!hasData) {
        return;
    }

    records.forEach(rule => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="mono">${escapeHtml(rule.ruleCode)}</td>
            <td>${escapeHtml(rule.ruleName)}</td>
            <td>${escapeHtml(rule.ruleTypeLabel || rule.ruleType)}</td>
            <td>${escapeHtml(rule.scopeLabel || rule.scope)}</td>
            <td>${formatThreshold(rule)}</td>
            <td><span class="badge badge-action">${escapeHtml(rule.actionLabel || rule.action)}</span></td>
            <td>${rule.priority ?? '-'}</td>
            <td><span class="badge ${rule.status === 'ENABLED' ? 'badge-enabled' : 'badge-disabled'}">${escapeHtml(rule.statusLabel || rule.status)}</span></td>
            <td>${formatDateTime(rule.updatedAt)}</td>
            <td>
                <div class="row-actions">
                    <button class="btn btn-secondary btn-sm" data-action="edit" data-id="${rule.id}">编辑</button>
                    <button class="btn btn-secondary btn-sm" data-action="toggle" data-id="${rule.id}" data-status="${rule.status}">${rule.status === 'ENABLED' ? '停用' : '启用'}</button>
                    <button class="btn btn-secondary btn-sm" data-action="logs" data-id="${rule.id}" data-code="${escapeHtml(rule.ruleCode)}">日志</button>
                    <button class="btn btn-danger btn-sm" data-action="delete" data-id="${rule.id}">删除</button>
                </div>
            </td>
        `;
        els.ruleTableBody.appendChild(tr);
    });
}

function formatThreshold(rule) {
    if (rule.thresholdValue != null) {
        return String(rule.thresholdValue);
    }
    if (rule.thresholdConfig) {
        const text = rule.thresholdConfig;
        return text.length > 28 ? `${text.slice(0, 28)}...` : text;
    }
    return '-';
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

async function loadRedisVersion() {
    try {
        const data = await request(`${API_BASE}/rules/redis-version`);
        if (data.published && data.version) {
            els.redisStatus.textContent = `Redis v${data.version} · ${data.ruleCount} 条`;
            els.redisStatus.className = 'redis-status ok';
        } else {
            els.redisStatus.textContent = data.message || 'Redis 未同步';
            els.redisStatus.className = 'redis-status warn';
        }
    } catch (error) {
        els.redisStatus.textContent = 'Redis 不可用';
        els.redisStatus.className = 'redis-status warn';
    }
}

async function syncRedis() {
    showLoading(true);
    try {
        const data = await request(`${API_BASE}/rules/sync-redis?operator=admin`, { method: 'POST' });
        if (data.published) {
            toast(`Redis 已同步：v${data.version}，${data.ruleCount} 条规则`);
        } else {
            toast(data.message || 'Redis 同步未完成', 'error');
        }
        await loadRedisVersion();
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

async function loadRules() {
    showLoading(true);
    try {
        const params = new URLSearchParams({
            page: String(state.page),
            size: String(state.size)
        });
        if (els.filterKeyword.value.trim()) params.set('keyword', els.filterKeyword.value.trim());
        if (els.filterRuleType.value) params.set('ruleType', els.filterRuleType.value);
        if (els.filterScope.value) params.set('scope', els.filterScope.value);
        if (els.filterStatus.value) params.set('status', els.filterStatus.value);

        const data = await request(`${API_BASE}/rules?${params.toString()}`);
        state.total = data.total || 0;
        renderRules(data.records || []);
        updatePagination();
        await loadRedisVersion();
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

function updatePagination() {
    const totalPages = Math.max(Math.ceil(state.total / state.size), 1);
    const currentPage = Math.min(state.page + 1, totalPages);
    els.paginationInfo.textContent = `共 ${state.total} 条，第 ${currentPage} / ${totalPages} 页`;
    document.getElementById('btnPrev').disabled = state.page <= 0;
    document.getElementById('btnNext').disabled = state.page + 1 >= totalPages;
}

async function submitForm(event) {
    event.preventDefault();
    showLoading(true);
    try {
        const payload = buildPayload();
        if (state.editingId) {
            await request(`${API_BASE}/rules/${state.editingId}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
            toast('规则更新成功');
        } else {
            await request(`${API_BASE}/rules`, {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            toast('规则创建成功');
        }
        closeRuleModal();
        await loadRules();
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

async function toggleStatus(id, currentStatus) {
    const nextStatus = currentStatus === 'ENABLED' ? 'DISABLED' : 'ENABLED';
    showLoading(true);
    try {
        await request(`${API_BASE}/rules/${id}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status: nextStatus, operator: 'admin' })
        });
        toast(nextStatus === 'ENABLED' ? '规则已启用' : '规则已停用');
        await loadRules();
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

async function deleteRule(id) {
    if (!window.confirm('确认删除该规则？此操作不可恢复。')) {
        return;
    }
    showLoading(true);
    try {
        await request(`${API_BASE}/rules/${id}?operator=admin`, { method: 'DELETE' });
        toast('规则已删除');
        await loadRules();
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

async function openLogs(id, ruleCode) {
    showLoading(true);
    try {
        const data = await request(`${API_BASE}/rules/${id}/change-logs?page=0&size=20`);
        els.logModalTitle.textContent = `变更日志 - ${ruleCode}`;
        els.logList.innerHTML = '';
        const records = data.records || [];
        els.logEmpty.classList.toggle('hidden', records.length > 0);

        records.forEach(log => {
            const item = document.createElement('div');
            item.className = 'log-item';
            item.innerHTML = `
                <div class="log-item-head">
                    <span>${escapeHtml(log.changeType)} · ${escapeHtml(log.operator)}</span>
                    <span>${formatDateTime(log.createdAt)}</span>
                </div>
                ${log.beforeSnapshot ? `<pre>Before: ${escapeHtml(log.beforeSnapshot)}</pre>` : ''}
                ${log.afterSnapshot ? `<pre>After: ${escapeHtml(log.afterSnapshot)}</pre>` : ''}
            `;
            els.logList.appendChild(item);
        });

        els.logModal.classList.add('open');
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

async function loadMetadata() {
    state.ruleTypes = await request(`${API_BASE}/rule-types`);
    fillSelect(els.filterRuleType, state.ruleTypes, true, '全部类型');
    fillSelect(els.ruleType, state.ruleTypes, false);
    fillSelect(els.filterScope, SCOPE_OPTIONS, true, '全部范围');
    fillSelect(els.scope, SCOPE_OPTIONS, false);
    fillSelect(els.filterStatus, STATUS_OPTIONS, true, '全部状态');
    fillSelect(els.action, ACTION_OPTIONS, false);
    updateRuleTypeHint();
}

function resetFilters() {
    els.filterKeyword.value = '';
    els.filterRuleType.value = '';
    els.filterScope.value = '';
    els.filterStatus.value = '';
    state.page = 0;
}

function bindEvents() {
    document.getElementById('btnCreate').addEventListener('click', openCreateModal);
    document.getElementById('btnRefresh').addEventListener('click', loadRules);
    document.getElementById('btnSyncRedis').addEventListener('click', syncRedis);
    document.getElementById('btnSearch').addEventListener('click', () => {
        state.page = 0;
        loadRules();
    });
    document.getElementById('btnReset').addEventListener('click', () => {
        resetFilters();
        loadRules();
    });
    document.getElementById('btnPrev').addEventListener('click', () => {
        if (state.page > 0) {
            state.page -= 1;
            loadRules();
        }
    });
    document.getElementById('btnNext').addEventListener('click', () => {
        state.page += 1;
        loadRules();
    });
    document.getElementById('btnCloseModal').addEventListener('click', closeRuleModal);
    document.getElementById('btnCancelForm').addEventListener('click', closeRuleModal);
    document.getElementById('btnCloseLogModal').addEventListener('click', closeLogModal);
    els.ruleForm.addEventListener('submit', submitForm);
    els.scope.addEventListener('change', updateScopeFields);
    els.ruleType.addEventListener('change', updateRuleTypeHint);

    els.ruleTableBody.addEventListener('click', event => {
        const button = event.target.closest('button[data-action]');
        if (!button) return;
        const { action, id, status, code } = button.dataset;
        if (action === 'edit') openEditModal(id);
        if (action === 'toggle') toggleStatus(id, status);
        if (action === 'delete') deleteRule(id);
        if (action === 'logs') openLogs(id, code);
    });

    els.ruleModal.addEventListener('click', event => {
        if (event.target === els.ruleModal) closeRuleModal();
    });
    els.logModal.addEventListener('click', event => {
        if (event.target === els.logModal) closeLogModal();
    });

    els.filterKeyword.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            state.page = 0;
            loadRules();
        }
    });
}

async function bootstrap() {
    bindEvents();
    showLoading(true);
    try {
        await loadMetadata();
        await loadRules();
        await loadRedisVersion();
    } catch (error) {
        toast(error.message, 'error');
    } finally {
        showLoading(false);
    }
}

bootstrap();
