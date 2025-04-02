import {mergeTypeDefs} from '@graphql-tools/merge'
import {typeDefs} from './typedefs.js'
import {resolvers} from './resolvers.js'
import {mutations} from './mutations.js'
import {queries} from './queries.js'
import { GraphQLUpload } from "graphql-upload-minimal";

export const itemTypeDefs = mergeTypeDefs([typeDefs, mutations, queries]);
export const itemResolvers ={  Upload: GraphQLUpload,...resolvers};