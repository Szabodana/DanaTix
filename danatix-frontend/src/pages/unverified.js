import React from "react";
import Head from "next/head";
import SendVerificationEmailButton from "@/components/SendVerificationEmailButton";
import styles from './unverified.module.css'

const UnverifiedEmail = () => {

    return (
        <div>
            <Head>
                <title>Email Not Verified</title>
            </Head>
            <div className={styles.container}>
                <div>
                    <h1 className={styles.message}>
                        Please verify your email:
                    </h1>
                </div>
                <SendVerificationEmailButton />
            </div>
        </div>
    );
}
export default UnverifiedEmail;