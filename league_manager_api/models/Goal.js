const mongoose = require('mongoose');

const GoalSchema = mongoose.Schema({
    fixtureId: {
        type: String,
        required: true
    },
    playerId: {
        type: String,
        required: true
    },
    goals: {
        type: Number,
        required: true
    }
});

module.exports = mongoose.model('Goal', GoalSchema);