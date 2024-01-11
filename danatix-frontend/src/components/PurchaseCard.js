import React, {useEffect} from "react";
import styles from "@/pages/tickets.module.css";

const PurchaseCard = ({purchase, status, handleUse, dateFormater}) => {

    return (
        <div className={styles.purchaseCard}>
            <div className={styles.purchaseHeader}>
                <strong>{purchase.hasOwnProperty('product') ? purchase.product.name : 'Ticket'}</strong>
                <div>
                    {status === 'NOT_ACTIVE' && (
                        <button
                            className={styles.useButton}
                            onClick={() => handleUse(purchase.id)}>
                            Use
                        </button>
                    )}
                </div>

            </div>
            <div className={styles.separator}></div>
            <div className={styles.purchaseBody}>
                {status !== 'EXPIRED' && (
                    <div className={styles.purchaseDuration}>
                        <strong>Duration:</strong>
                        <h2>{purchase.hasOwnProperty('product') ? purchase.product.duration : '0'}
                            {purchase.product.type === 'ticket' ? ' h' : ' d'}</h2>
                    </div>
                )}

                <div className={styles.purchaseDate}>
                    <strong>Date of purchase:</strong>
                    <h2>{dateFormater(purchase.paidDate)}</h2>
                </div>

                {status !== 'NOT_ACTIVE' && (
                    <div className={styles.expirationDate}>
                        <strong>Time of expiration:</strong>
                        <h2>{dateFormater(purchase.expirationDate)}</h2>
                    </div>
                )}
            </div>
        </div>
    )
}

export default PurchaseCard;