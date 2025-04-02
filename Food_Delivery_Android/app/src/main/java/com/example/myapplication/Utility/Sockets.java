package com.example.myapplication.Utility;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.Utility.Constant;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Sockets {

    private static Sockets sockets;
    private Socket socket;

    // Constructor now accepts a token to pass in the Authorization header
    private Sockets(Context context, String token) {

        try {
            URI uri = new URI(Constant.base_IP);

            // Set up options with the JWT token in the headers
            IO.Options options = new IO.Options();
            options.query = "token=" + token;

            // Initialize the socket with the URI and options
            socket = IO.socket(uri, options);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
