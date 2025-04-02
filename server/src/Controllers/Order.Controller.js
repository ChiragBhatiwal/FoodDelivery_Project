import orderModel from "../Models/Order.Model.js"
import userModel from "../Models/Users.Model.js"
import itemModel from "../Models/Item.Model.js"
import restaurantModel from "../Models/Restaurant.Model.js"
import { ObjectId } from 'mongodb'
// import { getUserSocket } from "../sockets"


//Placing Order For the Buyer With All Necessary Details
const buyerPlacingOrder = async (itemId, restaurantId, userId, quantity) => {

    if (!itemId) {
        return "Item Id is Required"
    }

    const itemDetails = await itemModel.findById(itemId);

    if (!itemDetails) {
        return "Item Is'nt Fetched"
    }

    const itemPrice = itemDetails.itemPrice;

    if (quantity == null) {
        return "Qunatity must required"
    }

    const totalBill = quantity * itemPrice;

    const orderPlacing = await orderModel.create({ itemDetails: itemId._id, addressId, restaurantId, quantity, userId: userId._id, itemPrice, totalBill });

    if (!orderPlacing) {
        return resp.status(404).send("Something went wrong while submitting data");
    }

    return "Order Placed Successfully";
}

//Fetching Product Details To Buyer With Hanlding Real Time Qunatity Updation 
const productDeatilsForBuyer = async (req, resp) => {

    const user = req.user;

    if (!user) {
        return resp.status(404).send("User Id is required");
    }

    const itemId = req.params;

    if (!itemId) {
        return resp.status(404).send("Item Id is Required");
    }

    const userSocket = getUserSocket(user._id);

    if (!userSocket) {
        return resp.status(404).send("Socket Id is required");
    }

    const itemDetails = await itemModel.aggregate([
        {
            $lookup: {
                from: "restaurants",
                foreignField: "_id",
                localField: "restaurantId",
                as: "RestaurantDetails"
            }
        },
        {
            $match: { _id: new ObjectId(itemId._id) }
        },
        {
            $project: {
                itemName: 1, itemPrice: 1, itemDescription: 1, dishType: 1, "RestaurantDetails.restaurantName": 1, restaurantId: 1, "RestaurantDetails.typeOfRestaurant": 1
            }
        }
    ]);

    const userDetails = await userModel.aggregate([
        {
            $lookup: {
                from: "locations",
                foreignField: "_id",
                localField: "address",
                as: "UserAddress"
            }
        },
        {
            $project: {
                "UserAddress._id": 1, "UserAddress.Address": 1, username: 1,
                phoneNumber: 1, "UserAddress.Latitude": 1, "UserAddress.Longitude": 1
            }

        }

    ]);

    const originalPrice = itemDetails[0].itemPrice;

    userSocket.on("quantity-updated", (quantity) => {

        const reCalculatedPrice = originalPrice * quantity

        if (reCalculatedPrice == 0) {
            userSocket.emit("updated-amount")
        } else {
            userSocket.emit("updated-amount", reCalculatedPrice);
        }
    });

    resp.status(200).json({ itemDetails, userDetails });
}

const orderForDelivery = async (req, resp) => {

    const { restaurantId } = req.body;

    if (!restaurantId) {
        return resp.status(404).send("Restaurant Id is required");
    }

    const findOrders = await orderModel.aggregate([
        {
            $match: { status: { $in: ["Pending", "Preparing"] } }
        },
        {
            $lookup: {
                from: "items",
                localField: "itemDetails",
                foreignField: "_id",
                as: "Items"
            }
        },
        {
            $unwind: "$Items"  // Unwind the "Items" array to flatten the structure
        },
        {
            $lookup: {
                from: "restaurants",
                localField: "restaurantId",
                foreignField: "_id",
                as: "Restaurant"
            }
        },
        {
            $unwind: "$Restaurant"  // Unwind the "Restaurant" array to flatten the structure
        },
        {
            $lookup: {
                from: "locations",
                localField: "addressId",
                foreignField: "_id",
                as: "Address"
            }
        },
        {
            $unwind: "$Address"  // Unwind the "Address" array to flatten the structure
        },
        {
            $lookup: {
                from: "users",
                localField: "userId",
                foreignField: "_id",
                as: "User"
            }
        },
        {
            $unwind: "$User"
        },
        {
            $match: { restaurantId: new ObjectId(restaurantId) }
        },
        {
            $project: {
                status: 1,
                orderId: "$_id",  // Project the orderId field (which is the _id in the order collection)
                _id: 0,  // Exclude the _id field from the output
                "Items._id": 1,
                "Items.itemName": 1,
                itemPrice: 1,
                totalBill: 1,
                quantity: 1,
                "Items.dishType": 1,
                "Items.itemImage": 1,
                "Restaurant.restaurantName": 1,
                "Restaurant.typeOfRestaurant": 1,
                "Restaurant._id": 1,
                "Address.Address": 1,
                "User.username": 1,
                "User.phoneNumber": 1
            }
        }
    ]);


    if (!findOrders) {
        return resp.status(404).send("Can't Fetched Successfully");
    }

    return findOrders[0];
}

//Fetching All The Orders Buyer Bought In Past
const getBuyAgainOrders = async (user) => {
    if (!user) {
        return "Token not Found"
    }


    const result = await orderModel.aggregate([
        {
            $match: {
                userId: new ObjectId(user._id),
                status: { $in: ["Delivered"] }
            }
        },
        {
            $lookup: {
                from: "products",
                foreignField: "_id",
                localField: "productId",
                as: "ProductDetails"
            }
        },
        {
            $addFields: {
                productDetails: { $arrayElemAt: ["$ProductDetails", 0] } // Extract first product object
            }
        },
        {
            $project: {
                productId: "$productDetails._id",
                productName: "$productDetails.productName",
                productImage: "$productDetails.productImage",
                productPrice: "$productDetails.productPrice",
                totalBill: 1,
                quantity: 1,
                paymentType: 1,
                status: 1,
                createdAt: 1
            }
        }
    ]);

    if (!result) {
        return "Not Found Any Orders"; 
    }


    return result;
}

//Fetching All The Orders Which Buyer Orders And In Processing Not Yet Delivered
const getCurrentOrderedItemList = async (user) => {


    if (!user) {
        return resp.send("Token not Found");
    }


    const result = await orderModel.aggregate([
        {
            $match: {
                userId: new ObjectId(user._id),
                status: { $in: ["Pending", "Confirmed", "Packed", "Shipped"] }
            }
        },
        {
            $lookup: {
                from: "products",
                foreignField: "_id",
                localField: "productId",
                as: "ProductDetails"
            }
        },
        {
            $addFields: {
                productDetails: { $arrayElemAt: ["$ProductDetails", 0] } // Extract first product object
            }
        },
        {
            $project: {
                productId: "$productDetails._id",
                productName: "$productDetails.productName",
                productImage: "$productDetails.productImage",
                productPrice: "$productDetails.productPrice",
                totalBill: 1,
                quantity: 1,
                paymentType: 1,
                status: 1,
                createdAt: 1
            }
        }
    ]);

    if (!result) {
        return "Error Something Went Wrong"
    }

    return  result 
    
}
export { buyerPlacingOrder,getBuyAgainOrders,getCurrentOrderedItemList}