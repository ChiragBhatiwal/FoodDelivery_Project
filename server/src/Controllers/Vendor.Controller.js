import bcrypt from "bcrypt"
import vendorModel from "../Models/Vendor.Model.js"
import uploadOnCloudinarySingle from "../Middlewares/CloudinarySingle.Middleware.js"
// import redis from 'ioredis'
import twilio from "twilio"

// const redisClient = new redis();
const globalOTP = {};
// const client = twilio(process.env.TWILIO_SID, process.env.TWILIO_AUTHTOKEN);     

//Generating Refresh Token And Access Token at the Time of Login Vendor
const generateAccessAndRefreshToken = async (vendorId) => {
    try {
        // Find the user by ID
        const vendor = await vendorModel.findById(vendorId).select("-password");

        if (!vendor) {
            return { success: false, message: "User not found" };
        }

        // Generate tokens
        const accessToken = vendor.generateAccessToken();
        const refreshToken = vendor.generateRefreshToken();


        // Set the refresh token
        vendor.refreshToken = refreshToken;

        // Save user with the new refresh token
        await vendor.save({ validateBeforeSave: false });

        return { accessToken, refreshToken };
    } catch (error) {
        return { success: false, message: "Error while generating tokens", error: error.message };
    }
};

//Creating Seller's Account With Info -> (Email,Phone Number,Username,Image(Optional))
const CreateSellerAccount = async ({ username, password, email }) => {
    try {
        if ([username, password, email].some((fields) => !fields || fields.trim() === "")) {
            return { success: false, message: "All Fields Required" }
        }

        const searchUser = await vendorModel.findOne({ $or: [{ username }, { email }] }).select("-password -refreshToken")

        if (searchUser) {
            return { success: false, message: "Username/Email are Already taken." }
        }

        const vendorCreated = await vendorModel.create({ username, password, email })

        const isUserSaved = await vendorModel.findById(vendorCreated._id).select("-password -refreshToken")

        if (!isUserSaved) {
            return { success: false, message: "Something Went Wrong" }
        }

        return { success: true, message: "User Created SuccessFully" };
    }
    catch (error) {
        return { sucess: false, message: "Something Went Wrong", error: error.message }
    }
}

//Loging Seller In To Account via Username/Email AND Password
const LoginSellerByEmailAndPassword = async ({ username, email, password }) => {
    try {

        if (!username && !email) {
            return { success: false, message: "Login Credential Needed" }
        }

        if (!password) {
            return { success: false, message: "Password is Required" }
        }

        let findUser = username
            ? await vendorModel.findOne({ username })
            : await vendorModel.findOne({ email });

        if (!findUser) {
            return { success: false, message: "No Record Found With This Username/Email." }
        }

        const passwordCorrect = await findUser.isPasswordCorrect(password)

        if (!passwordCorrect) {
            return { success: false, message: "Password is Incorrect." }
        }

        const { refreshToken, accessToken } = await generateAccessAndRefreshToken(findUser._id)

        return { success: true, refreshToken, accessToken };

    } catch (error) {
        return { success: false, message: `Something Went Wrong While Logging, ${error}` }
    }
}

//Updating Seller's Personal Info Not Restaurant's 
const UpdateVendorUsernameAndEmail = async (data, userId) => {
    try {

        if (!data || Object.keys(data).length === 0) {
            return { success: false, message: "No Update Credential Found" }
        }

        let updateFields = {};

        if (data.username) {
            updateFields.username = data.username;
        }

        if (data.email) {
            updateFields.email = data.email;
        }

        const updatedUser = await vendorModel.findByIdAndUpdate(
            userId,
            { $set: updateFields },
            { new: true, select: '-password' }
        );

        if (!updatedUser) {
            return { success: false, message: "Vendor Not Found" }
        }

        return { success: true, message: "Updated SuccessFully" }
    } catch (error) {
        return { success: false, message: "Something Went Wrong While Updating", error: error.message }
    }

}

