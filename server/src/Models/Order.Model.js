import mongoose from "mongoose"

const orderModel = mongoose.Schema({
    itemId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Item",
        required: true
    },
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    quantity: {
        type: Number,
        default: 1,
        required: true,
        min: 1,
        max: 10
    },
    totalBill: {
        type: Number,
        required: true
    },
    itemPrice: {
        type: Number,
        required: true
    },
    restaurantId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Restaurant",
        required:true
    },
    paymentStatus:{
        type:String,
        enum:["Pending","Paid","Failed"],
        required:true,
        deafult:"Pending"
    }
}, { timestamps: true });

export default mongoose.model("Order", orderModel);