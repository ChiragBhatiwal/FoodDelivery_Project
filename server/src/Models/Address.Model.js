import mongoose from "mongoose"

const AddressSchema = mongoose.Schema({
    addressLine:{
        type:String,
        required:true
    },
    landmark:{
        type:String,
    },
    cityName:{
        type:String,
        required:true
    },
    postalCode:{
        type:Number,
        required:true
    },
    stateName:{
        type:String,
        required:true
    }
},{timestamps:true})

export default mongoose.model("Address",AddressSchema);