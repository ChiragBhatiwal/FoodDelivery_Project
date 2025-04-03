import bcrypt from "bcrypt"
import vendorModel from "../Models/Vendor.Model.js"
import uploadOnCloudinarySingle from "../Middlewares/CloudinarySingle.Middleware.js"
// import redis from 'ioredis'
import twilio from "twilio"

// const redisClient = new redis();
const globalOTP = {};
const client = twilio(process.env.TWILIO_SID, process.env.TWILIO_AUTHTOKEN);

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

//Sending OTP to User For Checking User Is Valid
const sendOTPToVendor = async (phoneNumber, userId) => {
    try {
        if (!phoneNumber) {
            return { status: false, message: "Phone Number Required" }
        }

        const checkIfUserExist = await vendorModel.findById(userId)

        if (!checkIfUserExist) {
            return { success: false, message: "User Not Found" }
        }

        const otp = Math.floor(100000 + Math.random() * 900000)

        globalOTP.userId = otp;

        await client.messages.create(
            {
                body: `OTP is :- ${otp}. It Will Expires In 10 Minutes.`,
                from: process.env.TWILIO_PHONENUMBER,
                to: phoneNumber
            }
        )

        return { success: true, message: "OTP send SuccessFully" }
    } catch (error) {
        return { success: false, message: "Something went Wrong while Sending OTP", error: error.message }
    }
}

//Adding Vendor Phone/Mobile Number To Their Accounts
const verifyAndAddPhoneNumberToVendorAccount = async (phoneNumber, otp, userId) => {
    try {
        if (!phoneNumber || !otp) {
            return { success: false, message: "Phone Number And OTP Required." }
        }

        const checkIfUserExist = await vendorModel.findById(userId);

        if (!checkIfUserExist) {
            return { success: false, message: "User Not Exist." }
        }

        if (globalOTP.userId !== otp) {
            return { success: false, message: "OTP is Either Expired or Invalid" }
        }

        checkIfUserExist.phoneNumber = phoneNumber;

        await checkIfUserExist.save({ validateBeforeSave: false })

        return { success: true, message: "Phone Number Saved SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong While Adding Phone Number", error: error.message }
    }
}

//Checking If Old Password is Right and User Remeber it Than only allowing user to Update Password 
const checkingIfOldPasswordIsCorrectAndUpdatePassword = async (oldPassword, newPassword, userId) => {
    try {

        const checkIfUserExist = await vendorModel.findById(userId)

        if (!checkIfUserExist) {
            return { success: false, message: "User Not Found" }
        }

        const isPasswordCorrect = checkIfUserExist.isPasswordCorrect(oldPassword)

        if (!isPasswordCorrect) {
            return { success: false, message: "Your Password is Wrong. Please Enter Correct Password." }
        }

        const hashPassword = await bcrypt.hash(newPassword, 10);

        const saveNewPassword = await vendorModel.findByIdAndUpdate(userId, { $set: { password: hashPassword } })

        if (!saveNewPassword) {
            return { success: false, message: "Failed To Update Password" }
        }

        return { success: true, message: "Password Changed SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }

}

// Sending OTP to Vendor For Updating Password If They Don't Remembered Password
const sendOTPForPasswordChange = async (userId) => {
    try {

        const findUser = await vendorModel.findById(userId)

        if (!findUser) {
            return { success: false, message: "User Not Found" }
        }

        const otp = Math.floor(100000 + Math.random() * 900000)

        globalOTP.userId = otp;

        await client.messages.create(
            {
                body: `OTP is :- ${otp}. It Will Expires In 10 Minutes.`,
                from: process.env.TWILIO_PHONENUMBER,
                to: findUser.phoneNumber
            }
        );

        return { success: true, message: "Otp Send SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Update Password if User didn't remember password by sending otp to mobile
const updatePasswordWithOTP = async (otp, newPassword, userId) => {
    try {

        if (!otp || !newPassword) {
            return { success: false, message: "Otp and New Password Is Required" }
        }

        if (globalOTP.userId !== otp) {
            return { success: false, message: "Either Otp is Invalid or Expires" }
        }

        const checkIfUserExist = await vendorModel.findById(userId)

        if (!checkIfUserExist) {
            return { success: false, message: "User Not Found" }
        }

        const hashPassword = await bcrypt.hash(newPassword, 10)

        const changePassword = await vendorModel.findByIdAndUpdate(userId, { $set: { password: hashPassword } })

        if (!changePassword) {
            return { success: false, message: "Something Went Wrong" }
        }

        return { success: true, message: "Password Change SuccessFully" }
    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Uploading Image Functionality If Vendor Want
const addImageToVendorProfile = async (image, userId) => {
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

        const findUser = await vendorModel.findById(userId)

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
const updateImageOfVendorProfile = async (image, userId) => {
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

        const updateVendorImage = await vendorModel.findByIdAndUpdate(userId, { $set: { image: imageUrl.secure_url } }, { new: true })

        if (!updateVendorImage) {
            return { success: false, message: "Problem While Updating Image" }
        }

        return { success: true, message: "Image Updated SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

export {
    updateImageOfVendorProfile,
    addImageToVendorProfile,
    sendOTPForPasswordChange,
    sendOTPToVendor,
    CreateSellerAccount,
    LoginSellerByEmailAndPassword,
    LogoutSellerAccount,
    UpdateVendorUsernameAndEmail,
    checkingIfOldPasswordIsCorrectAndUpdatePassword,
    updatePasswordWithOTP,
    verifyAndAddPhoneNumberToVendorAccount
};