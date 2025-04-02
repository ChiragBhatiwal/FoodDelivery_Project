import { typeDefs } from "./typedefs.js";
import { mergeTypeDefs } from "@graphql-tools/merge"
import { resolvers } from "./resolvers.js"
import { mutations } from "./mutations.js"
import { queries } from "./queries.js"

export const orderTypeDefs = mergeTypeDefs([typeDefs, mutations, queries]);
export const orderResolvers = resolvers;