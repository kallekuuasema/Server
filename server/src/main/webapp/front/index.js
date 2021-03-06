angular.module('scoreList', ['ngRoute']).controller('scoreListController', function ($scope, $route, $routeParams, $location) {

    scoreList = this;

    this.playerScoreEntries = [];
    this.highScoreEntries = [];

    scoreList.loadPlayer = function () {
        cachedPlayerName = document.getElementById("playerName").value;
        cachedGameName = document.getElementById("gameName").value;
        cachedPlayerId = document.getElementById("playerId").value;

        //Get player name
        playerQuery = "/players?playerId=" + cachedPlayerId;
        this.makeRequest("GET", playerQuery, null, null, function () {
            //Found player with id
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById("playerName").value = this.response;

                scoreList.refreshPlayerScores();
            }
            //No player with that id
            if (this.readyState == 4 && this.status == 404) {
                //Create player
                message = JSON.stringify({ playerName: cachedPlayerName });
                headers = [{ name: "Content-Type", value: "application/json" }];
                scoreList.makeRequest("POST", "/players", message, headers, function () {
                    //New player created
                    if (this.readyState == 4 && this.status == 201) {
                        document.getElementById("playerId").value = this.response;

                        //Create empty score list
                        scoreList.playerScoreEntries = [];
                        $route.reload();
                    }
                })
            }
        });
    },

    scoreList.addScore = function () {
        cachedPlayerId = document.getElementById("playerId").value;
        cachedPlayerName = document.getElementById("playerName").value;
        cachedGameName = document.getElementById("gameName").value;
        newScore = document.getElementById("newScore").value;

        message = JSON.stringify({ playerName: cachedPlayerName, playerId: cachedPlayerId, gameName: cachedGameName, score: newScore });
        headers = [{ name: "Content-Type", value: "application/json" }];
        this.makeRequest("POST", "/scores", message, headers, function () {
            if (this.readyState == 4) {
                if (this.status == 200) {

                    scoreList.refreshPlayerScores();

                    scoreList.refreshGameScores();
                }
                else if (this.status == 201) {
                    scoreList.refreshPlayerScores();

                    scoreList.refreshGameScores();
                }
                else if (this.status == 304) {
                    //Highscore was lower than previous, so nothing changed
                }
            }
        })
    },

    scoreList.gameChanged = function () {
        cachedGameName = document.getElementById("gameName").value;

        scoreList.refreshGameScores();
    },

    scoreList.playerNameChanged = function () {
        document.getElementById("playerId").value = "";
    },

    scoreList.deleteScores = function () {
        cachedGameName = document.getElementById("gameName").value;
        cachedPlayerId = document.getElementById("playerId").value;

        //delete scores
        scoresQuery = "/scores?playerId=" + cachedPlayerId + "&gameName=" + cachedGameName;
        scoreList.makeRequest("DELETE", scoresQuery, null, null, function () {
            if (this.readyState == 4 && this.status == 200) {
                scoreList.playerScoreEntries = [];
                scoreList.refreshGameScores();
            }
        })
    },

    scoreList.refreshGameScores = function () {
        cachedGameName = document.getElementById("gameName").value;

        //get game scores
        scoresQuery = "/scores?gameName=" + cachedGameName;
        scoreList.makeRequest("GET", scoresQuery, null, null, function () {
            if (this.readyState == 4 && this.status == 200) {
                scoreList.highScoreEntries = JSON.parse(this.response);
                console.log(scoreList.highScoreEntries);
                $route.reload();
            }
        })
    },
    scoreList.refreshPlayerScores = function () {
        cachedPlayerId = document.getElementById("playerId").value;

        //get player scores
        scoresQuery = "/scores?playerId=" + cachedPlayerId;
        scoreList.makeRequest("GET", scoresQuery, null, null, function () {
            if (this.readyState == 4 && this.status == 200) {
                scoreList.playerScoreEntries = JSON.parse(this.response);
                console.log(scoreList.playerScoreEntries);
                $route.reload();
            }
        })
    },

    scoreList.makeRequest = function (type, path, message, headers, callback) {
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = callback;
        xhttp.open(type, path, true);
        if (null !== headers) {
            for (a = 0; a < headers.length; ++a) {
                xhttp.setRequestHeader(headers[a].name, headers[a].value);
            }
        }
        xhttp.send(message);
    }
});

