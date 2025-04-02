import mongoose from "mongoose"

const PaymentSchema = new mongoose.Schema({

    itemPrice:{
        type:Number,
        required:true,        
    },
    paymentMethod:{
      type:String,
      required:true,
      enum:["Cash on Delivery","Upi","Debit Card","Credit Card"]
    },
    userId:{
        type:mongoose.Schema.Types.ObjectId,
        red:'User'
    },
    itemId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Item"
    } 

},{timestamps:true})

export default mongoose.model("PaymentMethod",PaymentSchema);