import {PushItemInCart,RemoveItemInCart,findItemsInCart} from "../../Controllers/Cart.Controller.js"

const queries = {
    getAllItemOfCart:async(_,{user})=>{
        if(!user){
            throw new Error("User not authenticated");
        }
        try{
            const itemsInCart = await findItemsInCart(user);
            return itemsInCart;
        }catch(error){
            throw new Error(`Error fetching cart items: ${error.message}`);
        }
     }
}

const mutations = {
    addItemInCart:async(_,{itemId,quantity=1},user)=>{
        if(!user){
            throw new Error("User not authenticated");
        }
        if(!itemId){
            throw new Error("Item ID and quantity are required");
        }
        try{
          await PushItemInCart(itemId,quantity,user);
        }catch(error){
            throw new Error(`Error adding item to cart: ${error.message}`);
        }
    },
    removeItemFromCart:async(_,{itemId},user)=>{
        if(!user){
            throw new Error("User not authenticated");
        }
        if(!itemId){
            throw new Error("Item ID is required");
        }
        try{
            await RemoveItemInCart(itemId,user);
        }catch(error){
            throw new Error(`Error deleting item from cart: ${error.message}`);
        }
    }
}

export const resolvers = {queries,mutations}