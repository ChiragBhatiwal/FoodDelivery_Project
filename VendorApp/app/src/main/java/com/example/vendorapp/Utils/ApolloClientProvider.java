package com.example.vendorapp.Utils;

import com.apollographql.apollo3.ApolloClient;

public class ApolloClientProvider {
    private static ApolloClient apolloClient;

    public static ApolloClient getApolloClient() {
        if (apolloClient == null) {
            apolloClient = new ApolloClient.Builder()
                    .serverUrl("https://your-graphql-endpoint.com/graphql") // 🔥 Change this
                    .build();
        }
        return apolloClient;
    }
}
