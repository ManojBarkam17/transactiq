import { useState, useEffect } from "react";
import { BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";

const COLORS = ["#6366f1", "#8b5cf6", "#ec4899", "#f59e0b", "#10b981", "#3b82f6", "#ef4444"];

const categories = ["All", "Food & Dining", "Shopping", "Transport", "Entertainment", "Health", "Subscriptions", "Travel"];

const generateTransactions = () => {
  const merchants = {
    "Food & Dining": ["Starbucks", "McDonald's", "Chipotle", "Whole Foods", "DoorDash", "Uber Eats"],
    "Shopping": ["Amazon", "Target", "Nike", "Zara", "Best Buy", "Walmart"],
    "Transport": ["Uber", "Lyft", "Shell Gas", "Metro Card", "Parking"],
    "Entertainment": ["Netflix", "Spotify", "AMC Theaters", "Steam", "Apple TV+"],
    "Health": ["CVS Pharmacy", "Gym Membership", "Doctor Co-pay", "Vitamins"],
    "Subscriptions": ["Netflix", "Spotify", "Adobe CC", "AWS", "GitHub Pro"],
    "Travel": ["Delta Airlines", "Marriott", "Airbnb", "Expedia", "Hertz"],
  };
  const txns = [];
  for (let i = 0; i < 180; i++) {
    const cat = Object.keys(merchants)[Math.floor(Math.random() * Object.keys(merchants).length)];
    const merchant = merchants[cat][Math.floor(Math.random() * merchants[cat].length)];
    const date = new Date(2024, 0, Math.floor(Math.random() * 90) + 1);
    txns.push({
      id: i + 1,
      merchant,
      category: cat,
      amount: parseFloat((Math.random() * 200 + 5).toFixed(2)),
      date: date.toISOString().split("T")[0],
      status: Math.random() > 0.05 ? "Completed" : "Pending",
    });
  }
  return txns.sort((a, b) => new Date(b.date) - new Date(a.date));
};

const transactions = generateTransactions();

const weeklyData = [
  { week: "Week 1", spending: 842, budget: 800 },
  { week: "Week 2", spending: 1205, budget: 800 },
  { week: "Week 3", spending: 678, budget: 800 },
  { week: "Week 4", spending: 1456, budget: 800 },
  { week: "Week 5", spending: 923, budget: 800 },
  { week: "Week 6", spending: 1102, budget: 800 },
];

const categorySpend = [
  { name: "Food & Dining", value: 1240 },
  { name: "Shopping", value: 890 },
  { name: "Transport", value: 420 },
  { name: "Entertainment", value: 310 },
  { name: "Health", value: 280 },
  { name: "Subscriptions", value: 156 },
  { name: "Travel", value: 720 },
];

const aiInsights = [
  { icon: "📈", title: "Spending Spike Detected", body: "Your Week 4 spending was 82% above average. Primary driver: Shopping (+$340) and Travel (+$290). Consider setting weekly caps." },
  { icon: "🌙", title: "Weekend Pattern", body: "You spend 2.4x more on weekends vs. weekdays. Food & Dining accounts for 58% of weekend spend — mostly delivery apps." },
  { icon: "🔁", title: "Subscription Audit", body: "You have 6 active subscriptions totaling $156/mo. Adobe CC ($54.99) and GitHub Pro ($10) overlap with potential free alternatives." },
  { icon: "✈️", title: "Travel Trend", body: "Travel spend jumped 65% this quarter. Booking flights 3+ weeks in advance could save an estimated $180–240 based on your patterns." },
  { icon: "💡", title: "Savings Opportunity", body: "Switching 3 coffee shop visits/week to home brewing could save ~$85/month. You've spent $342 at Starbucks this quarter alone." },
];

export default function App() {
  const [tab, setTab] = useState("overview");
  const [search, setSearch] = useState("");
  const [filterCat, setFilterCat] = useState("All");
  const [insightLoading, setInsightLoading] = useState(false);
  const [insightsVisible, setInsightsVisible] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);

  const filteredTxns = transactions.filter((t) => {
    const matchSearch = t.merchant.toLowerCase().includes(search.toLowerCase());
    const matchCat = filterCat === "All" || t.category === filterCat;
    return matchSearch && matchCat;
  });

  const totalSpend = transactions.reduce((s, t) => s + t.amount, 0).toFixed(2);
  const avgTxn = (transactions.reduce((s, t) => s + t.amount, 0) / transactions.length).toFixed(2);
  const topCat = categorySpend.reduce((a, b) => (a.value > b.value ? a : b)).name;

  const runInsights = () => {
    setInsightLoading(true);
    setInsightsVisible(false);
    setTimeout(() => {
      setInsightLoading(false);
      setInsightsVisible(true);
    }, 2200);
  };

  return (
    <div style={{ minHeight: "100vh", background: "#0f0f1a", color: "#e2e8f0", fontFamily: "'Inter', sans-serif" }}>
      {/* Header */}
      <div style={{ background: "linear-gradient(135deg, #1e1b4b 0%, #312e81 100%)", padding: "20px 32px", display: "flex", alignItems: "center", justifyContent: "space-between", borderBottom: "1px solid #3730a3" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <div style={{ width: 38, height: 38, background: "linear-gradient(135deg,#6366f1,#8b5cf6)", borderRadius: 10, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 20 }}>💳</div>
          <div>
            <div style={{ fontSize: 22, fontWeight: 700, color: "#c7d2fe" }}>TransactIQ</div>
            <div style={{ fontSize: 12, color: "#818cf8" }}>AI-Powered Transaction Intelligence</div>
          </div>
        </div>
        <div style={{ display: "flex", gap: 8 }}>
          {["overview", "insights", "transactions"].map((t) => (
            <button key={t} onClick={() => setTab(t)} style={{ padding: "8px 18px", borderRadius: 8, border: "none", cursor: "pointer", fontWeight: 600, fontSize: 13, background: tab === t ? "#6366f1" : "rgba(99,102,241,0.15)", color: tab === t ? "#fff" : "#a5b4fc", transition: "all 0.2s" }}>
              {t === "overview" ? "📊 Overview" : t === "insights" ? "🤖 AI Insights" : "📋 Transactions"}
            </button>
          ))}
        </div>
      </div>

      <div style={{ maxWidth: 1200, margin: "0 auto", padding: 28 }}>
        {/* OVERVIEW TAB */}
        {tab === "overview" && (
          <div>
            {/* Stat Cards */}
            <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 16, marginBottom: 28 }}>
              {[
                { label: "Total Spend", value: `$${Number(totalSpend).toLocaleString()}`, sub: "Last 90 days", color: "#6366f1", icon: "💰" },
                { label: "Transactions", value: "180", sub: "Avg $" + avgTxn + " each", color: "#8b5cf6", icon: "🔄" },
                { label: "Top Category", value: topCat, sub: "$1,240 total", color: "#ec4899", icon: "🍽️" },
                { label: "Savings Potential", value: "$324/mo", sub: "AI identified", color: "#10b981", icon: "💡" },
              ].map((s) => (
                <div key={s.label} style={{ background: "linear-gradient(135deg,#1e1b4b,#1a1a2e)", border: `1px solid ${s.color}44`, borderRadius: 14, padding: 20 }}>
                  <div style={{ fontSize: 26 }}>{s.icon}</div>
                  <div style={{ fontSize: 22, fontWeight: 700, color: s.color, marginTop: 8 }}>{s.value}</div>
                  <div style={{ fontSize: 13, color: "#94a3b8", marginTop: 4 }}>{s.label}</div>
                  <div style={{ fontSize: 11, color: "#64748b", marginTop: 2 }}>{s.sub}</div>
                </div>
              ))}
            </div>

            {/* Charts Row */}
            <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: 20, marginBottom: 24 }}>
              <div style={{ background: "#1e1b4b", borderRadius: 14, padding: 22, border: "1px solid #3730a3" }}>
                <div style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#a5b4fc" }}>📅 Weekly Spending vs. Budget</div>
                <ResponsiveContainer width="100%" height={220}>
                  <LineChart data={weeklyData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#312e81" />
                    <XAxis dataKey="week" stroke="#64748b" fontSize={11} />
                    <YAxis stroke="#64748b" fontSize={11} />
                    <Tooltip contentStyle={{ background: "#1a1a2e", border: "1px solid #6366f1", borderRadius: 8 }} />
                    <Legend />
                    <Line type="monotone" dataKey="spending" stroke="#6366f1" strokeWidth={2.5} dot={{ r: 4 }} name="Spending" />
                    <Line type="monotone" dataKey="budget" stroke="#f59e0b" strokeWidth={2} strokeDasharray="5 5" name="Budget" />
                  </LineChart>
                </ResponsiveContainer>
              </div>
              <div style={{ background: "#1e1b4b", borderRadius: 14, padding: 22, border: "1px solid #3730a3" }}>
                <div style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#a5b4fc" }}>🥧 Spending by Category</div>
                <ResponsiveContainer width="100%" height={220}>
                  <PieChart>
                    <Pie data={categorySpend} cx="50%" cy="50%" innerRadius={55} outerRadius={85} dataKey="value" onClick={(d) => setSelectedCategory(d.name === selectedCategory ? null : d.name)}>
                      {categorySpend.map((_, i) => (
                        <Cell key={i} fill={COLORS[i % COLORS.length]} opacity={!selectedCategory || selectedCategory === categorySpend[i].name ? 1 : 0.3} />
                      ))}
                    </Pie>
                    <Tooltip contentStyle={{ background: "#1a1a2e", border: "1px solid #6366f1", borderRadius: 8 }} formatter={(v) => `$${v}`} />
                  </PieChart>
                </ResponsiveContainer>
                {selectedCategory && <div style={{ textAlign: "center", fontSize: 13, color: "#a5b4fc", marginTop: 8 }}>Selected: <b>{selectedCategory}</b></div>}
              </div>
            </div>

            {/* Bar Chart */}
            <div style={{ background: "#1e1b4b", borderRadius: 14, padding: 22, border: "1px solid #3730a3" }}>
              <div style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#a5b4fc" }}>📊 Category Spend Comparison</div>
              <ResponsiveContainer width="100%" height={200}>
                <BarChart data={categorySpend} layout="vertical">
                  <CartesianGrid strokeDasharray="3 3" stroke="#312e81" />
                  <XAxis type="number" stroke="#64748b" fontSize={11} />
                  <YAxis type="category" dataKey="name" stroke="#64748b" fontSize={11} width={120} />
                  <Tooltip contentStyle={{ background: "#1a1a2e", border: "1px solid #6366f1", borderRadius: 8 }} formatter={(v) => `$${v}`} />
                  <Bar dataKey="value" radius={[0, 6, 6, 0]}>
                    {categorySpend.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {/* AI INSIGHTS TAB */}
        {tab === "insights" && (
          <div>
            <div style={{ textAlign: "center", marginBottom: 28 }}>
              <div style={{ fontSize: 18, color: "#a5b4fc", marginBottom: 12 }}>Let Claude AI analyze your spending patterns</div>
              <button onClick={runInsights} disabled={insightLoading} style={{ padding: "12px 32px", background: insightLoading ? "#374151" : "linear-gradient(135deg,#6366f1,#8b5cf6)", border: "none", borderRadius: 10, color: "#fff", fontWeight: 700, fontSize: 15, cursor: insightLoading ? "not-allowed" : "pointer" }}>
                {insightLoading ? "🔄 Analyzing 180 transactions..." : "🤖 Run AI Analysis"}
              </button>
            </div>
            {insightLoading && (
              <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                {["Parsing transaction patterns...", "Identifying spending anomalies...", "Generating natural language insights..."].map((msg, i) => (
                  <div key={i} style={{ background: "#1e1b4b", border: "1px solid #3730a3", borderRadius: 10, padding: 16, color: "#818cf8", fontSize: 14, display: "flex", alignItems: "center", gap: 10 }}>
                    <div style={{ width: 8, height: 8, borderRadius: "50%", background: "#6366f1", animation: "pulse 1s infinite" }} />
                    {msg}
                  </div>
                ))}
              </div>
            )}
            {insightsVisible && (
              <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
                {aiInsights.map((ins, i) => (
                  <div key={i} style={{ background: "linear-gradient(135deg,#1e1b4b,#1a1a2e)", border: "1px solid #4f46e5", borderRadius: 14, padding: 22, borderLeft: "4px solid #6366f1" }}>
                    <div style={{ fontSize: 16, fontWeight: 700, color: "#c7d2fe", marginBottom: 8 }}>{ins.icon} {ins.title}</div>
                    <div style={{ color: "#94a3b8", lineHeight: 1.7, fontSize: 14 }}>{ins.body}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TRANSACTIONS TAB */}
        {tab === "transactions" && (
          <div>
            <div style={{ display: "flex", gap: 12, marginBottom: 20 }}>
              <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="🔍 Search merchants..." style={{ flex: 1, padding: "10px 16px", background: "#1e1b4b", border: "1px solid #3730a3", borderRadius: 10, color: "#e2e8f0", fontSize: 14, outline: "none" }} />
              <select value={filterCat} onChange={(e) => setFilterCat(e.target.value)} style={{ padding: "10px 16px", background: "#1e1b4b", border: "1px solid #3730a3", borderRadius: 10, color: "#e2e8f0", fontSize: 14 }}>
                {categories.map((c) => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div style={{ background: "#1e1b4b", borderRadius: 14, border: "1px solid #3730a3", overflow: "hidden" }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr 1fr 1fr 1fr", padding: "12px 20px", background: "#1a1a2e", fontSize: 12, color: "#6366f1", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.05em" }}>
                <span>Date</span><span>Merchant</span><span>Category</span><span>Amount</span><span>Status</span>
              </div>
              <div style={{ maxHeight: 480, overflowY: "auto" }}>
                {filteredTxns.slice(0, 50).map((t) => (
                  <div key={t.id} style={{ display: "grid", gridTemplateColumns: "1fr 2fr 1fr 1fr 1fr", padding: "12px 20px", borderTop: "1px solid #1a1a2e", fontSize: 13, alignItems: "center" }}>
                    <span style={{ color: "#64748b" }}>{t.date}</span>
                    <span style={{ color: "#e2e8f0", fontWeight: 500 }}>{t.merchant}</span>
                    <span style={{ color: "#a5b4fc", fontSize: 12 }}>{t.category}</span>
                    <span style={{ color: "#10b981", fontWeight: 600 }}>${t.amount.toFixed(2)}</span>
                    <span style={{ fontSize: 11, padding: "2px 8px", borderRadius: 6, background: t.status === "Completed" ? "#064e3b" : "#78350f", color: t.status === "Completed" ? "#6ee7b7" : "#fcd34d", display: "inline-block" }}>{t.status}</span>
                  </div>
                ))}
              </div>
              <div style={{ padding: "12px 20px", color: "#64748b", fontSize: 12, borderTop: "1px solid #1a1a2e" }}>
                Showing {Math.min(50, filteredTxns.length)} of {filteredTxns.length} transactions
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
