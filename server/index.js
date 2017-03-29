/**
 * Created by polarvenezia on 29/3/17.
 */
const app = require('express');
const server = require('http').Server(app);
const io = require('socket.io')(server);

const MOVE_UP = 19;
const MOVE_DOWN = 20;
const MOVE_LEFT = 21;
const MOVE_RIGHT = 22;
const MOVE_SPEED = 200;
const GAME_TIME = 60;
const MAX_PLAYER = 2;

// we need to share the data of orbsOfLight and players through server only
// shadows and ui can be computed locally with same algorithm
var rooms = [];
var players = [];
var orbsOfLight = [];
var pillars = [];
var shadows = [];
var host;
// socket refers to the socket assigned to the client
var gameStatus = new GameStatus(0, 0, 0, 1);
resetGame();
io.on("connection", function(socket){

    console.log("player connected");
    if (host == undefined) host = socket.id;
    socket.emit("socketID", { id: socket.id});
    socket.emit("getPlayers", players);
    socket.broadcast.emit("newPlayer", { id: socket.id });

    socket.on('playerMoved', function(data){
        data.id = socket.id;
        // moveAction = data.moveAction;
        // deltaTime = data.deltaTime;
        for (var i = 0; i < players.length; i++){
            if (players[i].id == socket.id){
                // switch(moveAction){
                // 	case MOVE_LEFT:
                // 		players[i].x -= MOVE_SPEED*deltaTime;
                // 		break;
                // 	case MOVE_RIGHT:
                // 		players[i].x += MOVE_SPEED*deltaTime;
                // 		break;
                // 	case MOVE_UP:
                // 		players[i].y += MOVE_SPEED*deltaTime;
                // 		break;
                // 	case MOVE_DOWN:
                // 		players[i].y -= MOVE_SPEED*deltaTime;
                // 		break;
                // 	default:
                // 		console.log("Invalid movement");
                // }
                // const vx = data.vx;
                // const vy = data.vy;
                // delete data.vx;
                // delete data.vy;
                players[i].x = data.x;
                players[i].y = data.y;
                // data.x = players[i].x;
                // data.y = players[i].y;
                socket.broadcast.emit("playerMoved", data);
                break;
            }
        }
    });

    socket.on('UIUpdate', function(data){
        console.log("UIUpdate");
    });

    socket.on('pickUpOrb', function(data){
        console.log("Player wants to pickUpOrb");
        const orbID = data.orbID;
        for (var i = 0; i < players.length; i++){
            if (players[i].id == socket.id && players[i].orbID === undefined){
                if (orbsOfLight[orbID].owner === undefined){
                    orbsOfLight[orbID].owner = socket.id;
                    players[i].orbID = orbID;
                    data.id = socket.id;
                    io.emit("pickUpOrb", data);
                }
                break;
            }
        }
    });

    socket.on('dropOrb', function(data){
        console.log("dropOrb");
        const orbID = data.orbID;
        for (var i = 0; i < players.length; i++){
            if (players[i].id == socket.id && players[i].orbID !== undefined){
                for (var j = 0; j < orbsOfLight.length; j++){
                    if (orbsOfLight[j].id == orbID && orbsOfLight[j].owner === undefined){
                        orbsOfLight[j].owner = undefined;
                        orbsOfLight[j].x = players[i].x;
                        orbsOfLight[j].y = players[i].y;
                        data.x = orbsOfLight[j].x;
                        data.y = orbsOfLight[j].y;
                        data.id = socket.id;
                        io.emit("dropOrb", data);
                        break;
                    }
                }
                break;
            }
        }
    });

    socket.on('placeOrbOnPillar', function(data){
        console.log("placeOrbOnPillar");
        const pillarID = data.pillarID;
        if (pillars[pillarID].orbID !== undefined){
            console.log("pillar already has orb")
            return;
        }
        for (var i = 0; i < players.length; i++){
            if (players[i].id == socket.id){
                if (players[i].orbID !== undefined) {
                    const orbID = players[i].orbID;
                    if (orbsOfLight[orbID].owner == socket.id) {
                        orbsOfLight[orbID].owner = undefined;
                        players[i].orbID = undefined;
                        pillars[pillarID].orbID = orbID;
                        data.id = socket.id;
                        io.emit("placeOrbOnPillar", data);
                    }
                }
                break;
            }
        }
    });

    socket.on('pickOrbFromPillar', function(data){
        console.log("pickOrbFromPillar");
        for (var i = 0; i < players.length; i++){
            if (players[i].id == socket.id){
                if (players[i].orbID === undefined) {
                    const pillarID = data.pillarID;
                    const orbID = pillars[pillarID].orbID;
                    if (orbsOfLight[orbID].owner === undefined) {
                        orbsOfLight[orbID].owner = socket.id;
                        players[i].orbID = orbID;
                        pillars[pillarID].orbID = undefined;
                        data.id = socket.id;
                        io.emit("pickOrbFromPillar", data);
                    }
                }
                break;
            }
        }
    });

    socket.on('disconnect', function(){
        console.log("Player disconnected");
        socket.broadcast.emit("playerDisconnected", { id : socket.id});
        for (var i = 0; i < players.length; i++){
            if (players[i].id == socket.id){
                players.splice(i,1);
                break;
            }
        }
        if(players.length == 0){
            resetGame();
        }
    });

    players.push(new Player(socket.id, 500, 500));
    orbsOfLight.push(new OrbOfLight(0, 500, 600));
    if (players.length == MAX_PLAYER){
        startGame(io);
    }
})

server.listen(8008, function(){
    console.log("server is now running at port 8008");
})

function Player(id, x, y){
    this.id = id;
    this.x = x;
    this.y = y;
    this.orbID = undefined;
}

function Shadow(id, direction, time) {
    this.id = id;
    // this.x = x;
    // this.y = y;
    this.direction = direction;
    this.time = time;
}

function GameStatus(id, score, time, level) {
    this.id = id; // room number
    this.score = score;
    this.time = time;
    this.level = level;
}

function OrbOfLight(id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.pillar = undefined;
    this.owner = undefined;
}

function Pillar(id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.hasOrb = false;
    this.orbID = undefined;
}

function GameStruct(id){
    this.id = id; // room number
    this.players = [];
    this.shadows = [];
    this.orbsOfLight = [];
    this.pillars = [];
    this.gameStatus = [];
}

function resetGame() {
    shadows = [];
    orbsOfLight = [];
    pillars = [];
    for (var i = 0; i < 4; i++){
        pillars.push(new Pillar(i, 0, 0));
    }
    gameStatus = new GameStatus(0,0,0,1);
    host = undefined;
}

// the number of shadows negatively exponentially proportional to level
function generateShadows(roomID, level) {
    var genShadows = [];
    // var numOfShadows = Math.floor(GAME_TIME*3* (1- Math.exp(-1*level)));
    const shadowInterval = 3;
    var numOfShadows = GAME_TIME/shadowInterval;
    for (var i = 0; i < numOfShadows; i++){
        genShadows.push(new Shadow(i, Math.round(4*Math.random()), level*shadowInterval));
    }

    return genShadows;
}

function startGame(io) {
    var startFlag = true;
    shadows = generateShadows(0, 0);
    gameStatus = new GameStatus(0,0,0,1);
    var data = {};
    data.shadows = shadows;
    data.orbsOfLight = orbsOfLight;
    data.gameStatus = gameStatus;
    io.emit("start", data);
}