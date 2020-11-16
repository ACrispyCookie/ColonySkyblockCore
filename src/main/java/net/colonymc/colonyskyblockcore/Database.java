package net.colonymc.colonyskyblockcore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	
	static final Connection conn = Main.getConnection();
	
	public static void sendStatement(String statement) {
		try {
			PreparedStatement ps = conn.prepareStatement(statement);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ResultSet getResultSet(String statement) {
		try {
			PreparedStatement ps = conn.prepareStatement(statement);
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getName(String uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT playerName FROM PlayerInfo WHERE playerUuid='" + uuid + "';");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString("playerName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getUuid(String name) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT playerUuid FROM PlayerInfo WHERE playerName='" + name + "';");
			ResultSet rs =ps.executeQuery();
			if(rs.next()) {
				return rs.getString("playerUuid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
