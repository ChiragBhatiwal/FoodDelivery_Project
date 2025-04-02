export const mutations = `#graphql
   input updateRestaurant {
        restaurantName:String
        address:String
        image:String
   }

   type Mutation {
        addRestaurant(restaurantName:String!,address:String!):Restaurant!
        updateRestaurant(restaurantId:String!,data:updateRestaurant!):Restaurant!
        deleteRestaurant(restaurantId:String!):String!
    }
   
`