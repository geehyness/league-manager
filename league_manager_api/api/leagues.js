const express = require('express');
const leagueRouter = express.Router();
const League = require('./../models/League')


/**
 * POST LEAGUE
 */
leagueRouter.post("/", async (req, res)=>{
    console.log(req.body);
    const league = new League({
        name:req.body.name
    });

    try {
        const postLeague = await league.save();
        res.json(postLeague);
    } catch(err) {
        res.status(400).json(err);
    }
});


/**
 * GET ALL LEAGUES
 */
leagueRouter.get("/", async (req, res)=>{
    try {
        const leagues = await League.find();
        res.json(leagues);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC LEAGUE
 */
leagueRouter.get("/:id", async (req, res)=>{
    try {
        const leagues = await League.findById(req.params.id);
        res.json(leagues);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * DELETE SPECIFIC LEAGUE
 */
leagueRouter.delete("/:id", async (req, res)=>{
    try {
        const leagues = await League.findByIdAndDelete(req.params.id);
        res.json(leagues);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC LEAGUE
 */
leagueRouter.patch("/:id", async (req, res)=>{
    try {
        const leagues = await League.findByIdAndUpdate(
            { _id: req.params.id },
            { name: req.body.name },
            {useFindAndModify: false});
        res.json(leagues);
    } catch (err) {
        res.status(400).json(err);
    }
});


module.exports = leagueRouter;