import { CreateSellerAccount, LoginSellerByEmailAndPassword, LogoutSellerAccount, updatePasswordOfSellerAccount } from "../../Controllers/Vendor.Controller.js"

const queries = {
    getVendorById: async () => { },
    getAllVendors: async () => { }
}

const mutations = {
    createVendor: async () => { },
    updateVendor: async () => { },
    deleteVendor: async () => { },
    loginVendor: async () => { },
    logoutVendor: async (_, any, user) => {
        if (!user.vendor) {
            return { message: "User Not Found" }
        }
        const vendorId = user.vendor;
        try {
            await LogoutSellerAccount(vendorId);
        } catch (error) {
            return { message: "Error Occured While Logging Out" }
        }
    },
}

export const resolvers = { queries, mutations }