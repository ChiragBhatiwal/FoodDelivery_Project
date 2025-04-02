export const queries = `#graphql
     type Query {
            getOrderByOrderId(orderId:String!):Order!
            getOrdersByUserId(userId:String!):[Order]!
            getOrdersByRestaurantId(restaurantId:String!):[Order]!
            getOrdersForBuyAgain(userId:String!):[Order]!
            getCurrentOrders(userId:String!):[Order]!
            getPannelDetailsForShowcasing(userId:String!):JSON
     }
`