export const mutations = `#graphql

     scalar Upload

     input itemData {
          itemName:String!
          itemPrice:Float!
          itemDescription:String!
          itemImage:Upload!
          restaurantId:String!
          dishType:String!
     }
    input updateItemData {
          itemName:String
          itemPrice:Float
          itemType:String
          itemDescription:String
          itemImage:Upload!,

     }

     type Mutation {     
          createItem(data:itemData!):String!
          updateItem(itemId:String!,data:updateItemData!):String!
          deleteItem(itemId:String!):String!
     }
`