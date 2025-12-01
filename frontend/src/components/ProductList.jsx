import React, {useEffect, useState, useContext} from "react";
import api from "../api";
import ProductCard from "./ProductCard";
import { CartContext } from "../context/CartContext";

export default function ProductList(){
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [q, setQ] = useState("");
  const [sort, setSort] = useState("name_asc");
  const [view, setView] = useState("grid"); // grid | list
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(9);
  const { addToCart } = useContext(CartContext);

  useEffect(()=>{
    setLoading(true);
    api.get("/api/products")
      .then(res => setProducts(res.data))
      .catch(err => setError(err.message || "Failed to load products"))
      .finally(()=> setLoading(false));
  },[]);

  // Derived data: filter, sort, paginate
  const filtered = products.filter(p => {
    const t = (p.name||"") + " " + (p.description||"");
    return t.toLowerCase().includes(q.toLowerCase());
  }).sort((a,b)=>{
    if (sort === "name_asc") return (a.name||"").localeCompare(b.name||"");
    if (sort === "name_desc") return (b.name||"").localeCompare(a.name||"");
    if (sort === "price_asc") return (a.price||0) - (b.price||0);
    if (sort === "price_desc") return (b.price||0) - (a.price||0);
    return 0;
  });

  const total = filtered.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const currentPage = Math.min(Math.max(1, page), totalPages);
  const start = (currentPage - 1) * pageSize;
  const end = Math.min(start + pageSize, total);
  const pageItems = filtered.slice(start, end);

  const gotoPage = (p) => setPage(Math.min(Math.max(1, p), totalPages));

  return (
    <div className="stack">
      <div className="toolbar">
        <h2>Products</h2>
        <div className="fields">
          <input placeholder="Search products" value={q} onChange={e=>{ setQ(e.target.value); setPage(1); }} />
          <select value={sort} onChange={e=>setSort(e.target.value)}>
            <option value="name_asc">Name A–Z</option>
            <option value="name_desc">Name Z–A</option>
            <option value="price_asc">Price Low→High</option>
            <option value="price_desc">Price High→Low</option>
          </select>
          <select value={pageSize} onChange={e=>{ setPageSize(parseInt(e.target.value)); setPage(1); }}>
            <option value={6}>6 / page</option>
            <option value={9}>9 / page</option>
            <option value={12}>12 / page</option>
            <option value={24}>24 / page</option>
          </select>
          <div className="row" style={{marginLeft:"auto", gap:8}}>
            <button className="btn btn-outline" onClick={()=>setView("grid")} disabled={view==="grid"}>Grid</button>
            <button className="btn btn-outline" onClick={()=>setView("list")} disabled={view==="list"}>List</button>
          </div>
        </div>
      </div>

      {error && <div className="card"><p className="muted">{error}</p></div>}

      {loading ? (
        <div className="grid grid-3">
          {Array.from({length: 6}).map((_,i)=> (
            <div key={i} className="card skeleton" style={{height:120}} />
          ))}
        </div>
      ) : (
        <>
          {total===0 && <div className="muted">No products match your search.</div>}
          {total>0 && (
            view === "grid" ? (
              <div className="grid grid-3">
                {pageItems.map(p => <ProductCard key={p._id} p={p} onAdd={addToCart} view="grid" />)}
              </div>
            ) : (
              <div className="stack">
                {pageItems.map(p => <ProductCard key={p._id} p={p} onAdd={addToCart} view="list" />)}
              </div>
            )
          )}

          {total>0 && (
            <div className="row sp-between mt-3">
              <span className="muted">Showing {total===0 ? 0 : start+1}-{end} of {total}</span>
              <div className="row" style={{gap:8}}>
                <button className="btn btn-outline" onClick={()=>gotoPage(currentPage-1)} disabled={currentPage<=1}>Prev</button>
                <button className="btn btn-outline" onClick={()=>gotoPage(currentPage+1)} disabled={currentPage>=totalPages}>Next</button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}