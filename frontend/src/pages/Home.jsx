import React from 'react';
import ProductList from '../components/ProductList';

const Home = () => {
    return (
        <div className="container">
            <h1 style={{ fontSize: '3rem', marginBottom: '2rem', background: 'linear-gradient(to right, #fff, #aaa)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                Discover Premium Gear
            </h1>
            <ProductList />
        </div>
    );
};

export default Home;
