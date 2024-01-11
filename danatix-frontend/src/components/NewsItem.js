import React from 'react';
import styles from '../pages/articles.module.css';

function NewsItem({newsItem}) {
    return (
        <div className={styles.tile}>
            <div className={styles.content}>{newsItem?.content}</div>
            <div className={styles.bottomTile}>{newsItem?.title}</div>
        </div>
    );
}

export default NewsItem;
