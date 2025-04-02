import DatabaseConnection from "./Database/DatabaseConnection.js"
import http from 'http'
import app from "./app.js"
// import {initializeSocket} from './sockets.js'
import dotenv from "dotenv"

dotenv.config({
    path:"../.env"
});

const server = http.createServer(app);

// initializeSocket(server);

DatabaseConnection()
.then(()=>{
 server.listen(process.env.PORT,()=>{
    console.log(`Port Connected To ${process.env.PORT}`);
 })
}).catch((error)=>{
    console.log("Error While Connected to Port And Database",error);
});