# 💳 TransactIQ — AI-Powered Transaction Insights Dashboard

> Turn raw bank transactions into natural-language spending intelligence — powered by Claude AI, Spring Boot, and React.

![TransactIQ Dashboard](https://placehold.co/1200x600/0f0f1a/6366f1?text=TransactIQ+Dashboard+Screenshot)

---

## ✨ Features

- **📊 Interactive Dashboard** — Weekly spend trends, category pie chart, bar comparisons with live filtering
- **🤖 AI Insights Engine** — Claude API generates 5 personalized, actionable spending insights from your transactions
- **📋 Transaction Explorer** — Search, filter, and paginate 180+ transactions in real time
- **📁 CSV Upload** — Import any bank statement; auto-classifies merchants into categories
- **🏷️ Smart Categorization** — Rule-based `CategoryClassifier` maps merchants → categories (Food, Shopping, Travel, etc.)
- **🐳 Dockerized** — One command (`docker-compose up`) to run the full stack locally
- **🔒 Secure** — `.env`-driven secrets, `.gitignore`-protected credentials

---

## 🛠️ Tech Stack

| Layer      | Technology                          |
|------------|--------------------------------------|
| Frontend   | React 18, Vite, Recharts             |
| Backend    | Spring Boot 3, Spring Data JPA       |
| Database   | PostgreSQL 15                        |
| AI         | Anthropic Claude API (`claude-opus-4-6`) |
| DevOps     | Docker, Docker Compose, Nginx        |
| Security   | Spring Security, JWT                 |

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose installed
- An [Anthropic API key](https://console.anthropic.com/)

### 1. Clone & configure

```bash
git clone https://github.com/YOUR_USERNAME/transactiq.git
cd transactiq

# Copy and fill in your API key
cp .env.example .env
nano .env   # Set ANTHROPIC_API_KEY
```

### 2. Run the full stack

```bash
docker-compose up --build
```

| Service   | URL                      |
|-----------|--------------------------|
| Frontend  | http://localhost:3000    |
| Backend   | http://localhost:8080    |
| Database  | localhost:5432           |

### 3. Run locally (without Docker)

**Backend:**
```bash
cd backend
# Set env vars first (see .env.example)
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev   # Starts on http://localhost:3000
```

---

## 📡 API Reference

| Method | Endpoint                          | Description                        |
|--------|-----------------------------------|------------------------------------|
| GET    | `/api/transactions`               | List all transactions (filterable) |
| POST   | `/api/transactions`               | Create a transaction               |
| POST   | `/api/transactions/upload`        | Upload CSV bank statement          |
| GET    | `/api/transactions/summary`       | Category spend summary             |
| DELETE | `/api/transactions/{id}`          | Delete a transaction               |
| POST   | `/api/transactions/analyze`       | Trigger Claude AI analysis         |

**Example CSV format:**
```
date,merchant,amount,category
2024-03-15,Starbucks,5.75,Food & Dining
2024-03-14,Amazon,34.99,Shopping
2024-03-13,Uber,12.50,Transport
```

---

## 🧠 How AI Insights Work

1. User clicks **"Run AI Analysis"** in the dashboard
2. `InsightService` fetches user's transactions from PostgreSQL
3. Builds a structured text summary (category totals + top transactions)
4. Sends to Claude API with a tailored prompt
5. Returns 5 natural-language insights rendered in the UI

```java
// InsightService.java (simplified)
String prompt = """
    Analyze these transactions and provide 5 actionable insights.
    Focus on: spending spikes, patterns, savings opportunities...
    """ + transactionSummary;

return callClaudeApi(prompt); // → "📈 Spending Spike: Your Week 4..."
```

---

## 📁 Project Structure

```
transactiq/
├── frontend/                  # React + Vite app
│   ├── src/
│   │   ├── App.jsx            # Main dashboard component
│   │   └── main.jsx           # React entry point
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── backend/                   # Spring Boot app
│   └── src/main/java/com/transactiq/
│       ├── TransactIqApplication.java
│       ├── controller/
│       │   └── TransactionController.java
│       ├── service/
│       │   ├── InsightService.java       ← Claude AI integration
│       │   ├── TransactionService.java
│       │   └── CategoryClassifier.java
│       ├── model/
│       │   └── Transaction.java
│       └── repository/
│           └── TransactionRepository.java
├── docker-compose.yml
├── .env.example
└── .gitignore
```

---

## 🌐 Deployment (AWS Free Tier)

```bash
# Build and push to Docker Hub
docker build -t yourusername/transactiq-backend ./backend
docker build -t yourusername/transactiq-frontend ./frontend
docker push yourusername/transactiq-backend
docker push yourusername/transactiq-frontend

# On EC2 (t2.micro)
docker-compose pull && docker-compose up -d
```

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first.

1. Fork the repo
2. Create your branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m 'Add some feature'`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) file.

---

## 👤 Author

Built with ❤️ and fintech domain expertise from experience at **Mastercard**.

> *"The best finance tools don't just show you data — they tell you what it means."*

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin)](https://linkedin.com/in/YOUR_PROFILE)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black?logo=github)](https://github.com/YOUR_USERNAME)
