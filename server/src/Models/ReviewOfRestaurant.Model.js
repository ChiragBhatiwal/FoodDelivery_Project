import mongoose from "mongoose"

const ReviewOfRestaurant = mongoose.Schema({

    restaurantId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Restaurant"
    },
    review:[{
        userid:{
            type:mongoose.Schema.Types.ObjectId,
            ref:"user"
        }
    }]

},{timestamps:true})

export default mongoose.model("RestaurantReview",ReviewOfRestaurant);