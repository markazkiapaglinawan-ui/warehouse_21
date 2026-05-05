public final class DatabaseConfig {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 3306;
    public static final String SCHEMA = "login_1";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root1234";
    public static final String JDBC_URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA +
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Manila";

    private DatabaseConfig() {
    }
}
