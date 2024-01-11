import React, {useState, useEffect} from 'react';
import styles from './Shop.module.css';
import ProductTabs from '../components/ProductTabs';
import { useSession } from 'next-auth/react';
import { useCart } from "@/components/CartContext";

const Shop = () => {
    const [selectedTab, setSelectedTab] = useState('Tickets');
    const [products, setProducts] = useState([]);
    const [errorMessage, setErrorMessage] = useState(null);
    const { addToCart } = useCart();


    const {data: session, status} = useSession();
    const token = session?.user?.accessToken;

    useEffect(() => {
        if (!session) {
            return;
        }
        const fetchData = async () => {
            try {
                const response = await fetch(process.env.NEXT_PUBLIC_API_URL + '/products', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw Error('Network response was not ok');
                }

                const data = await response.json();

                setProducts(data);
                setErrorMessage(null);

            } catch (error) {
                console.error('Error fetching data:', error);

                setErrorMessage('An error occurred while fetching data. Please try again later.');
            }
        };
        fetchData();
    }, [session, token]);

    const handleTabClick = (tab) => {
        setSelectedTab(tab);
    };

    const handleAddToCart = (product) => {
        addToCart(product);
    };

    if (!session) {
        return null;
    }

    return (
        <div>
            <div className={styles.shopContainer}>
                <h1 className={styles.title}>Tickets & passes</h1>

                {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}

                <div className={styles.tabButtons}>
                    <button
                        onClick={() => handleTabClick('Tickets')}
                        className={`${styles.tabButton} ${selectedTab === 'Tickets' ? styles.active : ''}`}
                    >
                        Tickets
                    </button>

                    <button
                        onClick={() => handleTabClick('Passes')}
                        className={`${styles.tabButton} ${selectedTab === 'Passes' ? styles.active : ''}`}
                    >
                        Passes
                    </button>
                </div>

                <div className={styles.itemList}>
                    {Array.isArray(products.products) && (
                        <ProductTabs
                            products={
                                selectedTab === 'Tickets'
                                    ? products.products.filter(product => product.type === 'ticket').map(product => ({
                                        ...product,
                                        name: product.name,
                                        price: `${product.price} FT`,
                                        duration: `${product.duration} h`,
                                    }))
                                    : products.products.filter(product => product.type === 'pass').map(product => ({
                                        ...product,
                                        name: product.name,
                                        price: `${product.price} FT`,
                                        duration: `${product.duration} d`,
                                    }))
                            }
                            selectedTab={selectedTab}
                            handleAddToCart={handleAddToCart}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};
export default Shop;