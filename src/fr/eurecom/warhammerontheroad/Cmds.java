package fr.eurecom.warhammerontheroad;

public final class Cmds {
	private Cmds(){}
	
	public static final String CMD_ERROR = 			"ERR";
	public static final String CMD_BIND = 			"BND";
	public static final String CMD_MESSAGE = 		"MSG";
	public static final String CMD_DISCONNECTED = 	"DSC";
	public static final String CMD_CONNECTED = 		"CNT";
	public static final String CMD_LIST = 			"LST";
	public static final String CMD_CREATE =			"CRT";
	public static final String CMD_ANS_LIST = 		"ALT";
	public static final String CMD_ANS_CREATE = 	"ACT";
	
	public static final int ANS_NOTHING = 			-1;
	public static final int ANS_CREATE =			0;
	public static final int ANS_LIST = 				1;
}
