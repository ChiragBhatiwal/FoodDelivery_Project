import { buyerPlacingOrder,getBuyAgainOrders,getCurrentOrderedItemList } from "../../Controllers/Order.Controller.js"

const queries = {
    getOrderByOrderId: async () => { },
    getOrdersByUserId: async () => { },
    getOrdersByRestaurantId: async () => { },
    getOrdersForBuyAgain: async (_,any,user) => { 
        if (!user) {
            throw new Error('Unauthorized')
        }
        try {
            return await getBuyAgainOrders(user.userId);
        } catch (err) {
            console.log(err)
            throw new Error('Error fetching orders for buy again')
        }
    },
    getCurrentOrders: async (_,any,user) => {
        if (!user) {
            throw new Error('Unauthorized')
        }
        try {
            return await getCurrentOrderedItemList(user);
        } catch (err) {
            console.log(err)
            throw new Error('Error fetching current orders')
        }
     }
}

const mutations = {
    createOrder: async (_, { itemId, restaurantId, userId, quantity }, user) => {
        if (!user) {
            throw new Error('Unauthorized')
        }
        try {
            await buyerPlacingOrder(itemId, restaurantId, userId, quantity);
        } catch (err) {
            console.log(err)
            throw new Error('Error creating order')
        }
    }
}

export const resolvers = { queries, mutations }