from __future__ import annotations

import datetime as dt
import hashlib
import html
import json
import sys
import threading
import traceback
import webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from helpers._logs_main import load_log_index
from helpers._main_overview import build_merged_dataset, split_survey_waves, survey_progress, survey_start
from helpers._shared import LOG_DIR, OUTPUT_DIR, SURVEY_EXPORT_PATH, clean, display_datetime, first_present, parse_datetime
from helpers._survey_io import load_tsv

APP_TITLE = "Follow-up email app"
DEFAULT_PORT = 8767
STATE_VERSION = 1
SLOT_LABELS = [
    "09:40–10:40", "10:40–11:40", "11:40–12:40", "12:40–13:40",
    "13:40–14:40", "14:40–15:40", "15:40–16:40", "Outside 09:40–16:40",
]


def now_iso() -> str:
    return dt.datetime.now().isoformat(timespec="seconds")


def stamp() -> str:
    return dt.datetime.now().strftime("%Y%m%dT%H%M%S-%f")


def rel(path: Path) -> str:
    try:
        return str(path.resolve().relative_to(REPO_ROOT.resolve()))
    except ValueError:
        return str(path.resolve())


def file_signature(path: Path) -> dict[str, Any]:
    if not path.exists():
        return {"exists": False, "path": str(path)}
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    stat = path.stat()
    return {
        "exists": True,
        "path": str(path),
        "name": path.name,
        "size_bytes": stat.st_size,
        "mtime": dt.datetime.fromtimestamp(stat.st_mtime).isoformat(timespec="seconds"),
        "sha256": h.hexdigest(),
    }


def parse_args(argv: list[str] | None = None) -> dict[str, Any]:
    args = argv if argv is not None else sys.argv[1:]
    config: dict[str, Any] = {"port": DEFAULT_PORT, "survey_path": SURVEY_EXPORT_PATH, "open_browser": True}
    for arg in args:
        if "=" not in arg:
            continue
        key, value = arg.split("=", 1)
        key = key.strip().lower().replace("-", "_")
        value = value.strip()
        if key == "port":
            config["port"] = int(value)
        elif key in {"survey", "survey_path", "tsv", "path"}:
            config["survey_path"] = Path(value)
        elif key in {"open", "browser", "open_browser"}:
            config["open_browser"] = value.lower() not in {"0", "false", "no", "off"}
    if not Path(config["survey_path"]).is_absolute():
        config["survey_path"] = REPO_ROOT / Path(config["survey_path"])
    return config


def slot_for_start(start: dt.datetime | None) -> tuple[str, int]:
    if start is None:
        return "Unknown start time", 999
    t = start.time().replace(second=0, microsecond=0)
    anchor = dt.datetime.combine(dt.date(2000, 1, 1), dt.time(9, 40))
    for i in range(7):
        begin = (anchor + dt.timedelta(hours=i)).time()
        end = (anchor + dt.timedelta(hours=i + 1)).time()
        if begin <= t < end:
            return SLOT_LABELS[i], i
    return SLOT_LABELS[-1], 7


def link_text(value: object) -> str:
    return clean(value) or "[FOLLOW-UP LINK MISSING]"


def email_subject(_: dict[str, Any]) -> str:
    return "Participant Pool: Follow-up survey for the Minecraft study"


def email_plain(p: dict[str, Any]) -> str:
    return "\n".join([
        "Dear participant,", "",
        "Thank you again for taking part in the Minecraft study last week.", "",
        "For this final part, we would like to know how much you still remember about the creatures you interacted with. As agreed during the study, please complete the follow-up survey within the next 24 hours (ideally today):",
        link_text(p.get("delayed_link")), "",
        "The survey is intended to be completed on a laptop, but it will likely also work on most smartphones. It should take around 15 minutes.", "",
        "If applicable, your credit for completing the study will be awarded tomorrow. Thank you again, and please feel free to reach out if you have any questions or would like to know more about the study.",
    ])


