import mongoose from "mongoose"

const DatabaseConnection = async ()=> {
    try {
        
        await mongoose.connect("mongodb://localhost:27017/FoodDelivery");

    } catch (error) {
              throw new Error("Error while connecting to server",error);
    }
}

export default DatabaseConnection;