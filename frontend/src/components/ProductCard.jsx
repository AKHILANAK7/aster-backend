import React from "react";

export default function ProductCard({p, onAdd, view = "grid"}) {
  const inStock = (p?.stock ?? 0) > 0;
  const price = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(p?.price || 0);

  return (
    <div className={`card product-card ${view}`}>
      <div style={{flex:1}} className="stack">
        <div className="row sp-between">
          <h3>{p.name}</h3>
          <span className={`badge ${inStock ? 'badge-green' : 'badge-red'}`}>{inStock ? 'In stock' : 'Out of stock'}</span>
        </div>
        <div className="price">{price}</div>
        {p?.description && <p className="muted">{p.description}</p>}
        <div className="row" style={{marginTop:'auto', justifyContent:'flex-end'}}>
          <button className="btn btn-primary" disabled={!inStock} onClick={()=>onAdd(p)}>
            {inStock ? 'Add to cart' : 'Unavailable'}
          </button>
        </div>
      </div>
    </div>
  );
}