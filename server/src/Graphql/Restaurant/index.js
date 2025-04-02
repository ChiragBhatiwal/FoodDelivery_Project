import {mergeTypeDefs} from '@graphql-tools/merge'
import {typeDefs} from './typedefs.js'
import {resolvers} from './resolvers.js'
import {mutations} from './mutations.js'
import {queries} from './queries.js'

export const restaurantTypeDefs = mergeTypeDefs([typeDefs, mutations, queries]);
export const restaurantResolvers = resolvers