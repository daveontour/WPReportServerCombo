package au.com.quaysystems.qrm.wp;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import au.com.quaysystems.qrm.server.wpreport.ReportProcessorWP;

@SuppressWarnings("serial")
public 	class MyConnectionProvider implements ConnectionProvider {
	public Connection getConnection() throws SQLException {	return ReportProcessorWP.conn;}
	public void closeConnection(Connection conn) throws SQLException {return;}
	public void close() {}
	public boolean isUnwrappableAs(Class arg0) {return false;}
	public <T> T unwrap(Class<T> arg0) {return null;}
	public boolean supportsAggressiveRelease() {return false;}
}
