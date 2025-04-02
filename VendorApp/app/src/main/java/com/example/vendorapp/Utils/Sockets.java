package com.example.vendorapp.Utils;

import android.content.Context;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Sockets {
    private static Sockets sockets;
    private Socket socket;

    // Constructor now accepts a token to pass in the Authorization header
    private Sockets(Context context, String token){

        URI uri = null;
        try {
            uri = new URI(Constants.Url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Set up options with the JWT token in the headers
        IO.Options options = new IO.Options();
        options.query = "token=" + token;

        // Initialize the socket with the URI and options
        socket = IO.socket(uri, options);

    }

    // Singleton pattern to get an instance of Sockets with token
    public static Sockets getInstance(Context context, String token) {
        if (sockets == null) {
            try {
                sockets = new Sockets(context.getApplicationContext(), token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sockets;
    }

    // Connect to the socket server
    public void connect() {
        socket.connect();
    }

    // Disconnect from the socket server
    public void disconnect() {
        socket.disconnect();
    }

    // Listen for socket events
    public void on(String event, Emitter.Listener listener) {
        socket.on(event, listener);
    }

    // Emit events to the server
    public void emit(String event, Object... args) {
        socket.emit(event, args);
    }
}
