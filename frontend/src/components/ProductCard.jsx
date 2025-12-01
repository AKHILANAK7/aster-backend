import React from 'react';
import { useCart } from '../context/CartContext';

const ProductCard = ({ product }) => {
    const { addToCart } = useCart();

    return (
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ height: '200px', background: 'rgba(255,255,255,0.1)', borderRadius: '8px' }}></div>
            <h3 style={{ margin: 0 }}>{product.name}</h3>
            <p style={{ color: '#aaa', fontSize: '0.9rem' }}>{product.description}</p>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto' }}>
                <span style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'var(--accent-color)' }}>₹{product.price}</span>
                <button onClick={() => addToCart(product)} className="btn btn-primary">Add to Cart</button>
            </div>
        </div>
    );
};

export default ProductCard;
