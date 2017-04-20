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
const GAME_TIME = 180;
const GAME_HEALTH = 5;

// we need to share the data of orbsOfLight and players through server only
// shadows and ui can be computed locally with same algorithm
var rooms = [];
io.on("connection", function(socket){

    console.log("player connected");
    var foundRoom = null;
    // player wants to join a room
    socket.on("room", function (data) {
        console.log("Player wants to join room with size "+data);
        if (!!foundRoom){
            console.log("Player has already joined room "+foundRoom.id)
            socket.emit("socketID", {id: socket.id, numOfPlayers: foundRoom.numOfPlayers});
            return;
        }
        try {
            var size = data;
            for (var i = 0; i < rooms.length; i++) {
                if (rooms[i].size == size && rooms[i].waiting) {
                    foundRoom = rooms[i].addPlayer(socket.id);
                    if (!!foundRoom){
                        console.log("Joined existing room!");
                        break;
                    }
                }
            }
            if (!foundRoom) {
                console.log("Creating room!");
                foundRoom = new Room(Math.random().toString(), size);
                foundRoom.addPlayer(socket.id);
                rooms.push(foundRoom);
                foundRoom.game = new GameStruct(foundRoom.id);
            }
            socket.join(foundRoom.id);
            if (foundRoom.host === undefined) foundRoom.host = socket.id;
            socket.broadcast.to(foundRoom.id).emit("newPlayer", {id: socket.id});
            socket.emit("socketID", {id: socket.id, numOfPlayers: foundRoom.numOfPlayers});

            if (foundRoom.numOfPlayers == foundRoom.size) {
                startGame(io, foundRoom);
            }
        }catch (err){
            console.log(err);
        }
    });

    socket.on('playerMoved', function(data){
        try {
            var players = foundRoom.game.players;
            data.id = socket.id;
            for (var i = 0; i < players.length; i++) {
                if (players[i].id == socket.id) {
                    players[i].x = data.x;
                    players[i].y = data.y;
                    socket.broadcast.to(foundRoom.id).emit("playerMoved", data);
                    break;
                }
            }
        }catch (err){
            console.log(err);
        }
    });

    socket.on('gameStatus', function(data){
        console.log("update game status");
        try {
            socket.broadcast.to(foundRoom.id).emit("gameStatus", data);
        }catch (err){
            console.log(err);
        }
    });

    socket.on('pickUpOrb', function(data){
        console.log("Player wants to pickUpOrb");
        try {
            const orbID = data.orbID;
            var players = foundRoom.game.players;
            var orbsOfLight = foundRoom.game.orbsOfLight;
            for (var i = 0; i < players.length; i++) {
                if (players[i].id == socket.id && players[i].orbID === undefined) {
                    if (orbsOfLight[orbID].owner === undefined) {
                        console.log("pick permission granted");
                        orbsOfLight[orbID].owner = socket.id;
                        players[i].orbID = orbID;
                        data.id = socket.id;
                        io.to(foundRoom.id).emit("pickUpOrb", data);
                    }
                    break;
                }
            }
        }catch (err){
            console.log(err);
        }
    });

    socket.on('dropOrb', function(data){
        console.log("dropOrb");
        try {
            const orbID = data.orbID;
            var players = foundRoom.game.players;
            var orbsOfLight = foundRoom.game.orbsOfLight;
            for (var i = 0; i < players.length; i++) {
                if (players[i].id == socket.id && players[i].orbID !== undefined) {
                    for (var j = 0; j < orbsOfLight.length; j++) {
                        if (orbsOfLight[j].id == orbID && orbsOfLight[j].owner === undefined) {
                            orbsOfLight[j].owner = undefined;
                            orbsOfLight[j].x = players[i].x;
                            orbsOfLight[j].y = players[i].y;
                            data.x = orbsOfLight[j].x;
                            data.y = orbsOfLight[j].y;
                            data.id = socket.id;
                            io.to(foundRoom.id).emit("dropOrb", data);
                            break;
                        }
                    }
                    break;
                }
            }
        }catch (err){
            console.log(err);
        }
    });

    socket.on('placeOrbOnPillar', function(data){
        console.log("placeOrbOnPillar");
        try {
            const pillarID = data.pillarID;
            var players = foundRoom.game.players;
            var orbsOfLight = foundRoom.game.orbsOfLight;
            var pillars = foundRoom.game.pillars;
            if (pillars[pillarID].orbID !== undefined) {
                console.log("pillar already has orb")
                return;
            }
            for (var i = 0; i < players.length; i++) {
                if (players[i].id == socket.id) {
                    if (players[i].orbID !== undefined) {
                        const orbID = players[i].orbID;
                        if (orbsOfLight[orbID].owner == socket.id) {
                            orbsOfLight[orbID].owner = undefined;
                            players[i].orbID = undefined;
                            pillars[pillarID].orbID = orbID;
                            data.id = socket.id;
                            io.to(foundRoom.id).emit("placeOrbOnPillar", data);
                        }
                    }
                    break;
                }
            }
        }catch (err){
            console.log(err);
        }
    });

    socket.on('pickOrbFromPillar', function(data){
        console.log("pickOrbFromPillar");
        try {
            var players = foundRoom.game.players;
            var orbsOfLight = foundRoom.game.orbsOfLight;
            var pillars = foundRoom.game.pillars;
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
                            data.orbID = orbID;
                            io.to(foundRoom.id).emit("pickOrbFromPillar", data);
                        }
                    }
                    break;
                }
            }
        }catch (err){
            console.log(err);
        }
    });

    socket.on('leave', function () {
       console.log("Player leaves current room");
       try{
           leaveRoom(foundRoom, socket);
           foundRoom = null;
       }catch (err){
           console.log(err);
       }
    });

    socket.on('end', function (data) {
        console.log("level done");
        try{
            socket.emit('end', data);
        }catch (err){
            console.log(err);
        }
    });

    socket.on('ready', function () {
        foundRoom.ready++;
        if (foundRoom.ready == foundRoom.size){
            nextLevel(io, foundRoom);
        }
    });

    socket.on('disconnect', function(){
        console.log("Player disconnected");
        try {
            leaveRoom(foundRoom, socket);
        }catch (err){
            console.log(err);
        }
    });

    socket.on('throughput', function (data) {
        try {
            socket.emit("throughput", data);
        }catch (err){
            console.log(err);
        }
    });

    socket.on('startNow', function () {
        try {
            foundRoom.ready++;
            if (foundRoom.ready == foundRoom.size){
                io.to(foundRoom.id).emit("startNow");
                foundRoom.ready = 0;
            }
        }catch (err){
            console.log(err);
        }
    })
});

