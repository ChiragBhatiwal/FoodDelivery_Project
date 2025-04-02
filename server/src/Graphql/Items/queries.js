export const queries = `#graphql
    type Query {
        getItemByItemId(itemId:String!):Item!
        getItemsByRestaurantId(restaurantId:String!):[Item]!
        searchingItem(value:String!):[Item]!
    }
`