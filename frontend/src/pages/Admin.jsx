import React, {useEffect, useState} from "react";
import ProductForm from "../components/ProductForm";
import api from "../api";

export default function Admin(){
  const [products,setProducts] = useState([]);

  const load = async () => {
    const res = await api.get("/api/products");
    setProducts(res.data);
  };

  useEffect(()=>{ load(); }, []);

  return (
    <div className="stack">
      <h2>Admin Dashboard</h2>
      <ProductForm onCreated={()=>load()} />
      <h3 className="mt-4">Existing products</h3>
      <div className="grid grid-2">
        {products.map(p => (
          <div key={p._id} className="card">
            <div className="card-header">
              <div>
                <div style={{fontWeight:600}}>{p.name}</div>
                <div className="muted">Stock: {p.stock}</div>
              </div>
              <div className="price">₹{p.price}</div>
            </div>
            <div className="row sp-between">
              <span className="muted">ID: {p._id}</span>
              <button className="btn btn-danger" onClick={async ()=>{ await api.delete(`/api/products/${p._id}`); load(); }}>Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}