import React, {useEffect, useState} from "react";
import ProductForm from "../components/ProductForm";
import api from "../api";

export default function Admin(){
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await api.get("/api/products");
      setProducts(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error("Failed to load products:", err);
      setError("Failed to load products. Please try again later.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (productId) => {
    if (!window.confirm('Are you sure you want to delete this product?')) {
      return;
    }
    
    try {
      await api.delete(`/api/products/${productId}`);
      // Only remove the deleted product from state instead of reloading all products
      setProducts(products.filter(p => p._id !== productId));
    } catch (err) {
      console.error("Failed to delete product:", err);
      alert(`Failed to delete product: ${err.response?.data?.error || err.message}`);
    }
  };

  useEffect(() => { 
    load(); 
  }, []);

  return (
    <div className="stack">
      <h2>Admin Dashboard</h2>
      <ProductForm onCreated={load} />
      
      <h3 className="mt-4">Existing products</h3>
      
      {isLoading ? (
        <div className="loading">Loading products...</div>
      ) : error ? (
        <div className="error-message">
          {error}
          <button onClick={load} className="btn btn-outline" style={{ marginLeft: '1rem' }}>
            Retry
          </button>
        </div>
      ) : products.length === 0 ? (
        <div className="empty-state">No products found. Add your first product above.</div>
      ) : (
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
                <span className="muted">ID: {p._id?.$oid ?? p._id}</span>
                <button 
                  className="btn btn-danger" 
                  onClick={() => handleDelete(p._id)}
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}