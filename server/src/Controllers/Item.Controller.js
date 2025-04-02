import { v2 as cloudinary } from "cloudinary"
import itemModel from "../Models/Item.Model.js"
import restaurantModel from "../Models/Restaurant.Model.js"

//creating item for restaurant through graphql payload and uploading image to cloudinary
const createItem = async ({ itemName, itemPrice, itemDescription, restaurantId, itemImage, dishType }) => {
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
            return console.log("Error in creating item")
        }

        return "Item Created Successfully";
    } catch (error) {
        console.error("Error creating item:", error);
    }
}

//Updating item details and images through graphql payload
const updateItem = async (itemId, data) => {
    try {
        const findItem = await itemModel.findById(itemId);

        if (!findItem) {
            return new Error("Item not found");
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

        return "Item Updated Successfully";
    } catch (error) {
        console.error("Error updating item:", error);
        return new Error("Internal Server Error");
    }
}

const deleteItem = async (itemId) => {

    if (!itemId) {
        return console.log("item id is required");
    }

    const deleteItem = await itemModel.findByIdAndDelete(itemId);

    if (!deleteItem) {
        return console.log("Error in Deleting Item");
    }

    const deleteItemFromRestaurant = await restaurantModel.findByIdAndUpdate(
        restaurantId,
        {
            $pull: { dishesAvailabe: deleteItem._id }
        },
        {
            new: true
        }
    );

    if (!deleteItemFromRestaurant) {
        return console.log("Error while deleting from restaurant fields");
    }

    return "Item Deleted Successfully";
}

// Fetching items from search bar and searching in the items collection
const searchingItemFromSearchBar = async (value) => {
    try {
        const { value } = req.body;
        if (!value) {
            return res.status(400).json({ message: "Value required for searching in the field" });
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
            return res.status(404).json({ message: "Oops! Nothing in the List" });
        }

        return searchInItems;

    } catch (error) {
        console.error("Error occurred while searching items:", error);
        res.status(500).json({ message: "Internal Server Error" });
    }
};

// Fetching all the items of a particular restaurant
const getItemsWithRestaurantId = async (restaurantId) => {
    try{
    if (!restaurantId) {
        return resp.status(404).json("User id Is Required.");
    }

    const findItems = await itemModel.find({ restaurantId: restaurantId._id });

    if (!findItems) {
        return resp.status(404).json("No Items Found");
    }

    return findItems
}catch(error){
    console.error("Error occurred while fetching items:", error);
    return "Internal Server Error"
    }
}

export { createItem, deleteItem, updateItem, searchingItemFromSearchBar, getItemsWithRestaurantId };