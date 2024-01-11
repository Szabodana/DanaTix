import React from 'react';
import {useRouter} from 'next/router';
import Head from "next/head";

const Custom404 = () => {
    const router = useRouter();

    const goToHome = () => {
        router.push('/');
    };

    return (
        <div>
            <Head>
                <title>Page not found!</title>
            </Head>
            <div style={{textAlign: 'center', padding: '20px'}}>
                <h1 style={{marginBottom: '20px', fontWeight: 'bold', fontSize: '2em'}}>
                    Looks like this page is missing! :(
                </h1>
                <button onClick={goToHome} style={{
                    backgroundColor: '#d0ac35',
                    padding: '10px 20px',
                    borderRadius: '15px',
                    border: 'none',
                    cursor: 'pointer',
                    color: '#fff',
                    fontSize: '18px',
                    fontWeight: 'bold'
                }}>
                    Go Home
                </button>
            </div>
        </div>
    );
};

export default Custom404;