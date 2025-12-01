import React, {useContext} from "react";
import { CartContext } from "../context/CartContext";

export default function Cart(){
  const { items, clearCart } = useContext(CartContext);
  const total = items.reduce((s, it) => s + (it.price||0), 0);

  return (
    <div className="stack">
      <h2>Your Cart</h2>
      {items.length === 0 ? (
        <div className="card">
          <p className="muted">Your cart is empty.</p>
        </div>
      ) : (
        <>
          <div className="stack">
            {items.map((i, idx) => (
              <div key={idx} className="card">
                <div className="card-header">
                  <div style={{fontWeight:600}}>{i.name}</div>
                  <div className="price">₹{i.price}</div>
                </div>
                {i.description && <p className="muted">{i.description}</p>}
              </div>
            ))}
          </div>
          <div className="card">
            <div className="row sp-between">
              <div><strong>Total</strong>: <span className="price">₹{total}</span></div>
              <div className="row" style={{gap:8}}>
                <button className="btn btn-outline" onClick={clearCart}>Clear</button>
                <button className="btn btn-primary" onClick={()=>alert("Checkout not implemented")}>Checkout</button>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}