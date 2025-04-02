export const mutations = `#graphql
     type Mutation {
        createVendor(name: String!, username: String!, email: String!, phone: String!, address: String!): Vendor
        updateVendor(_id: ID!, name: String, username: String, email: String, phone: String, address: String): Vendor
        deleteVendor(_id: ID!): Vendor
        loginVendor(username: String!, password: String!): String
        logoutVendor: String
     }
`