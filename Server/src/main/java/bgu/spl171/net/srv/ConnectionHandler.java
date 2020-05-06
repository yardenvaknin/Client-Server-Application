/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl171.net.srv;

import java.io.Closeable;
import java.io.IOException;

/**
 * The ConnectionHandler interface for Message of type T
 */
public interface ConnectionHandler<T> extends Closeable {
	/**
	 * sends msg T to the client. should be used by send and broadcast in the
	 * connections implementation
	 * 
	 * @param msg
	 */
	void send(T msg); // a function was added to the existing interface
}


