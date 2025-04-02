// import {Server} from 'socket.io'
// import jwt from 'jsonwebtoken' 
// import {updateRestaurantActiveStatus} from "./Controllers/Restaurant.Controller.js"

// let io;

// let connectedClients = {};

// const verifyToken = (socket, next) => {

//     const token = socket.handshake.query.token;

//     if (!token) {
//         return next(new Error('Authentication error: No token provided'));
//     }

//     jwt.verify(token, process.env.SecretKey, (err, decoded) => {
//         if (err) {
//             return next(new Error('Authentication error: Invalid token'));
//         }

//         socket.user = decoded;
//         next();
//     });
// };

// const initializeSocket = (server) => {

//     io = new Server(server);

//     io.use(verifyToken);

//     io.on('connection', (socket) => {

//         socket.on('register', (userId) => {

//             connectedClients[userId] = socket.id;
//         });

//         socket.on("toggleRestaurantActiveStatus", async ({restaurantId,status})=> {
//             await updateRestaurantActiveStatus(restaurantId,status);
//         });

//         socket.on('disconnect', () => {
//             for (const userId in connectedClients) {
//                 if (connectedClients[userId] === socket.id) {
//                     delete connectedClients[userId];
//                     break;
//                 }
//             }
//         });
//     });

//     return { io };
// };

// const getIoInstance = () => {
//     if (!io) {
//         throw new Error('Socket.IO is not initialized');
//     }
//     return io;
// };

// const getUserSocket = (userId) => {
//     const socketId = connectedClients[userId];
//     if (socketId) {
//         return io.sockets.sockets.get(socketId);
//     }
//     return null;
// };

// export { getUserSocket, initializeSocket,getIoInstance};