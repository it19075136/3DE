const express = require('express');
const cors = require('cors');
const speechRoute = require('./speechService');
const PORT = process.env.PORT || 5000;

let app = express();

app.use(cors());

app.use(express.json({limit: '50mb'}));

app.use(express.urlencoded({limit: '50mb', extended: true}))

app.use('/speech/api', speechRoute);

app.listen(PORT, () => {
    console.log("Listening on port: ", PORT);
});