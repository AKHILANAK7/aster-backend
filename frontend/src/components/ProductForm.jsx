import React, { useState } from 'react';
import { createProduct } from '../api';

const ProductForm = ({ onProductCreated }) => {
    const [formData, setFormData] = useState({
        name: '',
        price: '',
        stock: '',
        description: ''
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            await createProduct({
                ...formData,
                price: parseFloat(formData.price),
                stock: parseInt(formData.stock)
            }, token);
            setFormData({ name: '', price: '', stock: '', description: '' });
            if (onProductCreated) onProductCreated();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="glass-panel" style={{ padding: '2rem', maxWidth: '500px', margin: '0 auto' }}>
            <h2>Add New Product</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <input
                type="text"
                name="name"
                placeholder="Product Name"
                value={formData.name}
                onChange={handleChange}
                className="input-field"
                required
            />
            <input
                type="number"
                name="price"
                placeholder="Price"
                value={formData.price}
                onChange={handleChange}
                className="input-field"
                required
            />
            <input
                type="number"
                name="stock"
                placeholder="Stock"
                value={formData.stock}
                onChange={handleChange}
                className="input-field"
                required
            />
            <textarea
                name="description"
                placeholder="Description"
                value={formData.description}
                onChange={handleChange}
                className="input-field"
                rows="4"
            />
            <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Create Product</button>
        </form>
    );
};

export default ProductForm;
