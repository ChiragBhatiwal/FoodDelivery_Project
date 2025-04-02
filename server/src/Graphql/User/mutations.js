export const mutations = `#graphql    
input UpdateData {
        username: String
        password: String
        email: String
        phoneNumber: String
    }
       
type Mutation {
        registerUser(phoneNumber:String!):String!
        loginUser(phoneNumber:String!,otp:String!):String!
        logoutUser:String!
        updateUser(data:UpdateData!):User!
}
`
