import React, { useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

const spaces = [
  { name: 'Spending', note: 'Everyday money', icon: '↗', tint: 'peach' },
  { name: 'Savings', note: 'Future you', icon: '✧', tint: 'mint' },
  { name: 'Emergency', note: 'Just in case', icon: '＋', tint: 'lavender' },
];

const money = n => '₹' + n.toLocaleString('en-IN');

function App() {
  const [active, setActive] = useState('Home');
  const [transactions, setTransactions] = useState(() => JSON.parse(localStorage.getItem('tm-transactions') || '[]'));
  const [spaceBalances, setSpaceBalances] = useState(() => JSON.parse(localStorage.getItem('tm-spaces') || '{"Spending":0,"Savings":0,"Emergency":0}'));
  const [showLog, setShowLog] = useState(false);
  const [showAddMoney, setShowAddMoney] = useState(false);
  const [toast, setToast] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Food');
  const [selectedDay, setSelectedDay] = useState('W');
  const [receipt, setReceipt] = useState(null);
  const [incomeAmount, setIncomeAmount] = useState('');
  const [incomeDestination, setIncomeDestination] = useState('Spending');
  React.useEffect(() => { localStorage.setItem('tm-transactions', JSON.stringify(transactions)); localStorage.setItem('tm-spaces', JSON.stringify(spaceBalances)); }, [transactions, spaceBalances]);

  const spent = useMemo(() => transactions.filter(t => t.type !== 'income').reduce((sum, t) => sum + t.amount, 0), [transactions]);
  const spendingBalance = spaceBalances.Spending || 0;
  const addExpense = e => {
    e.preventDefault();
    const value = Number(amount);
    if (!value || !description.trim()) return;
    if (value > spendingBalance) { setToast(`Not enough in Spending. You have ${money(spendingBalance)}.`); setTimeout(() => setToast(''), 2400); return; }
    const now = new Date();
    const id = Date.now();
    const receiptNumber = `TM-${String(now.getFullYear()).slice(-2)}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}-${Math.random().toString(36).slice(2, 6).toUpperCase()}`;
    const expense = { id, title: description.trim(), category, source: 'UPI', amount: value, day: 'Today', icon: category === 'Food' ? '☕' : '✦', color: category === 'Food' ? 'orange' : 'violet' };
    setTransactions([expense, ...transactions]);
    setSpaceBalances({ ...spaceBalances, Spending: spendingBalance - value });
    setReceipt({ ...expense, receiptNumber, timestamp: now.toLocaleString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: false }), remaining: Math.max(0, spendingBalance - value) });
    setAmount(''); setDescription(''); setShowLog(false); setToast('Expense added to Spending');
    setTimeout(() => setToast(''), 2400);
  };
  const addMoney = e => {
    e.preventDefault(); const value = Number(incomeAmount); if (!value) return;
    const now = new Date(); const next = { ...spaceBalances, [incomeDestination]: (spaceBalances[incomeDestination] || 0) + value };
    setSpaceBalances(next); setTransactions([{ id: Date.now(), type: 'income', title: `Added to ${incomeDestination}`, category: 'Income', source: 'Manual', amount: value, day: 'Today', icon: '↓', color: 'mint' }, ...transactions]);
    setIncomeAmount(''); setShowAddMoney(false); setToast(`${money(value)} added to ${incomeDestination}`); setTimeout(() => setToast(''), 2400);
  };

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark">tm</div><div><strong>teenage</strong><span>money</span></div></div>
      <div className="profile"><div className="avatar">A</div><div><strong>Hey, Alex</strong><span>Building your future</span></div><button className="dots">•••</button></div>
      <nav>{['Home', 'Activity', 'Goals', 'Insights'].map((item, i) => <button key={item} className={active === item ? 'nav-item active' : 'nav-item'} onClick={() => setActive(item)}><span>{['⌂', '◷', '◇', '⌁'][i]}</span>{item}</button>)}</nav>
      <div className="sidebar-bottom"><div className="tip-card"><span className="tip-spark">✦</span><strong>Small steps,<br/>big changes.</strong><p>You’re doing better than you think.</p></div><button className="nav-item"><span>⚙</span>Settings</button></div>
    </aside>
    <main className="main">
      <header className="topbar"><div><p className="eyebrow">TUESDAY, 6 AUGUST</p><h1>{active === 'Home' ? 'Your money, your way.' : active}</h1></div><div className="top-actions"><button className="icon-btn">⌕</button><button className="icon-btn">♧</button><button className="profile-pill"><span className="mini-avatar">A</span>Alex <span>⌄</span></button></div></header>
      {active === 'Home' && <>
        <section className="hero-grid">
          <div className="balance-card"><div className="balance-top"><span>Spending money left</span><button>•••</button></div><div className="balance-amount">{money(spendingBalance)}</div><div className="balance-foot"><span>{transactions.length ? `${transactions.length} local entries` : 'No money logged yet'}</span></div><div className="balance-orbit"><i></i><i></i><i></i><div className="orbit-sun">✦</div></div></div>
          <div className="week-card"><div className="section-head"><div><span className="eyebrow">YOUR RHYTHM</span><h3>This week</h3></div><button className="text-btn">View all →</button></div><div className="days">{[['M','5'],['T','6'],['W','7'],['T','8'],['F','9'],['S','10'],['S','11']].map(([d,n]) => <button key={n} className={selectedDay === d+n ? 'day selected' : 'day'} onClick={() => setSelectedDay(d+n)}><span>{d}</span><b>{n}</b><i className={n === '6' ? 'dot active-dot' : n === '5' || n === '7' ? 'dot' : 'dot empty'}></i></button>)}</div><div className="week-total"><div><span>Spent this week</span><strong>{money(spent)}</strong></div><div className="mini-bars"><i style={{height:'30%'}}></i><i style={{height:'55%'}}></i><i style={{height:'44%'}}></i><i style={{height:'74%'}}></i><i style={{height:'32%'}}></i><i style={{height:'64%'}}></i><i style={{height:'20%'}}></i></div></div></div>
        </section>
        <section className="section"><div className="section-head"><div><span className="eyebrow">YOUR SPACES</span><h2>Where your money lives</h2></div><button className="round-add" onClick={() => setShowAddMoney(true)}>＋</button></div><div className="space-grid">{spaces.map(s => <div className={'space-card '+s.tint} key={s.name}><div className="space-icon">{s.icon}</div><div className="space-meta"><span>{s.name}</span><small>{s.note}</small></div><strong>{money(spaceBalances[s.name] || 0)}</strong><div className="space-line"><i style={{width: `${Math.min(100, (spaceBalances[s.name] || 0) / Math.max(1, spendingBalance, 100) * 100)}%`}}></i></div></div>)}</div></section>
        <section className="lower-grid"><div className="recent section-card"><div className="section-head"><div><span className="eyebrow">RECENT ACTIVITY</span><h2>Your money trail</h2></div><button className="text-btn" onClick={() => setActive('Activity')}>See all →</button></div><div className="transactions">{transactions.slice(0, 3).map(t => <div className="transaction" key={t.id}><div className={'transaction-icon '+t.color}>{t.icon}</div><div className="transaction-name"><strong>{t.title}</strong><span>{t.category} · {t.source}</span></div><div className="transaction-amount"><strong>−{money(t.amount)}</strong><span>{t.day}</span></div></div>)}</div></div><div className="goal-card"><div className="goal-title"><span className="eyebrow">ONE THING AT A TIME</span><button>•••</button></div><h2>New headphones</h2><p>Almost there. Keep going!</p><div className="progress-ring"><div><strong>72%</strong><span>saved</span></div></div><div className="goal-foot"><span>{money(7200)} of {money(10000)}</span><button className="small-arrow">↗</button></div></div></section>
      </>}
      {active === 'Activity' && <Activity transactions={transactions} />}
      {active === 'Goals' && <Goals />}
      {active === 'Insights' && <Insights spent={spent} />}
    </main>
    <button className="fab" onClick={() => setShowLog(true)}><span>＋</span>Log expense</button>
    {showLog && <div className="modal-backdrop" onMouseDown={() => setShowLog(false)}><form className="log-sheet" onSubmit={addExpense} onMouseDown={e => e.stopPropagation()}><div className="sheet-handle"></div><div className="sheet-title"><div><span className="eyebrow">QUICK LOG</span><h2>What did you spend?</h2></div><button type="button" className="close" onClick={() => setShowLog(false)}>×</button></div><label>Amount<input autoFocus inputMode="numeric" value={amount} onChange={e => setAmount(e.target.value)} placeholder="₹ 0" /></label><label>What was it?<input value={description} onChange={e => setDescription(e.target.value)} placeholder="e.g. Coffee" /></label><label>Category<select value={category} onChange={e => setCategory(e.target.value)}><option>Food</option><option>Transport</option><option>Education</option><option>Shopping</option><option>Entertainment</option></select></label><button className="submit" type="submit">Add expense <span>→</span></button></form></div>}
    {showAddMoney && <div className="modal-backdrop" onMouseDown={() => setShowAddMoney(false)}><form className="log-sheet" onSubmit={addMoney} onMouseDown={e => e.stopPropagation()}><div className="sheet-handle"></div><div className="sheet-title"><div><span className="eyebrow">ADD MONEY</span><h2>Where should it go?</h2></div><button type="button" className="close" onClick={() => setShowAddMoney(false)}>×</button></div><label>Amount received<input autoFocus inputMode="numeric" value={incomeAmount} onChange={e => setIncomeAmount(e.target.value)} placeholder="₹ 0" /></label><label>Money Space<select value={incomeDestination} onChange={e => setIncomeDestination(e.target.value)}>{spaces.map(s => <option key={s.name}>{s.name}</option>)}</select></label><button className="submit" type="submit">Add money <span>→</span></button></form></div>}
    {receipt && <Receipt receipt={receipt} onClose={() => setReceipt(null)} />}
    {toast && <div className="toast">✓ {toast}</div>}
  </div>
}

