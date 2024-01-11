import React, { useState } from "react";
import { useRouter } from 'next/router';
import {useSession} from "next-auth/react";
import styles from './SendVerificationEmailButton.module.css'

const SendVerificationEmailButton = () => {
    const [message, setMessage] = useState("");
    const [isButtonClicked, setIsButtonClicked] = useState(false);
    const router = useRouter();
    const {data: session} = useSession();

    const handleButtonClick = async () => {

        try {

            setIsButtonClicked(true);

            if(session) {

                const response = await fetch(process.env.NEXT_PUBLIC_API_URL + `/email-verification`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${session.user.accessToken}`
                    },
                });

                setMessage(response.ok ? "Email verification is sent out, please check your inbox" : "Request failed." )

            } else {
                await router.push('/login');
            }
        } catch (error) {
            console.error("Error sending POST request:", error);
        }
    };

    return (
        <div className={styles.container}>
            <button
                type="button"
                onClick={handleButtonClick}
                className={styles.emailButton}
            >
                Send verification email again
            </button>
            {isButtonClicked && <div><h2 className={styles.statusMessage}>{message}</h2></div>}
        </div>
    );
}
export default SendVerificationEmailButton;