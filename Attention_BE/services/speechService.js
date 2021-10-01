const router = require('express').Router();
const speechAPI = require('../APIs/speechAPI');

router.post('/', async (req,res) => {
    const result = await speechAPI.main(req.body);
    res.json(result);
});

module.exports = router;