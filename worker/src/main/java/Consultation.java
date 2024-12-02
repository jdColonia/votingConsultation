import java.sql.*;

public class Consultation {

	// Cargar los datos para la conexión con la base de datos
	private static final String DB_URL = "jdbc:postgresql://xhgrid2/votaciones";
	private static final String USER = "postgres";
	private static final String PASSWORD = "postgres";

	// Conexión persistente
	private Connection connection;

	// Método para abrir la conexión
	public void openConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		}
	}

	// Método para cerrar la conexión
	public void closeConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}

	// Método para consulta individual (abre y cierra la conexión)
	public String consultVotingTableSingle(String voterId) throws SQLException {
		try {
			openConnection();
			return consultVotingTable(voterId);
		} finally {
			closeConnection();
		}
	}

	// Método para consultas múltiples (usando una conexión existente)
	public String consultVotingTable(String voterId) throws SQLException {
		String query = """
				    SELECT pv.nombre AS voting_station
				    FROM ciudadano c
				    JOIN mesa_votacion mv ON c.mesa_id = mv.id
				    JOIN puesto_votacion pv ON mv.puesto_id = pv.id
				    WHERE c.documento = ?
				""";

		try (PreparedStatement st = connection.prepareStatement(query)) {
			st.setString(1, voterId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getString("voting_station");
			} else {
				throw new SQLException("Votante no encontrado");
			}
		}
	}

}