def email_html(p: dict[str, Any]) -> str:
    link = link_text(p.get("delayed_link"))
    escaped_link = html.escape(link)
    link_line = escaped_link
    if link.startswith(("http://", "https://")):
        link_line = f'<a href="{escaped_link}" style="color:#1155cc;text-decoration:underline;">{escaped_link}</a>'
    lines = [
        "Dear participant,", "",
        "Thank you again for taking part in the Minecraft study last week.", "",
        "For this final part, we would like to know how much you still remember about the creatures you interacted with. As agreed during the study, please complete the follow-up survey within the next 24 hours (ideally today):",
        link_line, "",
        "The survey is intended to be completed on a laptop, but it will likely also work on most smartphones. It should take around 15 minutes.", "",
        "If applicable, your credit for completing the study will be awarded tomorrow. Thank you again, and please feel free to reach out if you have any questions or would like to know more about the study.",
    ]
    divs = [f"<div>{line}</div>" if line else "<div><br></div>" for line in lines]
    return '<div style="font-family:Arial,sans-serif;color:#000000;font-size:13px;line-height:1.2;margin:0;padding:0;">' + "".join(divs) + "</div>"


def participant_record(mcid: str, immediate: dict[str, str], delayed: dict[str, str] | None) -> dict[str, Any]:
    start_raw = survey_start(immediate)
    start_dt = parse_datetime(start_raw)
    due_dt = start_dt + dt.timedelta(days=7) if start_dt else None
    slot_label, slot_order = slot_for_start(start_dt)
    p: dict[str, Any] = {
        "mcid": mcid,
        "start_raw": clean(start_raw),
        "start_display": display_datetime(start_raw),
        "start_iso": start_dt.isoformat(sep=" ", timespec="seconds") if start_dt else "",
        "due_date_key": due_dt.date().isoformat() if due_dt else "unknown-date",
        "due_date_label": due_dt.strftime("%a %d %b %Y") if due_dt else "Unknown follow-up date",
        "slot_label": slot_label,
        "slot_order": slot_order,
        "email": first_present(immediate, ["email_input", "RecipientEmail"]),
        "delayed_link": first_present(immediate, ["DELAYED_LINK"]),
        "remarks": first_present(immediate, ["remarks_input"]),
        "received_delayed_answer": delayed is not None,
        "delayed_progress": survey_progress(delayed),
        "delayed_start_display": display_datetime(survey_start(delayed)) if delayed else "",
    }
    p["has_missing_email"] = not bool(clean(p["email"]))
    p["has_missing_link"] = not bool(clean(p["delayed_link"]))
    p["email_subject"] = email_subject(p)
    p["email_plain"] = email_plain(p)
    p["email_html"] = email_html(p)
    return p


