# Admin Features Guide

## What Admin Users Should See

When an admin logs in, they should see:

### 1. **Admin Dashboard** (`/admin`)
   - Protected route that only admins can access
   - Redirects non-admins to home page with "Access Denied" message

### 2. **Add New Product Form**
   - Form to create new products with:
     - Product Name
     - Price
     - Stock Quantity
     - Description
   - "Create Product" button
   - Only admins can submit this (backend enforces via `RoleHandler.requireAdmin()`)

### 3. **Existing Products List**
   - Grid view of all products in the system
   - For each product:
     - Product Name
     - Price (₹)
     - Stock Level
     - Product ID
     - **Delete Button** (red button to remove product)

### 4. **Navigation Bar Updates**
   - "Admin" link appears in navbar (only for admin users)
   - "Logout" button clears both token and user data

## How Admin Login Works

### Step 1: Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@aster.com","password":"admin123","role":"admin"}'
```

### Step 2: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@aster.com","password":"admin123"}'
```

**Response includes:**
```json
{
  "token": "eyJhbGc...",
  "user": {
    "email": "admin@aster.com",
    "role": "admin"
  }
}
```

### Step 3: Frontend Storage
- Token stored in `localStorage.token`
- User object stored in `localStorage.user` as JSON string
- Frontend checks `user.role === "admin"` to show admin features

### Step 4: Route Protection
- `/admin` route wrapped with `ProtectedRoute` requiring `role="admin"`
- Non-admins see "Access Denied" page

## Backend Admin Endpoints

### Create Product (Admin Only)
```
POST /api/products
Authorization: Bearer {token}
Body: { name, price, stock, description }
```
- Requires valid JWT token
- Requires `role: "admin"` in token
- Returns 403 Forbidden if user is not admin

### Get All Products (Public)
```
GET /api/products
```
- No authentication required
- Returns array of all products

### Delete Product (Admin Only)
```
DELETE /api/products/{id}
Authorization: Bearer {token}
```
- Requires valid JWT token
- Requires `role: "admin"` in token

## Troubleshooting

### Admin doesn't see admin features:
1. Check `localStorage.user` in browser DevTools → Application → Local Storage
2. Verify `role` field is set to `"admin"`
3. Check browser console for errors
4. Verify token is valid: decode JWT at jwt.io

### Product creation fails:
1. Check Authorization header is sent with Bearer token
2. Verify token includes `role: "admin"`
3. Check server logs for validation errors
4. Ensure all required fields (name, price, stock) are provided

### Can't access /admin page:
1. Verify user is logged in (token exists in localStorage)
2. Verify `user.role === "admin"` in localStorage
3. Check ProtectedRoute component is working
4. Clear browser cache and reload
