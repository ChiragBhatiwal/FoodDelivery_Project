import {mutations} from "./mutations.js"
import {queries} from './queries.js'
import {mergeTypeDefs} from "@graphql-tools/merge"
import {resolvers} from "./resolvers.js"


export const cartTypeDefs = mergeTypeDefs([
    mutations,
    queries
])
export const cartResolvers = resolvers