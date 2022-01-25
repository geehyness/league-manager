const mongoose = require('mongoose');

const FixtureSchema = mongoose.Schema({
    leagueId: {
        type: String,
        required: true
    }, 
    team1Id: {
        type: String,
        required: true
    },
    team2Id: {
        type: String,
        required: true
    },
    venueId: {
        type: String,
        required: true
    },
    time: {
        type: Number,
        required: true
    }
});

module.exports = mongoose.model('Fixture', FixtureSchema);