class Store:
    def __init__(self, survey_path: Path):
        self.survey_path = survey_path
        self.backup_dir = OUTPUT_DIR / "backups"
        self.backup_dir.mkdir(parents=True, exist_ok=True)
        self.state_stem = f"{self.survey_path.stem}-followup-email-state"
        self.state_path = self.backup_dir / f"{self.state_stem}.json"
        self.lock = threading.Lock()
        self.state = self.load_state()
        self.sync(write=True)

    def blank_state(self) -> dict[str, Any]:
        return {
            "version": STATE_VERSION,
            "created_at": now_iso(),
            "updated_at": now_iso(),
            "source_tsv": str(self.survey_path),
            "source_tsv_display": rel(self.survey_path),
            "source_signature": file_signature(self.survey_path),
            "items": {},
        }

    def load_state(self) -> dict[str, Any]:
        if not self.state_path.exists():
            state = self.blank_state()
            self.write_state(state)
            return state
        try:
            state = json.loads(self.state_path.read_text(encoding="utf-8"))
            state.setdefault("items", {})
            state.setdefault("created_at", now_iso())
            state.setdefault("version", STATE_VERSION)
            return state
        except (OSError, json.JSONDecodeError):
            bad = self.backup_dir / f"{self.state_stem}-unreadable-{stamp()}.json"
            try:
                self.state_path.replace(bad)
            except OSError:
                pass
            state = self.blank_state()
            self.write_state(state)
            return state

    def write_state(self, state: dict[str, Any] | None = None) -> None:
        if state is not None:
            self.state = state
        self.state["updated_at"] = now_iso()
        tmp = self.state_path.with_suffix(".tmp")
        tmp.write_text(json.dumps(self.state, ensure_ascii=False, indent=2, sort_keys=True), encoding="utf-8")
        tmp.replace(self.state_path)

    def write_backup(self) -> Path:
        path = self.backup_dir / f"{self.state_stem}-{stamp()}.json"
        path.write_text(json.dumps(self.state, ensure_ascii=False, indent=2, sort_keys=True), encoding="utf-8")
        return path

    def current_participants(self) -> tuple[list[dict[str, Any]], dict[str, Any]]:
        if not self.survey_path.exists():
            raise FileNotFoundError(f"Survey TSV not found: {self.survey_path}")
        survey_rows, header = load_tsv(self.survey_path)
        log_index = load_log_index(LOG_DIR)
        merged = build_merged_dataset(survey_rows, log_index)
        waves = split_survey_waves(survey_rows)
        participants: list[dict[str, Any]] = []
        for p in sorted(merged["participants"], key=lambda x: x["participant_id"]):
            mcid = p["participant_id"]
            immediate = (waves["immediate"].get(mcid) or [None])[0]
            if immediate is None:
                continue
            delayed = (waves["delayed"].get(mcid) or [None])[0]
            participants.append(participant_record(mcid, immediate, delayed))
        audit = {
            **merged.get("audit", {}),
            "survey_header_count": len(header),
            "included_for_email_app": len(participants),
            "source_tsv": rel(self.survey_path),
            "state_json": rel(self.state_path),
            "backup_dir": rel(self.backup_dir),
        }
        return participants, audit

    def sync(self, write: bool = False) -> dict[str, Any]:
        participants, audit = self.current_participants()
        current_ids = {p["mcid"] for p in participants}
        items = self.state.setdefault("items", {})
        changed = False
        for mcid, item in list(items.items()):
            present = mcid in current_ids
            if item.get("present_in_current_tsv") != present:
                item["present_in_current_tsv"] = present
                changed = True
        for p in participants:
            mcid = p["mcid"]
            old = items.setdefault(mcid, {})
            new_item = {
                **p,
                "sent": bool(old.get("sent", False)),
                "sent_updated_at": clean(old.get("sent_updated_at")),
                "present_in_current_tsv": True,
            }
            if old != new_item:
                items[mcid] = new_item
                changed = True
        self.state["source_tsv"] = str(self.survey_path)
        self.state["source_tsv_display"] = rel(self.survey_path)
        self.state["source_signature"] = file_signature(self.survey_path)
        if write or changed:
            self.write_state()
        return self.payload_from(participants, audit)

    def payload_from(self, participants: list[dict[str, Any]], audit: dict[str, Any]) -> dict[str, Any]:
        items = self.state.setdefault("items", {})
        current = []
        for p in participants:
            item = {**p, "sent": bool(items.get(p["mcid"], {}).get("sent", False)), "sent_updated_at": clean(items.get(p["mcid"], {}).get("sent_updated_at"))}
            current.append(item)
        current.sort(key=lambda p: (p["due_date_key"], p["slot_order"], p["start_iso"], p["mcid"]))

        tabs_by_date: dict[str, dict[str, Any]] = {}
        for p in current:
            tabs_by_date.setdefault(p["due_date_key"], {"date_key": p["due_date_key"], "date_label": p["due_date_label"], "participants": []})["participants"].append(p)

        tabs = []
        for tab in tabs_by_date.values():
            slot_map = {label: {"slot_label": label, "slot_order": i, "participants": []} for i, label in enumerate(SLOT_LABELS)}
            for p in tab["participants"]:
                slot_map.setdefault(p["slot_label"], {"slot_label": p["slot_label"], "slot_order": p["slot_order"], "participants": []})["participants"].append(p)
            slots = sorted(slot_map.values(), key=lambda s: s["slot_order"])
            for s in slots:
                s["participants"].sort(key=lambda p: (p["start_iso"], p["mcid"]))
                s["count"] = len(s["participants"])
                s["sent_count"] = sum(1 for p in s["participants"] if p["sent"])
                s["received_count"] = sum(1 for p in s["participants"] if p["received_delayed_answer"])
            tab["slots"] = slots
            tab["total_count"] = len(tab["participants"])
            tab["sent_count"] = sum(1 for p in tab["participants"] if p["sent"])
            tab["received_count"] = sum(1 for p in tab["participants"] if p["received_delayed_answer"])
            del tab["participants"]
            tabs.append(tab)
        tabs.sort(key=lambda t: t["date_key"])

        warnings = []
        if audit.get("excluded_count"):
            warnings.append(f"{audit['excluded_count']} MCIDs are excluded by the same criteria used in summarise_merged.py and are not shown here.")
        missing_email = [p["mcid"] for p in current if p["has_missing_email"]]
        missing_link = [p["mcid"] for p in current if p["has_missing_link"]]
        if missing_email:
            warnings.append("Missing email_input for: " + ", ".join(missing_email))
        if missing_link:
            warnings.append("Missing DELAYED_LINK for: " + ", ".join(missing_link))

        return {
            "generated_at": now_iso(),
            "survey_path": rel(self.survey_path),
            "state_path": rel(self.state_path),
            "backup_dir": rel(self.backup_dir),
            "audit": audit,
            "tabs": tabs,
            "participants_by_mcid": {p["mcid"]: p for p in current},
            "totals": {
                "participants": len(current),
                "sent": sum(1 for p in current if p["sent"]),
                "not_sent": sum(1 for p in current if not p["sent"]),
                "received_delayed_answer": sum(1 for p in current if p["received_delayed_answer"]),
                "missing_email": len(missing_email),
                "missing_link": len(missing_link),
            },
            "warnings": warnings,
        }

    def payload(self) -> dict[str, Any]:
        with self.lock:
            return self.sync(write=False)

    def set_sent(self, mcid: str, sent: bool) -> dict[str, Any]:
        with self.lock:
            items = self.state.setdefault("items", {})

            # If the MCID is not yet in state, do one sync to account for a changed TSV.
            # Normal toggles do not re-read the TSV/log data, which keeps the UI fast.
            if mcid not in items:
                self.sync(write=True)
                items = self.state.setdefault("items", {})

            if mcid not in items:
                raise KeyError(f"Unknown MCID: {mcid}")

            sent_updated_at = now_iso()
            items[mcid]["sent"] = bool(sent)
            items[mcid]["sent_updated_at"] = sent_updated_at
            self.write_state()
            backup = self.write_backup()

            return {
                "ok": True,
                "mcid": mcid,
                "sent": bool(sent),
                "sent_updated_at": sent_updated_at,
                "backup_path": rel(backup),
            }
        
