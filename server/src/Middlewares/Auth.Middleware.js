import jwt from "jsonwebtoken";
import userModel from "../Models/Users.Model.js";
import vendorModel from "../Models/Vendor.Model.js";

const verifyToken = async (req) => {
    try {
        const userToken = req.headers['user-token'];
        const vendorToken = req.headers['vendor-token'];

        if(userToken) {
            const decoded = jwt.verify(userToken, process.env.SecretKey);
            const user = await userModel.findById(decoded.userId).select("-password -__v -createdAt -updatedAt -isDeleted");
            return { user };
        }else if(vendorToken) {
            const decoded = jwt.verify(vendorToken, process.env.SecretKey);
            const vendor = await vendorModel.findById(decoded.vendorId).select("-password -__v -createdAt -updatedAt -isDeleted");
            return { vendor };
        }
        else {
            throw new Error("No Token Provided");
        }
    } catch (error) {
        throw new Error("Invalid Token"); 
    }
};

export default verifyToken;
