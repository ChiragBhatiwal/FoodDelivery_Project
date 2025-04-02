import { mergeTypeDefs } from '@graphql-tools/merge';
import { typeDefs } from './typedefs.js';
import { queries } from './queries.js';
import { resolvers } from './resolvers.js';
import { mutations } from './mutations.js'

export const vendorTypeDefs = mergeTypeDefs([
    typeDefs,
    queries,
    mutations
])
export const vendorResolvers = resolvers