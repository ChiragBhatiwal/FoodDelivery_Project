import { createItem, deleteItem, updateItem ,getItemsWithRestaurantId,searchingItemFromSearchBar} from '../../Controllers/Item.Controller.js'

const queries = {
    getItemByItemId: async (_, { itemId },user) => {
        if(!user){
            return new Error("User not authenticated");
        }
        if (!itemId) {
            return new Error("Item ID is required");
        }
        try {
            const item = await findItemsWithItemId(itemId);
            return item;
        } catch (error) {
            throw new Error(`Error fetching item: ${error.message}`);
        }
    },
    getItemsByRestaurantId: async (_, { restaurantId },user) => {
        if(!user){
            return new Error("User not authenticated");
        }
        if (!restaurantId) {
            throw new Error("Restaurant ID is required");
        }
        try {
            const items = await getItemsWithRestaurantId(restaurantId);
            return items;
        } catch (error) {
            throw new Error(`Error fetching items: ${error.message}`);
        }
    },
    searchingItem: async (_, { value },user) => {
        if(!user){
            return new Error("User not authenticated");
        }
        if (!value) {
            throw new Error("Value is required for searching in the field");
        }
        try {
            const items = await searchingItemFromSearchBar(value);
            return items;
        } catch (error) {
            throw new Error(`Error searching items: ${error.message}`);
        }
    }
}

const mutations = {
    updateItem: async (_, { itemId, data }, user) => {
        if (!user) {
            throw new Error("User not authenticated");
        }
        try {
            await updateItem(itemId, data);
        } catch (error) {
            throw new Error(`Error updating item: ${error.message}`);
        }
    },

    createItem: async (_, { itemName, itemPrice, itemDescription, restaurantId, itemImage }, user) => {
        if (!user) {
            throw new Error("User not authenticated");
        }
        if (!itemName || !itemPrice || !itemDescription || !restaurantId || !itemImage) {
            throw new Error("All fields are required");
        }
        try {
            const createItemResponse = await createItem({ itemName, itemPrice, itemDescription, restaurantId, itemImage });
            return createItemResponse;
        } catch (error) {
            throw new Error(`Error creating item: ${error.message}`);
        }
    },

    deleteItem: async (_, { itemId }, user) => {
        if (!user) {
            throw new Error("User not authenticated");
        }
        if (!itemId) {
            throw new Error("Item ID is required");
        }
        try {
            const deleteItemResponse = await deleteItem(itemId);
            return deleteItemResponse;
        } catch (error) {
            throw new Error(`Error deleting item: ${error.message}`);
        }
    }
}

export const resolvers = { queries, mutations }