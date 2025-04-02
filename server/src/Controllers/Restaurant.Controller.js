import restaurantModel from "../Models/Restaurant.Model.js"
import UploadOnCloudinarySingle from "../Middlewares/CloudinarySingle.Middleware.js"
import cartModel from "../Models/Cart.Model.js"
import userModel from "../Models/Vendor.Model.js"
import { getIoInstance } from "../sockets.js"
import locationModel from "../Models/LangLong.Model.js"

//Creating Restaurant for Seller - setting up necessary information
const CreateRestaurant = async (req, resp) => {

    const { restaurantName, restaurantDescription, restaurantType } = req.body;

    if ([restaurantName, restaurantDescription, restaurantType].some((value) => value?.trim() === "")) {
        return console.log("Name and Address of Restaurant required");
    }

    const user = req.vendor;

    if (!user) {
        return console.log("User Not Found");
    }

    const createRestaurant = await restaurantModel({
        restaurantName: restaurantName,
        restaurantDescription: restaurantDescription,
        typeOfRestaurant: restaurantType,
        userId: user._id,
    });

    await createRestaurant.save();

    if (!createRestaurant) {
        return console.log("Something went Wrong While creating Restaurant");
    }

    const vendor = await userModel.findByIdAndUpdate(user._id, { $set: { restaurantId: createRestaurant._id } }, { upsert: true, new: true });

    if (!vendor) {
        return resp.status(404).json({ "message": "Interanl Error" });
    }

    resp.status(200)
        .json(createRestaurant);
}

//If seller somehow want to disable(delete) restaurant (Note:- If want to fresh start)
const DeleteRestaurant = async (req, resp) => {
    const restaurantId = req.params;

    console.log(restaurantId);
    if (!restaurantId) {
        return console.log("Didn't get restaurant Id");
    }

    const deleteRestaurant = await restaurantModel.findByIdAndDelete(restaurantId._id);

    if (!deleteRestaurant) {
        return console.log("Error while Deleting Restaurant Deleted ");
    }

    resp.status(200)
        .send("Restaurant Deleted Successfully");
}

//Update It's basic Info like Image and Name (Note:- Not included address info)
const UpdateRestaurant = async (req, resp) => {
    const restaurantId = req.params;

    if (!restaurantId) {
        return console.log("restaurant id is missing");
    }

    const data = req.body;

    console.log(data)

    if (!data) {
        return resp.status(404).json({ "message": "Credentials Required FOr Updation" });
    }

    const imagePath = req.files?.path;  // check if needed;

    const updateField = {};

    if (data.restaurantName) {
        updateField.restaurantName = data.restaurantName;
    }

    if (imagePath) {
        const imageUrl = await UploadOnCloudinarySingle(imagePath);

        if (!imageUrl) {
            return console.log("Error in generating image urls");
        }

        updateField.imageOfRestaurant = imageUrl.url;
    }

    try {

        const UpdateRestaurant = await restaurantModel.findByIdAndUpdate(
            restaurantId._id,
            {
                $set: updateField
            },
            {
                new: true
            }
        );
        resp.status(200)
            .json(UpdateRestaurant);
    } catch (error) {
        resp.status(400)
            .send("Error occure in api updating");
    }

}

//Showing Address Details if Needed
const getRestaurantDetails = async (req, resp) => {

    const value = req.body;

    if (!value) {
        return console.log("Restaurant Id Required");
    }

    const restaurantDetails = await restaurantModel.aggregate([
        {
            $lookup: {
                from: "items",
                localField: "_id",
                foreignField: "restaurantId",
                as: "RestaurantItems"
            }
        },

    ]);

    if (!restaurantModel) {
        return console.log("Can't find Restaurant with Items");
    }

    resp.json(data = { restaurantDetails });

}

// Updating Restaurant's Active Status So If Seller Want To Close(or don't want to take orders) Restaurant 
// It Didn't Shows To Any Buyer
const updateRestaurantActiveStatus = async (restaurantId, status) => {
    const findRestaurant = await restaurantModel.findById(restaurantId);

    if (!findRestaurant) {
        return "Restaurant Not Found"
    }

    const updatedRestaurant = await restaurantModel.findByIdAndUpdate(restaurantId, { $set: { isActive: status } }, { new: true });

    if (!updatedRestaurant) {
        return 'Something Went Wrong'
    }

    const io = getIoInstance();

    io.emit("activeStatusChanged", {
        restaurantId: updatedRestaurant._id,
        isActive: updatedRestaurant.isActive,
    });

    return updateStatus

}
//Searching Restaurant For User Within 5km Of Radius And Showing To User
const searchNearestRestaurantForBuyer = async (req, resp) => {

    const { latitude, longitude } = req.body;

    if (!latitude, longitude) {
        return resp.status(404).json({ "message": "Latitude and Logitude is Required" })
    }

    const findNearestRestaurants = await locationModel.aggregate([
        {
            $geoNear: {
                near: { type: "Point", coordinates: [parseFloat(longitude), parseFloat(latitude)] },
                distanceField: "distance",
                maxDistance: 5000, // 5km in meters
                spherical: true,
                query: { isActive: true },
            }
        }
    ]);

    if (!findNearestRestaurants.length) {
        return resp.status(404).json({ "message": "No Restaurant Found NearBy" })
    }

    return resp.status(200).json(findNearestRestaurants);
}

export { searchNearestRestaurantForBuyer, CreateRestaurant, DeleteRestaurant, UpdateRestaurant, getRestaurantDetails, updateRestaurantActiveStatus };