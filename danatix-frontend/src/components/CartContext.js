import React, {createContext, useContext, useEffect, useState} from 'react';
import {useSession} from "next-auth/react";

const CartContext = createContext();

export const CartProvider = ({children}) => {
    const [cart, setCart] = useState([]);
    const {data: session, status} = useSession();
    const token = session?.user?.accessToken;
    const clearCart = () => {
        setCart([]);
    }

    const addToCart = async (product) => {
        try {
            const response = await fetch(process.env.NEXT_PUBLIC_API_URL + '/orders', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({productId: product.id}),
            });

            if (response.ok) {
                const data = await response.json();
                setCart([...cart, product]);
            }
        } catch (error) {
            console.error('Error adding to cart:', error);
        }
    };

    useEffect(() => {
        const fetchCartData = async () => {
            if (token) {
                try {
                    const response = await fetch(process.env.NEXT_PUBLIC_API_URL + '/orders', {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    });

                    if (response.ok) {
                        const data = await response.json();
                        if (data.orders) {
                            setCart(data.orders);
                        } else {
                            console.error('Cart data is missing in the response:', data);
                        }
                    } else {
                        console.error('Error fetching cart data. Status:', response.status);
                    }
                } catch (error) {
                    console.error('Error fetching cart data:', error);
                }
            }
        };
        fetchCartData();
    }, [token]);


    const removeFromCart = (item) => {
        const updatedCart = cart.filter((cartItem) => cartItem.id !== item.id);
        setCart(updatedCart);
    };

    return (
        <CartContext.Provider value={{cart, addToCart, removeFromCart, clearCart}}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    return useContext(CartContext);
};