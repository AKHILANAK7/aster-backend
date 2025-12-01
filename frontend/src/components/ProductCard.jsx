import React from "react";

export default function ProductCard({p, onAdd}) {
  const inStock = (p?.stock ?? 0) > 0;
  const price = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(p?.price || 0);

  return (
    <div className="card product-card">
      <div className="product-thumb" aria-hidden="true" />
      <div style={{flex:1}}>
        <div className="row sp-between">
          <h3>{p.name}</h3>
          <span className={`badge ${inStock ? 'badge-green' : 'badge-red'}`}>{inStock ? 'In stock' : 'Out of stock'}</span>
        </div>
        <div className="price">{price}</div>
        <p className="muted">{p.description}</p>
      </div>
      <div className="stack">
        <button className="btn btn-primary" disabled={!inStock} onClick={()=>onAdd(p)}>
          {inStock ? 'Add to cart' : 'Unavailable'}
        </button>
      </div>
    </div>
  );
}