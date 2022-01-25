const express = require('express');
const fixtureRouter = express.Router();
const Fixture = require('./../models/Fixture');
const FixtureResult = require('./../models/FixtureResult');
const League = require('./../models/League');
const Team = require('./../models/Team');
const Venue = require('./../models/Venue');
const Player = require('./../models/Player');


/**
 * POST FIXTURE
 */
fixtureRouter.post("/", async (req, res)=>{
    try {
        var team1Goals = 0;
        var team2Goals = 0;

        const fixture = await Fixture.findById(req.body.fixtureId);

        const resultsList = await FixtureResult.find();
        var exists = false;
        for (var a = 0; a < resultsList.length; a++) {
            if (resultsList[a].fixtureId == fixture._id) {
                exists = true;
            }
        }

        if (exists) {
            console.log("exists")
            return res.status(403).json({message: "Fixture results exist! Try editing instead of creating new results."});
        } else {

            var arrayGoals = req.body.goals;

            for (var a = 0; a < arrayGoals.length; a++) {
                const player = await Player.findById(arrayGoals[a].playerId);
                if (player.teamId === fixture.team1Id){
                    team1Goals += arrayGoals[a].numGoals;
                } else if (player.teamId === fixture.team2Id) {
                    team2Goals += arrayGoals[a].numGoals;
                } else {
                    return res.status(401).json({ message: "Player is in neither team!"})
                }

                console.log(team1Goals + " " + team2Goals)
            }

            const results = new FixtureResult({
                fixtureId: req.body.fixtureId
            });

            if (team1Goals > team2Goals) {
                results.outcome = 1;
            } else if (team1Goals < team2Goals) {
                results.outcome = 2;
            } else {
                results.outcome = 3;
            }

            results.goals = arrayGoals;

            const savedResults = await results.save();
            res.json(savedResults);
        }
    } catch (err) {
        res.status(400).json(err);
    }

});


/**
 * GET ALL FIXTURE RESULTS
 */
fixtureRouter.get("/", async (req, res)=>{
    try {
        const resultsJson = await FixtureResult.find();
        res.json(resultsJson);
    } catch (err) {
        console.log(err);
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC FIXTURE
 */
fixtureRouter.get("/:id", async (req, res)=>{
    try {
        const resultsJson = await FixtureResult.findById(req.params.id);
        res.json(resultsJson);
    } catch (err) {
        console.log(err);
        res.status(400).json(err);
    }
});


/**
 * DELETE SPECIFIC FIXTURE
 */
fixtureRouter.delete("/:id", async (req, res)=>{
    try {
        const resultsJson = await FixtureResult.deleteOne(req.params.id);
        res.json(resultsJson);
    } catch (err) {
        console.log(err);
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC FIXTURE
 */
fixtureRouter.patch("/:id", async (req, res)=>{
    
});



























/**
 * POST FIXTURE
 */
fixtureRouter.post("/nillDraw", async (req, res)=>{
    try {
        var team1Goals = 0;
        var team2Goals = 0;

        const fixture = await Fixture.findById(req.body.fixtureId);

        const resultsList = await FixtureResult.find();
        var exists = false;
        for (var a = 0; a < resultsList.length; a++) {
            if (resultsList[a].fixtureId == fixture._id) {
                exists = true;
            }
        }

        if (exists) {
            console.log("exists")
            return res.status(403).json({message: "Fixture results exist! Try editing instead of creating new results."});
        } else {

            var arrayGoals = [];


            const players1 = await Player.find({teamId: fixtures[a].team1Id});
            const players2 = await Player.find({teamId: fixtures[a].team2Id});

            let player1goals = {
                playerId: players1[Math.floor(Math.random() * players1.length) + 0 ]._id,
                numGoals: 0 
            }

            let player2goals = {
                playerId: players2[Math.floor(Math.random() * players2.length) + 0 ]._id,
                numGoals: 0
            }

            

            arrayGoals.goals.push(player1goals);
            arrayGoals.goals.push(player2goals);


            const results = new FixtureResult({
                fixtureId: req.body.fixtureId
            });

            results.outcome = 3;

            results.goals = arrayGoals;

            const savedResults = await results.save();
            res.json(savedResults);
        }
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * POST FIXTURE
 */
fixtureRouter.post("/auto", async (req, res)=>{
    try {
        var team1Goals = 0;
        var team2Goals = 0;

        const fixtures = await Fixture.find();

        const resultsList = await FixtureResult.find();
        
        for (var a = 0; a < fixtures.length; a++) {
            var exists = false;
            for (var b = 0; b < resultsList.length; b++) {
                if (resultsList[b].fixtureId == fixtures[a]._id) {
                    exists = true;
                }
            }
                
            if (exists) {
                console.log("exists")
            } else {

                const players1 = await Player.find({teamId: fixtures[a].team1Id});
                const players2 = await Player.find({teamId: fixtures[a].team2Id});

                //console.log(players1)

                let player1goals = {
                    playerId: players1[Math.floor(Math.random() * players1.length) + 0 ]._id,
                    numGoals: Math.floor(Math.random() * 3) + 1  
                }

                let player2goals = {
                    playerId: players2[Math.floor(Math.random() * players2.length) + 0 ]._id,
                    numGoals: Math.floor(Math.random() * 3) + 1  
                }

                var results = new FixtureResult({
                    fixtureId: fixtures[a]._id
                });

                if (player1goals.numGoals > player2goals.numGoals) {
                    results.outcome = 1;
                } else if (player2goals.numGoals > player1goals.numGoals) {
                    results.outcome = 2;
                } else {
                    results.outcome = 3;
                }

                results.goals.push(player1goals);
                results.goals.push(player2goals);

                //console.log(results);
                await results.save();
            }
        }
        const resultsJson = await FixtureResult.find();
        res.json(resultsJson);
    } catch (err) {
        console.log(err);
        res.status(400).json(err);
    }

});


























module.exports = fixtureRouter;