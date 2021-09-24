const router = require('express').Router();
const mailAPI = require('../APIs/mailAPI')

router.post('/', async (req,res) => {
    console.log(req.body)
    const result = await mailAPI.send(req.body);
    res.json(result);
});

module.exports = router;