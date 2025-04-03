import { v2 as cloudinary } from "cloudinary"
import itemModel from "../Models/Item.Model.js"
import restaurantModel from "../Models/Restaurant.Model.js"

//creating item for restaurant through graphql payload and uploading image to cloudinary
const createItemWithRestaurant = async ({ itemName, itemPrice, itemDescription, restaurantId, itemImage, dishType }) => {
    try {
        const imageUrl = itemImage;  //considering itemImage as a string coming from frontend 

        //If not than handling image object/binary format here and converting it to String
        if (typeof data.itemImage === "object" && data.itemImage.createReadStream) {
            const { createReadStream } = await data.itemImage;
            const stream = createReadStream();

            const uploadResult = await new Promise((resolve, reject) => {
                const uploadStream = cloudinary.uploader.upload_stream((error, result) => {
                    if (error) reject(error);
                    resolve(result);
                });
                stream.pipe(uploadStream);
            });

            imageUrl = uploadResult.secure_url; // Store Cloudinary URL
        }

        const itemCreation = await itemModel.create({
            itemName,
            itemPrice,
            itemDescription,
            restaurantId,
            dishType,
            itemImage: imageUrl
        })

        if (!itemCreation) {
            return {success:false,message:"Failed To Create Item"}
        }

        return {success:true,message:"Item Created SuccessFully"}

    } catch (error) {
        return {success:false,message:"Something Went Wrong",error:error.message}
    }
}

//Updating item details and images through graphql payload
const updateItemInformations = async (itemId, data) => {
    try {
        const findItem = await itemModel.findById(itemId);

        if (!findItem) {
            return {success:false,message:"Item Not Found"}
        }

        const updatedFields = {};

        if (data.itemName !== undefined) updatedFields.itemName = data.itemName;
        if (data.itemPrice !== undefined) updatedFields.itemPrice = data.itemPrice;
        if (data.itemDescription !== undefined) updatedFields.itemDescription = data.itemDescription;

        if (data.itemImage !== undefined) {
            if (typeof data.itemImage === "string" && data.itemImage.startsWith("http")) {
                // If it's already a URL, keep it as is
                updatedFields.itemImage = data.itemImage;
            } else {
                // It's a new file upload, process it with Cloudinary
                const { createReadStream } = await data.itemImage;
                const stream = createReadStream();
                const uploadResult = await new Promise((resolve, reject) => {
                    const uploadStream = cloudinary.uploader.upload_stream((error, result) => {
                        if (error) reject(error);
                        resolve(result);
                    });
                    stream.pipe(uploadStream);
                });
                updatedFields.itemImage = uploadResult.secure_url;
            }
        }

        await itemModel.findByIdAndUpdate(itemId, { $set: updatedFields }, { new: true });

        return {success:true,message:"Item Updated SuccessFully"}

    } catch (error) {
       return {success:false,message:"Something Went Wrong",error:error.message}
    }
}

//Deleting Item With Item Id
const deleteItem = async (itemId) => {
    try {
        const deleteItem = await itemModel.findByIdAndDelete(itemId);

        if (!deleteItem) {
            return {success:false,message:"Failed To Delete Item"}
        }

        return {success:true,message:"Item Deleted SuccessFully"}

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

// Fetching items from search bar and searching in the items collection
const searchingItemFromSearchBar = async (value) => {
    try {

        if (!value) {
            return { success: false, message: "Please Provide Searching Value" }
        }

        const searchInItems = await restaurantModel.aggregate(
            [
                {
                    $lookup: {
                        from: 'items',
                        localField: '_id',
                        foreignField: 'restaurantId',
                        as: 'RestaurantWithItems'
                    }
                },
                {
                    $unwind: {
                        path: '$RestaurantWithItems',
                        preserveNullAndEmptyArrays: false
                    }
                },
                {
                    $match: {
                        'RestaurantWithItems.itemName': { $regex: new RegExp(value, 'i') }
                    }
                }

            ]
        );

        if (!searchInItems || searchInItems.length === 0) {
            return { success: false, message: "OOPS...No Item Found" }
        }

        return { success: true, message: "Items Found", searchInItems }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
};

// Fetching all the items of a particular restaurant
const getItemsWithRestaurantId = async (restaurantId) => {
    try {

        const findItems = await itemModel.find({ restaurantId: restaurantId._id });

        if (!findItems) {
            return { success: false, message: "No Item Found" }
        }

        return { success: true, message: "All Items Fetched", findItems }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

export { createItemWithRestaurant, deleteItem, updateItemInformations, searchingItemFromSearchBar, getItemsWithRestaurantId };