// src/components/ProductGrid.jsx
import React from "react";

export default function ProductGrid({ products, onAdd }) {
  return (
    <div className="grid">
      {products.map(p => (
        <div key={p._id} className="card grid-card">
          <div className="img-wrap">
            <img src={p.image || `/placeholder.png`} alt={p.name} />
          </div>
          <div>
            <h3 style={{margin:"6px 0"}}>{p.name}</h3>
            <div>₹{p.price}</div>
            <div style={{color:"#666", fontSize:13}}>Stock: {p.stock}</div>
            <div style={{marginTop:8}}>
              <button className="btn btn-primary" onClick={()=> onAdd?.(p)}>Add to cart</button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}