import mongoose from "mongoose"

const OrderHistorySchema = mongoose.Schema({
    status: {
        type: String,
        default: "Pending",
        required: true,
        enum: ["Pending", "Preparing", "Out-For-Delivery", "Delivered", "Rejected"]
    },
    orderId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Order",
        required: true,
    },
}, { timestamps: true })

export default mongoose.model("OrderHistory", OrderHistorySchema);