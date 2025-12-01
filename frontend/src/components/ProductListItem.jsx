// src/components/ProductListItem.jsx
import React from "react";

export default function ProductListItem({ p, onAdd }) {
  return (
    <div className="card list-item">
      <div style={{display:"flex", gap:12, alignItems:"center"}}>
        <div style={{width:120, height:80, overflow:"hidden", borderRadius:8}}>
          <img src={p.image || "/placeholder.png"} alt={p.name} style={{width:"100%", height:"100%", objectFit:"cover"}}/>
        </div>
        <div style={{flex:1}}>
          <h3 style={{margin:0}}>{p.name}</h3>
          <div style={{color:"#666"}}>{p.description}</div>
        </div>
        <div style={{width:160, textAlign:"right"}}>
          <div style={{fontWeight:700}}>₹{p.price}</div>
          <div style={{color:"#666", fontSize:13}}>Stock: {p.stock}</div>
          <div style={{marginTop:8}}>
            <button className="btn btn-primary" onClick={()=> onAdd?.(p)}>Add to cart</button>
          </div>
        </div>
      </div>
    </div>
  );
}