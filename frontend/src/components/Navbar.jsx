import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';

const Navbar = () => {
    const { cart } = useCart();
    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/login');
    };

    return (
        <nav className="glass-panel" style={{ padding: '1rem 2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
            <Link to="/" style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white' }}>Aster Computers</Link>
            <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
                <Link to="/">Home</Link>
                {user.role === 'admin' && <Link to="/admin">Admin</Link>}
                <Link to="/cart">Cart ({cart.reduce((acc, item) => acc + item.quantity, 0)})</Link>
                {token ? (
                    <button onClick={handleLogout} className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>Logout</button>
                ) : (
                    <Link to="/login" className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>Login</Link>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
