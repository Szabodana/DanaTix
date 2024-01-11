import React, {useEffect, useState} from "react";
import {useSession} from "next-auth/react";
import styles from './cart.module.css';
import Head from "next/head";
import {useCart} from "@/components/CartContext";

const Cart = () => {
    const [buyMessage, setBuyMessage] = useState('');
    const {data: session} = useSession();
    const {cart, clearCart} = useCart();

    const filterCartData = (orders) => {

        const itemsList = [];

        orders.forEach((order) => {
            const existingItem = itemsList.find((item) => item.name === order.name);

            if (existingItem) {
                existingItem.quantity ++;
            } else {

                itemsList.push({name: order.name, quantity: 1});
            }
        })

        return itemsList;
    }

    const handleBuy = async () => {

        const res = await fetch(process.env.NEXT_PUBLIC_API_URL + `/orders`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${session.user.accessToken}`
            },
        });
        setBuyMessage(res.ok ? 'Items bought successfully': 'Request failed');

        if(res.ok) {
            clearCart();
        }
    }

    return (
        <>
            <Head>
                <title>Cart</title>
            </Head>
            <div className={styles.cartWrap}>
                <div className={styles.cartTitle}>
                    <h1>My cart</h1>
                </div>
                {cart.length > 0 ? (
                    <div className={styles.listWrap}>
                        <table className={styles.itemsTable}>
                            <tbody>
                            {filterCartData(cart).map((item, index) => (
                                <tr key={index}>
                                    <td className={styles.itemName}>
                                        <p>{item.name}</p>
                                    </td>
                                    <td className={styles.itemQuantity}>
                                        <p>{item.quantity}</p>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                        <div className={styles.buttonsWrap}>
                            <button className={styles.buyButton} onClick={handleBuy}>Buy</button>
                        </div>
                    </div>
                ) : (
                    <div className={styles.buyMessage}>
                        <h2>{buyMessage}</h2>
                    </div>
                )}
            </div>
        </>

    )
}

export default Cart;