function Receipt({ receipt, onClose }) {
  const [date, time] = receipt.timestamp.split(', ');
  const bars = receipt.receiptNumber.split('').flatMap((char, i) => Array.from({ length: (char.charCodeAt(0) % 3) + 1 }, (_, j) => <i key={`${i}-${j}`} style={{ width: `${(char.charCodeAt(0) + j) % 3 + 1}px` }} />));
  const warning = receipt.remaining < 1000 ? `CAREFUL. ONLY ${money(receipt.remaining)} LEFT.` : receipt.remaining < 3000 ? `YOUR WALLET IS GETTING OPINIONATED.` : `MONEY HAS LEFT THE BUILDING.`;
  return <div className="receipt-backdrop" onMouseDown={onClose}><div className="receipt-paper" onMouseDown={e => e.stopPropagation()}><div className="receipt-content"><div className="receipt-brand">TEENAGE MONEY</div><div className="receipt-sub">MONEY RECEIPT</div><div className="receipt-number">NO. {receipt.receiptNumber}</div><div className="receipt-row"><span>{date.toUpperCase()}</span><span>{time}</span></div><div className="receipt-rule">- - - - - - - - - - - - - - - -</div><div className="receipt-label"><span>ITEM</span><span>PRICE</span></div><div className="receipt-row item"><span>{receipt.title}</span><strong>{money(receipt.amount)}</strong></div><div className="receipt-row tiny"><span>{receipt.category}</span><span>UPI</span></div><div className="receipt-rule">- - - - - - - - - - - - - - - -</div><div className="receipt-row total"><strong>TOTAL</strong><strong>{money(receipt.amount)}</strong></div><div className="receipt-block"><span>PAID FROM</span><strong>Spending / {receipt.source}</strong></div><div className="receipt-block balance"><span>REMAINING</span><strong>{money(receipt.remaining)}</strong></div><div className="receipt-rule">- - - - - - - - - - - - - - - -</div><div className="receipt-warning">{warning}</div><div className="receipt-barcode">{bars}</div><div className="receipt-thanks">THANK YOU.<br/><span>Your future self has received the receipt.</span></div><button className="receipt-close" onClick={onClose}>Tap anywhere to return</button></div></div></div>
}

