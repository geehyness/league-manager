const express = require('express');
const venueRouter = express.Router();
const Venue = require('./../models/Venue')


/**
 * POST VENUE
 */
venueRouter.post("/", async (req, res)=>{
    console.log(req.body);
    const venue = new Venue({
        name:req.body.name
    });

    try {
        const postVenue = await venue.save();
        res.json(postVenue);
    } catch(err) {
        res.status(400).json(err);
    }
});


/**
 * GET ALL VENUES
 */
venueRouter.get("/", async (req, res)=>{
    try {
        const venues = await Venue.find();
        res.json(venues);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * GET SPECIFIC VENUE
 */
venueRouter.get("/:id", async (req, res)=>{
    try {
        const venues = await Venue.findById(req.params.id);
        res.json(venues);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * DELETE SPECIFIC VENUE
 */
venueRouter.delete("/:id", async (req, res)=>{
    try {
        const venues = await Venue.findByIdAndDelete(req.params.id);
        res.json(venues);
    } catch (err) {
        res.status(400).json(err);
    }
});


/**
 * UPDATE SPECIFIC VENUE
 */
venueRouter.patch("/:id", async (req, res)=>{
    try {
        const venues = await Venue.findByIdAndUpdate(
            { _id: req.params.id },
            { name: req.body.name },
            {useFindAndModify: false});
        res.json(venues);
    } catch (err) {
        res.status(400).json(err);
    }
});


module.exports = venueRouter;