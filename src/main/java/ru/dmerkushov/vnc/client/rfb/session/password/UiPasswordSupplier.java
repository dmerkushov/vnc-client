/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session.password;

import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import ru.dmerkushov.vnc.client.rfb.operation.RfbOperationException;

/**
 *
 * @author dmerkushov
 */
public class UiPasswordSupplier implements PasswordSupplier {

	private static final ResourceBundle vncViewRB = ResourceBundle.getBundle("VncViewRB");

	@Override
	public String getPassword() throws RfbOperationException {
		String passwordEntered;

		JPanel panel = new JPanel();
		JLabel label = new JLabel(vncViewRB.getString("LBL_PASSWORDREQUEST"));
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[]{vncViewRB.getString("BTN_OK"), vncViewRB.getString("BTN_CANCEL")};
		int option = JOptionPane.showOptionDialog(null, panel, vncViewRB.getString("TITLE_VNCPASSWORD"),
				JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[1]);
		if (option == 0) {
			passwordEntered = new String(pass.getPassword());
		} else {
			throw new RfbOperationException("The server requires VNC authorization, but password entering cancelled by user");
		}

		return passwordEntered;
	}

}
