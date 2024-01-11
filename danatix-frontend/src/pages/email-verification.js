import React, {useEffect, useState} from "react";
import Head from "next/head";
import {useRouter} from "next/router";
import styles from './email-verification.module.css'

const VerifiedEmail = () => {
    const router = useRouter();
    const [message, setMessage] = useState("");
    const {token} = router.query;

    const goToHome = () => {
        router.push('/');
    };


    useEffect(() => {
        async function verifyEmail() {
            if (!router.isReady) return;

            if (token) {

                const encodedToken = encodeURIComponent(token);

                try {
                    const response = await fetch(
                        `${process.env.NEXT_PUBLIC_API_URL}/email-verification?token=${encodedToken}`,
                        {
                            method: 'PATCH'
                        });

                    if (response.ok) {
                        setMessage("Your email is now verified")
                    } else {
                        const responseData = await response.json();
                        setMessage(responseData.message)
                    }

                } catch (error) {
                    console.error('Error making backend request: ', error);
                }
            }

        }

        verifyEmail();
    }, [router.isReady, token]);

    return (
        <div>
            <Head>
                <title>Email Verified</title>
            </Head>
            <div className={styles.container}>
                <h1 className={styles.statusMessage}>
                    {message}
                </h1>
                <button onClick={goToHome} className={styles.goHomeButton}>
                    Go Home
                </button>
            </div>
        </div>
    );
}
export default VerifiedEmail;