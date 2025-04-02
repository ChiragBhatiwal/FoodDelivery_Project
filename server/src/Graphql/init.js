import { ApolloServer } from "@apollo/server"
import { mergeTypeDefs } from "@graphql-tools/merge";
import { itemTypeDefs, itemResolvers } from "./Items/index.js";
import { userTypeDefs, userResolvers } from "./User/index.js";
import { cartTypeDefs, cartResolvers } from "./Cart/index.js"
import { GraphQLJSON } from "graphql-scalars";
import { jsonTypeDefs } from "./jsonTypeDefs.js";
import { orderTypeDefs, orderResolvers } from "./Orders/index.js"
import { restaurantTypeDefs, restaurantResolvers } from "./Restaurant/index.js"
import { vendorTypeDefs, vendorResolvers } from "./Vendor/index.js"

const typeDefs = mergeTypeDefs([jsonTypeDefs, userTypeDefs, itemTypeDefs, cartTypeDefs, orderTypeDefs, restaurantTypeDefs, vendorTypeDefs]);

const resolvers = {
    Query: {
        ...userResolvers.queries,
        ...itemResolvers.queries,
        ...cartResolvers.queries,
        ...orderResolvers.queries,
        ...restaurantResolvers.queries,
        ...vendorResolvers.queries,
    },
    JSON: GraphQLJSON,
    Mutation: {
        ...userResolvers.mutations,
        ...itemResolvers.mutations,
        ...cartResolvers.mutations,
        ...orderResolvers.mutations,
        ...restaurantResolvers.mutations,
        ...vendorResolvers.mutations,
    },
};

const startApolloGraphqlServer = async () => {
    const server = new ApolloServer({
        typeDefs,
        resolvers,
        introspection: true,
    })

    await server.start();

    return server;
}

export { startApolloGraphqlServer, typeDefs, resolvers }