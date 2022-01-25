const mongoose = require('mongoose');

const FixtureResultSchema = mongoose.Schema({
    fixtureId: {
        type: String,
        required: true
    }, 
    outcome: {
        type: Number
    },
    goals: [{
        _id: false,
        playerId: { type: String, required:true },
        numGoals: { type: Number, required:true }
    }]
});

module.exports = mongoose.model('FixtureResult', FixtureResultSchema);