const mongoose = require('mongoose');

const PlayerSchema = mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    number: {
        type: String,
        required: true
    },
    teamId: {
        type: String,
        required: true
    },
    goals: {
        type: Number
    }
});

module.exports = mongoose.model('Player', PlayerSchema);