import mongoose from "mongoose"

const CartSchema = mongoose.Schema({
    userId:{
      type:mongoose.Schema.Types.ObjectId,
      ref:"User"
    },
    quantity:{
      type:Number,
      default:1,
      required:true
    },
    itemId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Item"
    }
},{timestamps:true})

export default mongoose.model("Cart",CartSchema);