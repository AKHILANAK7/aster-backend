import React, {useState} from "react";
import api, { setAuthToken } from "../api";

export default function ProductForm({onCreated}) {
  const [name,setName]=useState("");
  const [price,setPrice]=useState("");
  const [stock,setStock]=useState("");
  const [desc,setDesc]=useState("");

  const submit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem("token");
      if (token) api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      const body = { name, price: parseFloat(price), stock: parseInt(stock), description: desc };
      const res = await api.post("/api/products", body);
      onCreated && onCreated(res.data);
      alert("Product created");
      setName(""); setPrice(""); setStock(""); setDesc("");
    } catch (err) {
      console.error(err);
      alert("Create failed: " + (err.response?.data || err.message));
    }
  };

  return (
    <form onSubmit={submit} className="card">
      <div className="form-row"><label>Name</label><input value={name} onChange={e=>setName(e.target.value)} required /></div>
      <div className="form-row"><label>Price</label><input value={price} onChange={e=>setPrice(e.target.value)} required /></div>
      <div className="form-row"><label>Stock</label><input value={stock} onChange={e=>setStock(e.target.value)} required /></div>
      <div className="form-row"><label>Description</label><textarea value={desc} onChange={e=>setDesc(e.target.value)} /></div>
      <button className="btn btn-primary" type="submit">Create Product</button>
    </form>
  );
}