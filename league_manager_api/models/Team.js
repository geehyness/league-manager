const mongoose = require('mongoose');

const TeamSchema = mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    leagueId: {
        type: String,
        required: true
    },
    venueId: {
        type: String
    },
    played:{
        type: Number,
    },
    wins:{
        type: Number,
    },
    losses:{
        type: Number
    },
    draws:{
        type: Number
    },
    goalsScored:{
        type: Number
    },
    goalsAgainst:{
        type: Number
    },
    goalDifference:{
        type: Number
    },
    points:{
        type: Number
    }
});

module.exports = mongoose.model('Team', TeamSchema);