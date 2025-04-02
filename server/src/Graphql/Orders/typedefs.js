export const typeDefs = `#graphql
   type Order {
         _id: String!
         userId: User!
         restaurantId: Restaurant!
         itemId: Item!
         quantity: Int!
         status: String!
         createdAt: String!
         }
`