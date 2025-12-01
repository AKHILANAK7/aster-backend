# Aster Computers - E-Commerce Platform

A full-stack e-commerce application for computer hardware built with **Kotlin + Vert.x** backend and **React** frontend.

## 🏗️ Architecture

```
Frontend (React) ←→ Backend (Vert.x) ←→ MongoDB
Port 5173           Port 8080           Port 27017
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MongoDB running on `localhost:27017`

### 1. Start Backend
```bash
./gradlew run
```
✅ Backend running on `http://localhost:8080`

### 2. Start Frontend
```bash
cd frontend
npm install  # First time only
npm run dev
```
✅ Frontend running on `http://localhost:5173`

### 3. Test Integration
```bash
./test-integration.sh
```

## 📁 Project Structure

```
aster-backend/
├── src/main/kotlin/com/aster/
│   ├── MainVerticle.kt          # Application entry point
│   ├── auth/
│   │   └── JwtUtils.kt          # JWT token generation
│   ├── db/
│   │   ├── MongoClientProvider.kt  # MongoDB connection
│   │   └── Collections.kt       # Collection names
│   ├── models/
│   │   ├── User.kt              # User data model
│   │   └── Product.kt           # Product data model
│   └── routes/
│       ├── AuthRoutes.kt        # /api/auth/* endpoints
│       └── ProductRoutes.kt     # /api/products/* endpoints
├── frontend/
│   ├── src/
│   │   ├── api.js               # Backend API client
│   │   ├── components/          # React components
│   │   ├── pages/               # Page components
│   │   └── context/             # React context (cart)
│   └── package.json
└── build.gradle.kts
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Products
- `GET /api/products` - Get all products
- `POST /api/products` - Create product (requires auth)
- `GET /api/products/:id` - Get single product

### Health
- `GET /api/health` - Health check

## 💾 Database Schema

### Collection: `users`
```json
{
  "_id": "ObjectId",
  "username": "string",
  "password": "string (bcrypt)",
  "role": "customer | admin"
}
```

### Collection: `products`
```json
{
  "_id": "ObjectId",
  "name": "string",
  "price": "number",
  "stock": "number",
  "description": "string"
}
```

## 🔐 Authentication Flow

1. User registers/logs in via frontend
2. Backend validates credentials and generates JWT token
3. Frontend stores token in localStorage
4. Protected requests include token in Authorization header

## 🛠️ Development

### Backend
```bash
./gradlew build          # Build project
./gradlew run            # Run server
./gradlew test           # Run tests
```

### Frontend
```bash
cd frontend
npm run dev              # Development server
npm run build            # Production build
npm run preview          # Preview production build
```

## 🌐 Environment Variables

### Backend (Optional)
```bash
export MONGODB_URI="mongodb://localhost:27017/aster"
export PORT=8080
```

### Frontend
Update `API_URL` in `frontend/src/api.js` for production deployment.

## 📦 Tech Stack

### Backend
- **Kotlin** - Programming language
- **Vert.x** - Reactive web framework
- **MongoDB** - Database
- **JWT** - Authentication
- **BCrypt** - Password hashing

### Frontend
- **React** - UI library
- **React Router** - Routing
- **Vite** - Build tool
- **Context API** - State management

## ✅ Integration Verified

All layers are connected and tested:
- ✅ Frontend → Backend API calls
- ✅ Backend → MongoDB operations
- ✅ CORS enabled for cross-origin requests
- ✅ Authentication flow complete
- ✅ Product CRUD operations working

## 📚 Documentation

- [Integration Guide](file:///Users/balajim/.gemini/antigravity/brain/009e3c8d-2c1c-4780-a02a-a761cef806b6/integration_guide.md) - Complete integration details
- [Walkthrough](file:///Users/balajim/.gemini/antigravity/brain/009e3c8d-2c1c-4780-a02a-a761cef806b6/walkthrough.md) - Implementation walkthrough

## 🧪 Testing

Run the integration test script:
```bash
./test-integration.sh
```

This will test:
1. Backend health
2. User registration
3. User login
4. Product creation
5. Product retrieval

## 📝 License

MIT
