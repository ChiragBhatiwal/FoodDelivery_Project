export const mutations = `#graphql  
    type Mutation{
      addItemInCart(itemId:String!,quantity:Int!):String!
      removeItemFromCart(itemId:String!):String!
    } 
      
`