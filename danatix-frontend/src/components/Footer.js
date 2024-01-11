import Link from "next/link";
import React from "react";

const Footer = () => {
    return (
        <footer>
            <div className="footer-content">
                <div className="footer-links">
                    <p>&copy; 2023 danatix</p>
                    <Link href="/terms">Terms</Link>
                    <Link href="/contact">Contact</Link>
                </div>
            </div>
        </footer>
    );
};

export default Footer;

