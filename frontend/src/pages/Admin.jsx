import React from 'react';
import ProductForm from '../components/ProductForm';

const Admin = () => {
    return (
        <div className="container">
            <h1 style={{ textAlign: 'center', marginBottom: '2rem' }}>Admin Dashboard</h1>
            <ProductForm onProductCreated={() => alert('Product Created!')} />
        </div>
    );
};

export default Admin;
