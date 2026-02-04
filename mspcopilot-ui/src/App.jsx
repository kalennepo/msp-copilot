import { useEffect, useMemo, useState } from "react";

const API = "http://localhost:8080/api";

function pretty(ts) {
  try { return new Date(ts).toLocaleString(); } catch { return ts; }
}

export default function App() {
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [actions, setActions] = useState([]);
  const [jobId, setJobId] = useState("");
  const [job, setJob] = useState(null);
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    title: "User cannot login - MFA loop",
    description: "Office 365 sign-in keeps prompting MFA. Password reset didn’t help.",
    requesterEmail: "user@company.com",
    priority: "HIGH",
  });

  async function fetchTickets() {
    const res = await fetch(`${API}/tickets`);
    if (!res.ok) throw new Error(`Failed to list tickets (${res.status})`);
    return await res.json();
  }

  async function loadTickets() {
    try {
      setError("");
      const data = await fetchTickets();
      // newest first
      data.sort((a, b) => (b.createdAt || "").localeCompare(a.createdAt || ""));
      setTickets(data);
    } catch (e) {
      setError(e.message);
    }
  }

  async function loadTicketActions(ticketId) {
    const res = await fetch(`${API}/tickets/${ticketId}/actions`);
    if (!res.ok) throw new Error(`Failed to load actions (${res.status})`);
    return await res.json();
  }

  async function loadJob(jobId) {
    const res = await fetch(`${API}/jobs/${jobId}`);
    if (!res.ok) throw new Error(`Failed to load job (${res.status})`);
    return await res.json();
  }

  useEffect(() => {
    loadTickets();
    const id = setInterval(loadTickets, 2000); // poll
    return () => clearInterval(id);
  }, []);

  useEffect(() => {
    if (!selectedTicket?.id) return;
    (async () => {
      try {
        setError("");
        const a = await loadTicketActions(selectedTicket.id);
        setActions(a);
      } catch (e) {
        setError(e.message);
      }
    })();
  }, [selectedTicket]);

  useEffect(() => {
    if (!jobId) return;
    let canceled = false;
    const tick = async () => {
      try {
        const j = await loadJob(jobId);
        if (!canceled) setJob(j);
      } catch (e) {
        if (!canceled) setError(e.message);
      }
    };
    tick();
    const id = setInterval(tick, 1000);
    return () => { canceled = true; clearInterval(id); };
  }, [jobId]);

  const selected = useMemo(
    () => tickets.find(t => t.id === selectedTicket?.id) || selectedTicket,
    [tickets, selectedTicket]
  );

  async function submitTicket(e) {
    e.preventDefault();
    setError("");
    setJob(null);
    setJobId("");
    setSelectedTicket(null);
    setActions([]);

    const res = await fetch(`${API}/tickets`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });

    const bodyText = await res.text();
    if (!res.ok) {
      setError(`Create failed (${res.status}): ${bodyText}`);
      return;
    }

    const body = JSON.parse(bodyText);
    // expects { ticketId, jobId } (recommended backend tweak)
    if (body.ticketId) {
      setSelectedTicket({ id: body.ticketId });
    }
    if (body.jobId) setJobId(body.jobId);
    await loadTickets();
  }

  return (
    <div style={{ fontFamily: "system-ui, sans-serif", padding: 16, maxWidth: 1100, margin: "0 auto" }}>
      <h1 style={{ marginBottom: 8 }}>MSP Ops Copilot (MVP)</h1>
      <p style={{ marginTop: 0, color: "#666" }}>
        Create tickets → async triage → audit trail. Backend: Spring Boot + Postgres + Flyway.
      </p>

      {error && (
        <div style={{ padding: 12, border: "1px solid #f99", background: "#fff5f5", marginBottom: 12 }}>
          <b>Error:</b> {error}
        </div>
      )}

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16, alignItems: "start" }}>
        {/* Left: Create + Job */}
        <div style={{ border: "1px solid #ddd", borderRadius: 10, padding: 12 }}>
          <h2 style={{ marginTop: 0 }}>Create Ticket</h2>
          <form onSubmit={submitTicket} style={{ display: "grid", gap: 10 }}>
            <label>
              Title
              <input
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
                style={{ width: "100%", padding: 8, marginTop: 4 }}
              />
            </label>

            <label>
              Description
              <textarea
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                rows={4}
                style={{ width: "100%", padding: 8, marginTop: 4 }}
              />
            </label>

            <label>
              Requester Email
              <input
                value={form.requesterEmail}
                onChange={(e) => setForm({ ...form, requesterEmail: e.target.value })}
                style={{ width: "100%", padding: 8, marginTop: 4 }}
              />
            </label>

            <label>
              Priority
              <select
                value={form.priority}
                onChange={(e) => setForm({ ...form, priority: e.target.value })}
                style={{ width: "100%", padding: 8, marginTop: 4 }}
              >
                <option>LOW</option>
                <option>MEDIUM</option>
                <option>HIGH</option>
                <option>URGENT</option>
              </select>
            </label>

            <button type="submit" style={{ padding: 10, fontWeight: 600 }}>
              Submit
            </button>
          </form>

          <div style={{ marginTop: 14, borderTop: "1px solid #eee", paddingTop: 12 }}>
            <h3 style={{ margin: "0 0 8px" }}>Latest Job</h3>
            {!jobId && <div style={{ color: "#666" }}>Submit a ticket to start a job.</div>}
            {jobId && (
              <div>
                <div><b>jobId:</b> {jobId}</div>
                {job ? (
                  <div style={{ marginTop: 6 }}>
                    <div><b>Status:</b> {job.status}</div>
                    <div><b>Progress:</b> {job.progress}%</div>
                    {job.error && <div><b>Error:</b> {job.error}</div>}
                  </div>
                ) : (
                  <div style={{ color: "#666", marginTop: 6 }}>Loading job...</div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Right: Ticket list */}
        <div style={{ border: "1px solid #ddd", borderRadius: 10, padding: 12 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <h2 style={{ margin: 0 }}>Tickets</h2>
            <button onClick={loadTickets} style={{ padding: 8 }}>Refresh</button>
          </div>

          <div style={{ marginTop: 10, maxHeight: 360, overflow: "auto", border: "1px solid #eee", borderRadius: 8 }}>
            {tickets.length === 0 ? (
              <div style={{ padding: 12, color: "#666" }}>No tickets yet.</div>
            ) : (
              tickets.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTicket(t)}
                  style={{
                    padding: 10,
                    borderBottom: "1px solid #f0f0f0",
                    cursor: "pointer",
                    background: selectedTicket?.id === t.id ? "#f7f7ff" : "white",
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", gap: 10 }}>
                    <b>{t.title}</b>
                    <span style={{ fontSize: 12, color: "#666" }}>{t.priority}</span>
                  </div>
                  <div style={{ fontSize: 13, color: "#444", marginTop: 4 }}>
                    <span><b>Status:</b> {t.status}</span>{" · "}
                    <span><b>Category:</b> {t.category}</span>
                  </div>
                  <div style={{ fontSize: 12, color: "#666", marginTop: 4 }}>
                    {pretty(t.createdAt)}
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Selected ticket */}
          <div style={{ marginTop: 12 }}>
            <h3 style={{ margin: "8px 0" }}>Selected Ticket</h3>
            {!selected?.id ? (
              <div style={{ color: "#666" }}>Click a ticket to view details & audit trail.</div>
            ) : (
              <div style={{ border: "1px solid #eee", borderRadius: 8, padding: 10 }}>
                <div><b>ID:</b> {selected.id}</div>
                <div><b>Status:</b> {selected.status}</div>
                <div><b>Category:</b> {selected.category}</div>

                <h4 style={{ margin: "12px 0 6px" }}>Audit Trail</h4>
                <div style={{ maxHeight: 200, overflow: "auto", border: "1px solid #f0f0f0", borderRadius: 8 }}>
                  {actions.length === 0 ? (
                    <div style={{ padding: 10, color: "#666" }}>No actions loaded yet.</div>
                  ) : (
                    actions.map((a) => (
                      <div key={a.id} style={{ padding: 10, borderBottom: "1px solid #f0f0f0" }}>
                        <div style={{ fontSize: 12, color: "#666" }}>{pretty(a.createdAt)}</div>
                        <div style={{ fontSize: 13 }}><b>{a.type}:</b> {a.message}</div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      <div style={{ marginTop: 16, color: "#666", fontSize: 12 }}>
        Tip: leave this page open and submit tickets; the list polls every 2 seconds.
      </div>
    </div>
  );
}
