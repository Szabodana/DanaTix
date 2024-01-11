import Link from 'next/link';
import Image from 'next/image';
import {useSession, signIn, signOut} from 'next-auth/react';
import styles from './Header.module.css';
import {useCart} from "@/components/CartContext";
import {useState} from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCartShopping, faRightFromBracket, faRightToBracket, faShop, faTicket, faUser, faUserPlus} from '@fortawesome/free-solid-svg-icons'

const Header = () => {
    const {data: session} = useSession();
    const isLoggedIn = Boolean(session);
    const {cart} = useCart();
    const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);

    const handleLogin = () => {
        signIn();
    };

    const handleLogout = () => {
        setMobileMenuOpen(false);
        signOut({callbackUrl: '/'});
    };

    const toggleMobileMenu = () => {
        setMobileMenuOpen(!isMobileMenuOpen);
    };

    const closeMobileMenu = () => {
        setMobileMenuOpen(false);
    };

    return (
        <header className={styles.header}>
            <div className={styles.logo}>

                <div
                    className={`${isMobileMenuOpen ? styles.showMobileMenu : ''}`}
                    onClick={() => setMobileMenuOpen(false)}
                ></div>

                <Link href="/">
                    <div className={styles.logoImageContainer}>
                        <Image src="/img/danatix-logo.png" alt="Logo" width={150} height={40}/>
                    </div>
                </Link>
            </div>
            <button className={`${styles.hamburger} ${isMobileMenuOpen ? styles.open : ''}`} onClick={toggleMobileMenu}>
                <div className={`${styles.hamburgerIcon} ${styles.top}`}></div>
                <div className={`${styles.hamburgerIcon} ${styles.middle}`}></div>
                <div className={`${styles.hamburgerIcon} ${styles.bottom}`}></div>
            </button>
            <nav className={`${styles.navRight} ${isMobileMenuOpen ? styles.showMobileMenu : ''}`}>
                <div className={`${styles.mobileMenu} ${isMobileMenuOpen ? styles.showMobileMenu : ''}`}>
                    <ul className={styles.ul}>
                        {isLoggedIn ? (
                            <>
                                <li className={styles.navItem}>
                                    <Link href="/profile" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faUser} style={{color: "rgba(210, 157, 0, 0.8)",}} />                                            </div>
                                            <span>Profile</span>
                                        </div>
                                    </Link>
                                </li>
                                <li className={styles.navItem}>
                                    <Link href="/tickets" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faTicket} style={{color: "rgba(210, 157, 0, 0.8)",}} />                                            </div>
                                            <span>Tickets</span>
                                        </div>
                                    </Link>
                                </li>
                                <li className={styles.navItem}>
                                    <Link href="/shop" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faShop} style={{color: "rgba(210, 157, 0, 0.8)",}} />                                            </div>
                                            <span>Shop</span>
                                        </div>
                                    </Link>
                                </li>
                                <li className={styles.navItem}>
                                    <Link href="/cart" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faCartShopping} style={{color: "rgba(210, 157, 0, 0.8)",}} />                                            </div>
                                            <span>Cart</span>
                                        </div>
                                        <span className={styles.cartSizeIndicator}>{cart.length}</span>
                                    </Link>
                                </li>
                                <li className={styles.navItem} onClick={handleLogout}>
                                    <Link href="/" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faRightFromBracket} style={{color: "rgba(210, 157, 0, 0.8)",}} />
                                            </div>
                                            <span>Logout</span>
                                        </div>
                                    </Link>
                                </li>
                            </>
                        ) : (
                            <>
                                <li className={styles.navItem} onClick={handleLogin}>
                                    <Link href="/login" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faRightToBracket} style={{color: "rgba(210, 157, 0, 0.8)",}} />                                            </div>
                                            <span>Login</span>
                                        </div>
                                    </Link>
                                </li>
                                <li className={styles.navItem}>
                                    <Link href="/register" onClick={closeMobileMenu}>
                                        <div className={styles.navItemContent}>
                                            <div className={styles.navImageContainer}>
                                                <FontAwesomeIcon className={styles.icon} icon={faUserPlus} style={{color: "rgba(210, 157, 0, 0.8)",}} />
                                            </div>
                                            <span>Register</span>
                                        </div>
                                    </Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </nav>
        </header>
    );
}
export default Header;