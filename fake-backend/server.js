const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const fakeBackend = require('./faketoken');

const app = express();

const PORT = process.env.PORT || 8000;

app.use(cors());
app.use(bodyParser.json());

app.post('/api/login', (req, res) => {
    const {email, password} = req.body;

    console.log('Received login request:', email, password);

    const token = fakeBackend.authenticate({email, password});

    if (token) {
        res.status(200).json({token});
    } else {
        console.log('Authentication failed');
        res.status(401).json();
    }
});

app.get('/api/articles', (req, res) => {
    const articlesData = require('./db.json');

    const articles = articlesData.articles ?? { articles: [] };

    console.log('Articles:', articles);

    res.status(200).json({ articles });
});

app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