CSS = r"""
*{box-sizing:border-box} body{margin:0;background:#f6f7fb;color:#111827;font-family:system-ui,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif} button,input{font:inherit} button{cursor:pointer}
header{position:sticky;top:0;z-index:2;background:white;border-bottom:1px solid #d1d5db;padding:20px 28px;display:flex;justify-content:space-between;gap:16px;align-items:center} h1{margin:0 0 4px;font-size:24px} h2{margin:0} h3{margin:0}.muted{color:#6b7280}.small{font-size:12px}main{max-width:1400px;margin:0 auto;padding:22px 28px 48px}.summary{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:10px}.card,.slot{background:white;border:1px solid #d1d5db;border-radius:14px}.card{padding:12px}.value{display:block;font-size:24px;font-weight:800}.warn{margin:8px 0;padding:10px 12px;border-radius:12px;background:#fef3c7;color:#92400e;border:1px solid #fde68a}.tabs{display:flex;gap:8px;flex-wrap:wrap;margin:18px 0}.tab{border:1px solid #d1d5db;background:white;border-radius:999px;padding:8px 12px}.tab.active{border-color:#2563eb;background:#eff6ff;color:#1d4ed8;font-weight:800}.tabhead{display:flex;justify-content:space-between;gap:16px;align-items:end;margin:16px 0}.slots{display:grid;gap:12px}.slothead{padding:12px 14px;border-bottom:1px solid #d1d5db;background:#fbfdff;display:flex;justify-content:space-between;gap:12px;align-items:center}.badges{display:flex;gap:6px;flex-wrap:wrap;justify-content:flex-end}.badge{border-radius:999px;padding:3px 8px;font-size:12px;font-weight:800;border:1px solid transparent;white-space:nowrap}.badge.n{background:#f3f4f6;color:#374151;border-color:#e5e7eb}.badge.g{background:#dcfce7;color:#15803d;border-color:#bbf7d0}.badge.r{background:#fee2e2;color:#b91c1c;border-color:#fecaca}.badge.a{background:#fef3c7;color:#92400e;border-color:#fde68a}.rows{list-style:none;margin:0;padding:0}.row{display:grid;grid-template-columns:minmax(140px,210px) 1fr auto;gap:12px;align-items:center;padding:12px 14px;border-bottom:1px solid #eef2f7}.row:last-child{border-bottom:0}.mcid{border:1px solid #d1d5db;border-radius:10px;background:white;color:#2563eb;font-weight:900;text-align:left;padding:8px 10px}.mcid:hover{background:#eff6ff;border-color:#2563eb}.meta{color:#6b7280;font-size:13px}.meta span{display:inline-block;margin-right:12px}.empty{padding:12px 14px;color:#6b7280}.modalbg{position:fixed;inset:0;z-index:9;background:rgba(17,24,39,.55);padding:24px;overflow:auto}.modal{background:white;max-width:900px;margin:0 auto;border-radius:18px;padding:22px;position:relative;box-shadow:0 20px 45px rgba(15,23,42,.22)}.close{position:absolute;top:12px;right:12px;border:0;background:#f3f4f6;border-radius:50%;width:34px;height:34px;font-size:24px}.ref{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:8px 16px;background:#f9fafb;border:1px solid #d1d5db;border-radius:12px;padding:12px;margin:14px 0;font-size:13px}.wide{grid-column:1/-1}.ref strong{display:block;color:#374151;font-size:12px}.field{margin:14px 0}.fieldtop{display:flex;justify-content:space-between;align-items:center;margin-bottom:6px}.copy{border:1px solid #d1d5db;background:white;border-radius:999px;padding:6px 10px;font-size:13px}.copy.done{background:#dcfce7;color:#15803d;border-color:#15803d}pre,.msg{border:1px solid #d1d5db;border-radius:12px;background:#f9fafb;padding:12px;margin:0;white-space:pre-wrap}.msg{background:white;min-height:210px;font-family:Arial,sans-serif;color:#000;font-size:13px;line-height:1.2}.msg div{margin:0;padding:0}.sent{margin-top:18px;padding-top:14px;border-top:1px solid #d1d5db;display:flex;justify-content:space-between;gap:18px;align-items:center}.switch{position:relative;display:inline-block;width:68px;height:36px}.switch input{opacity:0;width:0;height:0}.slider{position:absolute;inset:0;background:#b91c1c;border-radius:999px;transition:.15s}.slider:before{content:"";position:absolute;width:28px;height:28px;left:4px;top:4px;background:white;border-radius:50%;transition:.15s}.switch input:checked+.slider{background:#15803d}.switch input:checked+.slider:before{transform:translateX(32px)}@media(max-width:720px){header{display:block}.row{grid-template-columns:1fr}.badges{justify-content:flex-start}.modalbg{padding:8px}}
"""

