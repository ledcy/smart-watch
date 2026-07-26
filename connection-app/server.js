const express = require("express");
const http = require("http");
const socketIO = require("socket.io");

const app = express();
const server = http.createServer(app);

const io = socketIO(server);

app.get("/", (req, res) => {
    res.send("Servidor SmartWatch");
});

io.on("connection", (socket) => {

    console.log("Dispositivo conectado:", socket.id);

    socket.on("Datos", (data) => {

        console.log("Datos recibidos");
        console.log(data);

        io.emit("watchUpdate", data);

    });

    socket.on("disconnect", () => {

        console.log("Desconectado");

    });

});

server.listen(3000,"0.0.0.0",()=>{

    console.log("Servidor iniciado");

});