import React, {createContext, useState} from "react";

export const CartContext = createContext();

export function CartProvider({children}) {
  const [items, setItems] = useState(() => {
    const raw = localStorage.getItem("cart");
    return raw ? JSON.parse(raw) : [];
  });

  const addToCart = (product) => {
    const next = [...items, product];
    setItems(next);
    localStorage.setItem("cart", JSON.stringify(next));
  };

  const clearCart = () => {
    setItems([]);
    localStorage.removeItem("cart");
  };

  return <CartContext.Provider value={{ items, addToCart, clearCart }}>
    {children}
  </CartContext.Provider>
}