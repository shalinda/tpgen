/**
 *
 */
package com.tpgen.gen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * @author Shalinda Ranaisnghe
 *
 */
public class DBImporter {

	public void DBImporter() {

	}

	public Collection getColumnDetails(Connection conn, String sql) {
		Collection cols = new ArrayList();
		Column col = null;
		String colName = null;
		Integer colType = null;
		String varName = null;
		String methodName = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 1; i < meta.getColumnCount(); i++) {
				col = new Column();
				colName = meta.getColumnName(i);
				colType = new Integer(meta.getColumnType(i));
				col.setColName(colName);
				col.setColType(colType);
				varName = toJavaName(colName, "_", true);
				methodName = toJavaName(colName, "_", false);
				col.setVarName(varName);
				col.setMethodName(methodName);
				cols.add(col);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cols;
	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@192.168.1.29:1521:DEV";
			conn = DriverManager.getConnection(url, "production", "production");

		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		return conn;
	}

	private static String toJavaName(String colName, String separator,
			boolean lowerFirst) {
		String javaName = "";
		StringTokenizer st = new StringTokenizer(colName, separator, false);
		while (st.hasMoreElements()) {
			String element = (String) st.nextElement();
			if (lowerFirst) {
				javaName += StringUtils.lowerCase(element);
				lowerFirst = false;
			} else {
				element = StringUtils.lowerCase(element);
				javaName += StringUtils.capitalize(element);
			}
		}
		return javaName;
	}

}
