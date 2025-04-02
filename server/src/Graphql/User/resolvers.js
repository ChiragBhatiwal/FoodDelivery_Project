import {CreateUser,LoginUser,LogoutUser,UpdateUser} from '../../Controllers/User.Controller.js'

const queries = {
    getCurrentUser:async()=>`Hello World`
}

const mutations = {
    registerUser:async(_,{phoneNumber})=>{
        try {
            return await CreateUser(phoneNumber);
        } catch (error) {
            throw new Error(`Error registering user: ${error.message}`);
        }
    },

    loginUser:async(_,{phoneNumber,otp})=>{
        try {
            return await LoginUser(phoneNumber,otp)
        } catch (error) {
            throw new Error(`Error registering user: ${error.message}`);
        }      
    },

    logoutUser:async(parent,_,{user})=>{
        if(!user) throw new Error(`User not logged in`);
        try {
            return await LogoutUser(user);
        } catch (error) {
            throw new Error(`Error logging out user: ${error.message}`);
        }
    },

    updateUser:async()=>{
        if(!user) throw new Error(`User not logged in`);
        try {
            return await UpdateUser(user);
        } catch (error) {
            throw new Error(`Error updating user: ${error.message}`);
        }
    }
}

export const resolvers = {queries,mutations}