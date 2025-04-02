export const queries = `#graphql
    type Query {
        getVendorById(_id: ID!): Vendor
        getAllVendors: [Vendor]
        }
`;