JS = r"""
const $=id=>document.getElementById(id); const S={data:null,date:null,p:null,silent:false};
function esc(v){return String(v??"").replace(/[&<>"']/g,m=>({"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#039;"}[m]))}
async function get(url,opt){const r=await fetch(url,opt);const j=await r.json();if(!r.ok)throw Error(j.error||r.status);return j}
async function load(keep){$('content').innerHTML='<div class="card">Loading…</div>';S.data=await get('/api/state');const dates=S.data.tabs.map(t=>t.date_key);S.date=keep&&dates.includes(keep)?keep:(dates.includes(S.date)?S.date:dates[0]);render()}
function render(){summary();warnings();tabs();content()}
function summary(){const t=S.data.totals,a=S.data.audit;const cards=[["Participants shown",t.participants],["Emails sent",t.sent],["Emails not sent",t.not_sent],["Delayed answers received",t.received_delayed_answer],["Excluded by merged criteria",a.excluded_count],["Missing email / link",`${t.missing_email} / ${t.missing_link}`]];$('summary').innerHTML=cards.map(c=>`<div class="card"><span class="muted">${esc(c[0])}</span><span class="value">${esc(c[1])}</span></div>`).join('')}
function warnings(){$('warnings').innerHTML=(S.data.warnings||[]).map(w=>`<div class="warn">${esc(w)}</div>`).join('')}
function tabs(){$('tabs').innerHTML=S.data.tabs.map(t=>`<button class="tab ${t.date_key===S.date?'active':''}" data-date="${esc(t.date_key)}">${esc(t.date_label)} · ${t.total_count}</button>`).join('')||'<span class="muted">No follow-up dates found.</span>';document.querySelectorAll('.tab').forEach(b=>b.onclick=()=>{S.date=b.dataset.date;render()})}
function content(){const tab=S.data.tabs.find(t=>t.date_key===S.date);if(!tab){$('content').innerHTML='<div class="card">No included participants found.</div>';return}$('content').innerHTML=`<div class="tabhead"><div><h2>${esc(tab.date_label)}</h2><p class="muted">${tab.total_count} participants · ${tab.sent_count} sent · ${tab.received_count} delayed answers received</p></div><p class="muted small">State: ${esc(S.data.state_path)}</p></div><div class="slots">${tab.slots.map(slot).join('')}</div>`;document.querySelectorAll('.mcid').forEach(b=>b.onclick=()=>openp(b.dataset.mcid))}
function slot(s){return `<section class="slot"><div class="slothead"><h3>${esc(s.slot_label)}</h3><div class="badges"><span class="badge n">Total ${s.count}</span><span class="badge ${s.sent_count?'g':'r'}">Sent ${s.sent_count}</span><span class="badge ${s.received_count?'g':'n'}">Received ${s.received_count}</span></div></div>${s.participants.length?`<ul class="rows">${s.participants.map(row).join('')}</ul>`:'<div class="empty">No participants in this slot.</div>'}</section>`}
function row(p){return `<li class="row"><button class="mcid" data-mcid="${esc(p.mcid)}">${esc(p.mcid)}</button><div class="meta"><span>Start: ${esc(p.start_display||p.start_raw)}</span><span>Email: ${esc(p.email||'—')}</span></div><div class="badges"><span class="badge ${p.sent?'g':'r'}">${p.sent?'Sent':'Not sent'}</span><span class="badge ${p.received_delayed_answer?'g':'n'}">${p.received_delayed_answer?'Delayed answer received':'No delayed answer yet'}</span>${p.has_missing_email?'<span class="badge a">Missing email</span>':''}${p.has_missing_link?'<span class="badge a">Missing link</span>':''}</div></li>`}
function openp(mcid){const p=S.data.participants_by_mcid[mcid];if(!p)return;S.p=p;$('title').textContent=`Email for ${p.mcid}`;$('ref').innerHTML=ref(p);$('to').textContent=p.email||'';$('subj').textContent=p.email_subject||'';$('msg').innerHTML=p.email_html||'';$('sentTime').textContent=p.sent_updated_at?`Last changed: ${p.sent_updated_at}`:'Default: not sent';S.silent=true;$('sent').checked=!!p.sent;S.silent=false;$('modal').hidden=false}
function ref(p){const rec=p.received_delayed_answer?`Yes${p.delayed_start_display?' · '+esc(p.delayed_start_display):''}`:'No';return `<div><strong>MCID</strong>${esc(p.mcid)}</div><div><strong>StartDate</strong>${esc(p.start_display||p.start_raw)}</div><div><strong>Follow-up date</strong>${esc(p.due_date_label)}</div><div><strong>Slot</strong>${esc(p.slot_label)}</div><div><strong>Delayed answer received</strong>${rec}</div><div><strong>DELAYED_LINK</strong>${esc(p.delayed_link||'—')}</div><div class="wide"><strong>remarks_input</strong>${esc(p.remarks||'—')}</div>`}
function closem(){$('modal').hidden=true;S.p=null}
async function copyPlain(v,b){await navigator.clipboard.writeText(v||'');flash(b)}
async function copyMsg(p,b){if(navigator.clipboard&&window.ClipboardItem){await navigator.clipboard.write([new ClipboardItem({'text/html':new Blob([p.email_html||''],{type:'text/html'}),'text/plain':new Blob([p.email_plain||''],{type:'text/plain'})})])}else await navigator.clipboard.writeText(p.email_plain||'');flash(b)}
function flash(b){const o=b.textContent;b.textContent='Copied';b.classList.add('done');setTimeout(()=>{b.textContent=o;b.classList.remove('done')},900)}

function setLocalSent(mcid,sent,sentUpdatedAt){
    if(!S.data||!S.data.participants_by_mcid||!S.data.participants_by_mcid[mcid])return;

    const canonical=S.data.participants_by_mcid[mcid];
    canonical.sent=!!sent;
    canonical.sent_updated_at=sentUpdatedAt||canonical.sent_updated_at||"";

    for(const tab of S.data.tabs){
        tab.total_count=0;
        tab.sent_count=0;
        tab.received_count=0;

        for(const sl of tab.slots){
            sl.count=sl.participants.length;
            sl.sent_count=0;
            sl.received_count=0;

            for(const p of sl.participants){
                if(p.mcid===mcid){
                    p.sent=!!sent;
                    p.sent_updated_at=canonical.sent_updated_at;
                }
                if(p.sent)sl.sent_count++;
                if(p.received_delayed_answer)sl.received_count++;
            }

            tab.total_count+=sl.count;
            tab.sent_count+=sl.sent_count;
            tab.received_count+=sl.received_count;
        }
    }

    const all=Object.values(S.data.participants_by_mcid);
    S.data.totals.sent=all.filter(p=>p.sent).length;
    S.data.totals.not_sent=all.filter(p=>!p.sent).length;
}

function refreshOpenModalIfNeeded(mcid){
    if(!$('modal').hidden&&S.p&&S.p.mcid===mcid){
        openp(mcid);
    }
}

$('refresh').onclick=()=>load(S.date).catch(e=>alert(e.message));
$('close').onclick=closem;
$('modal').onclick=e=>{if(e.target===$('modal'))closem()};
document.addEventListener('keydown',e=>{if(e.key==='Escape')closem()});

document.querySelectorAll('.copy').forEach(b=>b.onclick=()=>{
    if(!S.p)return;
    const t=b.dataset.copy;
    (t==='to'?copyPlain(S.p.email,b):t==='subj'?copyPlain(S.p.email_subject,b):copyMsg(S.p,b)).catch(e=>alert('Copy failed: '+e.message))
});

$('sent').onchange=async e=>{
    if(S.silent||!S.p)return;

    const mcid=S.p.mcid;
    const previousSent=!!S.p.sent;
    const previousUpdatedAt=S.p.sent_updated_at||"";
    const wantedSent=!!e.target.checked;

    // Immediate local update, so the red/green labels change even before the backup write finishes.
    setLocalSent(mcid,wantedSent,"Saving…");
    render();
    refreshOpenModalIfNeeded(mcid);

    try{
        const r=await get('/api/toggle',{
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify({mcid:mcid,sent:wantedSent})
        });

        setLocalSent(r.mcid,r.sent,r.sent_updated_at||"");
        render();
        refreshOpenModalIfNeeded(r.mcid);
    }catch(err){
        setLocalSent(mcid,previousSent,previousUpdatedAt);
        render();
        refreshOpenModalIfNeeded(mcid);
        alert(err.message);
    }
};

load().catch(e=>$('content').innerHTML=`<div class="warn">${esc(e.message)}</div>`);
"""

