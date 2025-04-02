export const mutations = `#graphql
      type Mutation {
          createOrder(itemId:String!,restaurantId:String!,userId:String!,quantity:Int!):String!
      }
`