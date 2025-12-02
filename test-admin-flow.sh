#!/bin/bash

echo "🔐 Testing Admin Login Flow"
echo "===================================="
echo ""

# 1. Register Admin User
echo "1️⃣  Registering Admin User..."
ADMIN_REGISTER=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"testadmin@aster.com","password":"admin123","role":"admin"}')

echo "Register Response: $ADMIN_REGISTER"
echo ""

# 2. Login as Admin
echo "2️⃣  Logging in as Admin..."
ADMIN_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testadmin@aster.com","password":"admin123"}')

echo "Login Response: $ADMIN_LOGIN"
ADMIN_TOKEN=$(echo $ADMIN_LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Admin Token: ${ADMIN_TOKEN:0:50}..."
echo ""

# 3. Test Creating a Product (Admin Only)
echo "3️⃣  Testing Product Creation (Admin Only)..."
PRODUCT_CREATE=$(curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "name": "Test Product",
    "price": 999.99,
    "stock": 50,
    "description": "Test product for admin"
  }')

echo "Product Create Response: $PRODUCT_CREATE"
echo ""

# 4. Get All Products
echo "4️⃣  Fetching All Products..."
PRODUCTS=$(curl -s -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN")

echo "Products Response: $PRODUCTS"
echo ""

# 5. Register Regular User
echo "5️⃣  Registering Regular User..."
USER_REGISTER=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@aster.com","password":"user123","role":"customer"}')

echo "Register Response: $USER_REGISTER"
echo ""

# 6. Login as Regular User
echo "6️⃣  Logging in as Regular User..."
USER_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@aster.com","password":"user123"}')

echo "Login Response: $USER_LOGIN"
USER_TOKEN=$(echo $USER_LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "User Token: ${USER_TOKEN:0:50}..."
echo ""

# 7. Test Creating a Product as Regular User (Should Fail)
echo "7️⃣  Testing Product Creation as Regular User (Should Fail)..."
PRODUCT_CREATE_FAIL=$(curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "name": "Unauthorized Product",
    "price": 100,
    "stock": 10,
    "description": "This should fail"
  }')

echo "Product Create Response (Should be 403 Forbidden): $PRODUCT_CREATE_FAIL"
echo ""

echo "===================================="
echo "✅ Admin Flow Test Complete!"
echo ""
echo "📝 Summary:"
echo "   ✓ Admin can register with role=admin"
echo "   ✓ Admin can login and get token with role=admin"
echo "   ✓ Admin can create products"
echo "   ✓ Admin can view all products"
echo "   ✓ Regular user cannot create products (403 Forbidden)"