server.listen(8008, function(){
    console.log("server is now running at port 8008");
});

function leaveRoom(room, socket) {
    if (!!room) {
        var playerIDs = room.playerIDs;
        socket.leave(room.id);
        socket.broadcast.to(room.id).emit("playerDisconnected", {id: socket.id});
        for (var i = 0; i < playerIDs.length; i++) {
            if (playerIDs[i] == socket.id) {
                playerIDs.splice(i, 1);
                console.log("remove player from room");
                break;
            }
        }
        if (playerIDs.length == 0) {
            for (i = 0; i < rooms.length; i++) {
                if (rooms[i].id == room.id) {
                    rooms.splice(i, 1);
                    console.log("destroy room");
                    break;
                }
            }
        }else {
            var players = room.game.players;
            const id = findById(players, socket.id);
            players.splice(id, 1);
        }
    }else {
        console.log("player doesn't have a room");
    }
}

function Player(id, x, y){
    this.id = id;
    this.x = x;
    this.y = y;
    this.orbID = undefined;
}

function Shadow(id, direction, time) {
    this.id = id;
    this.direction = direction;
    this.time = time;
}

function GameStatus(id, score, time, health, level) {
    this.id = id; // room number
    this.score = score;
    this.time = time;
    this.health = health;
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

function GameStruct(players, shadows, orbs, pillars, gameStatus){
    this.players = players;
    this.shadows = shadows;
    this.orbsOfLight = orbs;
    this.pillars = pillars;
    this.gameStatus = gameStatus;
}

function Room(id, size) {
    this.id = id;
    this.host = undefined;
    this.size = size;
    this.numOfPlayers = 0;
    this.playerIDs = [];
    this.game = null;
    this.ready = 0;
    this.waiting = true;
    this.addPlayer = function (id) {
        if (this.numOfPlayers === size) return null;
        else {
            this.numOfPlayers++;
            this.playerIDs.push(id);
            return this;
        }
    }
}

function resetGame(room) {
    room.game.shadows = [];
    room.game.orbsOfLight = [];
    room.game.pillars = [];
    for (var i = 0; i < 4; i++){
        room.game.pillars.push(new Pillar(i, 0, 0));
    }
    room.game.gameStatus = new GameStatus(0,0,0,5,1);
    room.host = undefined;
}

function nextLevel(io, room) {
    room.ready = 0;
    var game = room.game;
    var level = game.gameStatus.level+1;
    game.players = generatePlayers(room.playerIDs);
    game.shadows = generateShadows(room.size, level);
    game.orbsOfLight = generateOrbs(room.size, level);
    game.pillars = generatePillars(room.size, level);
    game.gameStatus = resetStatus(level);
    startGameEmit(io, room);
}

// the number of shadows negatively exponentially proportional to level
function generateShadows(numOfPlayers, level) {
    var genShadows = [];
    // var numOfShadows = Math.floor(GAME_TIME*3* (1- Math.exp(-1*level)));
    var shadowInterval;
    if (level > 5){
        shadowInterval = 4;
    }else {
        shadowInterval = 8 - level;
    }
    var numOfPillars = 8;
    var numOfShadows = GAME_TIME/shadowInterval-1;
    for (var i = 0; i < numOfShadows; i++){
        for (var j = 0; j < numOfPlayers && j < level; j++){
            genShadows.push(new Shadow(i, Math.floor(numOfPillars*Math.random()), i*shadowInterval+j));
        }
    }

    return genShadows;
}

function generateOrbs(numOfPlayers, level) {
    var genOrbs = [];
    var numOrbs = numOfPlayers;
    for (var i = 0; i < numOrbs; i++){
        genOrbs.push(new OrbOfLight(i, 400+50*i, 600));
    }
    return genOrbs;
}

function generatePlayers(playerIDs) {
    var genPlayers = [];
    for (var i = 0; i < playerIDs.length; i++){
        genPlayers.push(new Player(playerIDs[i], 400+50*i, 450))
    }
    return genPlayers;
}

function generatePillars(numOfPlayers, level) {
    var genPillars = [];
    if (numOfPlayers < 3 && level < 1) {
        for (var i = 0; i < 4; i++) {
            genPillars.push(new Pillar(i, 0, 0));
        }
    }else {
        for (i = 0; i < 8; i++) {
            genPillars.push(new Pillar(i, 0, 0));
        }
    }
    return genPillars;
}

function resetStatus(level) {
    return new GameStatus(0, 0, GAME_TIME, GAME_HEALTH, level);
}

function startGame(io, room) {
    room.waiting = false;
    var players = generatePlayers(room.playerIDs);
    var shadows = generateShadows(room.size, 1);
    var orbsOfLight = generateOrbs(room.size, 1);
    var pillars = generatePillars(room.size, 1);
    var gameStatus = new GameStatus(0,0,GAME_TIME,GAME_HEALTH,1);
    room.game = new GameStruct(players, shadows, orbsOfLight, pillars, gameStatus);
    room.ready = 0;
    startGameEmit(io, room);
}

function startGameEmit(io, room) {
    var data = {};
    data.players = room.game.players;
    data.shadows = room.game.shadows;
    data.orbs = room.game.orbsOfLight;
    data.gameStatus = room.game.gameStatus;
    data.host = room.host;
    io.to(room.id).emit("start", data);
}

function findById(array, id) {
    for (var i = 0; i < array.length; i++){
        if (array[i].id == id) return array[i];
    }
    return null;
}