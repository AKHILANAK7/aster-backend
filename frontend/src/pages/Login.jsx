import React, {useState} from "react";
import api, { setAuthToken } from "../api";
import { useNavigate } from "react-router-dom";

export default function Login(){
  const [email,setEmail] = useState("");
  const [pw,setPw] = useState("");
  const nav = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post("/api/auth/login", { email, password: pw });
      const token = res.data.token;
      localStorage.setItem("token", token);
      setAuthToken(token);
      alert("Logged in");
      nav("/admin");
    } catch (err) {
      console.error(err);
      alert("Login failed: " + (err.response?.data || err.message));
    }
  };

  return (
    <div className="auth-card card">
      <h2>Sign in</h2>
      <p className="muted">Access your account to manage products and cart.</p>
      <form onSubmit={submit} className="stack">
        <div className="form-row">
          <input type="email" placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} required/>
        </div>
        <div className="form-row">
          <input placeholder="Password" type="password" value={pw} onChange={e=>setPw(e.target.value)} required/>
        </div>
        <div className="row sp-between">
          <span className="muted"></span>
          <button className="btn btn-primary" type="submit">Login</button>
        </div>
      </form>
    </div>
  );
}