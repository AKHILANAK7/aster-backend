// src/components/ProductView.jsx
import React, { useState, useEffect } from "react";
import api from "../api";
import ProductGrid from "./ProductGrid";
import ProductListItem from "./ProductListItem";

export default function ProductView() {
  const [products, setProducts] = useState([]);
  const [view, setView] = useState("grid"); // "grid" or "list"

  useEffect(() => {
    api.get("/api/products").then(res => setProducts(res.data)).catch(console.error);
  }, []);

  return (
    <div>
      <div style={{display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:12}}>
        <h2>Products</h2>
        <div style={{display:"flex", gap:8}}>
          <button aria-label="Grid view" className={`btn ${view==="grid"?"btn-primary":""}`} onClick={()=>setView("grid")}>Grid</button>
          <button aria-label="List view" className={`btn ${view==="list"?"btn-primary":""}`} onClick={()=>setView("list")}>List</button>
        </div>
      </div>

      {view === "grid" ? <ProductGrid products={products} /> :
        <div>
          {products.map(p => <ProductListItem key={p._id} p={p} />)}
        </div>
      }
    </div>
  );
}