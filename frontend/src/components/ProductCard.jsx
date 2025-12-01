import React from "react";

export default function ProductCard({p, onAdd}) {
  return (
    <div className="card product-card">
      <div>
        <h3>{p.name}</h3>
        <div className="price">₹{p.price}</div>
        <div className="muted">Stock: {p.stock}</div>
        <p>{p.description}</p>
      </div>
      <div className="stack">
        <button className="btn btn-primary" onClick={()=>onAdd(p)}>Add to cart</button>
      </div>
    </div>
  );
}