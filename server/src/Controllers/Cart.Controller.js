import cartModel from "../Models/Cart.Model.js"
import userModel from "../Models/Users.Model.js"

//Adding All The Items Buyer Want in Cart
const PushItemInCart = async (itemId, quantity, user) => {
    try {

        console.log(user);

        const pushItemInCart = await cartModel.create({ itemId: itemId._id, quantity, userId: user._id });

        if (!pushItemInCart) {
            return console.log("Something went wrong in pushing item to cart");
        }

        return "Item Saved In Cart";
    } catch (error) {
        console.log(error);
        return "Something went wrong while pushing item to cart";
    }
}

//Deleting Items From Cart
const RemoveItemInCart = async (itemId, user) => {
    try {
        if (!itemId) {
            return console.log("No item Found");
        }

        if (!user) {
            return console.log("can't find user");
        }

        const DeleteItemFromCart = await cartModel.findByIdAndDelete(getCartItemId._id);

        if (!DeleteItemFromCart) {
            return console.log("Something Went Wrong While Deleting From Cart");
        }

        const removeFromUserCart = await userModel.findByIdAndUpdate(
            user._id,
            {
                $pull: { cartItems: DeleteItemFromCart._id }
            }, {
            new: true
        }
        );

        if (!removeFromUserCart) {
            return console.log("unabled to Removed from user carts");
        }

        return "Item Removed From Cart";
    } catch (error) {
        console.log(error);
        return "Something went wrong while removing item from cart";
    }
}

//Fetching All The Items In Cart
const findItemsInCart = async (user) => {
    if (!user) {
        return console.log("User Id is required");
    }

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

    return finditems;
}

export { PushItemInCart, RemoveItemInCart, findItemsInCart };