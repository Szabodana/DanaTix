import React, { useState, useEffect } from "react";
import { useSession, signIn } from 'next-auth/react';
import styles from './profile.module.css';
import Head from "next/head";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { useRouter } from "next/router";
import SendVerificationEmailButton from "@/components/SendVerificationEmailButton";

export default function ProfilePage() {
    const [name, setName] = useState("");
    const [newEmail, setNewEmail] = useState("");
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");
    const [errors, setErrors] = useState({});
    const { data: session, status } = useSession();
    const router = useRouter();
    const [messageType, setMessageType] = useState("");
    const [emailVerified, setEmailVerified] = useState(false);
  
     useEffect(() => {
        if(session) {
            setEmailVerified(session.user.isEmailVerified);
        }
    }, [session]);

    useEffect(() => {
        if (session) {
            setName(session.user.name);
            setNewEmail(session.user.email);
        }
    }, [session]);

    const updateProfile = async (updateData) => {
        const token = session?.user?.accessToken || "Token";

        if (token === "Token") {
            setMessage("You are not authenticated. Please log in again.");
            return;
        }

        try {
            const response = await fetch(process.env.NEXT_PUBLIC_API_URL+'/users', {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(updateData),
            });

            if (response.status === 419) {
                const errorData = await response.json();
                setMessage(Array.isArray(errorData) ? errorData.join('; ') : errorData);
                return;
            }

            if (!response.ok) {
                // Other errors
                const errorData = await response.json();

                // If errorData has fields, set them as individual errors, else set a general message
                if (errorData && typeof errorData === 'object' && Object.keys(errorData).length > 0) {
                    setMessage(Array.isArray(errorData) ? errorData.join('; ') : errorData);
                    setMessageType("success");
                } else {
                    setMessage("An error occurred while updating your profile.");
                    setMessageType("success");
                }
                return;
            }

            const data = await response.json();
            setMessage("Profile updated successfully!");
            setMessageType("success");

            if (session) {
                session.user = {...session.user, ...updateData};
            }

            await signIn('credentials', { callbackUrl: '/profile', redirect: false });

        } catch (error) {
            console.error('An unexpected error happened:', error);
            setMessage('An unexpected error happened. Please try again.');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setErrors({});

        const updateData = {};
        if (name && session?.user?.name !== name) {
            updateData.name = name;
        }

        if (newEmail && session?.user?.email !== newEmail) {
            updateData.email = newEmail;
        }

        if (newPassword) {
            if (newPassword !== confirmPassword) {
                setMessage('Passwords do not match.');
                setMessageType("success");
                return;
            }

            updateData.currentPassword = currentPassword;
            updateData.password = newPassword;
        }

        if (Object.keys(updateData).length === 0) {
            setMessage('No changes detected.');
            setMessageType("success");
            return;
        }

        await updateProfile(updateData);
    };

    return (
        <div className={styles.registrationContainer}>
            <Head>
                <title>Profile Update</title>
            </Head>
            <Header/>
            <form onSubmit={handleSubmit} className={styles.registrationForm}>
                <h2 className={styles.registrationFormH2}>Hello, {session?.user?.name || 'Guest'}</h2>
                {/* Username */}
                <div className={styles.inputBox}>
                    <label htmlFor="username" className={styles.labelField}>Update your username</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Update Username"
                        className={styles.inputField}
                    />
                    {errors.name && <p className={styles.errorMessage}>{errors.name}</p>}
                </div>

                {/* Email */}
                <div className={styles.inputBox}>
                    <label htmlFor="email" className={styles.labelField}>Update your email</label>
                    <input
                        type="email"
                        value={newEmail}
                        onChange={(e) => setNewEmail(e.target.value)}
                        placeholder="Update Email"
                        className={styles.inputField}
                    />
                    {errors.email && <p className={styles.errorMessage}>{errors.email}</p>}
                    {!emailVerified && <SendVerificationEmailButton />}
                </div>

                {/* Password */}
                <div>
                    <div className={styles.inputBox}>
                        <label htmlFor="currentPassword" className={styles.labelField}>Current Password:</label>
                        <input
                            type="password"
                            value={currentPassword}
                            onChange={(e) => setCurrentPassword(e.target.value)}
                            placeholder="Current Password"
                            className={styles.inputField}
                        />
                        {errors.currentPassword && <p className={styles.errorMessage}>{errors.currentPassword}</p>}
                    </div>
                    {errors.newPassword && <p className={styles.errorMessage}>{errors.newPassword}</p>}
                    <div className={styles.inputBox}>
                        <label htmlFor="newPassword" className={styles.labelField}>New Password</label>
                        <input
                            type="password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            placeholder="New Password"
                            className={styles.inputField}
                        />
                        {errors.newPassword && <p className={styles.errorMessage}>{errors.newPassword}</p>}
                    </div>
                    <div className={styles.inputBox}>
                        <label htmlFor="confirmPassword" className={styles.labelField}>Confirm New Password</label>
                        <input
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="Confirm New Password"
                            className={styles.inputField}
                        />
                        {errors.confirmPassword && <p className={styles.errorMessage}>{errors.confirmPassword}</p>}
                    </div>
                </div>

                <button type="submit" className={styles.registerButton}>
                    Update Profile
                </button>
                {message && <p className={messageType === "success" ? styles.successMessage : styles.errorMessage}>{message}</p>}
            </form>
            <Footer/>
        </div>
    );
}
