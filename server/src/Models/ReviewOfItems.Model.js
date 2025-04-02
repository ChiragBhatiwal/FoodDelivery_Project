import mongoose from "mongoose"
import RestaurantModel from "../Models/Restaurant.Model.js"

const ReviewOfItems = mongoose.Schema({
    itemId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Item"
    },
    RestaurantId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Restaurant"
    },
    review:[
        {
            userId:{
                type:mongoose.Schema.Types.ObjectId,
                ref:"User"
            },
            reviewStars:{
                type:Number,
                required:true,
                default:0
            }
        }
    ]
},{timestamps:true})

export default mongoose.model("ItemsReview",ReviewOfItems);