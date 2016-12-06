/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session.password;

import ru.dmerkushov.vnc.client.rfb.operation.RfbOperationException;

/**
 *
 * @author dmerkushov
 */
public interface PasswordSupplier {
	
	public String getPassword () throws RfbOperationException;
	
}
