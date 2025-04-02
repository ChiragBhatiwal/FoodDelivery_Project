import mongoose from "mongoose"

const ListOfItems = mongoose.Schema({
    itemName:{
        type:String,
        required:true
    },
    itemPrice:{
        type:String,
        required:true
    },
    itemImage:{
       type:String,
       required:true
    },       
    itemDescription:{
        type:String,
        required:true
    },
    restaurantId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Restaurant"
    },
    dishType:{
        type:String,
        required:true,
        enum:["Veg","Non-Veg"]
    },
    reviewItems:[
        {
            type:mongoose.Schema.Types.ObjectId,
            ref:"ItemsReview"
        },
    ]

},{timestamps:true})

export default mongoose.model("Item",ListOfItems);