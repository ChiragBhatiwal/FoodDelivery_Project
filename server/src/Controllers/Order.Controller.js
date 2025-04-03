import orderModel from "../Models/Order.Model.js"
import userModel from "../Models/Users.Model.js"
import itemModel from "../Models/Item.Model.js"
import restaurantModel from "../Models/Restaurant.Model.js"
import { ObjectId } from 'mongodb'
// import { getUserSocket } from "../sockets"


//Placing Order For the Buyer With All Necessary Details
const buyerPlacingOrder = async (itemId, restaurantId, userId, quantity) => {
    try{
    const itemDetails = await itemModel.findById(itemId);

    if (!itemDetails) {
        return {success:false,message:"Item Not Found"}
    }

    const itemPrice = itemDetails.itemPrice;

    if (quantity == null) {
        return {success:false,message:"Quantity Can't Be Null"}
    }

    const totalBill = quantity * itemPrice;

    const orderPlacing = await orderModel.create({ itemDetails: itemId._id, addressId, restaurantId, quantity, userId: userId._id, itemPrice, totalBill });

    if (!orderPlacing) {
        return {success:false,message:"Failed To Place Order"}
    }

    return {success:true,message:"Order Placed SuccessFully"}

}catch(error)
{
    return {success:false,message:"Something Went Wrong",error:error.message}
}
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

//Fetching All The Orders Buyer Bought In Past
const getBuyAgainOrders = async (user) => {
    try {
        const result = await orderModel.aggregate([
            {
                $match: {
                    userId: new ObjectId(user),
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
            return { success: false, message: "No Orders Found" }
        }


        return { success: true, message: "Buy-Again Orders Fetched", result }
    } catch (error) {
        return {
            success: false, meessage: "Something Went Wrong", error: error.message
        }
    }
}

//Fetching All The Orders Which Buyer Orders And In Processing Not Yet Delivered
const getCurrentOrderedItemList = async (user) => {
    try{
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
        return {success:false,message:"Failed To Fetch Current Orders"}
    }

    return {success:true,message:"Current Orders Fetched",result}

}catch(error)
{
    return {success:false,message:"Something Went Wrong",error:error.message}
}

}
export { buyerPlacingOrder, getBuyAgainOrders, getCurrentOrderedItemList }