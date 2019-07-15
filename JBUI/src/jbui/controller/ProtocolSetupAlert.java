package jbui.controller;

class ProtocolSetupAlert extends PathsSetupAlert<ProtocolSetupController>
{
	ProtocolSetupAlert()
	{
		super("New attack setup",
				"Here you can load a protocol file and set execution options, and trigger an execution",
				"view/protocol_setup_window.fxml");
	}
}