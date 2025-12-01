import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    // Check if user is authenticated
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    // Check if user has required role
    if (requiredRole && user.role !== requiredRole) {
        return (
            <div className="container" style={{ textAlign: 'center', marginTop: '4rem' }}>
                <div className="glass-panel" style={{ padding: '3rem', maxWidth: '500px', margin: '0 auto' }}>
                    <h2 style={{ color: '#ff7675', marginBottom: '1rem' }}>Access Denied</h2>
                    <p>You don't have permission to access this page.</p>
                    <p style={{ color: '#aaa', marginTop: '1rem' }}>Required role: <strong>{requiredRole}</strong></p>
                    <p style={{ color: '#aaa' }}>Your role: <strong>{user.role || 'none'}</strong></p>
                    <button
                        onClick={() => window.location.href = '/'}
                        className="btn btn-primary"
                        style={{ marginTop: '2rem' }}
                    >
                        Go to Home
                    </button>
                </div>
            </div>
        );
    }

    return children;
};

export default ProtectedRoute;
