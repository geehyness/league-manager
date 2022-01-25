const express = require('express');
const userRouter = express.Router();
const User = require('./../models/User')


/**
 * GET ALL USERS
 */
userRouter.get("/", async (req, res)=>{
    try {
        const mongoUser = await User.find();
        res.json(mongoUser);
    } catch(err) {
        res.status(400).json({ message: err });
    }
});


/**
 * LOGIN
 */
userRouter.post("/login", async (req, res)=>{
    const user = new User({
        email:req.body.email,
        password:req.body.password
    });

    try {
        var usersList = await User.find();
        var success = false;
        var loggedInUser;

        for (var a = 0; a < usersList.length; a++) {
            if (usersList[a].email == user.email) {
                if (usersList[a].password == user.password) {
                    success = true;
                    loggedInUser = usersList[a];
                }
            }
        }

        if (success) {
            res.json(loggedInUser);
        } else {
            res.status(401).json({message: "Check email and password and try again"})
        }
    } catch(err) {
        res.status(400).json({ message: err });
    }
});


/**
 * POST SPECIFIC USERS
 */
userRouter.post("/register", async (req, res)=>{
    try {
        console.log(req.body);
        const user = new User({
            email:req.body.email,
            password:req.body.password
        });

        var usersList = await User.find();
        var exists = false;

        for (var a = 0; a < usersList.length; a++) {
            if (usersList[a].email === user.email) {
                exists = true;
            }
        }

        if (!exists) {
            user.save()
            .then(data => {
                res.json(data);
            })
            .catch(err => {
                res.status(400).json({ message: err })
            });
        } else {
            res.status(401).json({ message: "user exists" });
        }
    } catch(err) {
        res.status(400).json({ message: err });
    }
});


/**
 * DELETE SPECIFIC USER
 */
userRouter.delete("/:id", async (req, res)=>{
    try {
        const fixtures = await User.findByIdAndDelete(req.params.id);
        res.json(fixtures);
    } catch (err) {
        res.status(400).json(err);
    }
});





module.exports = userRouter;