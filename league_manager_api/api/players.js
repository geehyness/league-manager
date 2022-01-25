const express = require('express');
const playerRouter = express.Router();
const Player = require('./../models/Player');
const Team = require('./../models/Team');
const Results = require('./../models/FixtureResult');


/**
 * POST PLAYER
 */
playerRouter.post("/", async (req, res)=>{
    console.log(req.body);
    const player = new Player({
        name:req.body.name,
        number:req.body.number,
        teamId: req.body.teamId
    });

    try {
        const team = await Team.findById(player.teamId);
        
        const postPlayer = await player.save();
        res.send(postPlayer);
        res.json(league);
    } catch(err) {
        res.status(400).json(err);
    }
});


/**
 * GET ALL PLAYERS
 */
playerRouter.get("/", async (req, res)=>{
    try {
        const players = await Player.find();
        res.json(players);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC PLAYER
 */
playerRouter.get("/:id", async (req, res)=>{
    try {
        const players = await Player.findById(req.params.id);
        res.json(players);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC TEAM PLAYERS
 */
playerRouter.get("/byTeam/:id", async (req, res)=>{
    try {
        const players = await Player.find({teamId: req.params.id});
        const results = await Results.find();

        for (var a = 0; a < players.length; a++) {
            players[a].goals = 0;

            for (var b = 0; b < results.length; b++) {
                for (var c = 0; c < results[b].goals.length; c++) {
                    if (results[b].goals[c].playerId == players[a]._id) {
                        players[a].goals += results[b].goals[c].numGoals;
                    }
                }
            }

        }

        players.sort((a, b) => a.points - b.points);
        var sortedPlayers = [];
        for (var a = players.length - 1; a >= 0; a--) {
            sortedPlayers.push(players[a]);
        }

        res.json(players);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * DELETE SPECIFIC PLAYER
 */
playerRouter.delete("/:id", async (req, res)=>{
    try {
        const players = await Player.findByIdAndDelete(req.params.id);
        res.json(players);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC PLAYER
 */
playerRouter.patch("/:id", async (req, res)=>{
    try {
        const team = await Team.findById(req.body.teamId);

        const players = await Player.findByIdAndUpdate(
            { _id: req.params.id },
            { 
                name: req.body.name,
                number: req.body.number, 
                teamId: req.body.teamId 
            },
            {useFindAndModify: false});
        res.json(players);
    } catch (err) {
        res.status(400).json(err);
    }
});


module.exports = playerRouter;