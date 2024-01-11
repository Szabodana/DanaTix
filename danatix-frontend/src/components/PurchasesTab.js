import React, {useState} from "react";
import styles from "@/pages/tickets.module.css";
import {useSession} from "next-auth/react";
import PurchaseCard from "@/components/PurchaseCard";

const PurchasesTab = ({purchases, filterStatus, fetchData}) => {
    const {data: session} = useSession();
    const [errorMessage, setErrorMessage] = useState("");

    const handleTicketUse = async (purchaseId) => {
        if (session) {

            try {
                const res = await fetch(process.env.NEXT_PUBLIC_API_URL + `/purchases/${purchaseId}`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${session.user.accessToken}`
                    },
                    body: JSON.stringify({status: 'active'})
                });
                if (!res.ok) {
                    const message = await res.text();
                    setErrorMessage(message);
                } else {
                    fetchData();
                }

            } catch (error) {
                setErrorMessage('Unable to fetch data.');
                console.error(error)
            }
        }
    }

    const simpleDateFormater = (purchaseDate) => {
        const date = new Date(purchaseDate);
        const options = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
        }

        return date.toLocaleDateString(undefined, options)
    }

    return (
        <div className={styles.scrollable}>
            {errorMessage !== '' && <div className={styles.errorMessage}>{errorMessage}</div>}
            {purchases.filter(purchase => purchase.status === filterStatus).map(
                purchase => (
                    <>
                        <PurchaseCard
                            purchase={purchase}
                            status={filterStatus}
                            handleUse={handleTicketUse}
                            dateFormater={simpleDateFormater}/>
                    </>
                )
            )}
        </div>


    )

}

export default PurchasesTab;