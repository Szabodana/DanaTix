import '@/styles/globals.css'
import '@/pages/articles.module.css'
import {SessionProvider} from "next-auth/react";
import Layout from "@/components/Layout";
import {CartProvider} from "@/components/CartContext";

function App({Component, pageProps}) {
    return (
        <SessionProvider session={pageProps.session}>
            <CartProvider>
                <Layout>
                    <Component {...pageProps} />
                </Layout>
            </CartProvider>
        </SessionProvider>
    );
}

export default App;