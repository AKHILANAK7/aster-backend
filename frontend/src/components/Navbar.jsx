import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { setAuthToken } from "../api";

export default function Navbar(){
  const nav = useNavigate();
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setAuthToken(null);
    window.location.href = "/login";
  };
  return (
    <header className="navbar">
      <div className="container nav" style={{justifyContent:"space-between"}}>
        <div className="brand"><Link to="/">Aster Computers</Link></div>
        <nav className="row" style={{gap:12}}>
          <Link to="/">Products</Link>
          <Link to="/cart">Cart</Link>
          {token ? (
            <>
              {user?.role === "admin" && <Link to="/admin">Admin</Link>}
              <button className="btn btn-outline" onClick={logout}>Logout</button>
            </>
          ) : (
            <Link to="/login">Login</Link>
          )}
        </nav>
      </div>
    </header>
  );
}