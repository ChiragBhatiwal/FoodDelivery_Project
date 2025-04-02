import mongoose from "mongoose"
import jwt from "jsonwebtoken"
import bcrypt from "bcrypt"

const vendorSchema = new mongoose.Schema({

    username:{
        type:String,
        required:true,
        unique:true
    },
    password:{
        type:String,
        required:true
    },
    profileImage:{
        type:String,
        default:null
    },
    email:{
        type:String,
        unique:true
    },
    refreshToken:{
        type:String
    },
    restaurantId:{
        type:String,
        default:null
    },
    phoneNumber:{
        type:String,
        unique:true,
        min:10,
        max:12
    }

},{timestamps:true});

vendorSchema.pre("save", async function (next) {
    if (!this.isModified("password")) return next()

    this.password = await bcrypt.hash(this.password, 10)   
    return next()
})

vendorSchema.methods.isPasswordCorrect = async function(password){
    return await bcrypt.compare(this.password,password);
}

vendorSchema.methods.generateAccessToken = function () {
    return jwt.sign({ _id: this._id, username: this.username }, process.env.SecretKey, { expiresIn: process.env.ACCESSTOKEN_EXPIRY })
}

vendorSchema.methods.generateRefreshToken = function () {
    return jwt.sign({ _id: this._id }, process.env.SecretKey, { expiresIn: process.env.REFERSHTOKEN_EXPIRY })

}

export default mongoose.model("Vendor",vendorSchema);