import React, { useMemo, useState, useEffect } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';
import { positiveAmount, percentage } from './finance.js';

const spaces = [
  { name: 'Spending', note: 'Everyday money', icon: '↗', tint: 'peach' },
  { name: 'Savings', note: 'Future you', icon: '✧', tint: 'mint' },
  { name: 'Emergency', note: 'Just in case', icon: '＋', tint: 'lavender' },
];

const defaultGoals = [
  { id: 1, title: 'New headphones', target: 10000, saved: 7200, tint: 'peach', emoji: '♫' },
  { id: 2, title: 'Weekend in Goa', target: 20000, saved: 7000, tint: 'mint', emoji: '◒' },
];

const money = n => '₹' + Number(n || 0).toLocaleString('en-IN');

const triggerHaptic = (duration = 15) => {
  try {
    if (window.Android && typeof window.Android.vibrate === 'function') {
      window.Android.vibrate(duration);
    } else if (typeof navigator !== 'undefined' && navigator.vibrate) {
      navigator.vibrate(duration);
    }
  } catch {
    // Ignore haptic errors on unsupported environments
  }
};

function App() {
  const [active, setActive] = useState('Home');
  const [transactions, setTransactions] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('tm-transactions') || '[]');
    } catch {
      return [];
    }
  });

  const [spaceBalances, setSpaceBalances] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('tm-spaces') || '{"Spending":0,"Savings":0,"Emergency":0}');
    } catch {
      return { Spending: 0, Savings: 0, Emergency: 0 };
    }
  });

  const [goals, setGoals] = useState(() => {
    try {
      const stored = localStorage.getItem('tm-goals');
      return stored ? JSON.parse(stored) : defaultGoals;
    } catch {
      return defaultGoals;
    }
  });

  const [showLog, setShowLog] = useState(false);
  const [showAddMoney, setShowAddMoney] = useState(false);
  const [showAddGoal, setShowAddGoal] = useState(false);
  const [toast, setToast] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Food');
  const [selectedDay, setSelectedDay] = useState('T6');
  const [receipt, setReceipt] = useState(null);
  const [incomeAmount, setIncomeAmount] = useState('');
  const [incomeDestination, setIncomeDestination] = useState('Spending');

  // New goal form state
  const [goalTitle, setGoalTitle] = useState('');
  const [goalTarget, setGoalTarget] = useState('');
  const [goalSaved, setGoalSaved] = useState('');
  const [goalTint, setGoalTint] = useState('peach');
  const [goalEmoji, setGoalEmoji] = useState('♫');

  useEffect(() => {
    try {
      localStorage.setItem('tm-transactions', JSON.stringify(transactions));
      localStorage.setItem('tm-spaces', JSON.stringify(spaceBalances));
      localStorage.setItem('tm-goals', JSON.stringify(goals));
    } catch (e) {
      console.error('Storage error', e);
    }
  }, [transactions, spaceBalances, goals]);

  const spent = useMemo(() => {
    return transactions.filter(t => t.type !== 'income').reduce((sum, t) => sum + t.amount, 0);
  }, [transactions]);

  const spendingBalance = spaceBalances.Spending || 0;

  const showToastMsg = msg => {
    setToast(msg);
    if (window.Android && typeof window.Android.showToast === 'function') {
      try { window.Android.showToast(msg); } catch {}
    }
    setTimeout(() => setToast(''), 2400);
  };

  const addExpense = e => {
    e.preventDefault();
    const value = positiveAmount(amount);
    if (!value || !description.trim()) {
      showToastMsg('Please enter a valid amount and description');
      return;
    }
    if (value > spendingBalance) {
      triggerHaptic(40);
      showToastMsg(`Not enough in Spending. You have ${money(spendingBalance)}.`);
      return;
    }

    triggerHaptic(25);
    const now = new Date();
    const id = Date.now();
    const receiptNumber = `TM-${String(now.getFullYear()).slice(-2)}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}-${Math.random().toString(36).slice(2, 6).toUpperCase()}`;
    const expense = {
      id,
      title: description.trim(),
      category,
      source: 'UPI',
      amount: value,
      day: 'Today',
      icon: category === 'Food' ? '☕' : category === 'Transport' ? '⚡' : category === 'Education' ? '✎' : category === 'Shopping' ? '🛍' : '✦',
      color: category === 'Food' ? 'orange' : category === 'Transport' ? 'blue' : category === 'Education' ? 'mint' : 'violet'
    };

    setTransactions([expense, ...transactions]);
    setSpaceBalances({ ...spaceBalances, Spending: spendingBalance - value });
    setReceipt({
      ...expense,
      receiptNumber,
      timestamp: now.toLocaleString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: false }),
      remaining: Math.max(0, spendingBalance - value)
    });
    setAmount('');
    setDescription('');
    setShowLog(false);
    showToastMsg('Expense added to Spending');
  };

  const addMoney = e => {
    e.preventDefault();
    const value = positiveAmount(incomeAmount);
    if (!value) {
      showToastMsg('Enter an amount greater than zero');
      return;
    }
    triggerHaptic(25);
    const next = { ...spaceBalances, [incomeDestination]: (spaceBalances[incomeDestination] || 0) + value };
    setSpaceBalances(next);
    setTransactions([
      {
        id: Date.now(),
        type: 'income',
        title: `Added to ${incomeDestination}`,
        category: 'Income',
        source: 'Manual',
        amount: value,
        day: 'Today',
        icon: '↓',
        color: 'mint'
      },
      ...transactions
    ]);
    setIncomeAmount('');
    setShowAddMoney(false);
    showToastMsg(`${money(value)} added to ${incomeDestination}`);
  };

  const handleCreateGoal = e => {
    e.preventDefault();
    const targetVal = positiveAmount(goalTarget);
    if (!goalTitle.trim() || !targetVal) {
      showToastMsg('Enter a goal title and target amount');
      return;
    }
    const savedVal = Math.min(targetVal, positiveAmount(goalSaved) || 0);
    triggerHaptic(20);
    const newGoal = {
      id: Date.now(),
      title: goalTitle.trim(),
      target: targetVal,
      saved: savedVal,
      tint: goalTint,
      emoji: goalEmoji || '★',
    };
    setGoals([newGoal, ...goals]);
    setGoalTitle('');
    setGoalTarget('');
    setGoalSaved('');
    setShowAddGoal(false);
    showToastMsg(`Goal "${newGoal.title}" created!`);
  };

  const featuredGoal = goals[0] || { title: 'New headphones', target: 10000, saved: 7200 };
  const featuredGoalPercent = Math.min(100, Math.round(((featuredGoal.saved || 0) / Math.max(1, featuredGoal.target)) * 100));

  const navItems = [
    { name: 'Home', icon: '⌂' },
    { name: 'Activity', icon: '◷' },
    { name: 'Goals', icon: '◇' },
    { name: 'Insights', icon: '⌁' },
  ];

  return (
    <div className="app-shell">
      {/* Desktop / Tablet Sidebar */}
      <aside className="sidebar">
        <div className="brand">
          <img className="brand-lockup" src="/teenage-money-logo-lockup.png" alt="Teenage Money" />
        </div>
        <div className="profile">
          <div className="avatar">A</div>
          <div><strong>Hey, Alex</strong><span>Building your future</span></div>
          <button className="dots" onClick={() => showToastMsg('Alex’s Offline Wallet')}>•••</button>
        </div>
        <nav>
          {navItems.map(item => (
            <button
              key={item.name}
              className={active === item.name ? 'nav-item active' : 'nav-item'}
              onClick={() => { triggerHaptic(10); setActive(item.name); }}
            >
              <span>{item.icon}</span>
              {item.name}
            </button>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <div className="tip-card">
            <span className="tip-spark">✦</span>
            <strong>Small steps,<br />big changes.</strong>
            <p>You’re doing better than you think.</p>
          </div>
          <button className="nav-item" onClick={() => showToastMsg('Teenage Money Mobile v1.0 • Offline Ready')}>
            <span>⚙</span>Settings
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="main">
        {/* Mobile Header */}
        <header className="mobile-header">
          <div className="mobile-brand">
            <img className="brand-lockup mobile-lockup" src="/teenage-money-logo-lockup.png" alt="Teenage Money" />
            <div>
              <p className="eyebrow">TUESDAY, 6 AUG</p>
              <h2>{active === 'Home' ? 'Hey, Alex' : active}</h2>
            </div>
          </div>
          <div className="mobile-actions">
            <button
              className="mobile-action-pill"
              onClick={() => { triggerHaptic(10); setShowAddMoney(true); }}
              title="Add Money"
            >
              <span>＋</span> Money
            </button>
            <div className="mini-avatar">A</div>
          </div>
        </header>

        {/* Desktop Topbar */}
        <header className="topbar desktop-only">
          <div>
            <p className="eyebrow">TUESDAY, 6 AUGUST</p>
            <h1>{active === 'Home' ? 'Your money, your way.' : active}</h1>
          </div>
          <div className="top-actions">
            <button className="icon-btn" onClick={() => showToastMsg('All offline searches active')}>⌕</button>
            <button className="icon-btn" onClick={() => { triggerHaptic(10); setShowAddMoney(true); }} title="Add Money">＋</button>
            <button className="profile-pill">
              <span className="mini-avatar">A</span>Alex <span>⌄</span>
            </button>
          </div>
        </header>

        {active === 'Home' && (
          <>
            <section className="hero-grid">
              <div className="balance-card">
                <div className="balance-top">
                  <span>Spending money left</span>
                  <button onClick={() => { triggerHaptic(10); setShowAddMoney(true); }}>＋ Add</button>
                </div>
                <div className="balance-amount">{money(spendingBalance)}</div>
                <div className="balance-foot">
                  <span>{transactions.length ? `${transactions.length} local entries` : 'No money logged yet'}</span>
                </div>
                <div className="balance-orbit">
                  <i></i><i></i><i></i>
                  <div className="orbit-sun">✦</div>
                </div>
              </div>

              <div className="week-card">
                <div className="section-head">
                  <div>
                    <span className="eyebrow">YOUR RHYTHM</span>
                    <h3>This week</h3>
                  </div>
                  <button className="text-btn" onClick={() => { triggerHaptic(10); setActive('Activity'); }}>View all →</button>
                </div>
                <div className="days">
                  {[
                    ['M', '5'], ['T', '6'], ['W', '7'], ['T', '8'], ['F', '9'], ['S', '10'], ['S', '11']
                  ].map(([d, n]) => (
                    <button
                      key={d + n}
                      className={selectedDay === d + n ? 'day selected' : 'day'}
                      onClick={() => { triggerHaptic(10); setSelectedDay(d + n); }}
                    >
                      <span>{d}</span>
                      <b>{n}</b>
                      <i className={n === '6' ? 'dot active-dot' : n === '5' || n === '7' ? 'dot' : 'dot empty'}></i>
                    </button>
                  ))}
                </div>
                <div className="week-total">
                  <div>
                    <span>Spent this week</span>
                    <strong>{money(spent)}</strong>
                  </div>
                  <div className="mini-bars">
                    <i style={{ height: '30%' }}></i>
                    <i style={{ height: '55%' }}></i>
                    <i style={{ height: '44%' }}></i>
                    <i style={{ height: '74%' }}></i>
                    <i style={{ height: '32%' }}></i>
                    <i style={{ height: '64%' }}></i>
                    <i style={{ height: '20%' }}></i>
                  </div>
                </div>
              </div>
            </section>

            <section className="section">
              <div className="section-head">
                <div>
                  <span className="eyebrow">YOUR SPACES</span>
                  <h2>Where your money lives</h2>
                </div>
                <button className="round-add" onClick={() => { triggerHaptic(10); setShowAddMoney(true); }} title="Add Money">＋</button>
              </div>
              <div className="space-grid">
                {spaces.map(s => {
                  const bal = spaceBalances[s.name] || 0;
                  const pct = Math.min(100, bal / Math.max(1, spendingBalance, 100) * 100);
                  return (
                    <div className={'space-card ' + s.tint} key={s.name}>
                      <div className="space-icon">{s.icon}</div>
                      <div className="space-meta">
                        <span>{s.name}</span>
                        <small>{s.note}</small>
                      </div>
                      <strong>{money(bal)}</strong>
                      <div className="space-line">
                        <i style={{ width: `${pct}%` }}></i>
                      </div>
                    </div>
                  );
                })}
              </div>
            </section>

            <section className="lower-grid">
              <div className="recent section-card">
                <div className="section-head">
                  <div>
                    <span className="eyebrow">RECENT ACTIVITY</span>
                    <h2>Your money trail</h2>
                  </div>
                  <button className="text-btn" onClick={() => { triggerHaptic(10); setActive('Activity'); }}>See all →</button>
                </div>
                <div className="transactions">
                  {transactions.length > 0 ? (
                    transactions.slice(0, 3).map(t => (
                      <div className="transaction" key={t.id}>
                        <div className={'transaction-icon ' + t.color}>{t.icon}</div>
                        <div className="transaction-name">
                          <strong>{t.title}</strong>
                          <span>{t.category} · {t.source}</span>
                        </div>
                        <div className="transaction-amount">
                          <strong className={t.type === 'income' ? 'income' : ''}>
                            {t.type === 'income' ? '+' : '−'}{money(t.amount)}
                          </strong>
                          <span>{t.day}</span>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="empty-state">No transactions yet. Tap "Log expense" below to get started!</div>
                  )}
                </div>
              </div>

              <div className="goal-card" onClick={() => { triggerHaptic(10); setActive('Goals'); }} style={{ cursor: 'pointer' }}>
                <div className="goal-title">
                  <span className="eyebrow">ONE THING AT A TIME</span>
                  <button onClick={e => { e.stopPropagation(); setShowAddGoal(true); }}>•••</button>
                </div>
                <h2>{featuredGoal.title}</h2>
                <p>Almost there. Keep going!</p>
                <div className="progress-ring">
                  <div>
                    <strong>{featuredGoalPercent}%</strong>
                    <span>saved</span>
                  </div>
                </div>
                <div className="goal-foot">
                  <span>{money(featuredGoal.saved)} of {money(featuredGoal.target)}</span>
                  <button className="small-arrow">↗</button>
                </div>
              </div>
            </section>
          </>
        )}

        {active === 'Activity' && (
          <Activity
            transactions={transactions}
            onLogExpense={() => setShowLog(true)}
          />
        )}

        {active === 'Goals' && (
          <Goals
            goals={goals}
            onOpenAddGoal={() => setShowAddGoal(true)}
          />
        )}

        {active === 'Insights' && (
          <Insights
            spent={spent}
            transactions={transactions}
          />
        )}
      </main>

      {/* Floating Action Button */}
      <button
        className="fab"
        onClick={() => { triggerHaptic(20); setShowLog(true); }}
        aria-label="Log expense"
      >
        <span>＋</span>
        <span>Log expense</span>
      </button>

      {/* Mobile Bottom Navigation Bar */}
      <nav className="mobile-bottom-nav">
        {navItems.map(item => (
          <button
            key={item.name}
            className={active === item.name ? 'mobile-nav-item active' : 'mobile-nav-item'}
            onClick={() => { triggerHaptic(12); setActive(item.name); }}
          >
            <span className="nav-icon">{item.icon}</span>
            <span className="nav-label">{item.name}</span>
          </button>
        ))}
      </nav>

      {/* Quick Log Sheet */}
      {showLog && (
        <div className="modal-backdrop" onMouseDown={() => setShowLog(false)}>
          <form className="log-sheet" onSubmit={addExpense} onMouseDown={e => e.stopPropagation()}>
            <div className="sheet-handle" onClick={() => setShowLog(false)}></div>
            <div className="sheet-title">
              <div>
                <span className="eyebrow">QUICK LOG</span>
                <h2>What did you spend?</h2>
              </div>
              <button type="button" className="close" onClick={() => setShowLog(false)}>×</button>
            </div>
            <label>
              Amount
              <input
                autoFocus
                inputMode="decimal"
                value={amount}
                onChange={e => setAmount(e.target.value)}
                placeholder="₹ 0"
              />
            </label>
            <label>
              What was it?
              <input
                value={description}
                onChange={e => setDescription(e.target.value)}
                placeholder="e.g. Coffee, Subway, Book"
              />
            </label>
            <label>
              Category
              <select value={category} onChange={e => setCategory(e.target.value)}>
                <option>Food</option>
                <option>Transport</option>
                <option>Education</option>
                <option>Shopping</option>
                <option>Entertainment</option>
              </select>
            </label>
            <button className="submit" type="submit">
              Add expense <span>→</span>
            </button>
          </form>
        </div>
      )}

      {/* Add Money Sheet */}
      {showAddMoney && (
        <div className="modal-backdrop" onMouseDown={() => setShowAddMoney(false)}>
          <form className="log-sheet" onSubmit={addMoney} onMouseDown={e => e.stopPropagation()}>
            <div className="sheet-handle" onClick={() => setShowAddMoney(false)}></div>
            <div className="sheet-title">
              <div>
                <span className="eyebrow">ADD MONEY</span>
                <h2>Where should it go?</h2>
              </div>
              <button type="button" className="close" onClick={() => setShowAddMoney(false)}>×</button>
            </div>
            <label>
              Amount received
              <input
                autoFocus
                inputMode="decimal"
                value={incomeAmount}
                onChange={e => setIncomeAmount(e.target.value)}
                placeholder="₹ 0"
              />
            </label>
            <label>
              Money Space
              <select value={incomeDestination} onChange={e => setIncomeDestination(e.target.value)}>
                {spaces.map(s => <option key={s.name} value={s.name}>{s.name} ({s.note})</option>)}
              </select>
            </label>
            <button className="submit" type="submit">
              Add money <span>→</span>
            </button>
          </form>
        </div>
      )}

      {/* Add Goal Sheet */}
      {showAddGoal && (
        <div className="modal-backdrop" onMouseDown={() => setShowAddGoal(false)}>
          <form className="log-sheet" onSubmit={handleCreateGoal} onMouseDown={e => e.stopPropagation()}>
            <div className="sheet-handle" onClick={() => setShowAddGoal(false)}></div>
            <div className="sheet-title">
              <div>
                <span className="eyebrow">NEW SAVINGS GOAL</span>
                <h2>What are you saving for?</h2>
              </div>
              <button type="button" className="close" onClick={() => setShowAddGoal(false)}>×</button>
            </div>
            <label>
              Goal title
              <input
                autoFocus
                value={goalTitle}
                onChange={e => setGoalTitle(e.target.value)}
                placeholder="e.g. Mechanical Keyboard"
              />
            </label>
            <label>
              Target amount
              <input
                inputMode="decimal"
                value={goalTarget}
                onChange={e => setGoalTarget(e.target.value)}
                placeholder="₹ 5000"
              />
            </label>
            <label>
              Already saved (optional)
              <input
                inputMode="decimal"
                value={goalSaved}
                onChange={e => setGoalSaved(e.target.value)}
                placeholder="₹ 0"
              />
            </label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <label>
                Theme tint
                <select value={goalTint} onChange={e => setGoalTint(e.target.value)}>
                  <option value="peach">Peach</option>
                  <option value="mint">Mint</option>
                  <option value="lavender">Lavender</option>
                </select>
              </label>
              <label>
                Icon symbol
                <select value={goalEmoji} onChange={e => setGoalEmoji(e.target.value)}>
                  <option value="♫">♫ Music</option>
                  <option value="◒">◒ Travel</option>
                  <option value="★">★ Star</option>
                  <option value="💻">💻 Tech</option>
                  <option value="👟">👟 Shoes</option>
                </select>
              </label>
            </div>
            <button className="submit" type="submit">
              Create goal <span>→</span>
            </button>
          </form>
        </div>
      )}

      {/* Thermal Receipt Overlay */}
      {receipt && <Receipt receipt={receipt} onClose={() => setReceipt(null)} />}

      {/* Toast Alert */}
      {toast && <div className="toast">✓ {toast}</div>}
    </div>
  );
}

function Receipt({ receipt, onClose }) {
  const [date, time] = receipt.timestamp ? receipt.timestamp.split(', ') : ['TODAY', ''];
  const bars = receipt.receiptNumber
    ? receipt.receiptNumber.split('').flatMap((char, i) =>
        Array.from({ length: (char.charCodeAt(0) % 3) + 1 }, (_, j) => (
          <i key={`${i}-${j}`} style={{ width: `${(char.charCodeAt(0) + j) % 3 + 1}px` }} />
        ))
      )
    : [];
  const warning = receipt.remaining < 1000
    ? `CAREFUL. ONLY ${money(receipt.remaining)} LEFT.`
    : receipt.remaining < 3000
    ? `YOUR WALLET IS GETTING OPINIONATED.`
    : `MONEY HAS LEFT THE BUILDING.`;

  return (
    <div className="receipt-backdrop" onMouseDown={onClose}>
      <div className="receipt-paper" onMouseDown={e => e.stopPropagation()}>
        <div className="receipt-content">
          <div className="receipt-brand">TEENAGE MONEY</div>
          <div className="receipt-sub">MONEY RECEIPT</div>
          <div className="receipt-number">NO. {receipt.receiptNumber}</div>
          <div className="receipt-row">
            <span>{date ? date.toUpperCase() : ''}</span>
            <span>{time}</span>
          </div>
          <div className="receipt-rule">- - - - - - - - - - - - - - - -</div>
          <div className="receipt-label">
            <span>ITEM</span>
            <span>PRICE</span>
          </div>
          <div className="receipt-row item">
            <span>{receipt.title}</span>
            <strong>{money(receipt.amount)}</strong>
          </div>
          <div className="receipt-row tiny">
            <span>{receipt.category}</span>
            <span>UPI</span>
          </div>
          <div className="receipt-rule">- - - - - - - - - - - - - - - -</div>
          <div className="receipt-row total">
            <strong>TOTAL</strong>
            <strong>{money(receipt.amount)}</strong>
          </div>
          <div className="receipt-block">
            <span>PAID FROM</span>
            <strong>Spending / {receipt.source}</strong>
          </div>
          <div className="receipt-block balance">
            <span>REMAINING</span>
            <strong>{money(receipt.remaining)}</strong>
          </div>
          <div className="receipt-rule">- - - - - - - - - - - - - - - -</div>
          <div className="receipt-warning">{warning}</div>
          <div className="receipt-barcode">{bars}</div>
          <div className="receipt-thanks">
            THANK YOU.<br />
            <span>Your future self has received the receipt.</span>
          </div>
          <button className="receipt-close" onClick={onClose}>
            Tap anywhere to return
          </button>
        </div>
      </div>
    </div>
  );
}

function Activity({ transactions, onLogExpense }) {
  return (
    <section className="page">
      <div className="page-intro">
        <div>
          <span className="eyebrow">ALL ACTIVITY</span>
          <h2>The money trail</h2>
          <p>A clear view of where your money has been moving.</p>
        </div>
        <button className="filter" onClick={onLogExpense}>＋ Log</button>
      </div>
      <div className="activity-card">
        {transactions.length ? (
          transactions.map(t => (
            <div className="transaction large" key={t.id}>
              <div className={'transaction-icon ' + t.color}>{t.icon}</div>
              <div className="transaction-name">
                <strong>{t.title}</strong>
                <span>{t.category} · {t.source}</span>
              </div>
              <div className="transaction-amount">
                <strong className={t.type === 'income' ? 'income' : ''}>
                  {t.type === 'income' ? '+' : '−'}{money(t.amount)}
                </strong>
                <span>{t.day}</span>
              </div>
            </div>
          ))
        ) : (
          <div className="empty-state">
            Nothing here yet. Your real activity will appear after you add money or log a spend.
          </div>
        )}
      </div>
    </section>
  );
}

function Goals({ goals, onOpenAddGoal }) {
  return (
    <section className="page">
      <div className="page-intro">
        <div>
          <span className="eyebrow">YOUR WHY</span>
          <h2>Goals worth saving for</h2>
          <p>Give your money somewhere meaningful to go.</p>
        </div>
        <button className="filter" onClick={onOpenAddGoal}>＋ New goal</button>
      </div>
      <div className="goal-list">
        {goals.map(g => {
          const pct = Math.min(100, Math.round(((g.saved || 0) / Math.max(1, g.target)) * 100));
          const left = Math.max(0, g.target - (g.saved || 0));
          return (
            <div className={'big-goal ' + (g.tint || 'peach')} key={g.id}>
              <div>
                <span className="eyebrow">{pct}% COMPLETE</span>
                <h2>{g.title}</h2>
                <p>{money(left)} left to go</p>
                <div className="wide-progress">
                  <i style={{ width: `${pct}%` }}></i>
                </div>
              </div>
              <div className="goal-emoji">{g.emoji || '★'}</div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

function Insights({ spent, transactions }) {
  const expenseCount = transactions.filter(t => t.type !== 'income').length;

  const topCategoryData = useMemo(() => {
    const expenses = transactions.filter(t => t.type !== 'income');
    if (!expenses.length) return { name: 'None yet', pct: 0 };
    const counts = {};
    expenses.forEach(e => {
      counts[e.category] = (counts[e.category] || 0) + e.amount;
    });
    let topCat = 'Food';
    let maxVal = 0;
    Object.entries(counts).forEach(([cat, val]) => {
      if (val > maxVal) {
        maxVal = val;
        topCat = cat;
      }
    });
    return {
      name: topCat,
      pct: percentage(maxVal, spent)
    };
  }, [transactions, spent]);

  return (
    <section className="page">
      <div className="page-intro">
        <div>
          <span className="eyebrow">A LITTLE CLARITY</span>
          <h2>Money, in perspective</h2>
          <p>Simple observations from your real activity.</p>
        </div>
        <button className="filter">This month</button>
      </div>
      <div className="insight-grid">
        <div className="insight-card">
          <span className="eyebrow">TOTAL SPENT</span>
          <strong>{money(spent)}</strong>
          <p>Across {expenseCount} {expenseCount === 1 ? 'purchase' : 'purchases'} logged</p>
          <div className="insight-bars">
            <i style={{ height: '35%' }}></i>
            <i style={{ height: '55%' }}></i>
            <i style={{ height: '40%' }}></i>
            <i style={{ height: '70%' }}></i>
            <i style={{ height: '48%' }}></i>
            <i style={{ height: '80%' }}></i>
            <i style={{ height: '62%' }}></i>
          </div>
        </div>

        <div className="insight-card lavender">
          <span className="eyebrow">YOUR TOP CATEGORY</span>
          <strong>{topCategoryData.name}</strong>
          <p>
            {topCategoryData.pct > 0
              ? `It’s ${topCategoryData.pct}% of your spending so far.`
              : 'Add expenses to see your top category.'}
          </p>
          <div className="category-meter">
            <i style={{ width: `${topCategoryData.pct || 0}%` }}></i>
          </div>
        </div>

        <div className="insight-card mint">
          <span className="eyebrow">A GOOD RHYTHM</span>
          <strong>{Math.min(7, Math.max(1, expenseCount))} {expenseCount === 1 ? 'entry' : 'entries'}</strong>
          <p>You’re building a mindful habit with each entry.</p>
          <div className="streak">• • • • •</div>
        </div>
      </div>
    </section>
  );
}

createRoot(document.getElementById('root')).render(<App />);
