const express = require('express');
const cors = require('cors');
const speechRoute = require('./services/speechService');
const mailRoute = require('./services/mailService');
const PORT = process.env.PORT || 5000;

let app = express();

app.use(cors());

app.use(express.json({limit: '50mb'}));

app.use(express.urlencoded({limit: '50mb', extended: true}))

app.get('/', (req,res) => {
    res.send("Hi, I'm Up")
});

app.use('/speech/api', speechRoute); // speech api

app.use('/mail/api', mailRoute); // mail api

app.listen(PORT, () => {
    console.log("Listening on port: ", PORT);
});