const speech = require('@google-cloud/speech');

async function main(payload) {

    const client = new speech.SpeechClient();

    const audio = {
        content: payload.audio
    };

    const config = {
        encoding: 'LINEAR16',
        sampleRateHertz: 16000,
        languageCode: payload.lang,
        speechContexts: [{
            "phrases": ["50	48 46 44 42	40 38 36 34	32 30 28 26	24 22 20 18	16 14 12 10	8 6 4 2	0"]
        }]
    };

    const request = {
        audio: audio,
        config: config
    }

    const [response] = await client.recognize(request);
    const transcription = response.results.map(result => result.alternatives[0].transcript).join('\n');
    console.log(`Transcription: ${transcription}`)

    return transcription;
}

module.exports = { main };