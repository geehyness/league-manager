const express = require('express');
const goalRouter = express.Router();
const Goal = require('./../models/Goal');
const Fixture = require('./../models/Fixture');
const Player = require('./../models/Player');


/**
 * POST GOAL
 */
goalRouter.post("/", async (req, res)=>{
    const goal = new Goal({
        fixtureId: req.body.fixtureId,
        playerId: req.body.playerId,
        goals: req.body.goals
    });

    try {
        const fixture = await Fixture.findById(goal.fixtureId);
        const player = await Player.findById(goal.playerId);

        const postGoal = await goal.save();
        res.json(postGoal);
    } catch(err) {
        res.status(400).json(err);
    }
});


/**
 * GET ALL GOALS
 */
goalRouter.get("/", async (req, res)=>{
    try {
        const goals = await Goal.find();
        res.json(goals);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC GOAL
 */
goalRouter.get("/:id", async (req, res)=>{
    try {
        const goals = await Goal.findById(req.params.id);
        res.json(goals);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET GOALS BY PLAYER
 */
goalRouter.get("/:pid/:fid", async (req, res)=>{
    try {
        const goals = await Goal.find({
            playerId: req.params.pid,
            fixtureId: re.params.fid
        });
        res.json(goals);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET TOTAL FIXTURE GOALS
 */
goalRouter.get("/fixtureGoals/:id", async (req, res)=>{
    try {
        const goals = await Goal.find({fixtureId: req.params.id});

        var totgoals = 0;
        for (var a = 0; a < goals.length; a++) {
            totgoals+=goals[a].goals;
        }

        res.json({fixtureGoals: totgoals});
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * DELETE SPECIFIC GOAL
 */
goalRouter.delete("/:id", async (req, res)=>{
    try {
        const goals = await Goal.findByIdAndDelete(req.params.id);
        res.json(goals);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC GOAL
 */
goalRouter.patch("/:id", async (req, res)=>{
    try {
        const goals = await Goal.findByIdAndUpdate(
            { _id: req.params.id },
            { name: req.body.name },
            {useFindAndModify: false});
        res.json(goals);
    } catch (err) {
        res.status(400).json(err);
    }
});


module.exports = goalRouter;