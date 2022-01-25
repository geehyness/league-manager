package com.yukicide.leaguemanager.JavaRepositories;

import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.PlayerModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.ResultModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.UserModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LeagueManagerAPI {

    /**
     * League API
     */
    @GET("leagues")
    Call<List<LeagueModel>> getLeague();
    @POST("leagues")
    Call<LeagueModel> postLeague(@Body LeagueModel league);
    @PATCH("leagues/{id}")
    Call<LeagueModel> patchLeague(@Path("id") String id, @Body LeagueModel leagueModel);
    @DELETE("leagues/{id}")
    Call<LeagueModel> deleteLeague(@Path("id") String id);


    /**
     * Teams API
     */
    @GET("teams/byLeague/{id}")
    Call<List<TeamModel>> getTeamsByLeague(@Path("id") String id);
    @GET("teams/log/{id}")
    Call<List<TeamModel>> getLogTable(@Path("id") String id);
    @POST("teams")
    Call<TeamModel> postTeam(@Body TeamModel team);
    @PATCH("teams/{id}")
    Call<TeamModel> patchTeam(@Path("id") String id, @Body TeamModel team);
    @DELETE("teams/{id}")
    Call<TeamModel> deleteTeam(@Path("id") String id);


    /**
     * Fixtures API
     */
    @GET("fixtures/upcoming/{id}")
    Call<List<FixtureModel>> getUpcomingFixtures(@Path("id") String id);
    @GET("fixtures/byLeague/{id}")
    Call<List<FixtureModel>> getAllFixtures(@Path("id") String id);
    @POST("fixtures")
    Call<FixtureModel> addFixture(@Body FixtureModel fixtureModel);
    @DELETE("fixtures/{id}")
    Call<FixtureModel> deleteFixture(@Path("id") String id);


    /**
     * Players API
     */
    @GET("players/byTeam/{id}")
    Call<List<PlayerModel>> getTeamPlayers(@Path("id") String id);
    @POST("players")
    Call<PlayerModel> postPlayer(@Body PlayerModel player);
    @PATCH("players/{id}")
    Call<PlayerModel> patchPlayer(@Path("id") String id, @Body PlayerModel player);
    @DELETE("players/{id}")
    Call<PlayerModel> deletePlayer(@Path("id") String id);


    /**
     * Venues API
     */
    @GET("venues")
    Call<List<VenueModel>> getAllVenues();
    @POST("venues")
    Call<VenueModel> postVenue(@Body VenueModel venueModel);
    @PATCH("venues/{id}")
    Call<VenueModel> patchVenue(@Path("id") String id, @Body VenueModel venueModel);
    @DELETE("venues/{id}")
    Call<VenueModel> deleteVenue(@Path("id") String id);


    /**
     * Users API
     */
    @POST("users/login")
    Call<UserModel> loginUser(@Body UserModel userModel);
    @POST("users/register")
    Call<UserModel> regUser(@Body UserModel userModel);


    /**
     * Results API
     */
    @POST("results")
    Call<ResultModel> addResults(@Body ResultModel resultModel);
    @GET("results")
    Call<List<ResultModel>> getAllResults();
}

