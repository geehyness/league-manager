const express = require('express');
const fixtureRouter = express.Router();
const Fixture = require('./../models/Fixture')
const League = require('./../models/League')
const Team = require('./../models/Team')
const Venue = require('./../models/Venue')


/**
 * POST FIXTURE
 */
fixtureRouter.post("/", async (req, res)=>{
    const fixture = new Fixture({
        leagueId: req.body.leagueId,
        team1Id: req.body.team1Id,
        team2Id: req.body.team2Id,
        venueId: req.body.venueId,
        time: req.body.time
    });

    try {
        const league = await League.findById(fixture.leagueId);
        const team1 = await Team.findById(fixture.team1Id);
        const team2 = await Team.findById(fixture.team2Id);
        const venue = await Venue.findById(fixture.venueId);

        const fixtures = await Fixture.find();
        for (var a = 0; a < fixtures.length; a++) {
            if (fixtures[a].venueId == fixture.venueId) {
                if ((fixtures[a].time <= fixture.time && fixture.time <= fixtures[a].time+10800000) ||
                    (fixture.time <= fixtures[a].time && fixtures[a].time <= fixture.time+10800000)) {
                        // VENUE BEING USED

                        return res.status(403).json({msg: "Venue Occupied"});
                    }
            }
        }

        const postFixture = await fixture.save();
        res.json(postFixture);
    } catch(err) {
        res.status(400).json(err);
    }
});


/**
 * GET ALL FIXTURES
 */
fixtureRouter.get("/", async (req, res)=>{
    try {
        const fixtures = await Fixture.find();
        res.json(fixtures);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC FIXTURE
 */
fixtureRouter.get("/:id", async (req, res)=>{
    try {
        const fixtures = await Fixture.findById(req.params.id);
        res.json(fixtures);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET LEAGUE FIXTURES
 */
fixtureRouter.get("/byLeague/:id", async (req, res)=>{
    try {
        const fixtures = await Fixture.find({leagueId: req.params.id});

        var results = [];
        for (var a = fixtures.length - 1; a >= 0; a--) {
            results.push(fixtures[a]);
        }

        res.json(results);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET UPCOMING LEAGUE FIXTURES
 */
fixtureRouter.get("/upcoming/:id", async (req, res)=>{
    try {
        const fixtures = await Fixture.find({leagueId: req.params.id});
        var upcoming = [];

        var date = Date.now();
        for (var a = 0; a < fixtures.length; a++) {
            if (fixtures[a].time > date) {
                upcoming.push(fixtures[a]);
            }
        }

        upcoming.sort((a, b) => a.time - b.time);

        res.json(upcoming);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * DELETE SPECIFIC FIXTURE
 */
fixtureRouter.delete("/:id", async (req, res)=>{
    try {
        const fixtures = await Fixture.findByIdAndDelete(req.params.id);
        res.json(fixtures);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC FIXTURE
 */
fixtureRouter.patch("/:id", async (req, res)=>{
    try {
        //const league = await League.findById(team.leagueId);
        //const team1 = await League.findById(team.team1Id);
        //const team2 = await League.findById(team.team2Id);
        //const venue = await League.findById(team.venueId);

        

        var fixture = await Fixture.findById(req.params.id);
        if (fixture.time != req.body.time) {
            const fixtures = await Fixture.find();
            for (var a = 0; a < fixtures.length; a++) {
                if (fixtures[a]._id != fixture._id) {
                    if (fixtures[a].venueId == fixture.venueId) {
                        if ((fixtures[a].time <= req.body.time && req.body.time <= fixtures[a].time+10800000) ||
                            (req.body.time <= fixtures[a].time && fixtures[a].time <= req.body.time+10800000)) {
                                // VENUE BEING USED
    
                                return res.status(403).json({msg: "Venue Occupied"});
                            }
                    }
                }
            }
        }

          
        
        fixture.outcome = req.body.outcome;
        fixture.goals = arrayGoals;

        await fixture.save();
        return res.json(fixture);
    } catch (err) {
        res.status(400).json(err);
    }
});


module.exports = fixtureRouter;