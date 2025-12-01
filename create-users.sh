#!/bin/bash

echo "🔐 Creating Admin and User Accounts"
echo "===================================="
echo ""

# Create Admin User
echo "1️⃣  Creating Admin User..."
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@aster.com","password":"admin123","role":"admin"}')

echo "Response: $ADMIN_RESPONSE"
echo ""

# Create Regular User
echo "2️⃣  Creating Regular User..."
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@aster.com","password":"user123","role":"customer"}')

echo "Response: $USER_RESPONSE"
echo ""

# Test Admin Login
echo "3️⃣  Testing Admin Login..."
ADMIN_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@aster.com","password":"admin123"}')

echo "Response: $ADMIN_LOGIN"
ADMIN_TOKEN=$(echo $ADMIN_LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Admin Token: ${ADMIN_TOKEN:0:30}..."
echo ""

# Test User Login
echo "4️⃣  Testing User Login..."
USER_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@aster.com","password":"user123"}')

echo "Response: $USER_LOGIN"
USER_TOKEN=$(echo $USER_LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "User Token: ${USER_TOKEN:0:30}..."
echo ""

echo "===================================="
echo "✅ Account Creation Complete!"
echo ""
echo "📝 Credentials:"
echo "   Admin: email=admin@aster.com, password=admin123"
echo "   User:  email=user@aster.com, password=user123"
echo ""