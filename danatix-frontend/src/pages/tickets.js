import React, {useEffect, useState} from "react";
import styles from "./tickets.module.css"
import {useSession} from "next-auth/react";
import PurchasesTab from "@/components/PurchasesTab";

const Tickets = () => {
    const {data: session} = useSession();
    const [errorMessage, setErrorMessage] = useState('');
    const [selectedTab, setSelectedTab] = useState('NOT_ACTIVE');
    const [purchases, setPurchases] = useState([]);

    const fetchPurchasesData = async () => {

        if(session) {
            try {

                const res = await fetch(process.env.NEXT_PUBLIC_API_URL + '/purchases', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${session.user.accessToken}`
                    }
                });
                if (!res.ok) {
                    setErrorMessage('Unable to fetch data. ' + res.status);
                }

                let data;
                const contentType = res.headers.get('content-type');

                if(contentType && contentType.includes('json')) {

                    data = await res.json()

                } else {

                    const message = await res.text();
                    setErrorMessage(message);
                }

                if (data && data.hasOwnProperty('purchases')) {

                    setPurchases(data.purchases);
                }

            } catch (error) {
                setErrorMessage('Unable to fetch data.');
                console.error(error)
            }
            console.log("Fetched:", purchases);
        }
    }

    const handleTabClick = (tab) => {
        setSelectedTab(tab);
    };

    useEffect(() => {
        fetchPurchasesData();
    }, [session]);

    return (
        <div>
            <div className={styles.ticketsContainer}>
                <h1 className={styles.title}>Purchased Tickets & Passes</h1>
                <div className={styles.tabButtons}>
                    <button
                        onClick={() => handleTabClick('NOT_ACTIVE')}
                        className={`${styles.tabButton} ${selectedTab === 'NOT_ACTIVE' ? styles.current : ''}`}
                    >
                        Not active
                    </button>

                    <button
                        onClick={() => handleTabClick('ACTIVE')}
                        className={`${styles.tabButton} ${selectedTab === 'ACTIVE' ? styles.current : ''}`}
                    >
                        Active
                    </button>
                    <button
                        onClick={() => handleTabClick('EXPIRED')}
                        className={`${styles.tabButton} ${selectedTab === 'EXPIRED' ? styles.current : ''}`}
                    >
                        Expired
                    </button>
                </div>
                    {errorMessage === '' ? (
                        <PurchasesTab
                            purchases={purchases}
                            filterStatus={selectedTab}
                            fetchData={fetchPurchasesData}/>
                    ) : (
                        <div className={styles.errorMessage}>{errorMessage}</div>
                    )}
            </div>
        </div>

    )
}

export default Tickets;