HTML = """<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Follow-up email app</title><style>__CSS__</style></head><body><header><div><h1>Follow-up email app</h1><p class="muted">Groups included participants by 7-day follow-up date and start-time slot.</p></div><button id="refresh">Reload TSV</button></header><main><section id="summary" class="summary"></section><section id="warnings"></section><nav id="tabs" class="tabs"></nav><section id="content"></section></main><div id="modal" class="modalbg" hidden><section class="modal" role="dialog" aria-modal="true"><button id="close" class="close" aria-label="Close">×</button><h2 id="title">Email</h2><div id="ref" class="ref"></div><div class="field"><div class="fieldtop"><strong>To:</strong><button class="copy" data-copy="to">Copy</button></div><pre id="to"></pre></div><div class="field"><div class="fieldtop"><strong>Subject:</strong><button class="copy" data-copy="subj">Copy</button></div><pre id="subj"></pre></div><div class="field"><div class="fieldtop"><strong>Message:</strong><button class="copy" data-copy="msg">Copy formatted</button></div><div id="msg" class="msg"></div></div><div class="sent"><div><strong>I have sent this email</strong><div id="sentTime" class="muted small"></div></div><label class="switch"><input id="sent" type="checkbox"><span class="slider"></span></label></div></section></div><script>__JS__</script></body></html>"""


