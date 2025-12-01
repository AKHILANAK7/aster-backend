import React from 'react';
import { useCart } from '../context/CartContext';

const Cart = () => {
    const { cart, removeFromCart, clearCart } = useCart();

    const total = cart.reduce((acc, item) => acc + item.price * item.quantity, 0);

    if (cart.length === 0) {
        return (
            <div className="container" style={{ textAlign: 'center', marginTop: '4rem' }}>
                <h2>Your cart is empty</h2>
            </div>
        );
    }

    return (
        <div className="container">
            <h1 style={{ marginBottom: '2rem' }}>Your Cart</h1>
            <div className="glass-panel" style={{ padding: '2rem' }}>
                {cart.map((item) => (
                    <div key={item._id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem 0', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                        <div>
                            <h3>{item.name}</h3>
                            <p style={{ color: '#aaa' }}>Quantity: {item.quantity}</p>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
                            <span style={{ fontSize: '1.2rem' }}>₹{item.price * item.quantity}</span>
                            <button onClick={() => removeFromCart(item._id)} style={{ color: '#ff7675', background: 'none' }}>Remove</button>
                        </div>
                    </div>
                ))}
                <div style={{ marginTop: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h2>Total: ₹{total}</h2>
                    <div style={{ display: 'flex', gap: '1rem' }}>
                        <button onClick={clearCart} className="btn" style={{ background: 'rgba(255,255,255,0.1)', color: 'white' }}>Clear Cart</button>
                        <button className="btn btn-primary">Checkout</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Cart;
