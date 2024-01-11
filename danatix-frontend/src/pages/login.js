import React, {useEffect, useState} from "react";
import styles from './login.module.css';
import {useRouter} from 'next/router';
import {signIn, useSession} from 'next-auth/react';
import Head from "next/head";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [formErrors, setFormErrors] = useState({});
    const [loginSuccess, setLoginSuccess] = useState(false);
    const [loginError, setLoginError] = useState("");
    const router = useRouter();

    const {query} = useRouter();

    useEffect(() => {
        if ('callbackUrl' in query) {
            router.replace('/login', undefined, {shallow: true});
        }
    }, [query, router]);

    const {data: session} = useSession();

    useEffect(() => {
        if (session) {
            router.replace('/');
        }
    }, [session, router]);
    const handleSubmit = async (e) => {
        e.preventDefault();

        setFormErrors({});
        setLoginError("");

        const validationErrors = validateLogin(email, password);

        if (Object.keys(validationErrors).length > 0) {
            setFormErrors({...validationErrors, allFieldsRequired: "All fields are required."});
            return;
        }

        try {
            const result = await signIn('credentials', {
                redirect: false,
                email,
                password
            });

            if (result.error) {
                setLoginError(result.error);
            } else {
                router.push('/articles');
            }
        } catch (error) {
            console.error("Login error:", error);
            setLoginError(error.message || "An unknown error occurred.");  // Ensure error is displayed in some form
        }
    };

    const validateLogin = (email, password) => {
        const errors = {};

        if (email.length === 0) {
            errors.email = "Email is required!";
        }

        if (password.length === 0) {
            errors.password = "Password is required!";
        }

        return errors;
    };

    return (
        <div className={styles.App}>
            <Head>
                <title>Login</title>
            </Head>
            <div className={styles["login-container"]}>
                <div className={styles.container}>
                    <span className={styles.borderLine}></span>
                    <form onSubmit={handleSubmit} className={styles["login-form"]}>
                        <h2>Login</h2>
                        {formErrors.allFieldsRequired &&
                            <div className={styles["error-message"]}>{formErrors.allFieldsRequired}</div>}
                        {loginError &&
                            <div className={styles["error-message"]}>{loginError}</div>}
                        <div className={styles.inputBox}>
                            {formErrors.email && <div className={styles["error-message"]}>{formErrors.email}</div>}
                            <input
                                type="text"
                                id="email"
                                name="email"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className={`${styles.inputField} ${formErrors.email ? styles.inputFieldError : ''}`}
                            />
                        </div>
                        <div className={styles.inputBox}>
                            {formErrors.password &&
                                <div className={styles["error-message"]}>{formErrors.password}</div>}
                            <input
                                type="password"
                                id="password"
                                name="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className={`${styles.inputField} ${formErrors.password ? styles.inputFieldError : ''}`}
                            />
                        </div>
                        <button
                            type="submit"
                            value="Login"
                            id="login-button"
                            className={styles["login-button"]}
                        >
                            Login with Credentials
                        </button>
                        <button
                            type="button"
                            onClick={() => signIn('github')}
                            className={styles["github-button"]}
                        >
                            Login with GitHub
                        </button>
                        <button
                            type="button"
                            onClick={() => signIn('discord')}
                            className={styles["discord-button"]}
                        >
                            Login with Discord
                        </button>
                        {loginSuccess && <div className="success-message">Login successful!</div>}
                    </form>
                </div>
            </div>
        </div>
    );
}