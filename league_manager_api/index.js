const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
var bodyparser = require('body-parser');

const PORT = process.env.PORT || 5000;

/**
 * API ROUTES
 */
const usersAPI = require('./api/users');
const leaguesAPI = require('./api/leagues');
const teamsAPI = require('./api/teams');
const playersAPI = require('./api/players');
const venuesAPI = require('./api/venues');
const fixturesAPI = require('./api/fixtures');
const goalsAPI = require('./api/goals');
const resultsAPI = require('./api/results');

const app = express();

app.use(cors());
app.use(bodyparser.json());

// RA68cPpd2GVYXxCN
MONGO_URI = 'mongodb+srv://admin:RA68cPpd2GVYXxCN@cluster0.jt3vx.gcp.mongodb.net/league_manager?retryWrites=true&w=majority'
if (process.env.NODE_ENV === 'production') {
    dbURI = process.env.MONGOLAB_URI;
}
mongoose.connect(MONGO_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true
});

mongoose.connection.on('connected', () => {
    console.log('Mongoose Connected!!!');
});

app.use('/users', usersAPI);
app.use('/leagues', leaguesAPI);
app.use('/teams', teamsAPI);
app.use('/players', playersAPI);
app.use('/venues', venuesAPI);
app.use('/fixtures', fixturesAPI);
app.use('/goals', goalsAPI);
app.use('/results', resultsAPI);

app.listen(PORT);
