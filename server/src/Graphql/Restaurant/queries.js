export const queries = `#graphql
type Query {
    getRestaurantDetails(restaurantId:String!):Restaurant!
    getNearbyRestaurants(location:String!):[Restaurant]!
}
`