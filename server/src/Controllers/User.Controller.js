import userModel from "../Models/Users.Model.js"
import cartModel from "../Models/Cart.Model.js"
import bcrypt from "bcrypt"
import uploadOnCloudinarySingle from "../Middlewares/CloudinarySingle.Middleware.js"
import twilio from 'twilio'

const generateAccessAndRefreshToken = async (userId) => {
    console.log("UserID:", userId);

    try {
        // Find the user by ID
        const user = await userModel.findById(userId).select("-password");
        if (!user) {
            console.error("User not found");
            return { error: "User not found" };
        }


        // Generate tokens
        const accessToken = user.generateAccessToken();
        const refreshToken = user.generateRefreshToken();


        // Set the refresh token
        user.refreshToken = refreshToken;

        // Save user with the new refresh token
        await user.save({ validateBeforeSave: false });


        return { accessToken, refreshToken };
    } catch (error) {
        console.error("Error while generating tokens:", error);
        return { error: "Error while generating tokens" };
    }
};


const CreateUser = async (phoneNumber) => {

    if (!phoneNumber) {
        return console.log("Phone Number is required");
    }

    try {
        const verification = await client.verify.v2.services(verifyServiceSid)
            .verifications
            .create({ to: phoneNumber, channel: 'sms' });

        return 'OTP sent successfully'
    } catch (error) {
       console.log("Error",error)
    }
}

const UpdateUser = async (req, resp) => {

    const userId = req.user;

    if (!userId) {
        return console.log("User id is required");
    }

    const data = req.body;

    let updateFields = {};

    if (data.username) {
        updateFields.username = data.username;
    }

    if (data.password) {
        try {
            const hashedPassword = await bcrypt.hash(data.password, 10);
            updateFields.password = hashedPassword;
        } catch (error) {
            console.error("Error hashing password:", error);
            return resp.status(500).json({ error: "Error hashing password" });
        }
    }

    if (data.email) {
        updateFields.email = data.email;
    }

    try {
        const updatedUser = await userModel.findByIdAndUpdate(
            userId,
            { $set: updateFields },
            { new: true, select: '-password' } // new: true returns the updated document
        );

        if (!updatedUser) {
            return resp.status(404).json({ error: "User not found" });
        }

        resp.status(200).json({ updatedUser });
    } catch (error) {
        console.error("Error while updating user:", error);
        resp.status(500).json({ error: "Internal server error" });
    }

}

const LoginUser = async (phoneNumber,otp) => {

    if ([phoneNumber, otp].some((field) => field.trim() == "")) {
        return resp.status(400).send("Phone Number and OTP is Required");
    }

    try {
        const verificationCheck = await client.verify.v2
            .services(verifyServiceSid)
            .verificationChecks.create({ to: phoneNumber, code: otp });

        if (verificationCheck.status === "approved") {
            return "OTP verified successfully!";
        } else {
            throw new Error("Invalid OTP");
        }
    } catch (error) {
        throw new Error("OTP verification failed: " + error.message);
    }

}

const LogoutUser = async (user) => {
    const userData = await userModel.findByIdAndUpdate(
        user._id,
        {
            $unset: {
                refreshToken: 1
            }
        },
        {
            new: true
        }
    ).select("-password -refreshToken")
   
    if(!userData)
    {
        return {error:"User not found"}
    }

    return {message:"User logged out successfully"}
}

export { CreateUser, LoginUser, LogoutUser, UpdateUser };