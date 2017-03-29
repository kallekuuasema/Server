angular.module('scoreList', []).controller('scoreListController', function () {
    var scoreList = this;
    this.scores = [1, 2, 3];
    this.currentPlayerId = "kalle";

    scoreList.loadPlayer = function () {
        //Get player id
        playerQuery = "?playerName=" + document.getElementById("playerName").value;
        this.makeRequest("GET", "/players" + playerQuery, null, function () {
            //Found player id
            if (this.readyState == 4 && this.status == 200) {
                scoreList.currentPlayerId = this.response
                console.log(scoreList.currentPlayerId);

                //get scores
                scoresQuery = "?playerId=" + scoreList.currentPlayerId;
                scoreList.makeRequest("GET", "/scores" + scoresQuery, null, function () {
                    if (this.readyState == 4 && this.status == 200) {
                        scoreList.scores = [2, 3, 4, 5];
                        console.log(this.response);
                    }
                })
            }
            //No player with that name
            if (this.readyState == 4 && this.status == 404) {
                //Create player
                message = "playerName=" + document.getElementById("playerName").value;
                scoreList.makeRequest("POST", "/players", message, function () {
                    //New player created
                    if (this.readyState == 4 && this.status == 201) {
                        scoreList.currentPlayerId = this.response
                        console.log(scoreList.currentPlayerId);

                        //Create empty score list
                        scoreList.scores = [];
                    }
                    //Failed to create new player. Just print error for now
                    if (this.readyState == 4 && this.status == 404) {
                        console.log("Failed to create new player");
                    }
                })
            }
        })
    };

    scoreList.addScore = function () {
        message = "playerId=" + this.currentPlayerId + "&score=" + document.getElementById("newScore").value;
        this.makeRequest("POST", "/scores", message, function () {
            if (this.readyState == 4 && this.status == 200) {
                scoreList.scores.push(4);
                console.log("Added score " + 4 + " to " + this.currentPlayerId);
            }
        })
    };

    scoreList.makeRequest = function (type, path, message, callback) {
        console.log(message);
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = callback;
        xhttp.open(type, path, true);
        //xhttp.setRequestHeader("myHeader", myHeaderValue);
        xhttp.send(message);
    }
});

