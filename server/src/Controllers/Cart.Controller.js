import cartModel from "../Models/Cart.Model.js"
import userModel from "../Models/Users.Model.js"

//Adding All The Items Buyer Want in Cart
const PushItemInCart = async (itemId, quantity, user) => {
    try {

        console.log(user);

        const pushItemInCart = await cartModel.create({ itemId: itemId._id, quantity, userId: user._id });

        if (!pushItemInCart) {
            return { success: false, message: "Failed To Add Item In Cart" }
        }

        return { success: true, message: "Item Added To Cart SuccessFully" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Deleting Items From Cart
const RemoveItemInCart = async (itemId, user) => {
    try {

        const DeleteItemFromCart = await cartModel.findByIdAndDelete(getCartItemId._id);

        if (!DeleteItemFromCart) {
            return { success: false, message: "Failed To Remove Item From Cart" }
        }


        return { success: true, message: "Item Removed From Cart" }

    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

//Fetching All The Items In Cart
const findItemsInCart = async (user) => {
    try {

        const finditems = await userModel.aggregate([
            {
                $match: { userId: user._id }
            },
            {
                $lookup: {
                    from: "carts",
                    localField: "cartItems",
                    foreignField: "_id",
                    as: "carts"
                }
            },
            {
                $lookup: {
                    from: "items",
                    localField: "carts.itemId",
                    foreignField: "_id",
                    as: "items"
                }
            },
            {
                $project: {
                    _id: 0,
                    items: "$items"
                }
            }
        ]);

        return {success:true,message:"Item Founds",findItems}
        
    } catch (error) {
        return { success: false, message: "Something Went Wrong", error: error.message }
    }
}

export { PushItemInCart, RemoveItemInCart, findItemsInCart };