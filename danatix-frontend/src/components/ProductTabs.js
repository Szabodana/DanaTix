import React from 'react';
import styles from '../pages/Shop.module.css';

const ProductTabs = ({ products, handleAddToCart }) => {

    return (
        <table className={styles.ticketTable}>
            <tbody>
            {products.map((product, index) => (
                <tr key={index}>
                    <td>
                        <strong>{product.name}</strong>
                        <br />
                        {product.description}
                    </td>
                    <td>
                        <div className={styles.price}>{product.price}</div>
                        <div className={styles.duration}>{product.duration}</div>
                    </td>
                    <td>
                        <button onClick={() => handleAddToCart(product)} className={styles.addToCartButton}>
                            Add to Cart
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};
export default ProductTabs;