//Logout Seller From The Account
const LogoutSellerAccount = async (userId) => {
    try {
        const user = await vendorModel.findByIdAndUpdate(
            userId,
            {
                $unset: {
                    refreshToken: 1
                }
            },
            {
                new: true
            }
        ).select("-password -refreshToken")

        if (!user) {
            return new Error("User not found")
        }
        return "User Logout Successfully"
    } catch (error) {
        return new Error("Error While Logging Out User", error);
    }
}

//Adding Vendor Phone/Mobile Number To Their Accounts
const addPhoneNumberToVendorAccount = async (phoneNumber, userId) => {
    try {
       const checkIfUserExist = await vendorModel.findById({userId})

        if(!checkIfUserExist)
        {
            return {success:false,message:"User Not Found"}
        }

        const sendOtpToVendor = 
       
    } catch (error) {
       return {success:false,message:"Something Went Wrong While Adding Phone Number",error:error.message}
    }
}

//Number can be updated if user have previous(Number which is stored in database) mobile number
const updatePhoneNumberInSellerAccount = async (req, resp) => {
    //TODO
}

//Sending OTP to User For Checking User Is Valid
const sendOTPToSeller = async (req, resp) => {
    const { phoneNum } = req.body;

    if (!phoneNum) {
        return resp.status(404).json({ "message": "Phone Number Is Required" });
    }

    const otp = Math.floor(100000 + Math.random() * 900000)

    if (otp) {
        globalOTP.otp = otp
    }

    await client.messages.create(
        {
            body: `OTP is :- ${otp}. It Will Expires In 10 Minutes.`,
            from: process.env.TWILIO_PHONENUMBER,
            to: phoneNum
        }
    )

    return resp.status(200).json({ "message": "OTP sent Successfully" });
}

//Checking If Old Password is Right and User Remeber it Than only allowing user to Update Password 
const checkingIfOldPasswordIsRightAndUpdatePassword = async (req, resp) => {
    const { oldPassword, newPassword } = req.body;

    if (!oldPassword) {
        return resp.status(404).json({ "messages": "Password Is Missing" })
    }

    const passwordCorrect = vendorModel.isPasswordCorrect(password)
    if (passwordCorrect) {

        const updatePassword = await vendorModel.findByIdAndUpdate(sellerId, { $set: { password: newPassword } }, { new: true });

        if (!updatePassword) {
            return resp.status(400).json({ "message": "Something Went Wrong While Upating Passowrd" })
        }

        return resp.status(200).json({ "message": "Password Updated SuccessFully" })

    } else {
        return resp.status(400).json({ "message": "Password is Wrong.Try Again" })
    }
}

//Update Password if User didn't remember password by sending otp to mobile
const updatePasswordOfSellerAccount = async (userId) => {

    const { otp, newPassword } = req.body;

    if (!otp && !newPassword) {
        return resp.status(404).json({ "message": "Otp or Password may bew Missing" });
    }

    const sellerId = req.vendor;

    if (!sellerId) {
        return resp.status(404).json({ "message": "Either token or Seller Missing" })
    }

    if (globalOTP.otp === otp) {
        const updatePassword = await vendorModel.findByIdAndUpdate(sellerId, { $set: { password: newPassword } }, { new: true });

        if (!updatePassword) {
            return resp.status(400).json({ "message": "Something Went Wrong While Upating Password" })
        }

        delete globalOTP.otp;

        return resp.status(200).json({ "message": "Password updated Successfully" })
    } else {
        delete globalOTP.otp;
        return resp.status(400).json({ "message": "Error Occured" })
    }
}

export {
    checkingIfOldPasswordIsRightAndUpdatePassword,
    sendOTPToSeller,
    CreateSellerAccount,
    LoginSellerByEmailAndPassword,
    LogoutSellerAccount,
    UpdateVendorUsernameAndEmail,
    updatePhoneNumberInSellerAccount,
    updatePasswordOfSellerAccount
};