import { mergeTypeDefs } from "@graphql-tools/merge";
import {typeDefs} from './typedefs.js'
import {resolvers} from './resolvers.js'
import { mutations } from './mutations.js'
import {queries} from './queries.js'

export const userTypeDefs = mergeTypeDefs([typeDefs, mutations, queries]);
export const userResolvers = resolvers;