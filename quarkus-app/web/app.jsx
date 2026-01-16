const { useEffect, useMemo, useState } = React;

const API_BASE = '/api';
const today = new Date().toISOString().slice(0, 10);

function App() {
  const [players, setPlayers] = useState([]);
  const [payments, setPayments] = useState([]);
  const [summary, setSummary] = useState(null);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [playerName, setPlayerName] = useState('');
  const [playerJoinedAt, setPlayerJoinedAt] = useState(today);
  const [paymentPlayerId, setPaymentPlayerId] = useState('');
  const [paymentDate, setPaymentDate] = useState(today);
  const [paymentAmount, setPaymentAmount] = useState('15.00');
  const [paymentType, setPaymentType] = useState('MEMBER_FEE');
  const [expenseDate, setExpenseDate] = useState(today);
  const [expenseAmount, setExpenseAmount] = useState('40.00');
  const [expenseDescription, setExpenseDescription] = useState('Alquiler cancha');
  const [error, setError] = useState(null);

  const monthLabel = useMemo(() => `${year}-${String(month).padStart(2, '0')}`, [year, month]);

  useEffect(() => {
    loadPlayers();
  }, []);

  useEffect(() => {
    loadMonthData();
  }, [year, month]);

  const loadPlayers = async () => {
    try {
      const response = await fetch(`${API_BASE}/players`);
      setPlayers(await response.json());
    } catch (err) {
      setError('No se pudo cargar jugadores.');
    }
  };

  const loadMonthData = async () => {
    try {
      const paymentsRes = await fetch(`${API_BASE}/payments?year=${year}&month=${month}`);
      setPayments(await paymentsRes.json());
      const summaryRes = await fetch(`${API_BASE}/summary?year=${year}&month=${month}`);
      setSummary(await summaryRes.json());
    } catch (err) {
      setError('No se pudo cargar resumen mensual.');
    }
  };

  const submitPlayer = async (event) => {
    event.preventDefault();
    setError(null);
    try {
      await fetch(`${API_BASE}/players`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: playerName, joinedAt: playerJoinedAt })
      });
      setPlayerName('');
      await loadPlayers();
    } catch (err) {
      setError('No se pudo registrar jugador.');
    }
  };

  const submitPayment = async (event) => {
    event.preventDefault();
    setError(null);
    try {
      const payload = {
        playerId: paymentPlayerId || null,
        paidAt: paymentDate,
        amount: Number(paymentAmount),
        type: paymentType
      };
      await fetch(`${API_BASE}/payments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      await loadMonthData();
    } catch (err) {
      setError('No se pudo registrar pago.');
    }
  };

  const submitExpense = async (event) => {
    event.preventDefault();
    setError(null);
    try {
      const payload = {
        spentAt: expenseDate,
        amount: Number(expenseAmount),
        description: expenseDescription
      };
      await fetch(`${API_BASE}/expenses`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      await loadMonthData();
    } catch (err) {
      setError('No se pudo registrar gasto.');
    }
  };

  return (
    <div>
      <header>
        <h1>Administrador de pagos</h1>
        <p>Control mensual de socios e invitados</p>
      </header>
      <main>
        {error && <div className="alert">{error}</div>}
        <div className="grid">
          <section className="card">
            <h3>Mes en revisión</h3>
            <small>Selecciona el mes a revisar</small>
            <label>Año</label>
            <input type="number" value={year} onChange={(e) => setYear(Number(e.target.value))} />
            <label>Mes</label>
            <input type="number" min="1" max="12" value={month} onChange={(e) => setMonth(Number(e.target.value))} />
            <div style={{ marginTop: '12px' }}>
              <span className="badge">{monthLabel}</span>
            </div>
            {summary && (
              <div className="summary">
                <div className="summary-item">
                  <span>Ingresos</span>
                  <strong>S/ {summary.income}</strong>
                </div>
                <div className="summary-item">
                  <span>Gastos</span>
                  <strong>S/ {summary.expenses}</strong>
                </div>
                <div className="summary-item">
                  <span>Balance</span>
                  <strong>{summary.balance >= 0 ? 'S/ ' : '-S/ '}{Math.abs(summary.balance)}</strong>
                </div>
              </div>
            )}
          </section>

          <section className="card">
            <h3>Registrar socio</h3>
            <small>Socio activo con pagos mensuales</small>
            <form onSubmit={submitPlayer}>
              <label>Nombre</label>
              <input value={playerName} onChange={(e) => setPlayerName(e.target.value)} required />
              <label>Fecha de ingreso</label>
              <input type="date" value={playerJoinedAt} onChange={(e) => setPlayerJoinedAt(e.target.value)} required />
              <button type="submit">Guardar</button>
            </form>
          </section>

          <section className="card">
            <h3>Registrar pago</h3>
            <small>Socios o invitados por lunes</small>
            <form onSubmit={submitPayment}>
              <label>Tipo</label>
              <select value={paymentType} onChange={(e) => setPaymentType(e.target.value)}>
                <option value="MEMBER_FEE">Socio mensual (S/15)</option>
                <option value="GUEST_FEE">Invitado (S/5)</option>
              </select>
              <label>Socio</label>
              <select value={paymentPlayerId} onChange={(e) => setPaymentPlayerId(e.target.value)}>
                <option value="">Sin socio</option>
                {players.map((player) => (
                  <option key={player.id} value={player.id}>{player.name}</option>
                ))}
              </select>
              <label>Fecha</label>
              <input type="date" value={paymentDate} onChange={(e) => setPaymentDate(e.target.value)} required />
              <label>Monto</label>
              <input type="number" step="0.01" value={paymentAmount} onChange={(e) => setPaymentAmount(e.target.value)} required />
              <button type="submit">Registrar</button>
            </form>
          </section>

          <section className="card">
            <h3>Registrar gasto</h3>
            <small>Alquiler, pelotas, chalecos, etc.</small>
            <form onSubmit={submitExpense}>
              <label>Fecha</label>
              <input type="date" value={expenseDate} onChange={(e) => setExpenseDate(e.target.value)} required />
              <label>Monto</label>
              <input type="number" step="0.01" value={expenseAmount} onChange={(e) => setExpenseAmount(e.target.value)} required />
              <label>Descripción</label>
              <input value={expenseDescription} onChange={(e) => setExpenseDescription(e.target.value)} required />
              <button type="submit">Registrar</button>
            </form>
          </section>

          <section className="card" style={{ gridColumn: '1 / -1' }}>
            <h3>Pagos del mes</h3>
            {payments.length === 0 ? (
              <div className="empty">Sin pagos registrados en este mes.</div>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Monto</th>
                    <th>Tipo</th>
                  </tr>
                </thead>
                <tbody>
                  {payments.map((payment) => (
                    <tr key={payment.id}>
                      <td>{payment.paidAt}</td>
                      <td>S/ {payment.amount}</td>
                      <td><span className="badge">{payment.type}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </section>
        </div>
      </main>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
