import restaurantModel from "../Models/Restaurant.Model.js"
import UploadOnCloudinarySingle from "../Middlewares/CloudinarySingle.Middleware.js"
import vendorModel from "../Models/Vendor.Model.js"
import { getIoInstance } from "../sockets.js"
import fs from 'fs'
import { v2 as cloudinary } from 'cloudinary'
import locationModel from "../Models/LangLong.Model.js"

//Creating Restaurant for Seller - setting up necessary information
const CreateRestaurant = async (restaurantName, restaurantType, restaurantDescription, userId) => {
    try {
        if (!restaurantName || !restaurantType || !restaurantDescription) {
            return { success: false, message: "All Fields Are Required." }
        }

        const createRestaurant = await restaurantModel({
            restaurantName,
            restaurantDescription,
            typeOfRestaurant: restaurantType,
            userId
        });

        await createRestaurant.save();

        if (!createRestaurant) {
            return { success: false, message: "Failed To Create Restaurant" }
        }

        return { success: true, message: "Restaurant Created SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }

}

//If seller somehow want to disable(delete) restaurant (Note:- If want to fresh start)
const DeleteRestaurant = async (restaurantId) => {
    try {
        if (!restaurantId) {
            return { success: false, message: "Restaurant Id Is Required" }
        }

        const deleteRestaurant = await restaurantModel.findByIdAndDelete(restaurantId._id);

        if (!deleteRestaurant) {
            return { success: false, message: "Failed To Delete Restaurant" }
        }

        return { success: false, message: "Restaurant Delete SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Update It's basic Info like Image and Name (Note:- Not included address info)
const UpdateRestaurantNameAndType = async (data, restaurantId) => {
    try {
        if (!restaurantId) {
            return console.log("restaurant id is missing");
        }

        if (!data) {
            return { success: false, message: "Fields Required For Updating." }
        }

        const updateField = {};

        if (data.restaurantName) {
            updateField.restaurantName = data.restaurantName;
        }

        if (data.restaurantType) {
            updateField.restaurantType = data.restaurantType;
        }

        if (data.restaurantDescription) {
            updateField.restaurantDescription = data.restaurantDescription;
        }

        const updateRestaurant = await restaurantModel.findByIdAndUpdate(
            restaurantId._id,
            {
                $set: updateField
            },
            {
                new: true
            }
        );

        if (!updateRestaurant) {
            return { success: false, message: "Failed To Update Restaurant" }
        }

        return { success: true, message: "Restaurant Data Updated SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

// Adding Profile Image To Restaurant If Vendor Needs Any
const addRestaurantImage = async (file, restaurantId) => {
    try {

        if (!file) {
            return { success: false, message: "Image File Is Required." }
        }

        const { createReadStream, fileName } = await file;

        const readStream = createReadStream();

        const tempPath = `./uploads/${fileName}`

        const writeStream = fs.createWriteStream(tempPath)

        await new Promise((reject, resolve) => {
            readStream.pipe(writeStream)
            writeStream.on("finish", resolve)
            writeStream.on("error", reject)
        })

        const imageUrl = await cloudinary.uploader.upload(tempPath)

        fs.unlinkSync(tempPath)

        const findRestaurant = await restaurantModel.findById(restaurantId)

        if (!findRestaurant) {
            return { success: false, message: "Restaurant Does Not Exist" }
        }

        findRestaurant.image = imageUrl.secure_url

        await findRestaurant.save();

        return { success: true, message: "Image Added SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

// Functionality Of Updating Profile Image Of A Restaurant
const updateRestaurantImage = async (file, restaurantId) => {
    try {

        if (!file) {
            return { success: false, message: "Image File Is Required" }
        }

        const { createReadStream, fileName } = await file;

        const readStream = createReadStream();

        const tempPath = `./uploads/${fileName}`

        const writeStream = fs.createWriteStream(tempPath)

        await new Promise((resolve, reject) => {
            readStream.pipe(writeStream)
            writeStream.on("finish", resolve)
            writeStream.on("error", reject)
        })

        const imageUrl = await cloudinary.uploader.upload(tempPath)

        fs.unlinkSync(tempPath)

        const updateImageInRestaurant = await restaurantModel.findByIdAndUpdate(restaurantId,{$set:{image:imageUrl_secure_url}})

        if(!updateImageInRestaurant)
        {
            return {success:false,message:"Failed To Upload Image"}
        }

        return {success:true,message:"Image Updated SuccessFully"}

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

// Updating Restaurant's Active Status So If Seller Want To Close(or don't want to take orders) Restaurant 
// It Didn't Shows To Any Buyer
const updateRestaurantActiveStatus = async (restaurantId, status) => {
    try {
        const findRestaurant = await restaurantModel.findById(restaurantId);

        if (!findRestaurant) {
            return { success: false, message: "Restaurant Not Found" }
        }

        const updatedRestaurant = await restaurantModel.findByIdAndUpdate(restaurantId, { $set: { isActive: status } }, { new: true });

        if (!updatedRestaurant) {
            return { success: false, message: "Failed To Change Active Status Of Restaurant" }
        }

        const io = getIoInstance();

        io.emit("activeStatusChanged", {
            restaurantId: updatedRestaurant._id,
            isActive: updatedRestaurant.isActive,
        });

        return { success: true, message: "Status Changed", updatedRestaurant }

    } catch (error) {
        return { success: false, message: "Sommething Went Wrong", error: error.message }
    }

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

export {addRestaurantImage,updateRestaurantImage,searchNearestRestaurantForBuyer, CreateRestaurant, DeleteRestaurant, UpdateRestaurantNameAndType, getRestaurantDetails, updateRestaurantActiveStatus };