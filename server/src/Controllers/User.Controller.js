import userModel from "../Models/Users.Model.js"
import cartModel from "../Models/Cart.Model.js"
import bcrypt from "bcrypt"
import twilio from 'twilio'
import fs from "fs"
import {v2 as cloudinary} from "cloudinary"

const client = twilio(process.env.TWILIO_SID, process.env.TWILIO_AUTHTOKEN)
const globalOTP = {}

//Generating Refresh Token & Access Token For User
const generateAccessAndRefreshToken = async (userId) => {
    try {
        const user = await userModel.findById(userId);

        if (!user) {
            return { success: false, message: "User Not Found" }
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

// Sending Otp To User For Creating Account
const sendOtpToUser = async (phoneNumber) => {
    try {

        if (!phoneNumber) {
            return { success: false, message: "Phone Number Required." }
        }

        const checkIfPhoneNumberExist = await userModel.findOne(phoneNumber)

        if (checkIfPhoneNumberExist) {
            return { success: false, message: "Phone Number Already Exist" }
        }

        const otp = Math.floor(100000 + Math.random() * 900000)

        globalOTP.phoneNumber = otp;

        await client.messages.create(
            {
                body: `OTP is :- ${otp}. It Will Expires In 10 Minutes.`,
                from: process.env.TWILIO_PHONENUMBER,
                to: phoneNumber
            }
        );

        return { success: true, message: `OTP send To ${phoneNumber}` }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }

}

// Checking Otp For Creation Of User Account
const checkOtpForCreatingUser = async (phoneNumber, otp) => {
    try {

        if (!phoneNumber || !otp) {
            return { success: false, message: "Phone Number & OTP is Required" }
        }

        if (globalOTP.phoneNumber !== otp) {
            return { success: false, message: "Otp is in Valid or Maybe Expired" }
        }

        const createUser = await userModel.create({ phoneNumber })

        if (!createUser) {
            return { success: false, message: "Failed To Create User" }
        }

        const { refreshToken, accessToken } = await generateAccessAndRefreshToken(createUser._id)

        if (!refreshToken || accessToken) {
            return { success: false, message: "Failed To Generate Access And Refresh Token" }
        }

        delete globalOTP.phoneNumber;

        return { success: true, message: "User Created SuccessFully", refreshToken, accessToken }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

// Logging out User by deleting RefreshToken From Document
const LogoutUser = async (userId) => {
    try {

        const userData = await userModel.findByIdAndUpdate(
            userId,
            {
                $unset: {
                    refreshToken: 1
                }
            },
            {
                new: true
            }
        ).select("-refreshToken")

        if (!userData) {
            return { error: "User not found" }
        }

        return { message: "User logged out successfully" }
    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Functionality Of Adding Image To User's Profile
const addImageToUserProfile = async (image, userId) => {
    try {

        if (!image) {
            return { success: false, message: "Image File is Required." }
        }

        const { createReadStream, filename } = await image;

        const stream = createReadStream();

        const tempPath = `./uploads/${filename}`

        const writeStream = fs.createWriteStream(tempPath)

        await new Promise((resolve, reject) => {           
            stream.pipe(writeStream);
            writeStream.on("finish", resolve)
            writeStream.on("error", reject)
        })

        const imageUrl = await cloudinary.uploader.upload(tempPath)

        fs.unlinkSync(tempPath);

        const findUser = await userModel.findById(userId)

        if (!findUser) {
            return { success: false, message: "User Not Found" }
        }

        findUser.image = imageUrl.secure_url

        await findUser.save({ validateBeforeSave: false })

        return { success: true, message: "Image Uploaded SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Providing Functionality of Updating Profile Image To The Vendor
const updateImageOfUserProfile = async (image, userId) => {
    try {

        if (!image) {
            return { success: false, message: "Image File Is Required." }
        }

        const { createReadStream, filename } = await image;

        const stream = createReadStream();
        const tempPath = `./upload/${filename}`
        const writeStream = fs.createWriteStream(tempPath)

        await new Promise((resolve, reject) => {           
            stream.pipe(writeStream)
            writeStream.on("finish", resolve)
            writeStream.on("error", reject)
        })

        const imageUrl = await cloudinary.uploader.upload(tempPath)

        fs.unlinkSync(tempPath)

        const updateUserImage = await userModel.findByIdAndUpdate(userId, { $set: { image: imageUrl.secure_url } }, { new: true })

        if (!updateUserImage) {
            return { success: false, message: "Problem While Updating Image" }
        }

        return { success: true, message: "Image Updated SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

// Adding Username For Helping Vendors
const usernameAddingToUser = async(username,userId) => {
    try{

        if(!username)
        {
            return {success:false,message:"Username is Required."}
        }

        const findUser = await userModel.findById(userId)

        if(!findUser)
        {
            return {success:false,message:"User Not Found"}
        }

        findUser.username = username

        await findUser.save({validateBeforeSave})

        return {success:true,message:"Username Added SuccessFully"}

    }catch(error)
    {
        return {success:false,message:"Something Went Wrong",error:error.message}
    }
}

//Updating Username Functionality to User
const updatingUsernameOfUser = async (username,userId) => {
    try{
        if(!username)
        {
            return {success:false,message:"Username Is Required."}
        }

        const updateUsername = await userModel.findByIdAndUpdate(userId,{$set:{username:username}})

        if(!updateUsername)
        {
            return {success:false,message:"Failed To Update Username"}
        }

        return {success:true,message:"User Updated SuccessFully"}
        
    }catch(error)
    {
        return {success:false,message:"Something Went Wrong",error:error.message}
    }
}

export { sendOtpToUser, checkOtpForCreatingUser, LogoutUser,addImageToUserProfile,updateImageOfUserProfile,usernameAddingToUser,updatingUsernameOfUser};