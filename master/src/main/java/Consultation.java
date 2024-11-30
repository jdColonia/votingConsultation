import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class Consultation {
    private static final Dotenv dotenv = Dotenv.load();

    // Cargar las variables de entorno desde el archivo .env
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("USER");
    private static final String PASSWORD = dotenv.get("PASSWORD");

    // Método para obtener el puesto de votación de un ciudadano
    public String consultVotingTable(String voterId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // Consulta para obtener el puesto de votación del ciudadano
            String query = """
                        SELECT pv.nombre AS voting_station
                        FROM ciudadano c
                        JOIN mesa_votacion mv ON c.mesa_id = mv.id
                        JOIN puesto_votacion pv ON mv.puesto_id = pv.id
                        WHERE c.documento = ?
                    """;
            PreparedStatement st = conn.prepareStatement(query);
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
