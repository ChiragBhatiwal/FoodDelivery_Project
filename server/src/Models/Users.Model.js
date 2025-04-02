import mongoose from "mongoose"
import jwt from "jsonwebtoken"
import bcrypt from "bcrypt"

const UserSchema = new mongoose.Schema(
    {
        username: {
            type: String,
            reuired:true,
            unique:true
        },
        phoneNumber: {
            type: String,
            required: true,
            unique: true
        },
        profileImage: {
            type: String,
        },
        accessToken: {
            type: String
        },
        refreshToken: {
            type: String,
            // required: true
        },
        cartItems: [
            {
                type: mongoose.Schema.Types.ObjectId,
                ref: "Cart"
            }
        ],
        address:
        {
            type: mongoose.Schema.Types.ObjectId,
            ref: "Address"
        }


    },
    { timestamps: true })

UserSchema.pre("save", async function (next) {
    if (!this.isModified("password")) return next()

    this.password = await bcrypt.hash(this.password, 10)
    return next()
})

UserSchema.methods.isPasswordCorrect = async function (password) {
    return await bcrypt.compare(password, this.password)
}

UserSchema.methods.generateAccessToken = function () {
    return jwt.sign({ _id: this._id, phoneNumber: this.phoneNumber }, process.env.SecretKey, { expiresIn: process.env.ACCESSTOKEN_EXPIRY })
}

UserSchema.methods.generateRefreshToken = function () {
    return jwt.sign({ _id: this._id }, process.env.SecretKey, { expiresIn: process.env.REFERSHTOKEN_EXPIRY })

}

export default mongoose.model("User", UserSchema)