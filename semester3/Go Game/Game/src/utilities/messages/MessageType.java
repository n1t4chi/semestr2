/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

/**
 *
 * @author n1t4chi
 */
public enum MessageType {
    //message type used for testing
    /**
     * Test message
     */
    TEST, 
    
    
    
    //Messages used for connection and latency testing. Used only between Socket classes.
    /**
     * Message send by testing socket.
     */
    PING,
    /**
     * Message send back to testing socket.
     */
    PONG, 
    
    
    // Server - Client communication
    /**
     * Server message for clients that it's shutting down.
     */
    SERVER_SHUTDOWN,
    
    
    // Socket -> Socket Owner
    /**
     * Error on closing socket.
     */
    SOCKET_CLOSE_ERROR,
    /**
     * Error while reading from socket.
     */
    SOCKET_INPUT_ERROR,
    /**
     * Error while writing to socket.
     */
    SOCKET_OUTPUT_ERROR,
    
    
    // Server - Launcher communication
    /**
     * List of rooms for clients.
     */
    RETURN_LIST_ROOMS,
    /**
     * Request for list of rooms by clients
     */
    REQUEST_LIST_ROOM,
    /**
     * Request to create new game room
     */
    CREATE_ROOM, 
    
    
    //Moves super type
    /**
     * Message type common for all moves.
     */
    MOVE,
    
    //Client <-> Client <-> Components messages
    /**
     * Chat messages used for displaying messages from other player or server/local system.
     */
    CHAT,
    
    //Client messages between RoomGUI and GameHandler
    /**
     * Request for GameHandler to return current latency
     */
    REQUEST_PING,
    /**
     * Requested value of latency
     */
    RETURN_PING, 
    /**
     * Request for GameHandler to return connection state
     */
    REQUEST_CONNECTION_STATE,
    /**
     * Requested value connection state
     */
    RETURN_CONNECTION_STATE,
    /**
     * Request for GameHandler to return current score
     */
    REQUEST_SCORE,
    /**
     * Requested value of current score
     */
    RETURN_SCORE,
    
    
    
    
     //Client messages between either RoomGUI or BoardGUI to GameHandler
    /**
     * Request for GameHandler to return game state
     */
    REQUEST_GAME_STATE,
    /**
     * Requested value of game state
     */
    RETURN_GAME_STATE,QUIT_GAME,
    
    
    
    
    
    
    //Client messages between BoardGUI and GameHandler
    /**
     * Request for GameHandler to return list of legal fields to place stone on
     */
    REQUEST_LEGAL_PLACES,
    /**
     * Requested value of list of legal fields to place stone on
     */
    RETURN_LEGAL_PLACES, 
    
    
    
    
     //Client messages between BoardGUI and GameHandler
    /**
     * Request for GameHandler to return list of default territories
     */
    REQUEST_DEFAULT_TERRITORIES,
    /**
     * Requested value of list of default territories
     */
    RETURN_DEFAULT_TERRITORIES,
    
    
    //Server <-> Game Handler and Game Handler <-> BoardGUI
    /**
     * Request for GameHandler or Server to return current board stone placement
     */
    REQUEST_BOARD,
    /**
     * Requested value of list of current board stone placement
     */
    RETURN_BOARD, 
    
    //Server <-> Game Handler
    REQUEST_OPPONENT_NAME,RETURN_OPPONENT_NAME, 
    PLAYER_CONNECTED,PLAYER_DISCONNECTED,ADD_AI
    
    
}
