import React, {useEffect, useState, useContext} from "react";
import api from "../api";
import ProductCard from "./ProductCard";
import { CartContext } from "../context/CartContext";

export default function ProductList(){
  const [products, setProducts] = useState([]);
  const { addToCart } = useContext(CartContext);

  useEffect(()=>{
    api.get("/api/products")
      .then(res => setProducts(res.data))
      .catch(err => console.error(err));
  },[]);

  return (
    <div className="stack">
      <h2>Products</h2>
      {products.length===0 && <div className="muted">No products yet</div>}
      <div className="grid grid-3">
        {products.map(p => <ProductCard key={p._id} p={p} onAdd={addToCart} />)}
      </div>
    </div>
  );
}