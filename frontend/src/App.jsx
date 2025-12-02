import React, { useEffect } from "react";
import { Routes, Route, useNavigate } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Admin from "./pages/Admin";
import Cart from "./pages/Cart";
import Navbar from "./components/Navbar";
import { setAuthToken } from "./api";
import ProtectedRoute from "./components/ProtectedRoute";

export default function App() {
  const nav = useNavigate();
  useEffect(()=>{
    const token = localStorage.getItem("token");
    if (token) setAuthToken(token);
  },[]);

  return (
    <div>
      <Navbar />
      <div className="container">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRole="admin">
                <Admin />
              </ProtectedRoute>
            }
          />
          <Route path="/cart" element={<Cart />} />
        </Routes>
      </div>
    </div>
  );
}