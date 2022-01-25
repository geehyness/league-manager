const express = require('express');
const teamRouter = express.Router();
const Team = require('./../models/Team');
const League = require('./../models/League');
const Results = require('./../models/FixtureResult');
const Fixture = require('./../models/Fixture');
const Player = require('./../models/Player');


/**
 * POST TEAM
 */
teamRouter.post("/", async (req, res)=>{
    console.log(req.body);
    const team = new Team({
        name:req.body.name,
        leagueId: req.body.leagueId
    });

    try {
        const league = await League.findById(team.leagueId);
        
        const postTeam = await team.save();
        res.send(postTeam);
        res.json(league);
    } catch(err) {
        res.status(400).json(err);
    }
});


/**
 * GET ALL TEAMS
 */
teamRouter.get("/", async (req, res)=>{
    try {
        const teams = await Team.find();
        res.json(teams);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC TEAM
 */
teamRouter.get("/:id", async (req, res)=>{
    try {
        const teams = await Team.findById(req.params.id);
        res.json(teams);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC LEAGUE TEAMS
 */
teamRouter.get("/byLeague/:id", async (req, res)=>{
    try {
        const teams = await Team.find({leagueId: req.params.id});
        res.json(teams);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET LEAGUE LOG TABLE
 */
teamRouter.get("/log/:id", async (req, res)=>{
    try {
        const leagueId = req.params.id;
        const teams = await Team.find({leagueId: req.params.id});
        const results = await Results.find();
        const fixtures = await Fixture.find({leagueId: req.params.id});
        const players = await Player.find();
        /*var leagueResults = [];
        var fixtures = [];

        for (var a = 0; a < results.length; a++) {
            var resultsFixture = await Fixture.findById(results[a].fixtureId);
            if (resultsFixture.leagueId === leagueId) {
                leagueResults.push(results[a]);
                fixtures.push(resultsFixture);
            }
        }*/

        //console.log(teams);
        //console.log(results);
        //console.log(fixtures);

        for (var a = 0; a < teams.length; a++) {
            teams[a].played = 0;
            teams[a].wins = 0;
            teams[a].losses = 0;
            teams[a].draws = 0;
            teams[a].goalsScored = 0;
            teams[a].goalsAgainst = 0;
            teams[a].goalDifference = 0;
            teams[a].points = 0;

            for (var b = 0; b < results.length; b++) {
                var resultsFixture = null;
                for (var c = 0; c < fixtures.length; c++) {
                    if (results[b].fixtureId == fixtures[c]._id) {
                        resultsFixture = fixtures[c];
                        break;
                    }

                }
                //console.log(resultsFixture);
                /**
                 * calculating wins draws and losses
                 */
                
                if (resultsFixture != null) {
                    if(resultsFixture.team1Id == teams[a]._id) {
                        //console.log("_");
    
                        if (results[b].outcome == 1) {
                            teams[a].wins+=1;
                        } else if (results[b].outcome == 2) {
                            teams[a].losses+=1;
                        }else if (results[b].outcome === 3) {
                            teams[a].draws+=1;
                        }
                    } else if(resultsFixture.team2Id == teams[a]._id) {
                        if (results[b].outcome === 2) {
                            teams[a].wins+=1;
                        } else if (results[b].outcome === 1) {
                            teams[a].losses+=1;
                        }else if (results[b].outcome === 3) {
                            teams[a].draws+=1;
                        }
                    }
                    
                    if((resultsFixture.team1Id == teams[a]._id)||(resultsFixture.team2Id == teams[a]._id)) {
                        teams[a].played+=1;
                    }

                    for (var c = 0; c < results[b].goals.length; c++) {
                        var player = null; //= await Player.findById(results[b].goals[c].playerId);

                        var oppositionId = null;
                        if (teams[a]._id == resultsFixture.team1Id) {
                            oppositionId = resultsFixture.team2Id;
                        } else if (teams[a]._id == resultsFixture.team2Id) {
                            oppositionId = resultsFixture.team1Id;
                        }
                        
                        /*for (var d = 0; d < teams.length; d++) {
                            if (teams[d]._id == oppositionId) {
                                opposition = teams[d];
                            }
                        }*/


                        for (var d = 0; d < players.length; d++) {
                            if (players[d]._id == results[b].goals[c].playerId) {
                                player = players[d];
                                break;
                            } else {
                                player = null;
                            }
                        }
    
                        if (player != null) {
                            if (player.teamId == teams[a]._id) {
                                teams[a].goalsScored += results[b].goals[c].numGoals;
                            } else if (player.teamId == oppositionId) {
                                teams[a].goalsAgainst += results[b].goals[c].numGoals;
                            }
                        }
                    }
                }
            }

            teams[a].goalDifference = teams[a].goalsScored - teams[a].goalsAgainst;
            teams[a].points = (teams[a].wins*3) + teams[a].draws;
        }

        teams.sort((a, b) => a.points - b.points);
        var log = [];
        for (var a = teams.length - 1; a >= 0; a--) {
            log.push(teams[a]);
        }

        res.json(log);
    } catch (err) {
        res.status(400).json({message: err});
        console.log(err);
    }
});


/**
 * DELETE SPECIFIC TEAM
 */
teamRouter.delete("/:id", async (req, res)=>{
    try {
        const teams = await Team.findByIdAndDelete(req.params.id);
        res.json(teams);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC TEAM
 */
teamRouter.patch("/:id", async (req, res)=>{
    try {
        const league = await League.findById(req.body.leagueId);

        const teams = await Team.findByIdAndUpdate(
            { _id: req.params.id },
            { 
                name: req.body.name, 
                leagueId: req.body.leagueId 
            },
            {useFindAndModify: false});
        res.json(teams);
    } catch (err) {
        res.status(400).json(err);
    }
});


module.exports = teamRouter;