def send_json(handler: BaseHTTPRequestHandler, status: int, payload: Any) -> None:
    body = json.dumps(payload, ensure_ascii=False, indent=2).encode("utf-8")
    handler.send_response(status)
    handler.send_header("Content-Type", "application/json; charset=utf-8")
    handler.send_header("Content-Length", str(len(body)))
    handler.send_header("Cache-Control", "no-store")
    handler.end_headers()
    handler.wfile.write(body)


def make_handler(store: Store) -> type[BaseHTTPRequestHandler]:
    class Handler(BaseHTTPRequestHandler):
        def do_GET(self) -> None:  # noqa: N802
            try:
                path = urlparse(self.path).path
                if path in {"/", "/index.html"}:
                    body = HTML.replace("__CSS__", CSS).replace("__JS__", JS).encode("utf-8")
                    self.send_response(200)
                    self.send_header("Content-Type", "text/html; charset=utf-8")
                    self.send_header("Content-Length", str(len(body)))
                    self.send_header("Cache-Control", "no-store")
                    self.end_headers()
                    self.wfile.write(body)
                    return
                if path == "/api/state":
                    send_json(self, 200, store.payload())
                    return
                send_json(self, 404, {"error": "Not found"})
            except Exception as exc:
                send_json(self, 500, {"error": str(exc), "traceback": traceback.format_exc()})

        def do_POST(self) -> None:  # noqa: N802
            try:
                path = urlparse(self.path).path
                raw = self.rfile.read(int(self.headers.get("Content-Length", "0") or 0)).decode("utf-8") or "{}"
                data = json.loads(raw)
                if path == "/api/toggle":
                    mcid = clean(data.get("mcid"))
                    if not mcid:
                        send_json(self, 400, {"error": "Missing mcid"})
                        return
                    send_json(self, 200, store.set_sent(mcid, bool(data.get("sent"))))
                    return
                send_json(self, 404, {"error": "Not found"})
            except Exception as exc:
                send_json(self, 500, {"error": str(exc), "traceback": traceback.format_exc()})

        def log_message(self, format: str, *args: Any) -> None:  # noqa: A002
            return

    return Handler


def main(argv: list[str] | None = None) -> int:
    config = parse_args(argv)
    store = Store(config["survey_path"])
    port = int(config["port"])
    server = ThreadingHTTPServer(("127.0.0.1", port), make_handler(store))
    url = f"http://127.0.0.1:{port}/"
    print(f"{APP_TITLE} running at {url}")
    print(f"Survey TSV: {store.survey_path.resolve()}")
    print(f"State JSON: {store.state_path.resolve()}")
    print(f"Toggle backups: {store.backup_dir.resolve()}")
    print("Press Ctrl+C to stop.")
    if config["open_browser"]:
        webbrowser.open(url)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopped.")
    finally:
        server.server_close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())