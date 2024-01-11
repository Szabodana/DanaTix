import React from 'react';
import Head from 'next/head';
import {useState, useEffect} from 'react';
import styles from './articles.module.css';
import NewsItem from "@/components/NewsItem";

export default function LandingPage() {

    const [newsData, setNewsData] = useState([]);

    useEffect(() => {
        async function fetchNewsData() {
            try {
                const response = await fetch(process.env.NEXT_PUBLIC_API_URL + '/articles');

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const data = await response.json();

                setNewsData(data.articles);

            } catch (error) {
                console.error('Error fetching news data:', error);
            }
        }

        fetchNewsData();
    }, []);

    return (
        <div className="wrapper">
            <Head>
                <title>Dana Ticket</title>
            </Head>
            <main>
                <div className={styles.content}>
                    <section className={styles.content}>
                        {Array.isArray(newsData) &&
                            newsData.map((newsItem, index) => (
                                <NewsItem newsItem={newsItem} key={index}/>
                            ))}
                    </section>
                </div>
            </main>
        </div>
    );
}