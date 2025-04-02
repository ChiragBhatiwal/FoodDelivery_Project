import mongoose from "mongoose"

const restaurantSchema = mongoose.Schema({
    userId:{
      type:mongoose.Schema.Types.ObjectId,
      ref:"User"
    },
    restaurantName:{        
        type:String,
        required:true
    },
    addressRestaurant:{       
        type:mongoose.Schema.Types.ObjectId,
        ref:"Location",
        required:true,
    },
    typeOfRestaurant:{
       type:String,
       enum:["Veg","Non-Veg","Both-Dish"],
       required:true
    },
    isActive:{
        type:String,
        enum:["True","False"],
        required:true
    },
    // dishesAvailabe:[
    //   {
    //     type:mongoose.Schema.Types.ObjectId,
    //     ref:"Item"
    //   }
    // ],   
    restaurantReview:[
        {
            type:mongoose.Schema.Types.ObjectId,
            ref:"RestaurantReview"
        }
    ]
},{timestamps:true})

export default mongoose.model("Restaurant",restaurantSchema);