function Activity({ transactions }) { return <section className="page"><div className="page-intro"><div><span className="eyebrow">ALL ACTIVITY</span><h2>The money trail</h2><p>A clear view of where your money has been moving.</p></div><button className="filter">This month⌄</button></div><div className="activity-card">{transactions.length ? transactions.map(t => <div className="transaction large" key={t.id}><div className={'transaction-icon '+t.color}>{t.icon}</div><div className="transaction-name"><strong>{t.title}</strong><span>{t.category} · {t.source}</span></div><div className="transaction-amount"><strong className={t.type === 'income' ? 'income' : ''}>{t.type === 'income' ? '+' : '−'}{money(t.amount)}</strong><span>{t.day}</span></div></div>) : <div className="empty-state">Nothing here yet. Your real activity will appear after you add money or log a spend.</div>}</div></section> }
function Goals() { return <section className="page"><div className="page-intro"><div><span className="eyebrow">YOUR WHY</span><h2>Goals worth saving for</h2><p>Give your money somewhere meaningful to go.</p></div><button className="filter">＋ New goal</button></div><div className="goal-list"><div className="big-goal peach"><div><span className="eyebrow">72% COMPLETE</span><h2>New headphones</h2><p>₹2,800 left to go</p><div className="wide-progress"><i></i></div></div><div className="goal-emoji">♫</div></div><div className="big-goal mint"><div><span className="eyebrow">35% COMPLETE</span><h2>Weekend in Goa</h2><p>₹13,000 left to go</p><div className="wide-progress"><i></i></div></div><div className="goal-emoji">◒</div></div></div></section> }
function Insights({ spent }) { return <section className="page"><div className="page-intro"><div><span className="eyebrow">A LITTLE CLARITY</span><h2>Money, in perspective</h2><p>Simple observations from your real activity.</p></div><button className="filter">August 2024⌄</button></div><div className="insight-grid"><div className="insight-card"><span className="eyebrow">TOTAL SPENT</span><strong>{money(spent)}</strong><p>Across 3 purchases this month</p><div className="insight-bars"><i></i><i></i><i></i><i></i><i></i><i></i><i></i></div></div><div className="insight-card lavender"><span className="eyebrow">YOUR TOP CATEGORY</span><strong>Food</strong><p>It’s 46% of your spending so far.</p><div className="category-meter"><i></i></div></div><div className="insight-card mint"><span className="eyebrow">A GOOD RHYTHM</span><strong>4 days</strong><p>You’ve logged your money four days in a row.</p><div className="streak">• • • • •</div></div></div></section> }

createRoot(document.getElementById('root')).render(<App />);
