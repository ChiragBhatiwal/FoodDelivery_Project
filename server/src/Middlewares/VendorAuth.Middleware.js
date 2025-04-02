import jwt from 'jsonwebtoken'
import user from "../Models/Vendor.Model.js"

const verifyVendorToken = async (req, _, next) => {
    try {
        const token = req.cookies?.accessToken || req.header("Authorization")?.replace("Bearer ","");        
      
        if (!token) {
           return console.log("Token Required");
        }
         
        const decodedToken = jwt.verify(token,process.env.SecretKey)
    
        const userId = await user.findById(decodedToken?._id).select("-password -refreshToken")
    
        if (!userId) {
            return console.log("User Not Found with this token");
           
        }
    
        req.vendor = userId;
        next();
    } catch (error) {
      console.log(error);
    }
}

export default verifyVendorToken