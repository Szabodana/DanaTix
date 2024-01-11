import React from "react";
import Head from "next/head";

const Protected = () => {

    return (
        <div>
            <Head>
                <title>Protected</title>
            </Head>
            <h1>Congratulations! Your email is verified so you can see me</h1>
        </div>
    )
}
export default Protected;