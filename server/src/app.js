import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { expressMiddleware } from "@apollo/server/express4";
import { startApolloGraphqlServer} from "./Graphql/init.js";
import jwt from "jsonwebtoken";
import userModel from "./Models/Users.Model.js";
import vendorModel from "./Models/Vendor.Model.js";

const app = express();

dotenv.config({ path: "./.env" });

app.use(cors());
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

async function initializeGraphQL() {

    const apolloServer = await startApolloGraphqlServer();

    app.use(
        "/graphql",
        expressMiddleware(apolloServer, {
            context: async ({ req }) => {
                try {
                    const userToken = req.headers["user-token"];
                    const vendorToken = req.headers["vendor-token"];
            
                    if (userToken) {
                        const decoded = jwt.verify(userToken, process.env.SecretKey);
                        const user = await userModel.findById(decoded.userId)
                            .select("-password -__v -createdAt -updatedAt -isDeleted");
                        return { user};
                    } 
            
                    if (vendorToken) {
                        const decoded = jwt.verify(vendorToken, process.env.SecretKey);
                        const vendor = await vendorModel.findById(decoded.vendorId)
                            .select("-password -__v -createdAt -updatedAt -isDeleted");
                        return { vendor};
                    }
            
                    return { user: null, vendor: null, role: null };
            
                } catch (error) {
                    console.error("Token verification failed:", error);
                    return { user: null, vendor: null, role: null }; // Prevent crashes
                }
            }
        })
    );
}

initializeGraphQL().catch((error) => {
    console.error("Error initializing GraphQL:", error);
});

export default app;
