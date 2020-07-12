package DataSource;
import org.kohsuke.args4j.Option;

public class ServerCommand {
    @Option(required = true, name = "-p", usage = "Port number")
    private int port;

    public int getPort() {
        return port;
    }
}
