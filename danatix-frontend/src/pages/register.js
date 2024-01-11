import React, {useRef, useState} from "react";
import styles from './register.module.css';
import Head from "next/head";

export default function RegisterPage() {

    const checkboxRef = useRef(null);

    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [terms, setTerms] = useState(false);
    const [formErrors, setFormErrors] = useState({});
    const [successMessage, setSuccessMessage] = useState("");

    const handleChange = () => {
        setTerms(checkboxRef.current.checked);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const user = {name:username, email, password};

        setFormErrors({});

        const errors = validateForm(username, email, password, terms);

        if (Object.keys(errors).length === 0) {

            fetch(process.env.NEXT_PUBLIC_API_URL + '/register', {
                method: 'POST',
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(user)
            }).then((res) => res.json())
                .then((data) => {
                        if (data.hasOwnProperty('id')) {
                            setFormErrors({});
                            setSuccessMessage("User has been successfully registered.");
                            setFormErrors(errors);
                        } else if (data.hasOwnProperty('message')) {
                            setFormErrors({});
                            errors.emailtaken = data.message;
                            setFormErrors(errors);
                        }
                    }
                )
        } else {
            setFormErrors(errors);
        }
    };
    const validateForm = (username, email, password, terms) => {
        const errors = {};

        if (!terms) {
            errors.terms = "Please accept the Terms and Conditions!";
        }

        if (username.length === 0) {
            errors.username = "Username is required!";
        }

        if (email.length === 0) {
            errors.email = "Email is required!";
        }


        if (password.length === 0) {
            errors.password = "Password is required!";
        } else if (password.length < 8) {
            errors.password = "Password must be at least 8 characters!";
        }

        return errors;
    };


    return (
        <div className={styles.App}>
            <Head>
                <title>Register</title>
            </Head>
            <div className={styles["registration-container"]}>
                <div className={styles.container}>
                    <span className={styles.borderLine}></span>
                    <form onSubmit={handleSubmit} className={styles["registration-form"]}>
                        <h2>Registration</h2>
                        <div className={styles.inputBox}>
                            <div className={styles.inputField}>
                                <input type="text"
                                       id="username"
                                       name="username"
                                       placeholder="Username"
                                       value={username}
                                       onChange={(e) => setUsername(e.target.value)}
                                       className="inputField"
                                />
                                {formErrors.username &&
                                    <div className={styles["error-message"]}> {formErrors.username}</div>}
                            </div>
                        </div>
                        <div className={styles.inputBox}>
                            <div className={styles.inputField}>
                                <input type="text"
                                       id="email"
                                       name="email"
                                       placeholder="Email"
                                       value={email}
                                       onChange={(e) => setEmail(e.target.value)}
                                       className="inputField"
                                />
                                {formErrors.email && <div className={styles["error-message"]}> {formErrors.email}</div>}
                            </div>
                        </div>
                        <div className={styles.inputBox}>
                            <div className={styles.inputField}>
                                <input type="password"
                                       id="password"
                                       name="password"
                                       placeholder="Password"
                                       value={password}
                                       onChange={(e) => setPassword(e.target.value)}
                                       className="inputField"
                                />
                                {formErrors.password &&
                                    <div className={styles["error-message"]}> {formErrors.password}</div>}
                            </div>
                        </div>
                        <div className={styles["accept-terms"]}>
                            <label htmlFor="terms">Please accept the Terms and Conditions</label>
                            <br/>
                        </div>
                        <div className={styles["checkbox-container"]}>
                            <input type="checkbox"
                                   id="terms"
                                   name="terms"
                                   ref={checkboxRef}
                                   onChange={handleChange}/>
                            <div className={styles["error-message-terms"]}> {formErrors.terms}</div>
                        </div>
                        <button type="submit"
                                data-testid="register-button"
                                value="Register"
                                id="register-button"
                                className={styles["register-button"]}>Register
                        </button>
                    </form>
                </div>

                {formErrors.emailtaken && <div id="registration-message">{formErrors.emailtaken}</div>}
                {successMessage && <div id="registration-message">{successMessage}</div>}
            </div>
        </div>
    );
}