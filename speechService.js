const router = require('express').Router();
const speechAPI = require('./speechAPI');

router.post('/', async (req,res) => {
    console.log(req.body)
    const result = await speechAPI.main(req.body);
    res.json(result);
});

module.exports = router;